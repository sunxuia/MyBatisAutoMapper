package net.sunxu.mybatis.automapper.processor.mapper.mapper;

import com.google.inject.Inject;
import net.sunxu.mybatis.automapper.mapper.EntityMapper;
import net.sunxu.mybatis.automapper.processor.mapper.MapperElementsCreator;
import net.sunxu.mybatis.automapper.processor.mapper.entity.EntityModel;
import net.sunxu.mybatis.automapper.processor.mapper.entity.EntityModelFactory;
import net.sunxu.mybatis.automapper.processor.util.HelpUtils;

import java.util.ArrayList;
import java.util.List;

import static net.sunxu.mybatis.automapper.processor.util.AutoMapperException.newException;

public abstract class EntityMethodHandler<T extends EntityMethodDecorator> extends AbstractDecorateHandler<T> {
    private final Class propertyClass;

    public EntityMethodHandler() {
        Class decoratorClass =
                (Class) HelpUtils.getGenericArgumentPassedToSuperClass(getClass(), EntityMethodHandler.class)
                        .get(0);
        propertyClass =
                (Class) HelpUtils.getGenericArgumentPassedToSuperClass(decoratorClass, EntityMethodDecorator.class)
                        .get(0);
    }

    @Override
    protected boolean canDecorate(MapperElementsCreator provider, MapperModel mapperModel) {
        if (mapperModel.getType().isAssignedFrom(EntityMapper.class)) {
            for (MapperMethod mapperMethod : mapperModel.getMapperMethods()) {
                if (mapperMethod.getMethod().contains(propertyClass) &&
                        isProcessorAllowed(mapperMethod)) {
                    return true;
                }
            }
        }
        return false;
    }


    @Override
    protected void initialDecorator(T decorator, MapperModel mapperModel) {
        EntityModel entityModel = getEntityModelInEntityMapper(mapperModel);
        decorator.setEntityModel(entityModel);
        List<MapperMethod> mapperMethods = new ArrayList<>();
        for (MapperMethod mapperMethod : mapperModel.getMapperMethods()) {
            if (mapperMethod.getMethod().contains(propertyClass)) {
                validate(mapperModel, mapperMethod, entityModel);
                mapperMethods.add(mapperMethod);
            }
            decorator.setMapperMethods(mapperMethods);
        }
    }

    @Inject
    private EntityModelFactory entityModelFactory;

    private EntityModel getEntityModelInEntityMapper(MapperModel mapperModel) {
        List<String> entityNames = mapperModel.getType().getPassedGenericArgumentNames(EntityMapper.class);
        if (entityNames.isEmpty()) {
            throw newException("EntityMapper [%s] has no entity.", mapperModel.getName());
        }
        String entityName = entityNames.get(0);
        return entityModelFactory.getEntityModel(entityName);
    }

    protected abstract void validate(MapperModel mapperModel, MapperMethod mapperMethod, EntityModel entityModel);
}
