package net.sunxu.mybatis.automapper.mapper.annotation.addition;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * add FlushCache to method in xml
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface MethodFlushCache {

    boolean value() default true;

}
