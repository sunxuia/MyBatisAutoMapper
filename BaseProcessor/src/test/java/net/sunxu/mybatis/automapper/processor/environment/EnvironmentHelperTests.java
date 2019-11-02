package net.sunxu.mybatis.automapper.processor.environment;

import net.sunxu.mybatis.automapper.processor.testutil.ProcessingEnvUtils;
import org.apache.ibatis.type.Alias;
import org.junit.Test;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import static net.sunxu.mybatis.automapper.processor.testutil.TestHelpUtils.convertToArray;
import static org.junit.Assert.*;


public class EnvironmentHelperTests {
    private static final String[] SIMPLE_NAMES = new String[]{"ClassForAPTUse"};
    private static final String OBJECT_CLASS_NAME = "java.lang.Object";

    private void runInEnvironment(Consumer<EnvironmentHelper> visitor) {
        ProcessingEnvUtils.getProcessingEnvironment((p, r) -> {
            EnvironmentHelper env = new EnvironmentHelper(p, r);
            visitor.accept(env);
        });
    }

    @Test
    public void getElementInheritedFrom_Object_allClass() {
        runInEnvironment(env -> {
            Set<TypeElement> res = env.getElementInheritedFrom(Object.class);

            Object[] resArray = convertToArray(res, e -> e.getSimpleName().toString());
            assertArrayEquals(SIMPLE_NAMES, resArray);
        });
    }

    @Test
    public void getElementAnnotatedWith_Alias_getOne() {
        runInEnvironment(env -> {
            Set<? extends Element> res = env.getElementsAnnotatedWith(Alias.class);

            Object[] resArray = convertToArray(res, e -> e.getSimpleName().toString());
            assertArrayEquals(SIMPLE_NAMES, resArray);
        });
    }

    @Test
    public void asElement_existMirror_getOne() {
        ProcessingEnvUtils.getProcessingEnvironment((p, r) -> {
            Element object = p.getElementUtils().getTypeElement(OBJECT_CLASS_NAME);
            TypeMirror type = object.asType();
            EnvironmentHelper env = new EnvironmentHelper(p, r);

            Element res = env.asElement(type);

            assertEquals(object, res);
        });
    }

    @Test
    public void getTypeElement_object_getObject() {
        runInEnvironment(env -> {
            TypeElement res = env.getTypeElement(OBJECT_CLASS_NAME);

            assertEquals(OBJECT_CLASS_NAME, res.getQualifiedName().toString());
        });
    }

    @Test
    public void isAssignedFrom_isAssigned_true() {
        ProcessingEnvUtils.getProcessingEnvironment((p, r) -> {
            EnvironmentHelper env = new EnvironmentHelper(p, r);
            TypeElement string = p.getElementUtils().getTypeElement("java.lang.String");

            boolean res = env.isAssignedFrom(string, CharSequence.class);

            assertTrue(res);
        });
    }

    @Test
    public void isAssignedFrom_isAssignedGeneric_true() {
        ProcessingEnvUtils.getProcessingEnvironment((p, r) -> {
            EnvironmentHelper env = new EnvironmentHelper(p, r);
            TypeElement stringArrayList = p.getElementUtils().getTypeElement(StringArrayList.CLASS_NAME);

            boolean res = env.isAssignedFrom(stringArrayList, List.class);

            assertTrue(res);
        });
    }

    private static class StringArrayList extends ArrayList<String> {
        public static final String CLASS_NAME = StringArrayList.class.getCanonicalName();
    }

    @Test
    public void isAssignedFrom_isNotAssigned_false() {
        ProcessingEnvUtils.getProcessingEnvironment((p, r) -> {
            EnvironmentHelper env = new EnvironmentHelper(p, r);
            TypeElement ele = p.getElementUtils().getTypeElement(OBJECT_CLASS_NAME);

            boolean res = env.isAssignedFrom(ele, CharSequence.class);

            assertFalse(res);
        });
    }

    @Test
    public void isClassExist_exist_true() {
        runInEnvironment(env -> {
            boolean res = env.isClassExist(OBJECT_CLASS_NAME);

            assertTrue(res);
        });
    }

    @Test
    public void isClassExist_notExist_false() {
        runInEnvironment(env -> {
            boolean res = env.isClassExist("some wrong class name");

            assertFalse(res);
        });
    }

    @Test
    public void getPassedGenericArgumentNames_StringArrayList_String() {
        ProcessingEnvUtils.getProcessingEnvironment((p, r) -> {
            EnvironmentHelper env = new EnvironmentHelper(p, r);
            TypeElement ele = p.getElementUtils().getTypeElement(StringArrayList.CLASS_NAME);

            List<String> res = env.getPassedGenericArgumentNames(ele, List.class);

            assertArrayEquals(new String[]{"java.lang.String"}, convertToArray(res, String.class));
        });
    }

    @Test
    public void saveJarFile_jarFile_invokeJWriteTo() {
        //TODO : add code here
    }
}
