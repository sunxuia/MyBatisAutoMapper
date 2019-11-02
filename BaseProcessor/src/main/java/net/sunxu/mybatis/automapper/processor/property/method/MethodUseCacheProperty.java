package net.sunxu.mybatis.automapper.processor.property.method;

import com.google.inject.Inject;
import net.sunxu.mybatis.automapper.mapper.annotation.addition.MethodUseCache;
import net.sunxu.mybatis.automapper.processor.property.PropertyForAnnotation;


@PropertyForAnnotation(MethodUseCache.class)
public class MethodUseCacheProperty extends AbstractMethodAnnotationProperty {
    @Inject
    private MethodUseCache useCache;

    public boolean useCache() {
        return useCache.value();
    }
}
