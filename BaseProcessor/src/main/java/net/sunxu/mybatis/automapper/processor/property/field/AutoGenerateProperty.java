package net.sunxu.mybatis.automapper.processor.property.field;

import net.sunxu.mybatis.automapper.entity.annotation.*;
import net.sunxu.mybatis.automapper.entity.annotation.AutoGenerate.Kind;
import net.sunxu.mybatis.automapper.processor.property.PropertyForAnnotation;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;


@PropertyForAnnotation({AutoGenerate.class, AutoGenerates.class})
public class AutoGenerateProperty extends AbstractFieldAnnotationProperty {
    private Map<Kind, String> autoGenerates;

    @Override
    protected void initial() {
        boolean isComplex = getAnnotations(Composite.class).length > 0 || getAnnotation(Reference.class) != null;
        validate(isComplex || getAnnotation(Column.class) != null,
                "@AutoGenerate should have @Column or @Composite or @Reference annotations");

        autoGenerates = new HashMap<>();
        for (AutoGenerate anno : getAnnotations(AutoGenerate.class)) {
            Kind type = anno.kind();
            validate(!autoGenerates.containsKey(type), "duplicated @AutoGenerate Type [%s]", type);
            autoGenerates.putIfAbsent(type, anno.value());
        }

        validate(!(containsType(Kind.INSERT) || containsType(Kind.UPDATE)) || !isComplex,
                "field with @Composite or @Reference cannot be annotated with" +
                        " @AutoGenerate with kind INSERT or UPDATE");
    }

    public Set<Kind> getSupportTypes() {
        return autoGenerates.keySet();
    }

    public boolean containsType(Kind type) {
        return autoGenerates.containsKey(type);
    }

    public String getExpression(Kind type) {
        return autoGenerates.get(type);
    }
}
