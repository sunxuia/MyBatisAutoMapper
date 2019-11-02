package net.sunxu.mybatis.automapper.processor.property.method;

import com.google.inject.Inject;
import net.sunxu.mybatis.automapper.mapper.annotation.addition.MethodFetchSize;
import net.sunxu.mybatis.automapper.processor.property.PropertyForAnnotation;

@PropertyForAnnotation(MethodFetchSize.class)
public class MethodFetchSizeProperty extends AbstractMethodAnnotationProperty {
    @Inject
    private MethodFetchSize size;

    public int size() {
        return size.value();
    }
}
