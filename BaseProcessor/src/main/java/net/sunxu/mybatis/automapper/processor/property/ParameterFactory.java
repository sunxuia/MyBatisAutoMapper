package net.sunxu.mybatis.automapper.processor.property;

import com.google.inject.Inject;
import com.google.inject.Module;
import com.google.inject.util.Providers;
import net.sunxu.mybatis.automapper.processor.environment.GenericHelper;
import net.sunxu.mybatis.automapper.processor.environment.SystemHelper;

import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;


public class ParameterFactory extends AbstractElementAdaptorFactory<VariableElement, Parameter> {
    public static final String PACKAGE_NAME = "net.sunxu.mybatis.automapper.processor.property.parameter";

    @Inject
    public ParameterFactory(SystemHelper sys) {
        super(sys, PACKAGE_NAME);
    }

    @Override
    protected Module getAdditionModule(VariableElement element, GenericHelper.GenericTypes genericTypes) {
        TypeMirror realType = genericTypes.getRealType(element);
        return binder -> binder.bind(TypeMirror.class).toProvider(Providers.of(realType));
    }

    @Override
    protected String decorateFailureMessage(Parameter elementAdaptor, String propertyClass, String failMessage) {
        return String.format("[%s] error for [%s][%s][%s] : %s.",
                propertyClass, elementAdaptor.getType(), elementAdaptor.getMethod(), elementAdaptor,
                failMessage);
    }

    @Override
    protected void setOtherAdaptors(Parameter adaptor,
                                    VariableElement element,
                                    GenericHelper.GenericTypes genericTypes) {
        //do nothing
    }
}
