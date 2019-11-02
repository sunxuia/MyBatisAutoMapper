package net.sunxu.mybatis.automapper.processor.property.method;

import com.google.inject.Inject;
import net.sunxu.mybatis.automapper.mapper.annotation.addition.MethodDatabaseId;
import net.sunxu.mybatis.automapper.processor.property.PropertyForAnnotation;


@PropertyForAnnotation(MethodDatabaseId.class)
public class MethodDatabaseIdProperty extends AbstractMethodAnnotationProperty {
    @Inject
    private MethodDatabaseId databaseId;

    public String databaseId() {
        return databaseId.value();
    }
}
