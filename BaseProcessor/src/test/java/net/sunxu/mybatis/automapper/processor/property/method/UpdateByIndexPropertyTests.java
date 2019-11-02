package net.sunxu.mybatis.automapper.processor.property.method;

import net.sunxu.mybatis.automapper.mapper.annotation.entity.UpdateByIndex;
import net.sunxu.mybatis.automapper.processor.property.Method;
import net.sunxu.mybatis.automapper.processor.property.TestForAnnotationProperty;
import net.sunxu.mybatis.automapper.processor.property.Type;
import org.junit.Test;
import org.mockito.Mock;

import javax.lang.model.element.ExecutableElement;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;


public class UpdateByIndexPropertyTests extends TestForAnnotationProperty<UpdateByIndexProperty> {
    @Mock
    private ExecutableElement element;
    @Mock
    private UpdateByIndex annotation;
    @Mock
    private Type type;
    @Mock
    private Method method;

    @Test
    public void indexName_noArg_getByAnnotationValue() {
        doReturn("indexName").when(annotation).value();
        initialProperty(element, annotation, type, method);

        String res = property.indexName();

        assertEquals("indexName", res);
    }
}
