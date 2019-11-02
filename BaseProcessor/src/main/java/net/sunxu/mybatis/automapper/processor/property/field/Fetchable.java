package net.sunxu.mybatis.automapper.processor.property.field;

import org.apache.ibatis.mapping.FetchType;

import static com.google.common.base.Strings.isNullOrEmpty;


public interface Fetchable {
    FetchType fetchType();

    default boolean hasFetchType() {
        return fetchType() != FetchType.DEFAULT;
    }

    String byMapper();

    default boolean hasByMapper() {
        return !isNullOrEmpty(byMapper());
    }

    String referEntity();

    String referIndex();

}
