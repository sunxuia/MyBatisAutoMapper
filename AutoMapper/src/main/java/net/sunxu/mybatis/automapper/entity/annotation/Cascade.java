package net.sunxu.mybatis.automapper.entity.annotation;


import net.sunxu.mybatis.automapper.mapper.EntityMapper;
import org.apache.ibatis.mapping.FetchType;

import java.lang.annotation.*;

/**
 * cascade with other entity.
 * notice : the other entity should also be a entity class (annotated with @Entity).
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Cascade {
    /**
     * entity type of cascade. It will select from database. by default it will be the type of field.
     * If it's type of List or array, it will use the component type as value.
     *
     * @return
     */
    Class<?> referEntity() default Object.class;

    /**
     * cascade entity's index, by default it's primary key
     *
     * @return
     */
    String value() default "";

    /**
     * local index, by default it's primary key
     *
     * @return
     */
    String localIndex() default "";

    /**
     * is the result has multiply rows.
     *
     * @return
     */
    boolean many() default false;

    /**
     * fetch type
     *
     * @return
     */
    FetchType fetchType() default FetchType.DEFAULT;

    /**
     * cascade select from a type
     *
     * @return
     */
    Class<? extends EntityMapper> byMapper() default EntityMapper.class;

}
