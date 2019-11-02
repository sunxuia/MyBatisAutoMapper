package net.sunxu.mybatis.automapper.processor.mapper.mapper;

import com.google.inject.Inject;
import net.sunxu.mybatis.automapper.processor.environment.EnvironmentHelper;
import net.sunxu.mybatis.automapper.processor.environment.MessageHelper;
import net.sunxu.mybatis.automapper.processor.mapper.entity.EntityModel;

import static net.sunxu.mybatis.automapper.processor.util.AutoMapperException.newException;

public class InsertOneHandler extends EntityMethodHandler<InsertOneDecorator> {
    @Inject
    private EnvironmentHelper env;
    @Inject
    private MessageHelper messageHelper;

    @Override
    protected void validate(MapperModel mapperModel, MapperMethod mapperMethod, EntityModel entityModel) {
        if (mapperMethod.getMethod().getParameters().size() != 1) {
            throw newException("[%s][%s] annotated with @InsertOne should have one parameter",
                    mapperModel.getName(), mapperMethod.getName());
        }

        String parameterType = mapperMethod.getMethod().getParameters().get(0).getSimpleType();
        if (!env.isAssignedFrom(parameterType, entityModel.getName())) {
            messageHelper.warning("[%s][%s] parameter type [%s] is not assignable from [%s]",
                    mapperModel.getName(), mapperMethod.getName(), parameterType, entityModel.getName());
        }
    }
}
