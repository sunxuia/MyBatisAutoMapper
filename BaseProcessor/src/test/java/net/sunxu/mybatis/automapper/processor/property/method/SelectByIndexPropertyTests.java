package net.sunxu.mybatis.automapper.processor.property.method;

import net.sunxu.mybatis.automapper.mapper.annotation.entity.SelectByIndex;
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


public class SelectByIndexPropertyTests extends TestForAnnotationProperty<SelectByIndexProperty> {
    @Mock
    private ExecutableElement element;
    @Mock
    private SelectByIndex annotation;
    @Mock
    private Type type;
    @Mock
    private Method method;

    @Test
    public void indexName_noArg_getByAnnotationValue() {
        doReturn("indexName").when(annotation).value();
        initialProperty();

        String res = property.indexName();

        assertEquals("indexName", res);
    }

    private void initialProperty() {
        initialProperty(element, annotation, type, method);
    }

    @Test
    public void withLockExpression_hasLock_validValue() {
        doReturn(" for update ").when(annotation).withLock();
        initialProperty();

        String res = property.withLockExpression();

        assertEquals("for update", res);
    }

    @Test
    public void hasLockExpression_hasLock_true() {
        doReturn("for update").when(annotation).withLock();
        initialProperty();

        boolean res = property.hasLock();

        assertTrue(res);
    }

    @Test
    public void hasLockExpression_hasNoLock_false() {
        doReturn(" ").when(annotation).withLock();
        initialProperty();

        boolean res = property.hasLock();

        assertFalse(res);
    }

    @Test
    public void withOrderBy_hasOrderBy_validValue() {
        doReturn(" order by column_name ").when(annotation).orderBy();
        initialProperty();

        String res = property.orderByExpression();

        assertEquals("column_name", res);
    }

    @Test
    public void hasOrderByExpression_hasOrderBy_true() {
        doReturn(" column_name ").when(annotation).orderBy();
        initialProperty();

        boolean res = property.hasOrderBy();

        assertTrue(res);
    }

    @Test
    public void hasOrderByExpression_hasNoOrderBy_false() {
        doReturn("   ").when(annotation).orderBy();
        initialProperty();

        boolean res = property.hasOrderBy();

        assertFalse(res);
    }
}
