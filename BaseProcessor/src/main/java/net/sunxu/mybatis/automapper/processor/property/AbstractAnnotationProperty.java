package net.sunxu.mybatis.automapper.processor.property;

import com.google.inject.Inject;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.lang.annotation.Annotation;

import static net.sunxu.mybatis.automapper.processor.util.AutoMapperException.newException;


public abstract class AbstractAnnotationProperty<E extends Element> {
    @Inject
    private E element;

    protected <A extends Annotation> A getAnnotation(Class<A> clazz) {
        return element.getAnnotation(clazz);
    }

    protected <A extends Annotation> A[] getAnnotations(Class<A> clazz) {
        return element.getAnnotationsByType(clazz);
    }

    protected AnnotationMirror getAnnotationMirror(Class<? extends Annotation> clazz) {
        final String annoName = clazz.getCanonicalName();
        return element.getAnnotationMirrors().stream()
                .filter(f -> ((TypeElement) f.getAnnotationType().asElement())
                        .getQualifiedName()
                        .toString()
                        .equals(annoName))
                .findAny()
                .orElseThrow(() -> newException("annotation [%s] not found for [%s]", annoName, element));
    }

    protected void validate(boolean isSuccess, String failMessage, Object... paras) {
        if (!isSuccess) {
            throw newException(failMessage, paras);
        }
    }

    protected void initial() {}

}
