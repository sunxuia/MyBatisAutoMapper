package net.sunxu.mybatis.automapper.processor.property.field;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import net.sunxu.mybatis.automapper.entity.annotation.Cascade;
import net.sunxu.mybatis.automapper.mapper.EntityMapper;
import net.sunxu.mybatis.automapper.processor.environment.GenericHelper;
import net.sunxu.mybatis.automapper.processor.environment.SystemHelper;
import net.sunxu.mybatis.automapper.processor.property.Field;
import net.sunxu.mybatis.automapper.processor.property.FieldFactory;
import net.sunxu.mybatis.automapper.processor.property.TestForAnnotationProperty;
import net.sunxu.mybatis.automapper.processor.property.Type;
import net.sunxu.mybatis.automapper.processor.property.type.DefaultColumnNamingRuleProperty;
import net.sunxu.mybatis.automapper.processor.testutil.TestHelpUtils;
import org.apache.ibatis.mapping.FetchType;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import java.util.Collections;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class CascadePropertyTests extends TestForAnnotationProperty<CascadeProperty> {
    private static class TestData {
        @Cascade
        ReferredTestData defaultValue;

        @Cascade(referEntity = ReferredTestData.class,
                value = "referIndexName",
                localIndex = "localIndexName",
                many = true,
                byMapper = TestMapper.class)
        ReferredTestData specifiedValue;

        @Cascade
        List<ReferredTestData> listValue;

        @Cascade
        ReferredTestData[] arrayValue;
    }

    private static class ReferredTestData {}

    private interface TestMapper extends EntityMapper<TestData> {}

    @Before
    public void setUp() {
        DefaultColumnNamingRuleProperty defaultColumnNamingRuleProperty = mock(DefaultColumnNamingRuleProperty.class);
        doReturn(defaultColumnNamingRuleProperty).when(type).get(DefaultColumnNamingRuleProperty.class);
        doAnswer(invocation -> invocation.getArgumentAt(0, String.class))
                .when(defaultColumnNamingRuleProperty)
                .getDefaultColumnName(any());
    }

    @Mock
    private Type type;

    @Inject
    private FieldFactory fieldFactory;
    @Inject
    private GenericHelper genericHelper;
    @Inject
    private SystemHelper sys;

    private Field field;
    private VariableElement fieldElement;
    private Cascade cascade;

    private void setField(String fieldName) {
        TypeElement typeElement = getTypeElement(TestData.class);
        fieldElement = TestHelpUtils.getFieldElement(typeElement, fieldName);
        fieldElement = spy(fieldElement);

        cascade = fieldElement.getAnnotation(Cascade.class);

        doReturn(Collections.emptySet()).when(sys).getClassesInPackage(FieldFactory.PACKAGE_NAME);
        field = fieldFactory.get(fieldElement, genericHelper.newGenericTypes(), ImmutableMap.of(Type.class, type));
        field = spy(field);
    }

    private void initialProperty() {
        initialProperty(fieldElement, type, field, cascade);
    }


    @Test
    public void localIndex_byDefault_empty() {
        setField("defaultValue");
        initialProperty();

        String res = property.localIndex();

        assertEquals("", res);
    }

    @Test
    public void localIndex_specified_spefifiedValue() {
        setField("specifiedValue");
        initialProperty();

        String res = property.localIndex();

        assertEquals("localIndexName", res);
    }

    @Test
    public void fetchType_byDefault_default() {
        setField("defaultValue");
        initialProperty();

        FetchType res = property.fetchType();

        assertEquals(FetchType.DEFAULT, res);
    }

    @Test
    public void byMapper_byDefault_empty() {
        setField("defaultValue");
        initialProperty();

        String res = property.byMapper();

        assertEquals("", res);
    }

    @Test
    public void byMapper_specified_spefifiedValue() {
        setField("specifiedValue");
        initialProperty();

        String res = property.byMapper();

        assertEquals(TestMapper.class.getCanonicalName(), res);
    }

    @Test
    public void referEntity_byDefault_fieldType() {
        setField("defaultValue");
        initialProperty();

        String res = property.referEntity();

        assertEquals(ReferredTestData.class.getCanonicalName(), res);
    }

    @Test
    public void referEntity_specified_spefifiedValue() {
        setField("specifiedValue");
        initialProperty();

        String res = property.referEntity();

        assertEquals(ReferredTestData.class.getCanonicalName(), res);
    }

    @Test
    public void referEntity_defaultWithList_listInnerValue() {
        setField("listValue");
        initialProperty();

        String res = property.referEntity();

        assertEquals(ReferredTestData.class.getCanonicalName(), res);
    }

    @Test
    public void referEntity_defaultWithArray_array() {
        setField("arrayValue");
        initialProperty();

        String res = property.referEntity();

        assertEquals(ReferredTestData.class.getCanonicalName(), res);
    }

    @Test
    public void fieldName_noArg_fieldName() {
        setField("specifiedValue");
        initialProperty();

        String res = property.fieldName();

        assertEquals("specifiedValue", res);
    }

    @Test
    public void javatype_noArg_fieldType() {
        setField("specifiedValue");
        initialProperty();

        String res = property.javaType();

        assertEquals(ReferredTestData.class.getCanonicalName(), res);
    }

    @Test
    public void referIndex_noArg_referIndex() {
        setField("specifiedValue");
        initialProperty();

        String res = property.referIndex();

        assertEquals("referIndexName", res);
    }

    @Test
    public void isMany_setTrue_true() {
        setField("specifiedValue");
        initialProperty();

        boolean res = property.isMany();

        assertTrue(res);
    }

    @Test
    public void isMany_defaultWithList_true() {
        setField("listValue");
        initialProperty();

        boolean res = property.isMany();

        assertTrue(res);
    }

    @Test
    public void isMany_defaultWithArray_true() {
        setField("arrayValue");
        initialProperty();

        boolean res = property.isMany();

        assertTrue(res);
    }
}
