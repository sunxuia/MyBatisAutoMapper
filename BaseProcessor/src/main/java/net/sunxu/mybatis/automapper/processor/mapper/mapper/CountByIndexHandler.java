package net.sunxu.mybatis.automapper.processor.mapper.mapper;

import com.google.inject.Inject;
import net.sunxu.mybatis.automapper.processor.environment.EnvironmentHelper;
import net.sunxu.mybatis.automapper.processor.environment.MessageHelper;
import net.sunxu.mybatis.automapper.processor.mapper.entity.EntityIndex;
import net.sunxu.mybatis.automapper.processor.mapper.entity.EntityModel;
import net.sunxu.mybatis.automapper.processor.property.Parameter;
import net.sunxu.mybatis.automapper.processor.property.method.CountByIndexProperty;
import net.sunxu.mybatis.automapper.processor.property.parameter.EntityValueProperty;
import net.sunxu.mybatis.automapper.processor.util.HelpUtils;

import java.util.List;

import static net.sunxu.mybatis.automapper.processor.util.AutoMapperException.newException;

public class CountByIndexHandler extends EntityMethodHandler<CountByIndexDecorator> {
    @Inject
    private EnvironmentHelper env;
    @Inject
    private MessageHelper messageHelper;

    @Override
    protected void validate(MapperModel mapperModel, MapperMethod mapperMethod, EntityModel entityModel) {
        CountByIndexProperty property = mapperMethod.getMethod().get(CountByIndexProperty.class);
        String indexName = property.indexName();
        EntityIndex index = entityModel.getEntityIndexes().get(indexName);
        String methodDebugName = String.format("[%s][%s] annotated with @CountByIndex",
                mapperModel.getName(), mapperMethod.getName());
        if (index == null) {
            throw newException("%s index [%s] not exist in entity [%s]",
                    methodDebugName, indexName, entityModel.getName());
        }
        List<Parameter> parameters = mapperMethod.getMethod().getParameters();
        if (parameters.isEmpty()) {
            throw newException("%s parameters should not be empty.", methodDebugName);
        } else if (parameters.get(0).contains(EntityValueProperty.class)) {
            if (parameters.size() > 1) {
                throw newException("%s and should only exist one parameter with @EntityValue", methodDebugName);
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

        if (!HelpUtils.existIn(mapperMethod.getMethod().getReturnType(),
                "int", "long", "double", "float", "byte", "boolean",
                Integer.class.getCanonicalName(), Long.class.getCanonicalName(), Double.class.getCanonicalName(),
                Float.class.getCanonicalName(), Byte.class.getCanonicalName(), Boolean.class.getCanonicalName())) {
            throw newException("%s return type should be number or boolean value.", methodDebugName);
        }
    }
}
