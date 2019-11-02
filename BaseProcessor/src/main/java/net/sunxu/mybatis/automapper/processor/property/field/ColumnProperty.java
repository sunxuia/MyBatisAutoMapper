package net.sunxu.mybatis.automapper.processor.property.field;

import com.google.inject.Inject;
import net.sunxu.mybatis.automapper.entity.annotation.Column;
import net.sunxu.mybatis.automapper.processor.environment.Configuration;
import net.sunxu.mybatis.automapper.processor.property.Field;
import net.sunxu.mybatis.automapper.processor.property.PropertyForAnnotation;
import net.sunxu.mybatis.automapper.processor.property.Type;
import net.sunxu.mybatis.automapper.processor.property.type.DefaultColumnNamingRuleProperty;
import org.apache.ibatis.type.JdbcType;

import javax.lang.model.element.AnnotationMirror;

import static com.google.common.base.Strings.isNullOrEmpty;
import static net.sunxu.mybatis.automapper.processor.util.HelpUtils.getAnnotationTypeValue;


@PropertyForAnnotation(Column.class)
public class ColumnProperty extends AbstractFieldAnnotationProperty implements ColumnField {
    @Inject
    private Column column;
    @Inject
    private Field field;
    @Inject
    private Type type;
    @Inject
    private Configuration configuration;

    private String javaType;
    private boolean useJavaType;
    private String typeHandler;
    private String columnName;
    private JdbcType jdbcType;

    @Override
    protected void initial() {
        AnnotationMirror columnMirror = getAnnotationMirror(Column.class);
        typeHandler = getAnnotationTypeValue(columnMirror, "typeHandler");

        columnName = column.value();
        if (isNullOrEmpty(columnName)) {
            columnName = type.get(DefaultColumnNamingRuleProperty.class).getDefaultColumnName(field.getName());
        }

        javaType = getAnnotationTypeValue(columnMirror, "javaType");
        if (isNullOrEmpty(javaType)) {
            useJavaType = false;
            javaType = field.getSimpleType();
        } else {
            useJavaType = true;
        }

        jdbcType = column.jdbcType();
        if (jdbcType == JdbcType.UNDEFINED) {
            jdbcType = configuration.getJdbcTypeByJavaType(javaType);
        }
    }

    @Override
    public String typeHandler() {
        return typeHandler;
    }

    @Override
    public boolean isPreferredWhenColumnNameConflict() {
        return column.isPreferredWhenColumnNameConflict();
    }

    @Override
    public String columnName() {
        return columnName;
    }

    @Override
    public JdbcType jdbcType() {
        return jdbcType;
    }

    @Override
    public String outDbExpression() {
        return column.outDbExpression().trim();
    }

    @Override
    public String inDbExpression() {
        return column.inDbExpression().trim();
    }

    @Override
    public boolean insertable() {
        return column.insertable();
    }

    @Override
    public boolean updatable() {
        return column.updatable();
    }

    @Override
    public String propertyName() {
        return field.getName();
    }

    @Override
    public String javaType() {
        return javaType;
    }

    @Override public boolean useJavaType() {
        return useJavaType;
    }
}
