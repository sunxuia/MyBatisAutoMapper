package net.sunxu.mybatis.automapper.processor.property;

import com.google.inject.Inject;
import com.google.inject.Module;
import com.google.inject.util.Providers;
import net.sunxu.mybatis.automapper.processor.environment.GenericHelper.GenericTypes;
import net.sunxu.mybatis.automapper.processor.environment.SystemHelper;

import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;


public class FieldFactory extends AbstractElementAdaptorFactory<VariableElement, Field> {
    public static final String PACKAGE_NAME = "net.sunxu.mybatis.automapper.processor.property.field";

    @Inject
    public FieldFactory(SystemHelper sys) {
        super(sys, PACKAGE_NAME);
    }

    @Override
    protected Module getAdditionModule(VariableElement element, GenericTypes genericTypes) {
        TypeMirror realType = genericTypes.getRealType(element);
        return binder -> binder.bind(TypeMirror.class).toProvider(Providers.of(realType));
    }

    @Override
    protected String decorateFailureMessage(Field elementAdaptor, String propertyClass, String failMessage) {
        return String.format("[%s] error for [%s][%s] : %s.",
                propertyClass, elementAdaptor.getType(), elementAdaptor, failMessage);
    }

    @Override
    protected void setOtherAdaptors(Field adaptor, VariableElement element, GenericTypes genericTypes) {
        //do nothing
    }
}
