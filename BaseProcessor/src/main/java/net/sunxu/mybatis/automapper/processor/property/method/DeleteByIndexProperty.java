package net.sunxu.mybatis.automapper.processor.property.method;

import com.google.inject.Inject;
import net.sunxu.mybatis.automapper.mapper.annotation.entity.DeleteByIndex;
import net.sunxu.mybatis.automapper.processor.property.PropertyForAnnotation;

@PropertyForAnnotation(DeleteByIndex.class)
public class DeleteByIndexProperty extends AbstractMethodAnnotationProperty {
    @Inject
    private DeleteByIndex index;

    public String indexName() {
        return index.value();
    }
}
