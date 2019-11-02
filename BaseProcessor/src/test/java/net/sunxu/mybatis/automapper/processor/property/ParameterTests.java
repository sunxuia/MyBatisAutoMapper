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

import static net.sunxu.mybatis.automapper.processor.testutil.TestHelpUtils.getParameterElement;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;

@RunWith(TestEnvRunner.class)
public class ParameterTests {

    private interface TestData {

        void setList(List<String> list);

        <T extends Date> void setValue(T value);
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
        doReturn(Collections.emptySet()).when(sys).getClassesInPackage(ParameterFactory.PACKAGE_NAME);
    }

    @Mock
    private Type type;

    @Mock
    private Method method;

    private Parameter parameter;

    private void setParameter(String methodName, int paraIndex) {
        TypeElement typeElement = env.getTypeElement(TestData.class.getCanonicalName());
        VariableElement parameterElement = getParameterElement(typeElement, methodName, paraIndex);

        ParameterFactory factory = injector.createChildInjector().getInstance(ParameterFactory.class);
        parameter = factory.get(parameterElement,
                genericHelper.newGenericTypes(),
                ImmutableMap.of(Type.class, type, Method.class, method));
    }

    @Test
    public void getMethod_noArg_mockMethod() {
        setParameter("setList", 0);

        Method res = parameter.getMethod();

        assertEquals(method, res);
    }

    @Test
    public void toString_nonGenericMethod_canonicalName() {
        setParameter("setList", 0);

        String res = parameter.toString();

        assertEquals("java.util.List<java.lang.String>", res);
    }

    @Test
    public void toString_genericMethod_canonicalName() {
        setParameter("setValue", 0);

        String res = parameter.toString();

        assertEquals("java.util.Date", res);
    }
}
