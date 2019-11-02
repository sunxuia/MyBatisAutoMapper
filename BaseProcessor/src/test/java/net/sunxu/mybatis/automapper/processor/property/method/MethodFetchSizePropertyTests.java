package net.sunxu.mybatis.automapper.processor.property.method;

import net.sunxu.mybatis.automapper.mapper.annotation.addition.MethodFetchSize;
import net.sunxu.mybatis.automapper.processor.property.Method;
import net.sunxu.mybatis.automapper.processor.property.TestForAnnotationProperty;
import net.sunxu.mybatis.automapper.processor.property.Type;
import org.junit.Test;
import org.mockito.Mock;

import javax.lang.model.element.ExecutableElement;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;


public class MethodFetchSizePropertyTests extends TestForAnnotationProperty<MethodFetchSizeProperty> {
    @Mock
    private ExecutableElement element;
    @Mock
    private MethodFetchSize annotation;
    @Mock
    private Type type;
    @Mock
    private Method method;

    @Test
    public void fetchSize_noArg_getByAnnotationValue() {
        doReturn(10).when(annotation).value();
        initialProperty(element, annotation, type, method);

        int res = property.size();

        assertEquals(10, res);
    }
}
