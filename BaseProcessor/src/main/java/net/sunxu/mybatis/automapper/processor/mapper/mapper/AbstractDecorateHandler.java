package net.sunxu.mybatis.automapper.processor.mapper.mapper;

import com.google.inject.Inject;
import net.sunxu.mybatis.automapper.processor.mapper.MapperElementsCreator;
import net.sunxu.mybatis.automapper.processor.property.type.ProcessorRestrictProperty;

import javax.annotation.Nullable;

public abstract class AbstractDecorateHandler<T extends MapperElementsCreator>
        implements HandlerForDecorator {

    @Inject
    @Nullable
    private DecoratorFactory<T> factory;

    private HandlerForDecorator next;

    AbstractDecorateHandler setNext(AbstractDecorateHandler nextChain) {
        this.next = nextChain;
        return nextChain;
    }

    @Override
    public final MapperElementsCreator decorate(MapperElementsCreator provider, MapperModel mapperModel) {
        if (factory != null && canDecorate(provider, mapperModel)) {
            T newProvider = factory.get(mapperModel, provider);
            initialDecorator(newProvider, mapperModel);
            provider = newProvider;
        }
        if (next != null) {
            provider = next.decorate(provider, mapperModel);
        }
        return provider;
    }

    protected abstract boolean canDecorate(MapperElementsCreator provider, MapperModel mapperModel);

    protected abstract void initialDecorator(T decorator, MapperModel mapperModel);

    protected boolean isProcessorAllowed(MapperMethod mapperMethod) {
        return !mapperMethod.getMethod().contains(ProcessorRestrictProperty.class) ||
                mapperMethod.getMethod().get(ProcessorRestrictProperty.class).isAllowed();
    }
}
