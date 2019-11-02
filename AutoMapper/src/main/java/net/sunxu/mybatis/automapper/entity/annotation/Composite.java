package net.sunxu.mybatis.automapper.entity.annotation;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.UnknownTypeHandler;

import java.lang.annotation.*;

/**
 * A complex field with need to construct locally with multiply properties of it. It'll generate associate locally in the mapper's result map.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Repeatable(Composites.class)
public @interface Composite {
    /**
     * property's name (without field name)
     *
     * @return
     */
    String value();

    /**
     * column's name. by default it's field name plus property's name (value, will change by DefaultColumnNamingRule)
     *
     * @return
     */
    String column() default "";

    /**
     * property's java type, by default it's the referred field's type
     *
     * @return
     */
    Class<?> javaType() default Object.class;

    /**
     * property's column in database
     *
     * @return
     */
    JdbcType jdbcType() default JdbcType.UNDEFINED;

    /**
     * type handler
     *
     * @return
     */
    Class<?> typeHandler() default UnknownTypeHandler.class;

    /**
     * If there exist columns with same name. It'll determine which one is used to save in the database.
     * @return
     */
    boolean isPreferredWhenColumnNameConflict() default false;

    String outDbExpression() default "";

    String inDbExpression() default "";

    boolean insertable() default true;

    boolean updatable() default true;
}
