package net.sunxu.mybatis.automapper.processor.property.field;

import org.apache.ibatis.type.JdbcType;

import static com.google.common.base.Strings.isNullOrEmpty;


public interface ColumnField {

    String columnName();

    JdbcType jdbcType();

    default boolean hasJdbcType() {
        return jdbcType() != JdbcType.UNDEFINED;
    }

    String outDbExpression();

    String inDbExpression();

    boolean insertable();

    boolean updatable();

    String propertyName();

    String javaType();

    boolean useJavaType();

    String typeHandler();

    default boolean hasTypeHandler() {
        return !isNullOrEmpty(typeHandler());
    }

    boolean isPreferredWhenColumnNameConflict();
}
