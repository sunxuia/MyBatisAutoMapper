package net.sunxu.mybatis.automapper.processor.property.method;

import com.google.inject.Inject;
import net.sunxu.mybatis.automapper.mapper.annotation.entity.UpdateIndexValue;
import net.sunxu.mybatis.automapper.processor.property.PropertyForAnnotation;

@PropertyForAnnotation(UpdateIndexValue.class)
public class UpdateIndexValueProperty extends AbstractMethodAnnotationProperty {
    @Inject
    private UpdateIndexValue index;

    public String indexNameToUpdate() {
        return index.indexNameToUpdate();
    }

    public String indexNameToRestrict() {
        return index.indexNameToRestrict();
    }
}
