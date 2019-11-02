package net.sunxu.mybatis.automapper.processor.property.method;

import net.sunxu.mybatis.automapper.mapper.annotation.addition.MethodDatabaseId;
import net.sunxu.mybatis.automapper.processor.property.Method;
import net.sunxu.mybatis.automapper.processor.property.TestForAnnotationProperty;
import net.sunxu.mybatis.automapper.processor.property.Type;
import org.junit.Test;
import org.mockito.Mock;

import javax.lang.model.element.ExecutableElement;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;

public class MethodDatabaseIdPropertyTests extends TestForAnnotationProperty<MethodDatabaseIdProperty> {
    @Mock
    private MethodDatabaseId methodDatabaseId;
    @Mock
    private ExecutableElement element;
    @Mock
    private Type type;
    @Mock
    private Method method;

    @Test
    public void databaseId_noArg_getByAnnotationValue() {
        doReturn("test").when(methodDatabaseId).value();
        initialProperty(element, methodDatabaseId, type, method);

        String res = property.databaseId();

        assertEquals("test", res);
    }

}
