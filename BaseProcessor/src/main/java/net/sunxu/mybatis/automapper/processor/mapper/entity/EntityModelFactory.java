package net.sunxu.mybatis.automapper.processor.mapper.entity;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

@Singleton
public class EntityModelFactory {
    private Map<String, EntityModel> entities = new HashMap<>();

    public Map<String, EntityModel> getEntities() {
        return entities;
    }

    public EntityModel getEntityModel(String entityName) {
        entityName = entityName.replaceAll("\\$", ".");
        if (!entities.containsKey(entityName)) {
            updateEntityModel(entityName);
        }
        return entities.get(entityName);
    }

    private void updateEntityModel(String requiredEntityName) {
        Map<String, EntityModelBuilder> buildersInQueue = new HashMap<>();
        EntityModelBuilder builder = getBuilderNotInQueue(buildersInQueue, requiredEntityName);
        for (Queue<EntityModelBuilder> builderQueue = new LinkedList<>();
             builder != null;
             builder = builderQueue.poll()) {
            builder.setInterrupt(false);

            BuildDirector director = getBuildDirector();
            director.build(builder);

            for (String relatedEntityName : builder.getRelatedEntityNames()) {
                EntityModelBuilder relatedBuilder = getBuilderNotInQueue(buildersInQueue, relatedEntityName);
                if (relatedBuilder != null) {
                    builderQueue.offer(relatedBuilder);
                    buildersInQueue.put(relatedEntityName, relatedBuilder);
                }
            }
            builder.getRelatedEntityNames().clear();
            if (builder.isInterrupt()) {
                builderQueue.add(builder);
            } else {
                EntityModel entityModel = builder.retrieveEntityModel();
                entities.put(entityModel.getName(), entityModel);
                buildersInQueue.remove(builder.getName());
            }
        }
    }

    private EntityModelBuilder getBuilderNotInQueue(Map<String, EntityModelBuilder> builders, String entityName) {
        if (builders.containsKey(entityName) || entities.containsKey(entityName)) {
            return null;
        }
        EntityModelBuilder builder = new EntityModelBuilder(entityName, entities);
        builders.put(entityName, builder);
        return builder;
    }

    private boolean initialized;

    private BuildDirector getBuildDirector() {
        if (!initialized) {
            initialChainOfResponsibilityForBuild();
            initialized = true;
        }
        return typeHandler;
    }

    @Inject
    private TypeHandler typeHandler;
    @Inject
    private EntityHandler schemaHandler;
    @Inject
    private IndexFieldsHandler indexFieldsHandler;
    @Inject
    private ColumnHandler columnHandler;
    @Inject
    private CompositeHandler compositeHandler;
    @Inject
    private ReferenceHandler referenceHandler;
    @Inject
    private EntityIndexHandler entityIndexHandler;
    @Inject
    private CascadeHandler cascadeHandler;
    @Inject
    private EntityAutoGenerateHandler entityAutoGenerateHandler;

    private void initialChainOfResponsibilityForBuild() {
        typeHandler
                .setNextHandler(schemaHandler)
                .setNextHandler(indexFieldsHandler)
                .setNextHandler(columnHandler)
                .setNextHandler(compositeHandler)
                .setNextHandler(referenceHandler)
                .setNextHandler(entityIndexHandler)
                .setNextHandler(cascadeHandler)
                .setNextHandler(entityAutoGenerateHandler)
        ;
    }


}
