package net.sunxu.mybatis.automapper.processor.property;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import net.sunxu.mybatis.automapper.processor.environment.EnvironmentHelper;
import net.sunxu.mybatis.automapper.processor.environment.EnvironmentModule;
import net.sunxu.mybatis.automapper.processor.environment.GenericHelper;
import net.sunxu.mybatis.automapper.processor.environment.SystemHelper;
import net.sunxu.mybatis.automapper.processor.testutil.TestEnvRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static net.sunxu.mybatis.automapper.processor.testutil.TestHelpUtils.getFieldElement;
import static net.sunxu.mybatis.automapper.processor.testutil.TestUtils.assertCollectionEquals;
import static org.junit.Assert.*;
import static org.mockito.Mockito.doReturn;

@RunWith(TestEnvRunner.class)
public class FieldTests {
    private static class TestData {
        String str;

        List<String> genericList;

        int[] intArray;

        String[] stringArray;

        List<? extends Date> genericDates;
    }

    @Inject
    private EnvironmentHelper env;
    @Inject
    private SystemHelper sys;
    @Inject
    private EnvironmentModule.EnvironmentInjector injector;
    @Inject
    private GenericHelper genericHelper;

    @Before
    public void setUp() {
        doReturn(Collections.emptySet()).when(sys).getClassesInPackage(FieldFactory.PACKAGE_NAME);
    }

    @Mock
    private Type type;

    private Field field;

    private void setField(String fieldName) {
        TypeElement typeElement = env.getTypeElement(TestData.class.getCanonicalName());
        VariableElement fieldElement = getFieldElement(typeElement, fieldName);
        assert fieldElement != null;

        FieldFactory factory = injector.createChildInjector().getInstance(FieldFactory.class);
        field = factory.get(fieldElement, genericHelper.newGenericTypes(), ImmutableMap.of(Type.class, type));
    }

    @Test
    public void getSimpleType_nonGenericType_typeName() {
        setField("str");

        String res = field.getSimpleType();

        assertEquals("java.lang.String", res);
    }

    @Test
    public void getSimpleType_genericType_nonGenericType() {
        setField("genericList");

        String res = field.getSimpleType();

        assertEquals("java.util.List", res);
    }

    @Test
    public void getCanonicalType_nromal_canoicalName() {
        setField("str");

        String res = field.getCanonicalType();

        assertEquals("java.lang.String", res);
    }

    @Test
    public void getCanonicalType_array_canoicalName() {
        setField("genericList");

        String res = field.getCanonicalType();

        assertEquals("java.util.List<java.lang.String>", res);
    }

    @Test
    public void getCanonicalType_genericType_canoicalName() {
        setField("intArray");

        String res = field.getCanonicalType();

        assertEquals("int[]", res);
    }

    @Test
    public void isArray_array_true() {
        setField("stringArray");

        boolean res = field.isArray();

        assertTrue(res);
    }

    @Test
    public void isArray_notArray_false() {
        setField("genericList");

        boolean res = field.isArray();

        assertFalse(res);
    }

    @Test
    public void getComponentType_array_compnentType() {
        setField("stringArray");

        String res = field.getComponentType();

        assertEquals("java.lang.String", res);
    }

    @Test
    public void getComponentType_notArray_empty() {
        setField("genericList");

        String res = field.getComponentType();

        assertNotNull(res);
        assertTrue(res.isEmpty());
    }

    @Test
    public void isGenericType_isGeneric_true() {
        setField("genericList");

        boolean res = field.isGenericType();

        assertTrue(res);
    }

    @Test
    public void isGenericType_notGeneric_false() {
        setField("intArray");

        boolean res = field.isGenericType();

        assertFalse(res);
    }

    @Test
    public void getGenericArgumentTypes_generic_noEmptyList() {
        setField("genericDates");

        List<String> res = field.getGenericArgumentTypes();

        assertCollectionEquals(res, Date.class.getCanonicalName());
    }

    @Test
    public void getEnenricArgumentTypes_nonGeneric_emptyList() {
        setField("intArray");

        List<String> res = field.getGenericArgumentTypes();

        assertTrue(res.isEmpty());
    }

    @Test
    public void getType_noArg_getMockType() {
        setField("intArray");

        Type type = field.getType();

        assertTrue(this.type == type);
    }

    @Test
    public void getName_noArg_fieldName() {
        setField("intArray");

        String res = field.getName();

        assertEquals("intArray", res);
    }

    @Test
    public void toString_noArg_typeNameWithFieldName() {
        setField("genericDates");

        String res = field.toString();

        assertEquals("java.util.List<java.util.Date> genericDates", res);
    }
}
