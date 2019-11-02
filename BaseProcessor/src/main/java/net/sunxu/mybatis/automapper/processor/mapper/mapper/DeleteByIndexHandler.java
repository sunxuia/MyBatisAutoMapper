package net.sunxu.mybatis.automapper.processor.mapper.mapper;

import com.google.inject.Inject;
import net.sunxu.mybatis.automapper.processor.environment.EnvironmentHelper;
import net.sunxu.mybatis.automapper.processor.environment.MessageHelper;
import net.sunxu.mybatis.automapper.processor.mapper.entity.EntityIndex;
import net.sunxu.mybatis.automapper.processor.mapper.entity.EntityModel;
import net.sunxu.mybatis.automapper.processor.property.Parameter;
import net.sunxu.mybatis.automapper.processor.property.method.DeleteByIndexProperty;
import net.sunxu.mybatis.automapper.processor.property.parameter.EntityValueProperty;

import java.util.List;

import static net.sunxu.mybatis.automapper.processor.util.AutoMapperException.newException;

public class DeleteByIndexHandler extends EntityMethodHandler<DeleteByIndexDecorator> {
    @Inject
    private EnvironmentHelper env;
    @Inject
    private MessageHelper messageHelper;

    @Override
    protected void validate(MapperModel mapperModel, MapperMethod mapperMethod, EntityModel entityModel) {
        DeleteByIndexProperty property = mapperMethod.getMethod().get(DeleteByIndexProperty.class);
        String indexName = property.indexName();
        EntityIndex index = entityModel.getEntityIndexes().get(indexName);
        String methodDebugName = String.format("[%s][%s] annotated with @DeleteByIndex",
                mapperModel.getName(), mapperMethod.getName());
        if (index == null) {
            throw newException("%s index [%s] not exist in entity [%s]",
                    methodDebugName, indexName, entityModel.getName());
        }
        List<Parameter> parameters = mapperMethod.getMethod().getParameters();
        if (parameters.isEmpty()) {
            throw newException("%s should have parameter.", methodDebugName);
        }
        if (parameters.get(0).contains(EntityValueProperty.class)) {
            if (parameters.size() > 1) {
                throw newException("%s should only have one parameter with @EntityValue.", methodDebugName);
            }
            if (env.isAssignedFrom(entityModel.getName(), parameters.get(0).getSimpleType())) {
                messageHelper.warning("%s parameter type [%s] is not assignable from entity [%s]",
                        methodDebugName, parameters.get(0).getSimpleType(), entityModel.getName());
            }
        } else {
            if (index.getFields().size() != parameters.size()) {
                throw newException("%s index [%s] field count [%d] not equal with parameter count [%d]",
                        methodDebugName, indexName, index.getFields().size(), parameters.size());
            }

            for (int i = 0; i < parameters.size(); i++) {
                String fieldName = index.getFields().get(i).getLabel();
                String javaType = entityModel.getFields().get(fieldName);
                String parameterType = parameters.get(i).getSimpleType();
                if (!env.isAssignedFrom(parameterType, javaType)) {
                    messageHelper.warning("%s parameter type [%s] is not assignable from [%s]",
                            methodDebugName, parameterType, javaType);
                }
            }
        }

    }
}
