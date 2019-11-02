package net.sunxu.mybatis.automapper.processor.property.method;

import net.sunxu.mybatis.automapper.mapper.annotation.entity.UpdateIndexValue;
import net.sunxu.mybatis.automapper.processor.property.Method;
import net.sunxu.mybatis.automapper.processor.property.TestForAnnotationProperty;
import net.sunxu.mybatis.automapper.processor.property.Type;
import org.junit.Test;
import org.mockito.Mock;

import javax.lang.model.element.ExecutableElement;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;


public class UpdateIndexValuePropertyTests extends TestForAnnotationProperty<UpdateIndexValueProperty> {
    @Mock
    private ExecutableElement element;
    @Mock
    private UpdateIndexValue annotation;
    @Mock
    private Type type;
    @Mock
    private Method method;

    @Test
    public void indexNameToUpdate_noArg_getByAnnotationValue() {
        doReturn("indexName").when(annotation).indexNameToUpdate();
        initialProperty(element, annotation, type, method);

        String res = property.indexNameToUpdate();

        assertEquals("indexName", res);
    }

    @Test
    public void indexNameToRestrict_noArg_getByAnnotationValue() {
        doReturn("indexName").when(annotation).indexNameToRestrict();
        initialProperty(element, annotation, type, method);

        String res = property.indexNameToRestrict();

        assertEquals("indexName", res);
    }
}
