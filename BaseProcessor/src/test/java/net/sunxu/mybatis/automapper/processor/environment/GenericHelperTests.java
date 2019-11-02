package net.sunxu.mybatis.automapper.processor.environment;

import net.sunxu.mybatis.automapper.processor.environment.GenericHelper.GenericTypes;
import net.sunxu.mybatis.automapper.processor.testutil.ProcessingEnvUtils;
import org.junit.Test;

import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import java.util.*;
import java.util.function.Consumer;

import static junit.framework.TestCase.assertNotNull;
import static net.sunxu.mybatis.automapper.processor.testutil.TestHelpUtils.convertToArray;
import static net.sunxu.mybatis.automapper.processor.testutil.TestUtils.assertArrayEqualsUnordered;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class GenericHelperTests {
    private void runInEnvironment(Consumer<GenericHelper> visitor) {
        ProcessingEnvUtils.getProcessingEnvironment((p, r) -> {
            GenericHelper genericHelper = new GenericHelper(p);
            visitor.accept(genericHelper);
        });
    }

    private interface Interface1<E> {
        E getValue();

        void setValue(E val);
    }

    private interface Interface2<E> extends Interface1<List<E>> {}

    private static class TestClass1<E> {
        protected E value;
    }

    private static class TestClass2 extends TestClass1<String> implements Interface2<String> {
        private static String CLASS_NAME = TestClass2.class.getCanonicalName();

        private int value;

        @Override public List<String> getValue() { return Collections.emptyList(); }

        @Override public void setValue(List<String> val) { }
    }


    @Test
    public void newGenericTypes_noArg_newInstance() {
        runInEnvironment(helper -> {
            GenericTypes genericTypes = helper.newGenericTypes();
            GenericTypes anotherGenericTypes = helper.newGenericTypes();

            assertTrue(genericTypes != anotherGenericTypes);
        });
    }

    @Test
    public void traversalClassAllElements_genericTypes_allCollect() {
        ProcessingEnvUtils.getProcessingEnvironment((p, r) -> {
            GenericHelper helper = new GenericHelper(p);
            TypeElement typeElement = p.getElementUtils().getTypeElement(TestClass2.CLASS_NAME);

            Set<String> names = new HashSet<>();
            helper.traversalClassAllElements(helper.newGenericTypes(),
                    typeElement,
                    (e, g) -> true,
                    (e, g) -> {
                        String typeName = g.getRealType(e).toString();
                        names.add(typeName + " - " + e.getSimpleName().toString());
                        return true;
                    });
            String[] actual = convertToArray(names, String.class);
            String[] expected = new String[]{
                    "()java.util.List<java.lang.String> - getValue",
                    "java.lang.String - CLASS_NAME",
                    "()void - <clinit>",
                    "int - value",
                    "(java.util.List<java.lang.String>)void - setValue",
                    "java.lang.String - value",
                    "()void - <init>"
            };
            assertArrayEqualsUnordered(expected, actual);
        });
    }

    @Test
    public void traversalInterfaceAllElements_genericTypes_allCollect() {
        ProcessingEnvUtils.getProcessingEnvironment((p, r) -> {
            GenericHelper helper = new GenericHelper(p);
            TypeElement typeElement = p.getElementUtils().getTypeElement(TestClass2.CLASS_NAME);

            Set<String> names = new HashSet<>();
            helper.traversalInterfaceAllElements(helper.newGenericTypes(),
                    typeElement,
                    (e, g) -> e instanceof ExecutableElement,
                    (e, g) -> {
                        String typeName = g.getRealType(((ExecutableElement) e).getReturnType()).toString();
                        names.add(typeName + " - " + e.getSimpleName().toString());
                        return true;
                    });
            String[] actual = convertToArray(names, String.class);
            String[] expected = new String[]{
                    "java.util.List<java.lang.String> - getValue",
                    "void - <clinit>",
                    "void - setValue",
                    "void - <init>"
            };
            assertArrayEqualsUnordered(expected, actual);
        });
    }

    @Test
    public void addMethodGenericArguments_genericMethod_get() {
        ProcessingEnvUtils.getProcessingEnvironment((p, r) -> {
            GenericHelper helper = new GenericHelper(p);
            TypeElement typeElement = p.getElementUtils().getTypeElement(Test2.class.getCanonicalName());
            ExecutableElement method = typeElement.getEnclosedElements().stream()
                    .filter(e -> e.getKind() == ElementKind.METHOD)
                    .map(e -> (ExecutableElement) e)
                    .filter(e -> e.getSimpleName().toString().equals("testMethod"))
                    .findAny()
                    .get();
            GenericTypes types = helper.newGenericTypes();
            helper.addMethodGenericArguments(types, method);

            TypeMirror res = types.getRealType(method.getTypeParameters().get(0));
            assertNotNull(res);
            String resStr = res.toString();
            assertEquals("java.util.Date", resStr);
        });
    }

    public static class Test2 {
        public <T extends Date> T testMethod(T test) {
            return test;
        }
    }
}
