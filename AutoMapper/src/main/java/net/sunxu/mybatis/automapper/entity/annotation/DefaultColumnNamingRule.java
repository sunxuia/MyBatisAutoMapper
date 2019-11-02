package net.sunxu.mybatis.automapper.entity.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Determine the way of column name genrated from field's name
 * include @Column's columnName, @Composite's column(), @Reference's localColumns
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface DefaultColumnNamingRule {
    enum NameRule {
        /**
         * it's field's name
         */
        FIELD_NAME,
        /**
         * all lower-case
         */
        LOWER_CASE,
        /**
         * split the camelCase field name like "camel_case"
         */
        LOWER_CASE_SPLIT_BY_UNDER_LINE,
        /**
         * all upper-case
         */
        UPPER_CASE,
        /**
         * split the camelCase field name like "CAMEL_CASE"
         */
        UPPER_CASE_SPLIT_BY_UNDER_LINE,
    }

    /**
     * DEFAULT column name generate rule
     * @return
     */
    NameRule value() default NameRule.FIELD_NAME;
}
