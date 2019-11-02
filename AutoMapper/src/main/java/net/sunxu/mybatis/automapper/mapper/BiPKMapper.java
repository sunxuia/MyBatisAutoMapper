package net.sunxu.mybatis.automapper.mapper;

import net.sunxu.mybatis.automapper.mapper.annotation.entity.DeleteByIndex;
import net.sunxu.mybatis.automapper.mapper.annotation.entity.SelectByIndex;
import net.sunxu.mybatis.automapper.mapper.annotation.entity.UpdateByIndex;
import org.apache.ibatis.annotations.Param;

/**
 * entity with 2 primary keys' s entity mapper method. Primary keys' order is the order of primary keys in entity.
 */
public interface BiPKMapper<T, P1, P2> extends EntityMapper<T> {
    /**
     * select by primary keys
     * @param pk1 primary key 1
     * @param pk2 primary key 2
     * @return entity with the primary key
     */
    @SelectByIndex("")
    T getByPK(@Param("pk1") P1 pk1, @Param("pk2") P2 pk2);

    /**
     * Pessimistic lock (add for update when select by pk)
     * @param pk1 primary key 1
     * @param pk2 primary key 2
     * @return entity with the primary key
     */

    T getByPKForUpdate(@Param("pk1") P1 pk1, @Param("pk2") P2 pk2);

    /**
     * update value to row with the primary keys
     * @param newValue the new value (might change the primary key's value)
     * @param pk1 primary key 1
     * @param pk2 primary key 2
     * @return affected rows count
     */
    @UpdateByIndex("")
    int updateByPK(@Param("value") T newValue, @Param("pk1") P1 pk1, @Param("pk2") P2 pk2);

    /**
     * delete row with primary keys
     * @param pk primary key 1
     * @param pk2 primary key 2
     * @return affected rows count
     */
    @DeleteByIndex("")
    int deleteByPK(@Param("pk1") P1 pk, @Param("pk2") P2 pk2);
}
