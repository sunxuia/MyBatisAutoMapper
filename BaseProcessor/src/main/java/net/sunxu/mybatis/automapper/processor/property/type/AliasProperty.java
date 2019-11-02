package net.sunxu.mybatis.automapper.processor.property.type;

import com.google.inject.Inject;
import net.sunxu.mybatis.automapper.processor.property.PropertyForAnnotation;
import org.apache.ibatis.type.Alias;


@PropertyForAnnotation(Alias.class)
public class AliasProperty extends AbstractTypeAnnotationProperty {
    @Inject
    private Alias alias;

    public String alias() {
        return alias.value();
    }
}
