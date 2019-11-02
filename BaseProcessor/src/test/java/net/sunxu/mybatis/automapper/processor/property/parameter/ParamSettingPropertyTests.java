package net.sunxu.mybatis.automapper.processor.property.parameter;

import com.google.inject.Inject;
import net.sunxu.mybatis.automapper.mapper.ParamSetting;
import net.sunxu.mybatis.automapper.processor.environment.Configuration;
import net.sunxu.mybatis.automapper.processor.property.Parameter;
import net.sunxu.mybatis.automapper.processor.property.TestForAnnotationProperty;
import org.apache.ibatis.mapping.ParameterMode;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.UnknownTypeHandler;
import org.junit.Test;
import org.mockito.Mock;

import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

import static junit.framework.TestCase.*;
import static net.sunxu.mybatis.automapper.processor.testutil.TestHelpUtils.getParameterElement;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;

public class ParamSettingPropertyTests extends TestForAnnotationProperty<ParamSettingProperty> {

    private interface TestIF {
        void method1(@ParamSetting(mode = ParameterMode.IN, typeHandler = UnknownTypeHandler.class) int para1);
    }

    @Inject
    private Configuration configuration;
    @Mock
    private Parameter parameter;
    @Mock
    private ParamSetting paramSetting;

    @Test
    public void paramName_noArg_getByAnnotationValue() {
        doReturn("int").when(parameter).getSimpleType();
        doReturn(JdbcType.NUMERIC).when(configuration).getJdbcTypeByJavaType(eq("int"));
        TypeElement typeElement = getTypeElement(TestIF.class);
        VariableElement para1Element = getParameterElement(typeElement, "method1", 0);
        paramSetting = para1Element.getAnnotation(ParamSetting.class);

        initialProperty(para1Element, parameter, paramSetting);

        assertFalse(property.hasJavaType());
        assertEquals("int", property.getJavaType());
        assertFalse(property.hasJdbcType());
        assertEquals(JdbcType.NUMERIC, property.getJdbcType());
        assertTrue(property.hasParameterMode());
        assertEquals(ParameterMode.IN, property.getParameterMode());
        assertTrue(property.hasTypeHandler());
        assertEquals(UnknownTypeHandler.class.getCanonicalName(), property.getTypeHandler());
    }
}
