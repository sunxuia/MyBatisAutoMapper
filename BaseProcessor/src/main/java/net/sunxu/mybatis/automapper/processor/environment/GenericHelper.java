package net.sunxu.mybatis.automapper.processor.environment;

import com.google.inject.Singleton;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.type.*;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.function.BiPredicate;


@Singleton
public class GenericHelper {
    public class GenericTypes {
        private Map<TypeMirror, TypeMirror> genericTypes = new HashMap<>(1);

        private boolean contains(TypeMirror key) {
            return genericTypes.containsKey(key);
        }

        private TypeMirror get(TypeMirror key) {
            return genericTypes.get(key);
        }

        private void add(TypeMirror key, TypeMirror value) {
            genericTypes.put(key, value);
        }

        public TypeMirror getRealType(TypeMirror type) {
            if (type.getKind() == TypeKind.TYPEVAR) {
                TypeMirror realType = genericTypes.get(type);
                if (realType == null)
                    realType = ((TypeVariable) type).getLowerBound();
                if (realType == null || realType.getKind() == TypeKind.NULL)
                    realType = ((TypeVariable) type).getUpperBound();
                if (realType == null || realType.getKind() == TypeKind.NULL)
                    return type;

                return getRealType(realType);
            } else if (type.getKind() == TypeKind.ARRAY) {
                TypeMirror componentType = ((ArrayType) type).getComponentType();
                TypeMirror packedComponentType = getRealType(componentType);

                return typeUtils.getArrayType(packedComponentType);
            } else if (type.getKind() == TypeKind.WILDCARD) {
                TypeMirror boundType = ((WildcardType) type).getExtendsBound();
                if (boundType == null || boundType.getKind() == TypeKind.NULL)
                    boundType = ((WildcardType) type).getSuperBound();
                if (boundType == null || boundType.getKind() == TypeKind.NULL)
                    boundType = elementUtils.getTypeElement(Object.class.getCanonicalName()).asType();

                return getRealType(boundType);
            } else if (type.getKind() == TypeKind.DECLARED) {
                DeclaredType declaredType = (DeclaredType) type;
                List<? extends TypeMirror> typeArguments = declaredType.getTypeArguments();
                if (typeArguments.size() == 0) return type;

                List<TypeMirror> arguments = new ArrayList<>(typeArguments.size());
                for (TypeMirror subType : typeArguments)
                    arguments.add(getRealType(subType));
                TypeMirror realType = typeUtils.getDeclaredType((TypeElement) declaredType.asElement(),
                        arguments.toArray(new TypeMirror[typeArguments.size()]));

                return realType;
            } else {
                return type;
            }
        }

        public TypeMirror getRealType(Element element) {
            return getRealType(element.asType());
        }
    }

    public GenericTypes newGenericTypes() {
        return new GenericTypes();
    }

    private final Types typeUtils;
    private final Elements elementUtils;

    public GenericHelper(ProcessingEnvironment processingEnv) {
        this.typeUtils = processingEnv.getTypeUtils();
        this.elementUtils = processingEnv.getElementUtils();
    }

    public void traversalClassAllElements(
            @NotNull GenericTypes generic,
            TypeElement typeElement,
            BiPredicate<Element, GenericTypes> isMatch,
            BiPredicate<Element, GenericTypes> processor) {
        for (; ; ) {
            for (Element element : typeElement.getEnclosedElements()) {
                if (isMatch.test(element, generic)) {
                    boolean isContinue = processor.test(element, generic);
                    if (!isContinue) return;
                }
            }

            TypeMirror superType = typeElement.getSuperclass();
            if (superType == null ||
                    superType.getKind() == TypeKind.NONE ||
                    superType.toString().equals("java.lang.Object"))
                break;
            List<? extends TypeMirror> typeArguments = ((DeclaredType) superType).getTypeArguments();
            typeElement = (TypeElement) ((DeclaredType) superType).asElement();
            List<? extends TypeParameterElement> genericElements = typeElement.getTypeParameters();

            for (int i = 0; i < genericElements.size(); i++) {
                TypeMirror realType = typeArguments.get(i);
                if (generic.contains(realType))
                    realType = generic.get(realType);
                generic.add(genericElements.get(i).asType(), realType);
            }
        }
    }

    public void traversalInterfaceAllElements(
            GenericTypes generic,
            TypeElement typeElement,
            BiPredicate<Element, GenericTypes> isMatch,
            BiPredicate<Element, GenericTypes> processor) {

        Queue<TypeElement> interfaceElements = new LinkedList<>();
        interfaceElements.offer(typeElement);
        while (!interfaceElements.isEmpty()) {
            TypeElement interfaceElement = interfaceElements.poll();

            for (Element element : interfaceElement.getEnclosedElements()) {
                if (isMatch.test(element, generic)) {
                    boolean isContinue = processor.test(element, generic);
                    if (!isContinue) return;
                }
            }

            List<? extends TypeMirror> superTypes = interfaceElement.getInterfaces();
            for (TypeMirror superType : superTypes) {
                List<? extends TypeMirror> typeArguments = ((DeclaredType) superType).getTypeArguments();
                interfaceElement = (TypeElement) ((DeclaredType) superType).asElement();
                List<? extends TypeParameterElement> genericElements = interfaceElement.getTypeParameters();
                for (int i = 0; i < genericElements.size(); i++) {
                    TypeMirror realType = typeArguments.get(i);
                    if (generic.contains(realType))
                        realType = generic.get(realType);
                    generic.add(genericElements.get(i).asType(), realType);
                }
                interfaceElements.offer(interfaceElement);
            }
        }
    }

    public void addMethodGenericArguments(
            GenericTypes generic,
            ExecutableElement methodElement) {
        List<? extends TypeParameterElement> genericElements = methodElement.getTypeParameters();
        for (TypeParameterElement genericElement : genericElements) {
            TypeMirror realType = generic.getRealType(genericElement);
            generic.add(genericElement.asType(), realType);
        }
    }
}
