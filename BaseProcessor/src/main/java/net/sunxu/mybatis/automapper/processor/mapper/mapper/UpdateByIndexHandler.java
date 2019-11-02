package net.sunxu.mybatis.automapper.processor.mapper.mapper;

import com.google.inject.Inject;
import net.sunxu.mybatis.automapper.processor.environment.EnvironmentHelper;
import net.sunxu.mybatis.automapper.processor.environment.MessageHelper;
import net.sunxu.mybatis.automapper.processor.mapper.entity.EntityIndex;
import net.sunxu.mybatis.automapper.processor.mapper.entity.EntityModel;
import net.sunxu.mybatis.automapper.processor.property.Parameter;
import net.sunxu.mybatis.automapper.processor.property.method.UpdateByIndexProperty;
import net.sunxu.mybatis.automapper.processor.property.parameter.EntityValueProperty;

import java.util.List;

import static net.sunxu.mybatis.automapper.processor.util.AutoMapperException.newException;

public class UpdateByIndexHandler extends EntityMethodHandler<UpdateByIndexDecorator> {
    @Inject
    private EnvironmentHelper env;
    @Inject
    private MessageHelper messageHelper;

    @Override
    protected void validate(MapperModel mapperModel, MapperMethod mapperMethod, EntityModel entityModel) {
        UpdateByIndexProperty property = mapperMethod.getMethod().get(UpdateByIndexProperty.class);
        String indexName = property.indexName();
        EntityIndex index = entityModel.getEntityIndexes().get(indexName);
        String methodDebugName = String.format("[%s][%s] annotated with @UpdateByIndex",
                mapperMethod.getName(), mapperMethod.getName());
        if (index == null) {
            throw newException("%s index [%s] not exist in entity [%s]",
                    methodDebugName, indexName, entityModel.getName());
        }
        List<Parameter> parameters = mapperMethod.getMethod().getParameters();
        if (parameters.size() < 2) {
            throw newException("%s parameters should be at least 2 with entity to update and index value",
                    methodDebugName);
        }
        String firstParamType = parameters.get(0).getSimpleType();
        if (!env.isAssignedFrom(firstParamType, entityModel.getName())) {
            throw newException("%s parameter type [%s] to update is not assignable from [%s]",
                    methodDebugName, firstParamType, entityModel.getName());
        }
        if (parameters.get(1).contains(EntityValueProperty.class)) {
            if (parameters.size() > 2) {
                throw newException("%s with second parameter with @EntityValue should only exist 2 parameters",
                        mapperMethod);
            }
        } else {
            int indexFieldSize = mapperMethod.getMethod().getParameters().size() - 1;
            if (index.getFields().size() != indexFieldSize) {
                throw newException("%s field count [%d] not equal with parameter count [%d]",
                        methodDebugName, indexName, index.getFields().size(), indexFieldSize);
            }

            for (int i = 0; i < indexFieldSize; i++) {
                String fieldName = index.getFields().get(i).getLabel();
                String javaType = entityModel.getFields().get(fieldName);
                String parameterType = mapperMethod.getMethod().getParameters().get(i + 1).getSimpleType();
                if (!env.isAssignedFrom(parameterType, javaType)) {
                    messageHelper.warning("[%s parameter type [%s] is not assignable from [%s]",
                            methodDebugName, parameterType, javaType);
                }
            }
        }
    }
}
