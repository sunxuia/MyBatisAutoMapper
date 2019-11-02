package net.sunxu.mybatis.automapper.mapper.annotation.addition;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * add UseCache to method in xml
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface MethodUseCache {
    boolean value() default true;
}
