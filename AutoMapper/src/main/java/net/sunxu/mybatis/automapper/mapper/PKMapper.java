package net.sunxu.mybatis.automapper.mapper;

import net.sunxu.mybatis.automapper.mapper.annotation.entity.DeleteByIndex;
import net.sunxu.mybatis.automapper.mapper.annotation.entity.SelectByIndex;
import net.sunxu.mybatis.automapper.mapper.annotation.entity.UpdateByIndex;
import org.apache.ibatis.annotations.Param;

/**
 * entity with 1 primary key's s entity mapper method.
 */
public interface PKMapper<T, P> extends EntityMapper<T> {
    /**
     * select by primary key
     * @param pk primary key
     * @return entity with the primary key
     */
    @SelectByIndex("")
    T getByPK(P pk);

    /**
     * Pessimistic lock (add for update when select by pk)
     * @param pk primary key
     * @return entity with the primary key
     */
    T getByPKForUpdate(P pk);

    /**
     * update value to row with the primary key
     * @param newValue the new value (might change the primary key's value)
     * @param pk primary key
     * @return affected rows count
     */
    @UpdateByIndex("")
    int updateByPK(@Param("newValue") T newValue, @Param("pk") P pk);

    /**
     * delete row with primary key
     * @param pk primary key
     * @return affected rows count
     */
    @DeleteByIndex("")
    int deleteByPK(P pk);
}
