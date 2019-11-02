package net.sunxu.mybatis.automapper.processor.property.method;

import com.google.inject.Inject;
import net.sunxu.mybatis.automapper.mapper.annotation.entity.UpdateByIndex;
import net.sunxu.mybatis.automapper.processor.property.PropertyForAnnotation;

@PropertyForAnnotation(UpdateByIndex.class)
public class UpdateByIndexProperty extends AbstractMethodAnnotationProperty {
    @Inject
    private UpdateByIndex index;

    public String indexName() {
        return index.value();
    }
}
