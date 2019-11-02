package net.sunxu.mybatis.automapper.processor.mapper.mapper;

import com.google.inject.Inject;
import net.sunxu.mybatis.automapper.processor.environment.Configuration;
import net.sunxu.mybatis.automapper.processor.mapper.entity.EntityIndex;
import net.sunxu.mybatis.automapper.processor.mapper.entity.EntityModel;
import net.sunxu.mybatis.automapper.processor.mapper.template.ResultMapBuilder;
import net.sunxu.mybatis.automapper.processor.mapper.template.SelectTemplate;
import net.sunxu.mybatis.automapper.processor.mapper.template.SqlBuilder;
import net.sunxu.mybatis.automapper.processor.mapper.template.SqlTemplate;
import net.sunxu.mybatis.automapper.processor.mapper.xml.XmlElement;
import net.sunxu.mybatis.automapper.processor.property.field.*;
import org.javatuples.LabelValue;

import java.util.*;

import static com.google.common.base.Strings.isNullOrEmpty;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

public class EntityMapperDecorator extends AbstractCreatorDecorator {
    public static final String RESULT_MAP_FULL_COLUMN = "ENTITY_FULL_COLUMN";
    public static final String SQL_FULL_COLUMN = "ENTITY_FULL_COLUMN";
    public static final String SELECT_SELECT_BY_COLUMN_FROM_INDEX_PREFIX = "selectByColumnFromIndex_";

    @Inject
    private Configuration configuration;

    private List<EntityModel> entityModels;

    private Set<String> entitiesInMapper;

    private Map<String, EntityModel> entities;

    private String annoymousMapperName;

    void setEntityModels(List<EntityModel> entityModels) {
        this.entityModels = entityModels;
    }

    void setEntitiesInMapper(Set<String> entitiesInMapper) {
        this.entitiesInMapper = entitiesInMapper;
    }

    void setAnnoymousMapperName(String annoymousMapperName) {
        this.annoymousMapperName = annoymousMapperName;
    }

    void setEntities(Map<String, EntityModel> entities) {
        this.entities = entities;
    }

    @Override
    protected String getDecoratorName() {
        return "entity mapper";
    }

    @Override
    protected List<? extends XmlElement> getXmlElements() {
        List<XmlElement> res = new ArrayList<>(4 * entityModels.size());
        for (EntityModel entityModel : entityModels) {
            final String prefix = getIdPrefix(entityModel);
            final String databaseId = configuration.getMybatisDefaultDatabaseId();

            ResultMapBuilder resultMapBuilder =
                    new ResultMapBuilder(prefix + RESULT_MAP_FULL_COLUMN, entityModel.getAlias());
            setUpFullColumnResultMap(resultMapBuilder, entityModel);
            res.add(resultMapBuilder.getResultMap());

            SqlTemplate sql = new SqlTemplate(prefix + SQL_FULL_COLUMN, databaseId);
            setUpFullColumnSql(sql, entityModel);
            res.add(sql);

            entityModel.getEntityIndexes()
                    .keySet().stream()
                    .sorted(Comparator.comparing(indexName -> indexName))
                    .forEach(indexName -> {
                        SelectTemplate select =
                                new SelectTemplate(prefix + SELECT_SELECT_BY_COLUMN_FROM_INDEX_PREFIX + indexName,
                                        databaseId);
                        setUpSelectColumnByIndex(select, indexName, entityModel);
                        res.add(select);
                    });
        }
        return res;
    }

    protected final String getIdPrefix(EntityModel entityModel) {
        if (entitiesInMapper.contains(entityModel.getName())) {
            return "";
        } else {
            return entityModel.getName().replaceAll("\\.", "_") + "_";
        }
    }

