package net.sunxu.mybatis.automapper.processor.property;

import com.google.inject.Inject;

import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;


public class Parameter extends Field {
    @Inject
    public Parameter(VariableElement parameterElement, TypeMirror parameterType) {
        super(parameterElement, parameterType);
    }

    @Inject
    private Method method;

    public int index() {
        if (method.getParameters() != null) {
            for (int i = 0; i < method.getParameters().size(); i++) {
                if (this == method.getParameters().get(i)) {
                    return i;
                }
            }
        }
        throw new RuntimeException("Method not initialed");
    }

    public Method getMethod() {
        return method;
    }

    @Override
    public String toString() {
        return getCanonicalType();
    }
}
