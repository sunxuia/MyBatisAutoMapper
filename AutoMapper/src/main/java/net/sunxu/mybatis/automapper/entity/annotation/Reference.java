package net.sunxu.mybatis.automapper.entity.annotation;

import net.sunxu.mybatis.automapper.mapper.EntityMapper;
import org.apache.ibatis.mapping.FetchType;
import org.apache.ibatis.type.JdbcType;

import java.lang.annotation.*;

/**
 * reference to other entity
 * notice : other entity should also be a entity class (annotated with @Entity).
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Reference {


    /**
     * local columns. by default it's field's name
     *
     * @return
     */
    LocalColumn[] localColumns() default {};

    /**
     * entity type referred to, by default it's it's field type
     *
     * @return
     */
    Class<?> referTo() default Object.class;

    /**
     * referred entity's index name. by default it's primary key.
     *
     * @return
     */
    String referIndex() default "";

    /**
     * fetch type
     *
     * @return
     */
    FetchType fetchType() default FetchType.DEFAULT;

    /**
     * cascade select from a mapper (entity mapper)
     *
     * @return
     */
    Class<? extends EntityMapper> byMapper() default EntityMapper.class;

    /**
     * local column definition
     */
    @Retention(RetentionPolicy.RUNTIME)
    @interface LocalColumn {
        /**
         * column name
         *
         * @return
         */
        String value();

        JdbcType jdbcType() default JdbcType.UNDEFINED;

        boolean isPreferredWhenColumnNameConflict() default false;

        boolean insertable() default true;

        boolean updatable() default true;
    }
}
