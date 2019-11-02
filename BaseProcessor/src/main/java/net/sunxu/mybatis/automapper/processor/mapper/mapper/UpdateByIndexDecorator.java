package net.sunxu.mybatis.automapper.processor.mapper.mapper;

import net.sunxu.mybatis.automapper.entity.annotation.AutoGenerate;
import net.sunxu.mybatis.automapper.processor.mapper.entity.EntityAutoGenerateSqlEmbedded;
import net.sunxu.mybatis.automapper.processor.mapper.entity.EntityIndex;
import net.sunxu.mybatis.automapper.processor.mapper.template.SelectKeyTemplate;
import net.sunxu.mybatis.automapper.processor.mapper.template.SqlBuilder;
import net.sunxu.mybatis.automapper.processor.mapper.template.UpdateTemplate;
import net.sunxu.mybatis.automapper.processor.property.Parameter;
import net.sunxu.mybatis.automapper.processor.property.field.ColumnField;
import net.sunxu.mybatis.automapper.processor.property.method.UpdateByIndexProperty;
import net.sunxu.mybatis.automapper.processor.property.type.AliasProperty;

import java.util.*;

public class UpdateByIndexDecorator
        extends EntityIndexDecorator<UpdateByIndexProperty, UpdateTemplate> {

    @Override
    protected UpdateTemplate getXmlElement(MapperMethod mapperMethod, UpdateByIndexProperty property) {
        String databaseId = configuration.getMybatisDefaultDatabaseId();
        return new UpdateTemplate(mapperMethod.getName(), databaseId);
    }

    @Override
    protected void setUpXmlElement(UpdateTemplate update, MapperMethod mapperMethod, UpdateByIndexProperty property) {
        if (entityModel.getEntityAutoGenerateSelectKeys().containsKey(AutoGenerate.Kind.BEFORE_UPDATE)) {
            update.setBeforeSelectKey(new SelectKeyTemplate(
                    entityModel.getEntityAutoGenerateSelectKeys().get(AutoGenerate.Kind.BEFORE_UPDATE)));
        }

        EntityAutoGenerateSqlEmbedded autoGenerateUpdate =
                entityModel.getEntityAutoGenerateSqlEmbeddeds().get(AutoGenerate.Kind.UPDATE);
        Set<String> autoColumnNames;
        if (autoGenerateUpdate != null) {
            setAutoGenerateAttributes(update, autoGenerateUpdate);
            autoColumnNames = autoGenerateUpdate.getColumnNames();
        } else {
            autoColumnNames = Collections.emptySet();
        }

        int maxColumnNameLength = 0;
        for (Map.Entry<String, String> entry : entityModel.getColumnNamePropertyNameMap().entrySet()) {
            String columnName = entry.getKey();
            if ((!autoColumnNames.contains(columnName) ||
                    autoGenerateUpdate.hasExpression(columnName))) {
                maxColumnNameLength = Math.max(maxColumnNameLength, columnName.length());
            }
        }
        if (maxColumnNameLength == 0) {
            update.setSql("<!-- no non-auto generated column to update -->");
        } else {
            String paramPrefix;
            if (mapperMethod.getMethod().getParameters().get(0).contains(AliasProperty.class)) {
                paramPrefix = mapperMethod.getMethod()
                        .getParameters()
                        .get(0)
                        .get(AliasProperty.class)
                        .alias() + ".";
            } else {
                paramPrefix = "0.";
            }

            SqlBuilder sb = new SqlBuilder();
            sb.append("update ").append(entityModel.getTableName()).append(" set ");
            sb.addIndent();
            final int indent = maxColumnNameLength;
            entityModel.getColumnNamePropertyNameMap()
                    .entrySet().stream()
                    .sorted(Comparator.comparing(e -> e.getKey()))
                    .forEach(entry -> {
                        String columnName = entry.getKey();
                        String propertyName = entry.getValue();
                        ColumnField columnField = entityModel.getColumnFields().get(propertyName);

                        if (autoColumnNames.contains(columnName)) {
                            if (autoGenerateUpdate.hasExpression(columnName)) {
                                sb.newLine(indent - columnName.length())
                                        .append(columnName)
                                        .append(" = ")
                                        .append(autoGenerateUpdate.getExpression(columnName))
                                        .append(", ");
                            }
                        } else {
                            sb.newLine(indent - columnName.length())
                                    .append(columnName)
                                    .append(" = ")
                                    .addParameter(paramPrefix + propertyName, columnField)
                                    .append(", ");
                        }
                    });
            sb.trimEnd(", ");

            EntityIndex index = entityModel.getEntityIndexes().get(property.indexName());
            List<Parameter> parameters = mapperMethod.getMethod().getParameters();
            if (isParameterEntityValue(parameters.get(1))) {
                setWhereExpression(sb, index, parameters.get(1));
            } else {
                setWhereExpression(sb, index, mapperMethod, 1);
            }
            sb.endWhere();

            update.setSql(sb.toString());
        }
        if (entityModel.getEntityAutoGenerateSelectKeys().containsKey(AutoGenerate.Kind.AFTER_UPDATE)) {
            update.setAfterSelectKey(new SelectKeyTemplate(
                    entityModel.getEntityAutoGenerateSelectKeys().get(AutoGenerate.Kind.AFTER_UPDATE)));
        }

        if (!"void".equals(mapperMethod.getReturnType())) {
            update.addAttribute("resultType", mapperMethod.getReturnType());
        }
    }

    @Override
    protected String getDecoratorName() {
        return "UpdateByIndex";
    }
}
