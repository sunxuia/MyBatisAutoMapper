package net.sunxu.mybatis.automapper.processor.property.method;

import net.sunxu.mybatis.automapper.mapper.annotation.addition.MethodUseCache;
import net.sunxu.mybatis.automapper.processor.property.Method;
import net.sunxu.mybatis.automapper.processor.property.TestForAnnotationProperty;
import net.sunxu.mybatis.automapper.processor.property.Type;
import org.junit.Test;
import org.mockito.Mock;

import javax.lang.model.element.ExecutableElement;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;


public class UseCachePropertyTests extends TestForAnnotationProperty<MethodUseCacheProperty> {
    @Mock
    private ExecutableElement element;
    @Mock
    private MethodUseCache useCache;
    @Mock
    private Type type;
    @Mock
    private Method method;

    @Test
    public void useCache_noArg_getByAnnotationValue() {
        doReturn(true).when(useCache).value();
        initialProperty(element, useCache, type, method);

        boolean res = property.useCache();

        assertTrue(res);
        verify(useCache).value();
    }
}
