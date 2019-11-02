package net.sunxu.mybatis.automapper.processor.property;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface PropertyForAnnotation {
    Class<? extends Annotation>[] value();

    Option option() default Option.OR;

    enum Option {
        AND,
        OR
    }

    boolean alwaysCreate() default false;
}
