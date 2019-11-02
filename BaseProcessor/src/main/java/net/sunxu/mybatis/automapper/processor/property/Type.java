package net.sunxu.mybatis.automapper.processor.property;

import com.google.inject.Inject;
import net.sunxu.mybatis.automapper.processor.environment.EnvironmentHelper;

import javax.lang.model.element.TypeElement;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Type extends AbstractElementAdaptor {

    @Inject
    private EnvironmentHelper env;

    private final TypeElement typeElement;

    private final String className;

    @Inject
    public Type(TypeElement typeElement) {
        this.typeElement = typeElement;
        className = typeElement.getQualifiedName().toString();
    }

    public List<String> getPassedGenericArgumentNames(Class<?> clazz) {
        return env.getPassedGenericArgumentNames(typeElement, clazz);
    }

    public List<String> getGenericTypes() {
        return typeElement.getTypeParameters().stream()
                .map(t -> t.asType().toString())
                .collect(Collectors.toList());
    }

    public boolean isAssignedFrom(Class<?> clazz) {
        return env.isAssignedFrom(typeElement, clazz);
    }

    public String getSimpleName() {
        return typeElement.getSimpleName().toString();
    }

    @Override
    public String getName() {
        return className;
    }

    @Override
    public String toString() {
        return getName();
    }

    private Set<Field> fields = Collections.emptySet();

    private Set<Method> methods = Collections.emptySet();

    void setFields(Set<Field> fields) {
        this.fields = Collections.unmodifiableSet(fields);
    }

    void setMethods(Set<Method> methods) {
        this.methods = Collections.unmodifiableSet(methods);
    }

    public Set<Field> getFields() {
        return fields;
    }

    public Set<Method> getMethods() {
        return methods;
    }
}
