package net.sunxu.mybatis.automapper.processor.property;

import com.google.inject.Inject;
import com.google.inject.Module;
import com.google.inject.util.Providers;
import net.sunxu.mybatis.automapper.processor.environment.GenericHelper;
import net.sunxu.mybatis.automapper.processor.environment.SystemHelper;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import java.util.*;

import static com.google.inject.name.Names.named;
import static net.sunxu.mybatis.automapper.processor.property.Method.RETURN_TYPE_NAME;


public class MethodFactory extends AbstractElementAdaptorFactory<ExecutableElement, Method> {
    public static final String PACKAGE_NAME = "net.sunxu.mybatis.automapper.processor.property.method";

    @Inject
    public MethodFactory(SystemHelper sys) {
        super(sys, PACKAGE_NAME);
    }

    @Override
    protected Module getAdditionModule(ExecutableElement element, GenericHelper.GenericTypes genericTypes) {
        genericHelper.addMethodGenericArguments(genericTypes, element);

        TypeMirror returnType = genericTypes.getRealType(element.getReturnType());
        return binder -> binder.bind(TypeMirror.class)
                .annotatedWith(named(RETURN_TYPE_NAME))
                .toProvider(Providers.of(returnType));
    }

    @Override
    protected String decorateFailureMessage(Method elementAdaptor, String propertyClass, String failMessage) {
        return String.format("[%s] error for [%s][%s] : %s.",
                propertyClass, elementAdaptor.getType(), elementAdaptor, failMessage);
    }

    @Inject
    private GenericHelper genericHelper;
    @Inject
    private ParameterFactory parameterFactory;

    @Override
    protected void setOtherAdaptors(Method adaptor,
                                    ExecutableElement element,
                                    GenericHelper.GenericTypes genericTypes) {
        Map<Class<? extends AbstractElementAdaptor>, AbstractElementAdaptor> relatedProperties = new HashMap<>();
        relatedProperties.put(Type.class, adaptor.getType());
        relatedProperties.put(Method.class, adaptor);

        List<Parameter> parameters = new ArrayList<>(element.getParameters().size());
        for (VariableElement variableElement : element.getParameters()) {
            Parameter parameterProperty =
                    parameterFactory.get(variableElement, genericTypes, relatedProperties);
            parameters.add(parameterProperty);
        }
        adaptor.setParameters(parameters);
    }


}
