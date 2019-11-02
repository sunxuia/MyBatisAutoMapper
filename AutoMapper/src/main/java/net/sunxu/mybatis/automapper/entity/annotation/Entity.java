package net.sunxu.mybatis.automapper.entity.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * mark a entity class to a database's table.
 * The entity class should has a non-parameter constructor method.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Entity {
    /**
     * table name, by default it's class's simple name
     * @return
     */
    String value() default "";
}
