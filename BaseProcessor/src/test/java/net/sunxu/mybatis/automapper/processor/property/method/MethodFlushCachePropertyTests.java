package net.sunxu.mybatis.automapper.processor.property.method;

import net.sunxu.mybatis.automapper.mapper.annotation.addition.MethodFlushCache;
import net.sunxu.mybatis.automapper.processor.property.Method;
import net.sunxu.mybatis.automapper.processor.property.TestForAnnotationProperty;
import net.sunxu.mybatis.automapper.processor.property.Type;
import org.junit.Test;
import org.mockito.Mock;

import javax.lang.model.element.ExecutableElement;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;


public class MethodFlushCachePropertyTests extends TestForAnnotationProperty<MethodFlushCacheProperty> {
    @Mock
    private ExecutableElement element;
    @Mock
    private MethodFlushCache flushCache;
    @Mock
    private Type type;
    @Mock
    private Method method;

    @Test
    public void flushCache_noArg_getByAnnotationValue() {
        doReturn(true).when(flushCache).value();
        initialProperty(element, flushCache, type, method);

        boolean res = property.flushCache();

        assertTrue(res);
        verify(flushCache, atLeast(1)).value();
    }
}
