package net.sunxu.mybatis.automapper.entity.annotation;

import java.lang.annotation.*;

/**
 * index of a entity.
 * There is no need to exist a real index with same name in the databse, it's only used to manage columns and fields of the code.
 * A field can have different index name.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Repeatable(Indexes.class)
public @interface Index {

    /**
     * index's name, case sensitive. By default it's same with the field's name.
     *
     * @return
     */
    String value() default "";

    /**
     * order in the index. All fields with same index name should not have same order.
     * If the field is Anotated with {@link Reference} or {@link Composites} the column's order is on its order in it.
     *
     * @return
     */
    int order() default 100;
}
