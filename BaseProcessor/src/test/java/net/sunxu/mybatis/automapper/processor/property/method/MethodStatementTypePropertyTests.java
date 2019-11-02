package net.sunxu.mybatis.automapper.processor.property.method;

import net.sunxu.mybatis.automapper.mapper.annotation.addition.MethodStatementType;
import net.sunxu.mybatis.automapper.processor.property.Method;
import net.sunxu.mybatis.automapper.processor.property.TestForAnnotationProperty;
import net.sunxu.mybatis.automapper.processor.property.Type;
import org.apache.ibatis.mapping.StatementType;
import org.junit.Test;
import org.mockito.Mock;

import javax.lang.model.element.ExecutableElement;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;


public class MethodStatementTypePropertyTests extends TestForAnnotationProperty<MethodStatementTypeProperty> {
    @Mock
    private ExecutableElement element;
    @Mock
    private MethodStatementType statementType;
    @Mock
    private Type type;
    @Mock
    private Method method;

    @Test
    public void statementType_noArg_getByAnnotationValue() {
        doReturn(StatementType.CALLABLE).when(statementType).value();
        initialProperty(element, statementType, type, method);

        StatementType res = property.statementType();

        assertEquals(StatementType.CALLABLE, res);
        verify(statementType).value();
    }
}
