package net.sunxu.mybatis.automapper.processor.mapper.mapper;

import net.sunxu.mybatis.automapper.processor.mapper.MapperElementsCreator;

public interface DecoratorFactory<T extends MapperElementsCreator> {
    T get(MapperModel mapperModel, MapperElementsCreator decorated);
}
