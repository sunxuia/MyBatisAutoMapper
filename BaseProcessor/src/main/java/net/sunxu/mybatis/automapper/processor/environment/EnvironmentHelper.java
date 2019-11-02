package net.sunxu.mybatis.automapper.processor.environment;

import com.squareup.javapoet.JavaFile;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.WildcardType;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static net.sunxu.mybatis.automapper.processor.util.AutoMapperException.newException;


public class EnvironmentHelper {
    private final Types typeUtils;
    private final Elements elementUtils;
    private final Filer filer;
    private final RoundEnvironment roundEnv;
    private final Messager messager;

    public EnvironmentHelper(ProcessingEnvironment processingEnv, RoundEnvironment roundEnv) {
        typeUtils = processingEnv.getTypeUtils();
        elementUtils = processingEnv.getElementUtils();
        filer = processingEnv.getFiler();
        messager = processingEnv.getMessager();

        this.roundEnv = roundEnv;
    }

    //region environment
    public Set<TypeElement> getElementInheritedFrom(Class<?> clazz) {
        Set<TypeElement> res = new HashSet<>();
        for (Element element : roundEnv.getRootElements()) {
            if (element instanceof TypeElement) {
                TypeElement typeElement = (TypeElement) element;
                if (isAssignedFrom(typeElement, clazz)) {
                    res.add(typeElement);
                }
            }
        }
        return res;
    }

    public Set<? extends Element> getElementsAnnotatedWith(Class<? extends Annotation> clazz) {
        return roundEnv.getElementsAnnotatedWith(clazz);
    }

    public <T extends Element> T asElement(TypeMirror typeMirror) {
        return (T) typeUtils.asElement(typeMirror);
    }

    //endregion

    //region class

    /**
     * get typeElement by className
     *
     * @param className
     * @return
     */
    public TypeElement getTypeElement(String className) {
        return elementUtils.getTypeElement(className);
    }

    /**
     * if theTypeElement if assigned from className
     *
     * @param childType  the type element
     * @param parentType class's full name
     * @return
     */
    public boolean isAssignedFrom(TypeElement childType, Class<?> parentType) {
        String className = parentType.getCanonicalName();
        TypeMirror superType = getTypeWithWildCard(className);
        boolean isAssignable = typeUtils.isAssignable(childType.asType(), superType);
        return isAssignable;
    }

    public boolean isAssignedFrom(String childType, String parentType) {
        TypeElement child = getTypeElement(childType);
        TypeElement parent = getTypeElement(parentType);
        if (child == null || parent == null) {
            return false;
        }
        TypeMirror superType = getTypeWithWildCard(parentType);
        boolean isAssignable = typeUtils.isAssignable(child.asType(), superType);
        return isAssignable;
    }

    private DeclaredType getTypeWithWildCard(String className) {
        TypeElement superElement = getTypeElement(className);
        DeclaredType superType = (DeclaredType) superElement.asType();
        final int typedParameterCount = superElement.getTypeParameters().size();
        if (typedParameterCount > 0) {
            WildcardType wildcardType = typeUtils.getWildcardType(null, null);
            TypeMirror[] argumentWildCards = Collections.nCopies(typedParameterCount, wildcardType)
                    .toArray(new TypeMirror[typedParameterCount]);
            superType = typeUtils.getDeclaredType(superElement, argumentWildCards);
        }
        return superType;
    }

    public boolean isClassExist(String className) {
        return elementUtils.getTypeElement(className) != null;
    }

    /**
     * Get generic arguments's name in className passed by typeElement.
     * If the typeElement is generic, it might contains type variable's name.
     *
     * @param typeElement the inherited type which pass type arguments
     * @param clazz       super class's non-generic name
     * @return
     */
    public List<String> getPassedGenericArgumentNames(TypeElement typeElement, Class<?> clazz) {
        String className = clazz.getCanonicalName();
        DeclaredType superType = getTypeWithWildCard(className);
        List<TypeMirror> realTypes = getGenericArgumentName(superType, (DeclaredType) typeElement.asType());
        return realTypes.stream().map(t -> t.toString()).collect(Collectors.toList());
    }

    private List<TypeMirror> getGenericArgumentName(DeclaredType wantedType, DeclaredType currentType) {
        if (typeUtils.isSameType(wantedType.asElement().asType(), currentType.asElement().asType())) {
            return (List<TypeMirror>) currentType.getTypeArguments();
        }
        if (typeUtils.isAssignable(currentType, wantedType)) {
            for (DeclaredType superType : (List<DeclaredType>) typeUtils.directSupertypes(currentType)) {
                List<TypeMirror> types = getGenericArgumentName(wantedType, superType);
                if (types.size() == 0) continue;
                return types;
            }
        }
        return Collections.emptyList();
    }

    //endregion

    //region file
    public void saveJavaFile(JavaFile javaFile) {
        try {
            javaFile.writeTo(filer);
        } catch (IOException e) {
            e.printStackTrace();
            throw newException(String.format("Error while save java file [%s] : %s", javaFile, e.getMessage()));
        }
    }


    //endregion

}
