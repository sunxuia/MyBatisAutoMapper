package net.sunxu.mybatis.automapper.processor.mapper.mapper;

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import net.sunxu.mybatis.automapper.mapper.EntityMapper;
import net.sunxu.mybatis.automapper.processor.mapper.MapperElementsCreator;
import net.sunxu.mybatis.automapper.processor.mapper.entity.EntityModel;
import net.sunxu.mybatis.automapper.processor.mapper.entity.EntityModelFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static net.sunxu.mybatis.automapper.processor.util.AutoMapperException.newException;

public class EntityMapperHandler extends AbstractDecorateHandler<EntityMapperDecorator> {

    @Inject
    protected EntityModelFactory entityModelFactory;

    private Set<String> entitiesInMapper = new HashSet<>();

    List<EntityMapperDecorator> providers = new ArrayList<>();

    @Override
    protected boolean canDecorate(MapperElementsCreator provider, MapperModel mapperModel) {
        return mapperModel.isAnnoymousMapper() || mapperModel.getType().isAssignedFrom(EntityMapper.class);
    }

    @Override
    protected void initialDecorator(EntityMapperDecorator decorator, MapperModel mapperModel) {
        if (!mapperModel.isAnnoymousMapper()) {
            initialForEntityMapper(decorator, mapperModel);
            providers.add(decorator);
        } else {
            initialForAnnoymousMapper(decorator, mapperModel);
            for (EntityMapperDecorator providerInMapper : providers) {
                providerInMapper.setAnnoymousMapperName(decorator.getName());
            }
        }
    }

    private void initialForEntityMapper(EntityMapperDecorator decorator, MapperModel mapperModel) {
        String entityName = getEntityName(mapperModel);
        EntityModel entityModel = entityModelFactory.getEntityModel(entityName);

        decorator.setEntityModels(ImmutableList.of(entityModel));
        decorator.setEntitiesInMapper(entitiesInMapper);
        decorator.setEntities(entityModelFactory.getEntities());
        entitiesInMapper.add(entityName);
    }

    private String getEntityName(MapperModel mapperModel) {
        List<String> entityNames = mapperModel.getType().getPassedGenericArgumentNames(EntityMapper.class);
        if (entityNames.isEmpty()) {
            throw newException("EntityMapper [%s] has no entity name.", mapperModel.getName());
        }
        return entityNames.get(0);
    }

    private void initialForAnnoymousMapper(EntityMapperDecorator decorator, MapperModel mapperModel) {
        List<EntityModel> entitiesNotInMapper = entityModelFactory.getEntities()
                .values().stream()
                .filter(e -> !entitiesInMapper.contains(e.getName()))
                .collect(Collectors.toList());

        decorator.setEntityModels(entitiesNotInMapper);
        decorator.setEntitiesInMapper(entitiesInMapper);
        decorator.setEntities(entityModelFactory.getEntities());
    }
}
