package net.sunxu.mybatis.automapper.processor.mapper.mapper;

import net.sunxu.mybatis.automapper.processor.mapper.entity.EntityIndex;
import net.sunxu.mybatis.automapper.processor.mapper.template.AbstractXmlMapperElement;
import net.sunxu.mybatis.automapper.processor.mapper.template.SqlBuilder;
import net.sunxu.mybatis.automapper.processor.property.AbstractAnnotationProperty;
import net.sunxu.mybatis.automapper.processor.property.Parameter;
import net.sunxu.mybatis.automapper.processor.property.field.ColumnField;
import net.sunxu.mybatis.automapper.processor.property.parameter.EntityValueProperty;
import net.sunxu.mybatis.automapper.processor.property.parameter.ParamProperty;

import javax.lang.model.element.ExecutableElement;
import java.util.List;

abstract class EntityIndexDecorator
        <P extends AbstractAnnotationProperty<ExecutableElement>, E extends AbstractXmlMapperElement>
        extends EntityMethodDecorator<P, E> {
    protected final void setWhereExpression(SqlBuilder sqlBuilder, EntityIndex index,
                                            MapperMethod method, int startIndex) {
        List<Parameter> parameters = method.getMethod().getParameters();
        for (int i = startIndex; i < startIndex + index.getFields().size(); i++) {
            List<ColumnField> columnFields = index.getFields().get(i - startIndex).getValue();
            for (ColumnField columnField : columnFields) {
                sqlBuilder.where(columnField.columnName())
                        .append(" = ")
                        .addParameter(parameters.get(i), columnField);
            }
        }
    }

    protected final void setWhereExpression(SqlBuilder sqlBuilder, EntityIndex index, Parameter parameter) {
        String paramPrefix = parameter.contains(ParamProperty.class) ?
                parameter.get(ParamProperty.class).paramName() + "." : "";
        for (ColumnField columnField : index.getColumnFields()) {
            sqlBuilder.where(columnField.columnName())
                    .append(" = ")
                    .addParameter(paramPrefix + columnField.propertyName(), columnField);
        }
    }

    protected final boolean isParameterEntityValue(Parameter parameter) {
        return parameter.contains(EntityValueProperty.class);
    }
}
