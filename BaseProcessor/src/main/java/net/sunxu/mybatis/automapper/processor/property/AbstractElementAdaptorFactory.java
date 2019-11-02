package net.sunxu.mybatis.automapper.processor.property;

import com.google.inject.*;
import com.google.inject.util.Providers;
import net.sunxu.mybatis.automapper.processor.environment.EnvironmentModule;
import net.sunxu.mybatis.automapper.processor.environment.GenericHelper.GenericTypes;
import net.sunxu.mybatis.automapper.processor.environment.SystemHelper;

import javax.lang.model.element.Element;
import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toSet;
import static net.sunxu.mybatis.automapper.processor.util.AutoMapperException.newException;
import static net.sunxu.mybatis.automapper.processor.util.HelpUtils.forEach;
import static net.sunxu.mybatis.automapper.processor.util.HelpUtils.getGenericArgumentPassedToSuperClass;


public abstract class AbstractElementAdaptorFactory<E extends Element, A extends AbstractElementAdaptor> {
    private final Set<Class<? extends AbstractAnnotationProperty>> propertyClasses;
    private final Set<Class<? extends Annotation>> annotations;
    private final Type elementType;
    private final Type elementAdaptorType;

    public AbstractElementAdaptorFactory(SystemHelper sys, String packageName) {
        propertyClasses = sys.getClassesInPackage(packageName).stream()
                .filter(c -> !Modifier.isAbstract(c.getModifiers()) &&
                        AbstractAnnotationProperty.class.isAssignableFrom(c) &&
                        c.getAnnotation(PropertyForAnnotation.class) != null)
                .map(c -> (Class<? extends AbstractAnnotationProperty>) c)
                .collect(toSet());

        annotations = propertyClasses.stream()
                .flatMap(a -> Stream.of(a.getAnnotation(PropertyForAnnotation.class).value()))
                .collect(toSet());


        List<Type> passedTypes = getGenericArgumentPassedToSuperClass(getClass(), AbstractElementAdaptorFactory.class);
        elementType = passedTypes.get(0);
        elementAdaptorType = passedTypes.get(1);
    }

    @Inject
    private EnvironmentModule.EnvironmentInjector injector;

    public A get(E element,
                 GenericTypes genericTypes,
                 Map<Class<? extends AbstractElementAdaptor>, AbstractElementAdaptor> relatedAdaptors) {
        Injector childInjector = injector.createChildInjector(
                getStandardModule(element, relatedAdaptors),
                getAdditionModule(element, genericTypes));

        A elementAdaptor = (A) childInjector.getInstance(Key.get(elementAdaptorType));
        elementAdaptor = decorateElementAdaptor(elementAdaptor, element, genericTypes, relatedAdaptors);

        Set<AbstractAnnotationProperty> annotationPropertySet = new HashSet<>();
        for (Class<? extends AbstractAnnotationProperty> propertyClass : propertyClasses) {
            PropertyForAnnotation propertyFor = propertyClass.getAnnotation(PropertyForAnnotation.class);
            if (isMatch(element, propertyFor)) {
                AbstractAnnotationProperty property;
                try {
                    property = childInjector.getInstance(propertyClass);
                    property.initial();
                } catch (Exception err) {
                    err.printStackTrace();
                    String failMessage = decorateFailureMessage(
                            elementAdaptor,
                            propertyClass.getCanonicalName(),
                            err.getMessage());
                    throw newException(failMessage);
                }
                annotationPropertySet.add(property);
            }
        }
        elementAdaptor.setProperties(annotationPropertySet);
        setOtherAdaptors(elementAdaptor, element, genericTypes);
        return elementAdaptor;
    }

    protected A decorateElementAdaptor(
            A adaptor,
            E element,
            GenericTypes genericTypes,
            Map<Class<? extends AbstractElementAdaptor>, AbstractElementAdaptor> relatedProperties) {
        return adaptor;
    }

    @SuppressWarnings("unchecked")
    protected Module getStandardModule(
            E element,
            Map<Class<? extends AbstractElementAdaptor>, AbstractElementAdaptor> relatedProperties) {
        return new AbstractModule() {
            @Override
            protected void configure() {
                bind((TypeLiteral) TypeLiteral.get(elementType)).toProvider(Providers.of(element));
                bind(AbstractElementAdaptor.class)
                        .to((TypeLiteral) TypeLiteral.get(elementAdaptorType))
                        .in(Singleton.class);
                for (Class<? extends Annotation> annotation : annotations) {
                    bind((Class) annotation).toProvider(Providers.of(element.getAnnotation(annotation)));
                }
                forEach(relatedProperties, (clazz, value) -> {
                    bind((Class) clazz).toProvider(Providers.of(value));
                });
            }
        };
    }

    protected abstract Module getAdditionModule(E element, GenericTypes genericTypes);

    protected abstract String decorateFailureMessage(A elementAdaptor,
                                                     String propertyClass,
                                                     String failMessage);

    private final boolean isMatch(Element target, PropertyForAnnotation propertyFor) {
        if (propertyFor.alwaysCreate()) return true;

        boolean hasOne = false;
        for (Class<? extends Annotation> anno : propertyFor.value()) {
            boolean hasAnno = target.getAnnotation(anno) != null;
            hasOne = hasOne || hasAnno;
            if (hasAnno && propertyFor.option() == PropertyForAnnotation.Option.OR)
                return true;
            if (!hasAnno && propertyFor.option() == PropertyForAnnotation.Option.AND)
                return false;
        }
        return hasOne;
    }

    protected abstract void setOtherAdaptors(A adaptor, E element, GenericTypes genericTypes);
}
