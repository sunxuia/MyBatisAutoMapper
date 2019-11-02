package net.sunxu.mybatis.automapper.mapper;

import net.sunxu.mybatis.automapper.mapper.annotation.entity.InsertOne;
import net.sunxu.mybatis.automapper.mapper.annotation.entity.SelectAll;
import net.sunxu.mybatis.automapper.mapper.annotation.entity.DeleteByIndex;
import net.sunxu.mybatis.automapper.mapper.annotation.entity.UpdateByIndex;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * select, insert, update, delete related method for entity
 */
public interface CRUDMapper<T> extends EntityMapper<T> {
    /**
     * select all values
     *
     * @return list of entity
     */
    @SelectAll
    List<T> listAll();

    /**
     * insert a new value to daabse
     *
     * @param val new entity value
     * @return affected rows count
     */
    @InsertOne
    int insert(@Param("val") T val);

    /**
     * update value, if the entity does not have a primary key, it'll not generate sql.
     *
     * @param val value to be udpated
     * @return affected rows count
     */
    @UpdateByIndex("")
    int update(@Param("val") T val);

    /**
     * delete value.
     * If the entity has primary key(s), it will delete the value with same primary keys.
     * If the tntiy does not have primary keys(s), it will delete the value only if all column match.
     *
     * @param val value to be deleted
     * @return affected rows count
     */
    @DeleteByIndex("")
    int delete(@Param("val") T val);
}
