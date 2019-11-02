package net.sunxu.mybatis.automapper.entity.annotation;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.UnknownTypeHandler;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * a column - field relationship
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Column {
    /**
     * Column name in database. by default it's field's name. It's name will be affected by configuration
     * or @DefaultColumnNamingRule in of entity.
     * @return
     */
    String value() default "";

    /**
     * Field's java type, by default it's field type.
     * @return
     */
    Class<?> javaType() default Object.class;

    /**
     * column's jdbc type in database.
     * @return
     */
    JdbcType jdbcType() default JdbcType.UNDEFINED;

    /**
     * typehandler
     * @return
     */
    Class<? extends TypeHandler> typeHandler() default UnknownTypeHandler.class;

    /**
     * If there exist columns with same name in a entity. It'll determine which one is used to save into the database.
     * @return
     */
    boolean isPreferredWhenColumnNameConflict() default false;

    String outDbExpression() default "";

    String inDbExpression() default "";

    boolean insertable() default true;

    boolean updatable() default true;
}