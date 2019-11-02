package net.sunxu.mybatis.automapper.processor.property.method;

import net.sunxu.mybatis.automapper.mapper.annotation.addition.MethodTimeout;
import net.sunxu.mybatis.automapper.processor.property.Method;
import net.sunxu.mybatis.automapper.processor.property.TestForAnnotationProperty;
import net.sunxu.mybatis.automapper.processor.property.Type;
import org.junit.Test;
import org.mockito.Mock;

import javax.lang.model.element.ExecutableElement;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;


public class MethodTimeoutPropertyTests extends TestForAnnotationProperty<MethodTimeoutProperty> {
    @Mock
    private ExecutableElement element;

    @Mock
    private MethodTimeout timeout;
    @Mock
    private Type type;
    @Mock
    private Method method;

    @Test
    public void timeout_noArg_getByAnnotationValue() {
        doReturn(100).when(timeout).value();
        initialProperty(element, timeout, type, method);

        int res = property.timeout();

        assertEquals(100, res);
        verify(timeout).value();
    }
}
