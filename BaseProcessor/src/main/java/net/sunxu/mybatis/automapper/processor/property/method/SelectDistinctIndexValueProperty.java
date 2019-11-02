package net.sunxu.mybatis.automapper.processor.property.method;

import com.google.inject.Inject;
import net.sunxu.mybatis.automapper.mapper.annotation.entity.SelectDistinctIndexValue;
import net.sunxu.mybatis.automapper.processor.property.PropertyForAnnotation;

@PropertyForAnnotation(SelectDistinctIndexValue.class)
public class SelectDistinctIndexValueProperty extends AbstractMethodAnnotationProperty {
    @Inject
    private SelectDistinctIndexValue index;

    public String indexName() {
        return index.value();
    }
}
