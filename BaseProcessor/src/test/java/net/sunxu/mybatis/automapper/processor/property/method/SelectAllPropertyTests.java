package net.sunxu.mybatis.automapper.processor.property.method;

import net.sunxu.mybatis.automapper.mapper.annotation.entity.SelectAll;
import net.sunxu.mybatis.automapper.processor.property.Method;
import net.sunxu.mybatis.automapper.processor.property.TestForAnnotationProperty;
import net.sunxu.mybatis.automapper.processor.property.Type;
import org.junit.Test;
import org.mockito.Mock;

import javax.lang.model.element.ExecutableElement;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;

public class SelectAllPropertyTests extends TestForAnnotationProperty<SelectAllProperty> {
    @Mock
    private ExecutableElement element;
    @Mock
    private SelectAll annotation;
    @Mock
    private Type type;
    @Mock
    private Method method;

    @Test
    public void orderBy_noArg_getByAnnotationValue() {
        doReturn("column_name ").when(annotation).orderBy();
        initialProperty();

        String res = property.orderBy();

        assertEquals("column_name", res);
    }

    private void initialProperty() {
        initialProperty(element, annotation, type, method);
    }

    @Test
    public void hasOrderBy_has_true() {
        doReturn("column_name").when(annotation).orderBy();
        initialProperty();

        boolean res = property.hasOrderBy();

        assertTrue(res);
    }

    @Test
    public void hasOrderBy_no_false() {
        doReturn(" ").when(annotation).orderBy();
        initialProperty();

        boolean res = property.hasOrderBy();

        assertFalse(res);
    }
}
