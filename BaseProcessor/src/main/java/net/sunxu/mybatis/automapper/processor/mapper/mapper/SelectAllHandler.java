package net.sunxu.mybatis.automapper.processor.mapper.mapper;

import com.google.inject.Inject;
import net.sunxu.mybatis.automapper.processor.environment.EnvironmentHelper;
import net.sunxu.mybatis.automapper.processor.environment.MessageHelper;
import net.sunxu.mybatis.automapper.processor.mapper.entity.EntityModel;

import static net.sunxu.mybatis.automapper.processor.util.AutoMapperException.newException;

public class SelectAllHandler extends EntityMethodHandler<SelectAllDecorator> {
    @Inject
    private EnvironmentHelper env;
    @Inject
    private MessageHelper messageHelper;

    @Override
    protected void validate(MapperModel mapperModel, MapperMethod mapperMethod, EntityModel entityModel) {
        if (!mapperMethod.getMethod().getParameters().isEmpty()) {
            throw newException("[%s][%s] annotated with @InsertOne should be empty.",
                    mapperModel.getName(), mapperMethod.getName());
        }

        String returnType = mapperMethod.getMethod().getReturnType();
        if (!env.isAssignedFrom(entityModel.getName(), returnType)) {
            messageHelper.warning("[%s][%s] parameter type [%s] is not assignable to [%s]",
                    mapperModel.getName(), mapperMethod.getName(), returnType, entityModel.getName());
        }
    }
}
