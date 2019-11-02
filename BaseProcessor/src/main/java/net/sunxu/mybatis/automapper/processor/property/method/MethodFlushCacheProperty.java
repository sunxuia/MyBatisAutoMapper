package net.sunxu.mybatis.automapper.processor.property.method;

import com.google.inject.Inject;
import net.sunxu.mybatis.automapper.mapper.annotation.addition.MethodFlushCache;
import net.sunxu.mybatis.automapper.processor.property.PropertyForAnnotation;


@PropertyForAnnotation(MethodFlushCache.class)
public class MethodFlushCacheProperty extends AbstractMethodAnnotationProperty {
    @Inject
    private MethodFlushCache flushCache;

    public boolean flushCache() {
        return flushCache.value();
    }
}
