package net.sunxu.mybatis.automapper.processor.mapper.mapper;

import net.sunxu.mybatis.automapper.entity.annotation.AutoGenerate;
import net.sunxu.mybatis.automapper.processor.mapper.entity.EntityAutoGenerateSqlEmbedded;
import net.sunxu.mybatis.automapper.processor.mapper.template.InsertTemplate;
import net.sunxu.mybatis.automapper.processor.mapper.template.SelectKeyTemplate;
import net.sunxu.mybatis.automapper.processor.mapper.template.SqlBuilder;
import net.sunxu.mybatis.automapper.processor.property.Parameter;
import net.sunxu.mybatis.automapper.processor.property.field.ColumnField;
import net.sunxu.mybatis.automapper.processor.property.method.InsertOneProperty;
import net.sunxu.mybatis.automapper.processor.property.type.AliasProperty;

import java.util.Collections;
import java.util.Comparator;
import java.util.Set;

public class InsertOneDecorator extends EntityMethodDecorator<InsertOneProperty, InsertTemplate> {

    @Override
    protected String getDecoratorName() {
        return "InsertOne";
    }

    @Override
    protected InsertTemplate getXmlElement(MapperMethod mapperMethod, InsertOneProperty property) {
        String databaseId = configuration.getMybatisDefaultDatabaseId();
        return new InsertTemplate(mapperMethod.getName(), databaseId);
    }

    @Override
    protected void setUpXmlElement(InsertTemplate insert, MapperMethod mapperMethod, InsertOneProperty property) {
        if (entityModel.getEntityAutoGenerateSelectKeys().containsKey(AutoGenerate.Kind.BEFORE_INSERT)) {
            insert.setBeforeSelectKey(new SelectKeyTemplate(
                    entityModel.getEntityAutoGenerateSelectKeys().get(AutoGenerate.Kind.BEFORE_INSERT)));
        }

        EntityAutoGenerateSqlEmbedded autoGenerateInsert =
                entityModel.getEntityAutoGenerateSqlEmbeddeds().get(AutoGenerate.Kind.INSERT);
        Set<String> autoColumnNames;
        if (autoGenerateInsert != null) {
            setAutoGenerateAttributes(insert, autoGenerateInsert);
            autoColumnNames = autoGenerateInsert.getColumnNames();
        } else {
            autoColumnNames = Collections.emptySet();
        }

        String paramPrefix = getParamPrefix(mapperMethod);

        SqlBuilder sb = new SqlBuilder();
        sb.append("insert into ").append(entityModel.getTableName()).append(" (");
        SqlBuilder valueBuilder = new SqlBuilder();
        entityModel.getColumnNamePropertyNameMap()
                .entrySet().stream()
                .sorted(Comparator.comparing(e -> e.getKey()))
                .forEach(entry -> {
                    String columnName = entry.getKey();
                    String propertyName = entry.getValue();
                    ColumnField columnField = entityModel.getColumnFields().get(propertyName);
                    if (!columnField.insertable()) {
                        return;
                    }

                    if (autoColumnNames.contains(columnName)) {
                        if (autoGenerateInsert.hasExpression(columnName)) {
                            sb.append(columnName).append(", ");
                            valueBuilder.append(autoGenerateInsert.getExpression(columnName)).append(", ");
                        }
                    } else {
                        sb.append(columnName).append(", ");
                        valueBuilder.addParameter(paramPrefix + propertyName, columnField)
                                .append(", ");
                    }
                });
        sb.trimEnd(", ");
        valueBuilder.trimEnd(", ");
        sb.append(")")
                .newLine(6 + entityModel.getTableName().length())
                .append("values (").append(valueBuilder.toString().trim()).append(")");
        insert.setSql(sb.toString());

        if (entityModel.getEntityAutoGenerateSelectKeys().containsKey(AutoGenerate.Kind.AFTER_INSERT)) {
            insert.setAfterSelectKey(new SelectKeyTemplate(
                    entityModel.getEntityAutoGenerateSelectKeys().get(AutoGenerate.Kind.AFTER_INSERT)));
        }

        setAttributeWithMapperMethod(insert, mapperMethod);
    }

    private String getParamPrefix(MapperMethod mapperMethod) {
        Parameter parameter = mapperMethod.getMethod().getParameters().get(0);
        String paramName = parameter.contains(AliasProperty.class) ?
                parameter.get(AliasProperty.class).alias() + "." :
                "";
        return paramName;
    }
}
