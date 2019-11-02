package net.sunxu.mybatis.automapper.processor.mapper.mapper;

import net.sunxu.mybatis.automapper.processor.mapper.MapperElementsCreator;

interface HandlerForDecorator {
    MapperElementsCreator decorate(MapperElementsCreator provider, MapperModel mapperModel);
}
