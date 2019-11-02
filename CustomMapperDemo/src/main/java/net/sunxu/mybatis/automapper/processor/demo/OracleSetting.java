package net.sunxu.mybatis.automapper.processor.demo;

import com.google.common.collect.ImmutableSet;
import net.sunxu.mybatis.automapper.processor.environment.Configuration;
import net.sunxu.mybatis.automapper.processor.mapper.MapperElementsCreator;
import net.sunxu.mybatis.automapper.processor.processor.CustomSetting;

import java.util.Set;

public class OracleSetting implements CustomSetting {
    @Override
    public Class<? extends Configuration> getConfiguration() {
        return OracleConfiguration.class;
    }

    @Override
    public Set<Class<? extends MapperElementsCreator>> getMapperBuilder() {
        return ImmutableSet.of(OracleSelectByIndexDecorator.class);
    }

    @Override
    public String getProcessorName() {
        return "oracle";
    }
}
