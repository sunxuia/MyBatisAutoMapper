package net.sunxu.mybatis.automapper.processor.property;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.TypeMirror;
import java.util.Collections;
import java.util.List;


public class Method extends AbstractElementAdaptor {
    public static final String RETURN_TYPE_NAME = "returnType";


    private final ExecutableElement element;

    private final String methodName;

    @Inject
    @Named(RETURN_TYPE_NAME)
    private TypeMirror returnType;

    @Inject
    public Method(ExecutableElement element) {
        this.element = element;
        this.methodName = element.getSimpleName().toString();
    }

    public String getReturnType() {
        return returnType.toString();
    }

    public boolean isGeneric() {
        return element.getTypeParameters().size() > 0;
    }

    @Override
    public String getName() {
        return methodName;
    }

    private String signature;

    @Inject
    private Type type;

    private List<Parameter> parameters;

    public Type getType() {
        return type;
    }

    void setParameters(List<Parameter> parameters) {
        this.parameters = Collections.unmodifiableList(parameters);
        this.signature = makeSignature();
    }

    public List<Parameter> getParameters() {
        return parameters;
    }

    public String makeSignature() {
        StringBuilder sb = new StringBuilder();
        sb.append(methodName).append('(');
        if (parameters.size() > 0) {
            for (Parameter parameter : parameters) {
                sb.append(parameter.getCanonicalType()).append(',');
            }
            sb.setCharAt(sb.length() - 1, ')');
        } else {
            sb.append(')');
        }
        return sb.toString();
    }

    public String getSignature() {
        return signature;
    }

    @Override public String toString() {
        return signature;
    }
}
