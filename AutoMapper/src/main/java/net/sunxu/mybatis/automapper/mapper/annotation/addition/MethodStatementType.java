package net.sunxu.mybatis.automapper.mapper.annotation.addition;


import org.apache.ibatis.mapping.StatementType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * add StatementType to method in xml
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface MethodStatementType {
    StatementType value();
}
