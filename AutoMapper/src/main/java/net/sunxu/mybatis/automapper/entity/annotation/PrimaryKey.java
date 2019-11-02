package net.sunxu.mybatis.automapper.entity.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * primary key. Infer the field's column is a primary key.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface PrimaryKey {
    /**
     * The order of the primary key. All primary keys (include parent's primary keys) should
     * have different order.
     * The field should have {@link Column} or {@link Composite} or {@link Reference} or their collection annotations
     * to point out it column name.
     * @return
     */
    int value() default 100;
}
