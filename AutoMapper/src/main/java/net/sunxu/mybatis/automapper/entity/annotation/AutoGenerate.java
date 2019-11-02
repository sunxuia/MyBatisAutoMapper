package net.sunxu.mybatis.automapper.entity.annotation;

import java.lang.annotation.*;

/**
 * Use mybatis autogenerate to auto generate the field. By default it will auto generated while insert
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Repeatable(AutoGenerates.class)
public @interface AutoGenerate {
    enum Kind {
        BEFORE_INSERT,
        INSERT,
        AFTER_INSERT,
        BEFORE_UPDATE,
        UPDATE,
        AFTER_UPDATE
    }

    Kind kind() default Kind.INSERT;

    String value() default "";

}
