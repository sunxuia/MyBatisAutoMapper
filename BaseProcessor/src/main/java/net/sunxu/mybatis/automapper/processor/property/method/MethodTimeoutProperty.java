package net.sunxu.mybatis.automapper.processor.property.method;

import com.google.inject.Inject;
import net.sunxu.mybatis.automapper.mapper.annotation.addition.MethodTimeout;
import net.sunxu.mybatis.automapper.processor.property.PropertyForAnnotation;


@PropertyForAnnotation(MethodTimeout.class)
public class MethodTimeoutProperty extends AbstractMethodAnnotationProperty {
    @Inject
    private MethodTimeout timeout;

    public int timeout() {
        return timeout.value();
    }
}
