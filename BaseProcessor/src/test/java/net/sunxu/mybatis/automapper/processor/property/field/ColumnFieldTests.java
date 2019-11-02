package net.sunxu.mybatis.automapper.processor.property.field;

import org.apache.ibatis.type.JdbcType;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;

public class ColumnFieldTests {
    @Mock(answer = Answers.CALLS_REAL_METHODS)
    private TestClass columnField;

    private static abstract class TestClass implements ColumnField {}

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void hasTypeHandler_has_true() {
        doReturn("typeHandler").when(columnField).typeHandler();

        boolean res = columnField.hasTypeHandler();

        assertTrue(res);
    }

    @Test
    public void hasJdbcType_arrayJdbcType_true() {
        doReturn(JdbcType.ARRAY).when(columnField).jdbcType();

        boolean res = columnField.hasJdbcType();

        assertTrue(res);
    }

    @Test
    public void hasJdbcType_undefinedJdbcType_false() {
        doReturn(JdbcType.UNDEFINED).when(columnField).jdbcType();

        boolean res = columnField.hasJdbcType();

        assertFalse(res);
    }
}
