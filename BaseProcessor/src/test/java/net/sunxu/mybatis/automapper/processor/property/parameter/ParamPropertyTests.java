package net.sunxu.mybatis.automapper.processor.property.parameter;

import net.sunxu.mybatis.automapper.processor.property.TestForAnnotationProperty;
import org.apache.ibatis.annotations.Param;
import org.junit.Test;
import org.mockito.Mock;

import javax.lang.model.element.VariableElement;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;


public class ParamPropertyTests extends TestForAnnotationProperty<ParamProperty> {
    @Mock
    private VariableElement element;
    @Mock
    private Param param;

    @Test
    public void paramName_noArg_getByAnnotationValue() {
        doReturn("test").when(param).value();
        initialProperty(element, param);

        String res = property.paramName();

        assertEquals("test", res);
        verify(param).value();
    }
}
