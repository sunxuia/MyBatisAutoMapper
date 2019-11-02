package net.sunxu.mybatis.automapper.processor.mapper.mapper;

import com.google.inject.Inject;
import net.sunxu.mybatis.automapper.processor.environment.GenericHelper;
import net.sunxu.mybatis.automapper.processor.property.Type;
import net.sunxu.mybatis.automapper.processor.property.TypeFactory;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static net.sunxu.mybatis.automapper.processor.util.AutoMapperException.newException;

public class MapperTypeFactory {
    private TypeFactory typeFactory;

    @Inject
    private GenericHelper genericHelper;

    @Inject
    private void setTypeFactory(TypeFactory typeFactory) {
        this.typeFactory = typeFactory;
        typeFactory.setMethodFilterPrototype(new Filter());
    }

    private class Filter extends TypeFactory.Filter<ExecutableElement> {
        private Set<String> signatures = new HashSet<>();


        @Override
        public boolean test(ExecutableElement element, GenericHelper.GenericTypes genericTypes) {
            genericHelper.addMethodGenericArguments(genericTypes, element);

            StringBuilder signature = new StringBuilder(element.getSimpleName().toString())
                    .append("(");
            for (VariableElement variableElement : element.getParameters()) {
                signature.append(genericTypes.getRealType(variableElement).toString()).append(",");
            }
            if (element.getParameters().size() > 0) {
                signature.setCharAt(signature.length() - 1, ')');
            } else {
                signature.append(')');
            }
            boolean res = !element.isDefault() && signatures.add(signature.toString());
            return res;
        }

        @Override
        protected TypeFactory.Filter<ExecutableElement> clone() {
            return new Filter();
        }
    }

    public MapperModel get(TypeElement typeElement) {
        if (typeElement.getTypeParameters().size() > 0) {
            throw newException("auto mapper [%s] should not have type parameters", typeElement.getQualifiedName());
        }
        Type type = typeFactory.get(typeElement, genericHelper.newGenericTypes(), Collections.emptyMap());
        return new MapperModel(type);
    }


}
