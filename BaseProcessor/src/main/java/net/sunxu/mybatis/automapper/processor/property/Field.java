package net.sunxu.mybatis.automapper.processor.property;

import com.google.inject.Inject;

import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class Field extends AbstractElementAdaptor {
    private final VariableElement fieldElement;
    private final TypeMirror fieldType;
    private final String fieldName;
    private final String canonicalFieldType;

    @Inject
    public Field(VariableElement fieldElement, TypeMirror fieldType) {
        this.fieldElement = fieldElement;
        fieldName = fieldElement.getSimpleName().toString();
        this.fieldType = fieldType;
        canonicalFieldType = fieldType.toString();
    }

    private String simpleType;

    public String getSimpleType() {
        if (simpleType == null) {
            String type = getCanonicalType();
            if (type.contains("<"))
                type = type.substring(0, type.indexOf("<"));
            simpleType = type;
        }
        return simpleType;
    }

    public String getCanonicalType() {
        return canonicalFieldType;
    }

    public boolean isArray() {
        return fieldType.getKind() == TypeKind.ARRAY;
    }

    public String getComponentType() {
        if (!isArray()) return "";
        ArrayType arrayType = (ArrayType) fieldType;
        return arrayType.getComponentType().toString();
    }

    public boolean isGenericType() {
        return canonicalFieldType.contains("<");
    }

    public List<String> getGenericArgumentTypes() {
        if (fieldType instanceof DeclaredType) {
            List<? extends TypeMirror> arguments = ((DeclaredType) fieldType).getTypeArguments();
            if (arguments.isEmpty()) return Collections.emptyList();

            List<String> res = new ArrayList<>(arguments.size());
            for (TypeMirror argument : arguments) {
                res.add(argument.toString());
            }
            return res;
        }
        return Collections.emptyList();
    }

    @Inject
    private Type type;

    public Type getType() {
        return type;
    }

    @Override
    public String getName() {
        return fieldName;
    }

    @Override
    public String toString() {
        return canonicalFieldType + " " + fieldName;
    }
}
