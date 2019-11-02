package net.sunxu.mybatis.automapper.processor.mapper.entity;


import net.sunxu.mybatis.automapper.processor.property.Type;

import javax.validation.constraints.NotNull;

import static net.sunxu.mybatis.automapper.processor.util.AutoMapperException.newException;

abstract class AbstractBuildHandler implements BuildDirector {
    private BuildDirector nextHandler;

    AbstractBuildHandler setNextHandler(AbstractBuildHandler handler) {
        this.nextHandler = handler;
        return handler;
    }

    protected EntityModelBuilder builder;

    protected Type type;

    @Override
    public void build(@NotNull EntityModelBuilder builder) {
        this.builder = builder;
        this.type = builder.getType();

        build();

        if (this.builder.isInterrupt()) {
            return;
        }
        if (nextHandler != null) {
            nextHandler.build(this.builder);
        }
    }

    protected abstract void build();

    protected final void validate(boolean isSuccess, String failMessage, Object... paras) {
        if (!isSuccess) {
            failMessage = "Entity [" + builder.getName() + "] validate failed : " + failMessage + ".";
            throw newException(failMessage, paras);
        }
    }
}
