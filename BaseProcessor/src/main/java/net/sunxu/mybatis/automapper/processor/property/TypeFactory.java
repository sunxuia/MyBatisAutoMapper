package net.sunxu.mybatis.automapper.processor.property;

import com.google.inject.Inject;
import com.google.inject.Module;
import net.sunxu.mybatis.automapper.processor.environment.GenericHelper;
import net.sunxu.mybatis.automapper.processor.environment.GenericHelper.GenericTypes;
import net.sunxu.mybatis.automapper.processor.environment.SystemHelper;

import javax.lang.model.element.*;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TypeFactory extends AbstractElementAdaptorFactory<TypeElement, Type> {
    public static final String PACKAGE_NAME = "net.sunxu.mybatis.automapper.processor.property.type";

    @Inject
    public TypeFactory(SystemHelper sys) {
        super(sys, PACKAGE_NAME);
    }

    @Override
    protected Module getAdditionModule(TypeElement element, GenericTypes genericTypes) {
        return binder -> {};
    }

    @Override
    protected String decorateFailureMessage(Type elementAdaptor, String propertyClass, String failMessage) {
        return String.format("[%s] error for [%s] : %s.", propertyClass, elementAdaptor, failMessage);
    }

    @Inject
    private GenericHelper genericHelper;
    @Inject
    private FieldFactory fieldFactory;
    @Inject
    private MethodFactory methodFactory;

    @Override
    protected void setOtherAdaptors(Type adaptor, TypeElement element, GenericTypes genericTypes) {
        Map<Class<? extends AbstractElementAdaptor>, AbstractElementAdaptor> relatedProperties = new HashMap<>();
        relatedProperties.put(Type.class, adaptor);

        if (element.getKind() == ElementKind.CLASS) {
            traversalClass(adaptor, element, genericTypes, relatedProperties);
        } else if (element.getKind() == ElementKind.INTERFACE) {
            traversalInterface(adaptor, element, genericTypes, relatedProperties);
        }
    }

    private void traversalClass(
            Type type,
            TypeElement typeElement,
            GenericTypes genericTypes,
            Map<Class<? extends AbstractElementAdaptor>, AbstractElementAdaptor> relatedAdaptors) {
        Set<Field> fields = new HashSet<>();
        Set<Method> methods = new HashSet<>();

        Filter<VariableElement> fieldFilter = fieldFilterPrototype.clone();
        Filter<ExecutableElement> methodFilter = methodFilterPrototype.clone();
        fieldFilter.genericTypes = methodFilter.genericTypes = genericTypes;
        fieldFilter.type = methodFilter.type = type;
        fieldFilter.relatedAdaptors = methodFilter.relatedAdaptors = relatedAdaptors;

        genericHelper.traversalClassAllElements(
                genericTypes,
                typeElement,
                (e, g) -> e.getKind() == ElementKind.FIELD && fieldFilter.test((VariableElement) e, genericTypes) ||
                        e.getKind() == ElementKind.METHOD && methodFilter.test((ExecutableElement) e, genericTypes),
                (e, g) -> {
                    if (e.getKind() == ElementKind.FIELD) {
                        Field field = fieldFactory.get((VariableElement) e, g, relatedAdaptors);
                        fields.add(field);
                    } else if (e.getKind() == ElementKind.METHOD) {
                        Method method =
                                methodFactory.get((ExecutableElement) e, g, relatedAdaptors);
                        methods.add(method);
                    }
                    return true;
                });
        type.setFields(fields);
        type.setMethods(methods);
    }

    private void traversalInterface(
            Type type,
            TypeElement typeElement,
            GenericTypes genericTypes,
            Map<Class<? extends AbstractElementAdaptor>, AbstractElementAdaptor> relatedAdaptors) {
        Set<Method> methodSet = new HashSet<>();
        Filter<ExecutableElement> methodFilter = methodFilterPrototype.clone();
        methodFilter.genericTypes = genericTypes;
        methodFilter.type = type;
        methodFilter.relatedAdaptors = relatedAdaptors;
        genericHelper.traversalInterfaceAllElements(genericTypes,
                typeElement,
                (e, g) -> e.getKind() == ElementKind.METHOD && methodFilter.test((ExecutableElement) e, genericTypes),
                (e, g) -> {
                    Method methodProperty = methodFactory.get((ExecutableElement) e, g, relatedAdaptors);
                    methodSet.add(methodProperty);
                    return true;
                });
        type.setMethods(methodSet);
    }

    private Filter<VariableElement> fieldFilterPrototype = new AlwaysFailFilter<>();

    private Filter<ExecutableElement> methodFilterPrototype = new AlwaysFailFilter<>();

    public void setFieldFilterPrototype(@NotNull Filter<VariableElement> fieldFilterPrototype) {
        this.fieldFilterPrototype = fieldFilterPrototype;
    }

    public void setMethodFilterPrototype(@NotNull Filter<ExecutableElement> methodFilterPrototype) {
        this.methodFilterPrototype = methodFilterPrototype;
    }

    public static abstract class Filter<T extends Element> implements Cloneable {
        protected Type type;

        protected GenericTypes genericTypes;

        protected Map<Class<? extends AbstractElementAdaptor>, AbstractElementAdaptor> relatedAdaptors;

        public abstract boolean test(T element, GenericTypes genericTypes);

        protected abstract Filter<T> clone();
    }

    private static class AlwaysFailFilter<T extends Element> extends Filter<T> {

        @Override
        public boolean test(T element, GenericTypes genericTypes) {
            return false;
        }

        @Override
        protected Filter<T> clone() {
            return this;
        }
    }
}
