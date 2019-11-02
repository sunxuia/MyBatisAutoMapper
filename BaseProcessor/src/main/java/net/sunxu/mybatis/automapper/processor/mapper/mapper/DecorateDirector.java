package net.sunxu.mybatis.automapper.processor.mapper.mapper;

import com.google.inject.ImplementedBy;
import net.sunxu.mybatis.automapper.processor.mapper.MapperElementsCreator;

@ImplementedBy(DecorateDirectorImpl.class)
public interface DecorateDirector {
    MapperElementsCreator decorate(MapperElementsCreator provider, MapperModel mapperModel);
}
