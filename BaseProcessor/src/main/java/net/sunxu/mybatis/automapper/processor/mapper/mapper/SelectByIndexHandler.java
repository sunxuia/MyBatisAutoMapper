package net.sunxu.mybatis.automapper.processor.mapper.mapper;

import com.google.inject.Inject;
import net.sunxu.mybatis.automapper.processor.environment.EnvironmentHelper;
import net.sunxu.mybatis.automapper.processor.environment.MessageHelper;
import net.sunxu.mybatis.automapper.processor.mapper.entity.EntityIndex;
import net.sunxu.mybatis.automapper.processor.mapper.entity.EntityModel;
import net.sunxu.mybatis.automapper.processor.property.Parameter;
import net.sunxu.mybatis.automapper.processor.property.method.SelectByIndexProperty;
import net.sunxu.mybatis.automapper.processor.property.parameter.EntityValueProperty;

import java.util.List;

import static net.sunxu.mybatis.automapper.processor.util.AutoMapperException.newException;

public class SelectByIndexHandler extends EntityMethodHandler<SelectByIndexDecorator> {
    @Inject
    private EnvironmentHelper env;
    @Inject
    private MessageHelper messageHelper;

    @Override
    protected void validate(MapperModel mapperModel, MapperMethod mapperMethod, EntityModel entityModel) {
        SelectByIndexProperty property = mapperMethod.getMethod().get(SelectByIndexProperty.class);
        String indexName = property.indexName();
        EntityIndex index = entityModel.getEntityIndexes().get(indexName);
        String methodDebugName = String.format("[%s][%s] anntated with @SelectByIndex",
                mapperModel.getName(), mapperMethod.getName());
        if (index == null) {
            throw newException("%s index [%s] not exist in entity [%s]",
                    methodDebugName, indexName, entityModel.getName());
        }
        List<Parameter> parameters = mapperMethod.getMethod().getParameters();
        int length = parameters.size();
        if (length == 0) {
            throw newException("%s parameters should not be empty");
        } else if (parameters.get(0).contains(EntityValueProperty.class)) {
            if (length != 1) {
                throw newException("%s parameter with @EntityValue should have only one parameter.");
            }
        } else {
            if (index.getFields().size() != length) {
                throw newException(
                        "%s index [%s] field count [%d] not equal with parameter count [%d]",
                        methodDebugName, indexName, index.getFields().size(), length);
            }
            for (int i = 0; i < length; i++) {
                String fieldName = index.getFields().get(i).getLabel();
                String javaType = entityModel.getFields().get(fieldName);
                String parameterType = mapperMethod.getMethod().getParameters().get(i).getSimpleType();
                if (!env.isAssignedFrom(parameterType, javaType)) {
                    messageHelper.warning(
                            "%s parameter type [%s] is not assignable from [%s]",
                            methodDebugName, parameterType, javaType);
                }
            }
        }

        String returnType = mapperMethod.getMethod().getReturnType();
        if (!env.isAssignedFrom(entityModel.getName(), returnType)) {
            messageHelper.warning("[%s][%s] annotated with SelectByIndex return type [%s] is not aequal" +
                            " or super class of entity [%s].",
                    mapperModel.getName(), mapperMethod.getName(), returnType, entityModel.getName());
        }
    }
}
