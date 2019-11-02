package net.sunxu.mybatis.automapper.processor.mapper.mapper;

import net.sunxu.mybatis.automapper.processor.property.Type;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

public class MapperModel {
    private final Type type;
    private final Map<String, MapperMethod> mapperMethods;

    protected MapperModel() {
        type = null;
        mapperMethods = Collections.emptyMap();
    }

    public MapperModel(Type type) {
        this.type = type;
        mapperMethods = type.getMethods().stream()
                .collect(Collectors.toMap(m -> m.getSignature(), m -> new MapperMethod(m)));
    }

    public Type getType() {
        return type;
    }

    public String getName() {
        return type.getName();
    }

    public boolean isAnnoymousMapper() {
        return false;
    }

    public Collection<MapperMethod> getMapperMethods() {
        return mapperMethods.values();
    }
}
