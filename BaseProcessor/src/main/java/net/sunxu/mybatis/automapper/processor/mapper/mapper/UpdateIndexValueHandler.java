package net.sunxu.mybatis.automapper.processor.mapper.mapper;

import com.google.inject.Inject;
import net.sunxu.mybatis.automapper.processor.environment.EnvironmentHelper;
import net.sunxu.mybatis.automapper.processor.environment.MessageHelper;
import net.sunxu.mybatis.automapper.processor.mapper.entity.EntityIndex;
import net.sunxu.mybatis.automapper.processor.mapper.entity.EntityModel;
import net.sunxu.mybatis.automapper.processor.property.Parameter;
import net.sunxu.mybatis.automapper.processor.property.method.UpdateIndexValueProperty;
import net.sunxu.mybatis.automapper.processor.property.parameter.EntityValueProperty;

import java.util.List;

import static net.sunxu.mybatis.automapper.processor.util.AutoMapperException.newException;

public class UpdateIndexValueHandler extends EntityMethodHandler<UpdateIndexValueDecorator> {
    @Inject
    private EnvironmentHelper env;
    @Inject
    private MessageHelper messageHelper;

    @Override
    protected void validate(MapperModel mapperModel, MapperMethod mapperMethod, EntityModel entityModel) {
        UpdateIndexValueProperty property = mapperMethod.getMethod().get(UpdateIndexValueProperty.class);
        String restrictIndexName = property.indexNameToRestrict();
        EntityIndex restrictIndex = entityModel.getEntityIndexes().get(restrictIndexName);
        String methodDebugName = String.format("[%s][%s] annotated with @UpdateIndexValue",
                mapperModel.getName(), mapperMethod.getName());
        if (restrictIndex == null) {
            throw newException("%s index [%s] not exist in entity [%s]", restrictIndexName, entityModel.getName());
        }

        String updateIndexName = property.indexNameToUpdate();
        EntityIndex updateIndex = entityModel.getEntityIndexes().get(restrictIndexName);
        if (updateIndex == null) {
            throw newException("%s index [%s] not exist in entity [%s]",
                    methodDebugName, restrictIndexName, entityModel.getName());
        }

        List<Parameter> parameters = mapperMethod.getMethod().getParameters();
        if (parameters.size() < 2) {
            throw newException("%s should have at least 2 parameters", methodDebugName);
        }
        int restrictIndexStartPos = 1;
        if (!parameters.get(0).contains(EntityValueProperty.class)) {
            warnOnIndexTypeNotAssignable(parameters, 0, entityModel, updateIndex, methodDebugName);
            restrictIndexStartPos += updateIndex.getFields().size();
        }
        if (restrictIndexStartPos >= parameters.size()) {
            throw newException("%s parameter count not enough for index [%s]",
                    methodDebugName, restrictIndex.getName());
        }
        if (!parameters.get(restrictIndexStartPos).contains(EntityValueProperty.class)) {
            warnOnIndexTypeNotAssignable(parameters, restrictIndexStartPos, entityModel, updateIndex, methodDebugName);
        }
    }

    private void warnOnIndexTypeNotAssignable(List<Parameter> parameters, int startPos,
                                              EntityModel entityModel, EntityIndex index, String methodDebugName) {
        if (parameters.size() < startPos + index.getFields().size()) {
            throw newException("%s parameter count not enough for index [%s]", methodDebugName, index.getName());
        }
        for (int i = 0; i < index.getFields().size(); i++) {
            String fieldName = index.getFields().get(i).getLabel();
            String javaType = entityModel.getFields().get(fieldName);
            String parameterType = parameters.get(startPos + i).getSimpleType();
            if (!env.isAssignedFrom(parameterType, javaType)) {
                messageHelper.warning("%s parameter type [%s] is not assignable from [%s]",
                        methodDebugName, parameterType, javaType);
            }
        }
    }
}
