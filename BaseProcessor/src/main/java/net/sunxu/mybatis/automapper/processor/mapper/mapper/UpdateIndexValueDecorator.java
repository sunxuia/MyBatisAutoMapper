package net.sunxu.mybatis.automapper.processor.mapper.mapper;

import com.google.inject.Inject;
import net.sunxu.mybatis.automapper.processor.environment.EnvironmentHelper;
import net.sunxu.mybatis.automapper.processor.environment.MessageHelper;
import net.sunxu.mybatis.automapper.processor.mapper.entity.EntityIndex;
import net.sunxu.mybatis.automapper.processor.mapper.template.SqlBuilder;
import net.sunxu.mybatis.automapper.processor.mapper.template.UpdateTemplate;
import net.sunxu.mybatis.automapper.processor.property.Parameter;
import net.sunxu.mybatis.automapper.processor.property.field.ColumnField;
import net.sunxu.mybatis.automapper.processor.property.method.UpdateIndexValueProperty;
import net.sunxu.mybatis.automapper.processor.property.parameter.ParamProperty;

import java.util.List;

public class UpdateIndexValueDecorator
        extends EntityIndexDecorator<UpdateIndexValueProperty, UpdateTemplate> {
    @Inject
    private EnvironmentHelper env;
    @Inject
    private MessageHelper messageHelper;

    @Override
    protected UpdateTemplate getXmlElement(MapperMethod mapperMethod, UpdateIndexValueProperty property) {
        String databaseId = configuration.getMybatisDefaultDatabaseId();
        return new UpdateTemplate(mapperMethod.getName(), databaseId);
    }

    @Override
    protected void setUpXmlElement(UpdateTemplate update, MapperMethod mapperMethod,
                                   UpdateIndexValueProperty property) {
        if (!"void".equals(mapperMethod.getReturnType())) {
            update.addAttribute("resultType", mapperMethod.getReturnType());
        }
        EntityIndex updateIndex = entityModel.getEntityIndexes().get(property.indexNameToUpdate());

        SqlBuilder sb = new SqlBuilder();
        sb.append("update ").append(entityModel.getTableName()).append(" set");
        List<Parameter> parameters = mapperMethod.getMethod().getParameters();
        int restrictStartPos = 1;
        if (isParameterEntityValue(parameters.get(0))) {
            Parameter updateParameter = parameters.get(0);
            String paramName = updateParameter.contains(ParamProperty.class) ?
                    updateParameter.get(ParamProperty.class).paramName() : "0";
            for (ColumnField columnField : updateIndex.getColumnFields()) {
                sb.newLine().append(columnField.columnName())
                        .append(" = ")
                        .addParameter(paramName + "." + columnField.propertyName(), columnField)
                        .append(", ");
            }
        } else {
            for (int i = 0; i < updateIndex.getFields().size(); i++) {
                String paramName = parameters.get(i).contains(ParamProperty.class) ?
                        parameters.get(i).get(ParamProperty.class).paramName() :
                        String.valueOf(i);
                for (ColumnField columnField : updateIndex.getFields().get(i).getValue()) {
                    String subParamName = getSubPropertyName(columnField.propertyName(), paramName);
                    sb.newLine().append(columnField.columnName())
                            .append(" = ")
                            .addParameter(subParamName, columnField)
                            .append(", ");
                }
            }
        }
        sb.trimEnd(", ");

        EntityIndex restrictIndex = entityModel.getEntityIndexes().get(property.indexNameToRestrict());
        if (isParameterEntityValue(parameters.get(restrictStartPos))) {
            setWhereExpression(sb, restrictIndex, parameters.get(restrictStartPos));
        } else {
            setWhereExpression(sb, restrictIndex, mapperMethod, restrictStartPos);
        }
        sb.endWhere();

        update.setSql(sb.toString());
    }

    private String getSubPropertyName(String propertyName, String paramName) {
        int idx = propertyName.indexOf(".");
        if (idx == -1) {
            return paramName;
        }
        return paramName + propertyName.substring(idx, propertyName.length());
    }

    @Override
    protected String getDecoratorName() {
        return "UpdateIndexValue";
    }
}
