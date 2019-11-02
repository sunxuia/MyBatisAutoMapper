package net.sunxu.mybatis.automapper.processor.property.field;

import com.google.inject.Inject;
import net.sunxu.mybatis.automapper.entity.annotation.Column;
import net.sunxu.mybatis.automapper.processor.environment.Configuration;
import net.sunxu.mybatis.automapper.processor.property.Field;
import net.sunxu.mybatis.automapper.processor.property.TestForAnnotationProperty;
import net.sunxu.mybatis.automapper.processor.property.Type;
import net.sunxu.mybatis.automapper.processor.property.type.DefaultColumnNamingRuleProperty;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.apache.ibatis.type.UnknownTypeHandler;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import javax.lang.model.element.VariableElement;

import static net.sunxu.mybatis.automapper.processor.testutil.TestHelpUtils.getFieldElement;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

public class ColumnPropertyTests extends TestForAnnotationProperty<ColumnProperty> {
    private static class TestData {
        @Column
        private Long id;

        @Column(typeHandler = TestTypeHandler.class)
        private Long idWithTypeHandler;

        @Column(value = "id_with_column_name")
        private Long idWithColumnName;

        @Column(javaType = Integer.class)
        private Long idWithJavaType;
    }

    private static class TestTypeHandler extends UnknownTypeHandler {
        public TestTypeHandler(TypeHandlerRegistry typeHandlerRegistry) {
            super(typeHandlerRegistry);
        }
    }


    private VariableElement fieldElement;

    private Column column;

    @Mock
    private Field field;
    @Mock
    private Type type;
    @Mock
    private DefaultColumnNamingRuleProperty namingRuleProperty;
    @Inject
    private Configuration configuration;

    private void setFieldElement(String fieldName) {
        fieldElement = getFieldElement(getTypeElement(TestData.class), fieldName);
        fieldElement = spy(fieldElement);
        column = fieldElement.getAnnotation(Column.class);
    }

    private void initialProperty() {
        initialProperty(fieldElement, column, field, type);
    }

    @Before
    public void setUp() {
        doReturn(namingRuleProperty).when(type).get(DefaultColumnNamingRuleProperty.class);
        doReturn("test_column_name").when(namingRuleProperty).getDefaultColumnName(any());
    }

    @Test
    public void typeHandler_defaultValue_noValue() {
        setFieldElement("id");
        initialProperty();

        String res = property.typeHandler();

        assertEquals("", res);
    }

    @Test
    public void typeHandler_hasTypeHandler_hasValue() {
        setFieldElement("idWithTypeHandler");
        initialProperty();

        String res = property.typeHandler();

        assertEquals(TestTypeHandler.class.getCanonicalName(), res);
    }

    @Test
    public void isPreferredWhenColumnNameConflict_noArg_annoValue() {
        setFieldElement("id");
        column = mock(Column.class);
        initialProperty();
        doReturn(true).when(column).isPreferredWhenColumnNameConflict();
        assertTrue(property.isPreferredWhenColumnNameConflict());

        doReturn(false).when(column).isPreferredWhenColumnNameConflict();
        assertFalse(property.isPreferredWhenColumnNameConflict());
    }

    @Test
    public void columnName_notSet_fieldNameWithConfiguration() {
        setFieldElement("id");
        initialProperty();

        String res = property.columnName();

        assertEquals("test_column_name", res);
    }

    @Test
    public void columnName_setted_settedColumnName() {
        setFieldElement("idWithColumnName");
        initialProperty();

        String res = property.columnName();

        assertEquals("id_with_column_name", res);

    }

    @Test
    public void jdbcType_default_getByConfiguration() {
        doReturn(Long.class.getCanonicalName()).when(field).getSimpleType();
        doReturn(JdbcType.NUMERIC).when(configuration).getJdbcTypeByJavaType(eq(Long.class.getCanonicalName()));
        setFieldElement("id");
        initialProperty();

        JdbcType res = property.jdbcType();

        assertEquals(JdbcType.NUMERIC, res);
    }

    @Test
    public void jdbcType_setted_settedJdbcType() {
        setFieldElement("id");
        column = mock(Column.class);
        doReturn(JdbcType.NUMERIC).when(column).jdbcType();
        initialProperty();

        JdbcType res = property.jdbcType();

        assertEquals(JdbcType.NUMERIC, res);
    }

    @Test
    public void outDbExpression_noArg_getAnnoValue() {
        setFieldElement("id");
        column = mock(Column.class);
        doReturn("out exp").when(column).outDbExpression();
        initialProperty();

        String res = property.outDbExpression();

        assertEquals("out exp", res);
    }

    @Test
    public void inIndExpression_noArg_getAnnoValue() {
        setFieldElement("id");
        column = mock(Column.class);
        doReturn("in exp").when(column).inDbExpression();
        initialProperty();

        String res = property.inDbExpression();

        assertEquals("in exp", res);
    }

    @Test
    public void insertable_noArg_getAnnoValue() {
        setFieldElement("id");
        column = mock(Column.class);
        doReturn(false).when(column).insertable();
        initialProperty();

        boolean res = property.insertable();

        assertFalse(res);
    }

    @Test
    public void updatable_noArg_getAnnoValue() {
        setFieldElement("id");
        column = mock(Column.class);
        doReturn(false).when(column).updatable();
        initialProperty();

        boolean res = property.updatable();

        assertFalse(res);
    }

    @Test
    public void propertyName_noArg_fieldName() {
        setFieldElement("id");
        doReturn("fieldName").when(field).getName();
        initialProperty();

        String res = property.propertyName();

        assertEquals("fieldName", res);
    }

    @Test
    public void javaType_default_fieldSimpleType() {
        setFieldElement("id");
        doReturn("java.lang.Long").when(field).getSimpleType();
        initialProperty();

        String res = property.javaType();

        assertEquals("java.lang.Long", res);
    }

    @Test
    public void javaType_setted_settedValue() {
        setFieldElement("idWithJavaType");
        initialProperty();

        String res = property.javaType();

        assertEquals("java.lang.Integer", res);
    }

    @Test
    public void useJavaType_default_false(){
        setFieldElement("id");
        initialProperty();

        boolean res = property.useJavaType();

        assertFalse(res);
    }

    @Test
    public void useJavaType_javaTypeSetted_true() {
        setFieldElement("idWithJavaType");
        initialProperty();

        boolean res = property.useJavaType();

        assertTrue(res);
    }
}