    protected void setUpFullColumnResultMap(ResultMapBuilder builder, EntityModel entityModel) {
        EntityIndex primaryKeyIndex = entityModel.getEntityIndexes().get("");
        final Set<String> pkFieldNames;
        if (primaryKeyIndex != null) {
            pkFieldNames = primaryKeyIndex.getFields().stream()
                    .filter(pair -> pair.getValue().size() == 1 &&
                            !pair.getValue().get(0).propertyName().contains("."))
                    .map(LabelValue::getLabel)
                    .collect(toSet());
            pkFieldNames.stream()
                    .sorted()
                    .map(fieldName -> entityModel.getColumns().get(fieldName))
                    .forEach(builder::addId);
        } else {
            pkFieldNames = Collections.emptySet();
        }
        entityModel.getColumns()
                .values().stream()
                .filter(column -> !pkFieldNames.contains(column.propertyName()))
                .sorted(Comparator.comparing(ColumnProperty::propertyName))
                .forEach(builder::addResult);

        entityModel.getComposites()
                .values().stream()
                .sorted(Comparator.comparing(CompositesProperty::fieldName))
                .forEach(builder::addAssociateionLocal);

        entityModel.getReferences()
                .values().stream()
                .sorted(Comparator.comparing(ReferenceProperty::fieldName))
                .forEach(reference -> builder.addAssociationSelectByAssociate(reference,
                        getRefer(entityModel, reference.byMapper(), reference.referEntity(), reference.referIndex())));

        entityModel.getCascades()
                .values().stream()
                .sorted(Comparator.comparing(CascadeProperty::fieldName))
                .forEach(cascade -> {
                    EntityIndex localIndex = entityModel.getEntityIndexes().get(cascade.localIndex());
                    String referSelect =
                            getRefer(entityModel, cascade.byMapper(), cascade.referEntity(), cascade.referIndex());
                    if (cascade.isMany()) {
                        builder.addCollectionSelectByCascade(cascade, localIndex, referSelect);
                    } else {
                        builder.addAssociationSelectByCascade(cascade, localIndex, referSelect);
                    }
                });
    }

    protected final String getRefer(EntityModel localEntityModel, String byMapper,
                                    String referEntityName, String referIndexName) {
        StringBuilder sb = new StringBuilder();
        if (localEntityModel.getName().equals(referEntityName)) {
            //do nothing
        } else if (!entitiesInMapper.contains(localEntityModel.getName()) &&
                !entitiesInMapper.contains(referEntityName)) {
            sb.append(referEntityName.replaceAll("\\.", "_")).append("_");
        } else if (!isNullOrEmpty(byMapper)) {
            sb.append(byMapper).append(".");
        } else if (!entitiesInMapper.contains(referEntityName)) {
            referEntityName = entities.get(referEntityName).getAlias();
            sb.append(annoymousMapperName).append(".")
                    .append(referEntityName.replaceAll("\\.", "_")).append("_");
        } else {
            sb.append(referEntityName).append(".");
        }
        sb.append(SELECT_SELECT_BY_COLUMN_FROM_INDEX_PREFIX);
        sb.append(referIndexName);
        return sb.toString();
    }

    protected void setUpFullColumnSql(SqlTemplate sql, EntityModel entityModel) {
        SqlBuilder sb = new SqlBuilder();
        List<String> propertyNames = entityModel.getColumnNamePropertyNameMap()
                .entrySet().stream()
                .sorted(Comparator.comparing(Map.Entry::getKey))
                .map(Map.Entry::getValue)
                .collect(toList());
        for (String propertyName : propertyNames) {
            ColumnField columnField = entityModel.getColumnFields().get(propertyName);
            sb.addSelectColumnName(columnField).append(", ");
        }
        sb.trimEnd(", ");
        sql.setSql(sb.toString());
    }

    protected void setUpSelectColumnByIndex(SelectTemplate select, String indexName, EntityModel entityModel) {
        SqlBuilder sqlBuilder = new SqlBuilder();
        sqlBuilder.append("select ").addIncludeSql(getIdPrefix(entityModel) + SQL_FULL_COLUMN)
                .newLine().append("from ").append(entityModel.getTableName());
        EntityIndex index = entityModel.getEntityIndexes().get(indexName);

        int i = -1;
        String lastFieldName = "";
        for (ColumnField columnField : index.getColumnFields()) {
            String fieldName = columnField.propertyName();
            String suffix = "";
            if (fieldName.contains("\\.")) {
                suffix = fieldName.substring(fieldName.indexOf('.'), fieldName.length());
                fieldName = fieldName.substring(0, fieldName.indexOf('.'));
            }
            i += lastFieldName.equals(fieldName) ? 0 : 1;
            lastFieldName = fieldName;

            sqlBuilder.where(columnField.columnName())
                    .append(" = ")
                    .addParameter(String.valueOf(i) + suffix, columnField);
        }
        sqlBuilder.endWhere();

        select.setSql(sqlBuilder.toString());
        select.addAttribute("resultMap", getIdPrefix(entityModel) + RESULT_MAP_FULL_COLUMN);
    }
}
