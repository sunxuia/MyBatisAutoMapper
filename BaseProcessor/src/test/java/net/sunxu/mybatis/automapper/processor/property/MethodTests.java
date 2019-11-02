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

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static net.sunxu.mybatis.automapper.processor.testutil.TestHelpUtils.getMethodElement;
import static net.sunxu.mybatis.automapper.processor.testutil.TestUtils.assertCollectionEquals;
import static org.junit.Assert.*;
import static org.mockito.Mockito.doReturn;

@RunWith(TestEnvRunner.class)
public class MethodTests {
    private interface TestData {
        List<String> getList();

        void setList(List<String> list);

        int getCount();

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
        doReturn(Collections.emptySet()).when(sys).getClassesInPackage(MethodFactory.PACKAGE_NAME);
        doReturn(Collections.emptySet()).when(sys).getClassesInPackage(ParameterFactory.PACKAGE_NAME);
    }

    @Mock
    private Type type;

    private Method method;

    private void setMethod(String methodName) {
        TypeElement typeElement = env.getTypeElement(TestData.class.getCanonicalName());
        ExecutableElement methodElement = getMethodElement(typeElement, methodName);
        assert methodElement != null;

        MethodFactory factory = injector.createChildInjector().getInstance(MethodFactory.class);
        method = factory.get(methodElement, genericHelper.newGenericTypes(), ImmutableMap.of(Type.class, type));
    }

    @Test
    public void getReturnType_voidType_getVoid() {
        setMethod("setList");

        String res = method.getReturnType();

        assertEquals("void", res);
    }

    @Test
    public void getReturnType_intType_int() {
        setMethod("getList");

        String res = method.getReturnType();

        assertEquals("java.util.List<java.lang.String>", res);
    }

    @Test
    public void isGeneric_isGeneric_true() {
        setMethod("setValue");

        boolean res = method.isGeneric();

        assertTrue(res);
    }

    @Test
    public void isGeneric_isNotGeneric_false() {
        setMethod("getList");

        boolean res = method.isGeneric();

        assertFalse(res);
    }

    @Test
    public void getName_noArg_MethodName() {
        setMethod("getList");

        String res = method.getName();

        assertEquals("getList", res);
    }

    @Test
    public void getType_noArg_mockType() {
        setMethod("getList");

        Type res = method.getType();

        assertEquals(type, res);
    }

    @Test
    public void getParameters_noArg_validParameters() {
        setMethod("setList");

        List<String> res = method.getParameters().stream().map(Parameter::toString).collect(Collectors.toList());

        assertCollectionEquals(res, "java.util.List<java.lang.String>");
    }

    @Test
    public void getSignature_nonGenericMethod_validSignature() {
        setMethod("setList");

        String res = method.getSignature();

        assertEquals("setList(java.util.List<java.lang.String>)", res);
    }

    @Test
    public void setSignature_genericMethod_validSignature() {
        setMethod("setValue");

        String res = method.getSignature();

        assertEquals("setValue(java.util.Date)", res);
    }

    @Test
    public void toString_noArg_signature() {
        setMethod("setList");

        String res = method.getSignature();

        assertEquals("setList(java.util.List<java.lang.String>)", res);
    }
}
