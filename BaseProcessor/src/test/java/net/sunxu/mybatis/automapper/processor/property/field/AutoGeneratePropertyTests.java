package net.sunxu.mybatis.automapper.processor.property.field;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import net.sunxu.mybatis.automapper.entity.annotation.AutoGenerate;
import net.sunxu.mybatis.automapper.entity.annotation.Cascade;
import net.sunxu.mybatis.automapper.entity.annotation.Column;
import net.sunxu.mybatis.automapper.processor.environment.GenericHelper;
import net.sunxu.mybatis.automapper.processor.environment.SystemHelper;
import net.sunxu.mybatis.automapper.processor.property.Field;
import net.sunxu.mybatis.automapper.processor.property.FieldFactory;
import net.sunxu.mybatis.automapper.processor.property.TestForAnnotationProperty;
import net.sunxu.mybatis.automapper.processor.property.Type;
import net.sunxu.mybatis.automapper.processor.property.type.DefaultColumnNamingRuleProperty;
import net.sunxu.mybatis.automapper.processor.testutil.TestHelpUtils;
import net.sunxu.mybatis.automapper.processor.util.AutoMapperException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import java.util.Collections;
import java.util.Set;

import static net.sunxu.mybatis.automapper.processor.testutil.TestUtils.assertCollectionEqualsUnordered;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class AutoGeneratePropertyTests extends TestForAnnotationProperty<AutoGenerateProperty> {

    public static class TestData {
        @AutoGenerate
        @AutoGenerate
        @Column
        private int fieldWithDuplicatedSetting;

        @AutoGenerate
        @Cascade
        private int fieldWithCascade;

        @AutoGenerate
        @Column
        private int fieldWith1Settings;

        @AutoGenerate
        @AutoGenerate(kind = AutoGenerate.Kind.UPDATE, value = "test")
        @Column
        private int fieldWith2Settings;
    }


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

    @Test(expected = AutoMapperException.class)
    public void initial_dumplicateType_throwException() {
        setField("fieldWithDuplicatedSetting");
        initialProperty();
    }

    @Test(expected = AutoMapperException.class)
    public void initial_compositeType_throwException() {
        setField("fieldWithCascade");
        initialProperty();
    }

    @Test
    public void getSupportTypes_1Type_get1Type() {
        setField("fieldWith1Settings");
        initialProperty();

        Set<AutoGenerate.Kind> res = property.getSupportTypes();

        assertCollectionEqualsUnordered(res, AutoGenerate.Kind.INSERT);
    }

    @Test
    public void getSupportTypes_2Type_get2Type() {
        setField("fieldWith2Settings");
        initialProperty();

        Set<AutoGenerate.Kind> res = property.getSupportTypes();

        assertCollectionEqualsUnordered(res, AutoGenerate.Kind.INSERT, AutoGenerate.Kind.UPDATE);
    }

    @Test
    public void containsKey_containKey_true() {
        setField("fieldWith2Settings");
        initialProperty();

        boolean res = property.containsType(AutoGenerate.Kind.INSERT);

        assertTrue(res);
    }

    @Test
    public void getExpression_nonDefaultValue_true() {
        setField("fieldWith2Settings");
        initialProperty();

        String res = property.getExpression(AutoGenerate.Kind.UPDATE);

        assertEquals("test", res);
    }

}
