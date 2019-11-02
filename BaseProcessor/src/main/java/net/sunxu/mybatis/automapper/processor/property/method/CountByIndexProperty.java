package net.sunxu.mybatis.automapper.processor.property.method;

import com.google.inject.Inject;
import net.sunxu.mybatis.automapper.mapper.annotation.entity.CountByIndex;
import net.sunxu.mybatis.automapper.processor.property.PropertyForAnnotation;

@PropertyForAnnotation(CountByIndex.class)
public class CountByIndexProperty extends AbstractMethodAnnotationProperty {
    @Inject
    private CountByIndex index;

    public String indexName() {
        return index.value();
    }
}
