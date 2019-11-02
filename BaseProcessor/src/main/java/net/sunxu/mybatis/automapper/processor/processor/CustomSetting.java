package net.sunxu.mybatis.automapper.processor.processor;

import net.sunxu.mybatis.automapper.processor.environment.Configuration;
import net.sunxu.mybatis.automapper.processor.mapper.MapperElementsCreator;

import javax.validation.constraints.NotNull;
import java.util.Set;


public interface CustomSetting {
    @NotNull Class<? extends Configuration> getConfiguration();

    @NotNull Set<Class<? extends MapperElementsCreator>> getMapperBuilder();

    @NotNull String getProcessorName();
}
