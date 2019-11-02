package net.sunxu.mybatis.automapper.processor.property;

import com.google.inject.Binder;
import com.google.inject.Inject;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;
import com.google.inject.util.Providers;
import net.sunxu.mybatis.automapper.processor.environment.EnvironmentHelper;
import net.sunxu.mybatis.automapper.processor.environment.EnvironmentModule;
import net.sunxu.mybatis.automapper.processor.testutil.TestEnvRunner;
import org.junit.runner.RunWith;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import static net.sunxu.mybatis.automapper.processor.testutil.TestHelpUtils.getModule;
import static net.sunxu.mybatis.automapper.processor.testutil.TestHelpUtils.getPassedTargetClass;

@RunWith(TestEnvRunner.class)
public abstract class TestForAnnotationProperty<P extends AbstractAnnotationProperty> {
    protected final Class<P> propertyType;
    protected final Class<Element> elementType;

    public TestForAnnotationProperty() {
        propertyType = (Class<P>) getPassedTargetClass(getClass(), TestForAnnotationProperty.class).get(0);
        elementType = (Class<Element>) getPassedTargetClass(propertyType, AbstractAnnotationProperty.class).get(0);
    }

    protected P property;

    @Inject
    private EnvironmentModule.EnvironmentInjector injector;

    protected final void initialProperty(Element element, Module... otherModule) {
        property = injector.createChildInjector(otherModule)
                .getInstance(propertyType);
        property.initial();
    }

    @SuppressWarnings("unchecked")
    protected final void initialProperty(Element element, Class[] nullTypesToInject, Object[] mockFields) {
        initialProperty(element, getModule(this, mockFields),
                binder -> {
                    binder.bind(TypeLiteral.get(elementType)).toProvider(Providers.of(element));
                    for (Class aClass : nullTypesToInject) {
                        bindToProvider(binder, aClass, null);
                    }
                });
    }

    private <T> void bindToProvider(Binder binder, Class<T> clazz, T value) {
        binder.bind(clazz).toProvider(Providers.of(value));
    }

    protected final void initialProperty(Element element, Object... mockFields) {
        initialProperty(element, new Class[0], mockFields);
    }

    @Inject
    private EnvironmentHelper env;

    protected final TypeElement getTypeElement(Class<?> clazz) {
        return env.getTypeElement(clazz.getCanonicalName());
    }

}
