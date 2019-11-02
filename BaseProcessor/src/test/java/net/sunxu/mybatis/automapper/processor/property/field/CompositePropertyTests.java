package net.sunxu.mybatis.automapper.processor.property.field;

import com.google.inject.Inject;
import net.sunxu.mybatis.automapper.entity.annotation.Composite;
import net.sunxu.mybatis.automapper.entity.annotation.Composites;
import net.sunxu.mybatis.automapper.processor.environment.Configuration;
import net.sunxu.mybatis.automapper.processor.environment.MessageHelper;
import net.sunxu.mybatis.automapper.processor.property.Field;
import net.sunxu.mybatis.automapper.processor.property.TestForAnnotationProperty;
import net.sunxu.mybatis.automapper.processor.property.Type;
import net.sunxu.mybatis.automapper.processor.property.type.DefaultColumnNamingRuleProperty;
import net.sunxu.mybatis.automapper.processor.util.AutoMapperException;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.apache.ibatis.type.UnknownTypeHandler;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import javax.lang.model.element.VariableElement;

import static net.sunxu.mybatis.automapper.processor.testutil.TestHelpUtils.getFieldElement;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class CompositePropertyTests extends TestForAnnotationProperty<CompositesProperty> {
    private static class TestData {
        @Composite("id")
        private CompositeData singleComposite;

        @Composite("id")
        @Composite("name")
        @Composite("next.id")
        private CompositeData complexComposite;

        @Composites(value = @Composite("hash"), javaType = String.class)
        private CompositeData compositeWithJavaType;

        @Composite("next.id2")
        private CompositeData wrongComposite;

        @Composite(value = "id", typeHandler = TestTypeHandler.class)
        private CompositeData compositeWithTypeHandler;

        @Composite(value = "id", column = "id_column")
        private CompositeData compositeWithColumnName;

        @Composite(value = "id", jdbcType = JdbcType.VARCHAR)
        private CompositeData compositeDataWithJdbcType;

        @Composite(value = "id",
                inDbExpression = "indbexp",
                outDbExpression = "outdbexp",
                insertable = false,
                updatable = false)
        private CompositeData compositeData2;

        @Composite(value = "id", javaType = long.class)
        private CompositeData compositeDataWithJavaType;
    }

    private static class CompositeData {
        private Long id;
        private String name;
        private CompositeData next;
    }

    private static class TestTypeHandler extends UnknownTypeHandler {
        public TestTypeHandler(TypeHandlerRegistry typeHandlerRegistry) {
            super(typeHandlerRegistry);
        }
    }

    private VariableElement fieldElement;

    @Mock
    private Field field;
    @Mock
    private Type type;
    @Mock
    private DefaultColumnNamingRuleProperty namingRuleProperty;
    @Inject
    private Configuration configuration;
    @Inject
    private MessageHelper messageHelper;

    private void setFieldElement(String fieldName) {
        fieldElement = getFieldElement(getTypeElement(TestData.class), fieldName);
        fieldElement = spy(fieldElement);
    }

    private void initialProperty() {
        initialProperty(fieldElement, field, type);
    }

    @Before
    public void setUp() {
        doReturn(namingRuleProperty).when(type).get(DefaultColumnNamingRuleProperty.class);
        doReturn("test_column_name").when(namingRuleProperty).getDefaultColumnName(any());

        doReturn(CompositeData.class.getCanonicalName()).when(field).getSimpleType();
    }

    @Test
    public void fieldName_noArg_fieldName() {
        doReturn("fieldName").when(field).getName();
        setFieldElement("singleComposite");
        initialProperty();

        String res = property.fieldName();

        assertEquals("fieldName", res);
    }

    @Test
    public void javaType_default_fieldType() {
        setFieldElement("singleComposite");
        initialProperty();

        String res = property.javaType();

        assertEquals(CompositeData.class.getCanonicalName(), res);
    }

    @Test
    public void javaType_setted_settedValue() {
        setFieldElement("compositeWithJavaType");
        initialProperty();

        String res = property.javaType();

        assertEquals(String.class.getCanonicalName(), res);
    }

    @Test
    public void javaType_setted_warnOnJavaTypeNotAssignable() {
        setFieldElement("compositeWithJavaType");
        initialProperty();

        property.javaType();

        verify(messageHelper).warning(any(), Mockito.anyVararg());
    }

    @Test
    public void components_singleComposite_1CompositeProperty() {
        setFieldElement("singleComposite");
        initialProperty();

        assertEquals(1, property.components().size());
    }

    @Test
    public void components_complexComposite_3compositeProperty() {
        setFieldElement("complexComposite");
        initialProperty();

        assertEquals(3, property.components().size());
    }

    @Test(expected = AutoMapperException.class)
    public void initial_notExistField_throwException() {
        setFieldElement("wrongComposite");
        initialProperty();
    }

    @Test
    public void compositeTypeHandler_default_noValue() {
        setCompositeProperty("singleComposite", 0);

        String res = compositeProperty.typeHandler();

        assertEquals("", res);
    }

    private CompositesProperty.CompositeProperty compositeProperty;

    private void setCompositeProperty(String fieldName, int seq) {
        setFieldElement(fieldName);
        initialProperty();
        compositeProperty = property.components().get(seq);
    }

    @Test
    public void compositeTypeHandler_setted_settedValue() {
        setCompositeProperty("compositeWithTypeHandler", 0);

        String res = compositeProperty.typeHandler();

        assertEquals(TestTypeHandler.class.getCanonicalName(), res);
    }

    @Test
    public void compositeIsPreferredWhenColumnNameConflict_default_false() {
        setCompositeProperty("singleComposite", 0);

        boolean res = compositeProperty.isPreferredWhenColumnNameConflict();

        assertFalse(res);
    }

    @Test
    public void compositeColumnName_default_configColumnName() {
        doAnswer(inv -> inv.getArgumentAt(0, String.class))
                .when(namingRuleProperty)
                .getDefaultColumnName(any());
        doReturn("fieldName").when(field).getName();
        setCompositeProperty("singleComposite", 0);

        String res = compositeProperty.columnName();

        assertEquals("fieldNameId", res);
    }

    @Test
    public void compositeColumnName_setted_validColumnName() {
        setCompositeProperty("compositeWithColumnName", 0);

        String res = compositeProperty.columnName();

        assertEquals("id_column", res);
    }

    @Test
    public void compositeJdbcType_default_configJdbcType() {
        doReturn(JdbcType.NUMERIC).when(configuration).getJdbcTypeByJavaType(Long.class.getCanonicalName());
        setCompositeProperty("singleComposite", 0);

        JdbcType res = compositeProperty.jdbcType();

        assertEquals(JdbcType.NUMERIC, res);
    }

    @Test
    public void compositeJdbcType_setted_settedJdbcType() {
        setCompositeProperty("compositeDataWithJdbcType", 0);

        JdbcType res = compositeProperty.jdbcType();

        assertEquals(JdbcType.VARCHAR, res);
    }

    @Test
    public void compositeOutDbExpression_noArg_useAnnoValue() {
        setCompositeProperty("compositeData2", 0);

        String res = compositeProperty.outDbExpression();

        assertEquals("outdbexp", res);
    }

    @Test
    public void compositeInDbExpression_noArg_useAnnoValue() {
        setCompositeProperty("compositeData2", 0);

        String res = compositeProperty.inDbExpression();

        assertEquals("indbexp", res);
    }

    @Test
    public void compositeInsertable_noArg_useAnnoValue() {
        setCompositeProperty("compositeData2", 0);

        boolean res = compositeProperty.insertable();

        assertFalse(res);
    }

    @Test
    public void compositeUpdatable_noArg_useAnnoValue() {
        setCompositeProperty("compositeData2", 0);

        boolean res = compositeProperty.updatable();

        assertFalse(res);
    }

    @Test
    public void compositePropertyName_noArg_useAnnoValue() {
        doReturn("fieldName").when(field).getName();
        setCompositeProperty("singleComposite", 0);

        String res = compositeProperty.propertyName();

        assertEquals("fieldName.id", res);
    }

    @Test
    public void compositePropertyNameWOFieldName_noArg_useAnnoValue() {
        doReturn("fieldName").when(field).getName();
        setCompositeProperty("singleComposite", 0);

        String res = compositeProperty.propertyNameWOFieldName();

        assertEquals("id", res);
    }

    @Test
    public void compositeJavaType_default_fieldType() {
        setCompositeProperty("singleComposite", 0);

        String res = compositeProperty.javaType();

        assertEquals(Long.class.getCanonicalName(), res);
    }

    @Test
    public void compositeJavaType_setted_settedType() {
        setCompositeProperty("compositeDataWithJavaType", 0);

        String res = compositeProperty.javaType();

        assertEquals("long", res);
    }

    @Test
    public void compositeUseJavaType_default_false() {
        setCompositeProperty("singleComposite", 0);

        boolean res = compositeProperty.useJavaType();

        assertFalse(res);
    }

    @Test
    public void compositeUseJavaType_setted_true() {
        setCompositeProperty("compositeDataWithJavaType", 0);

        boolean res = compositeProperty.useJavaType();

        assertTrue(res);
    }
}
