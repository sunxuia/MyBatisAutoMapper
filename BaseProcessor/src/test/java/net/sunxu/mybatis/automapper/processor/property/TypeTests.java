package net.sunxu.mybatis.automapper.processor.property;

import com.google.inject.Inject;
import net.sunxu.mybatis.automapper.processor.environment.EnvironmentHelper;
import net.sunxu.mybatis.automapper.processor.environment.GenericHelper;
import net.sunxu.mybatis.automapper.processor.environment.SystemHelper;
import net.sunxu.mybatis.automapper.processor.testutil.TestEnvRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static java.util.Collections.emptySet;
import static java.util.stream.Collectors.toSet;
import static net.sunxu.mybatis.automapper.processor.testutil.TestUtils.assertCollectionEquals;
import static net.sunxu.mybatis.automapper.processor.testutil.TestUtils.assertCollectionEqualsUnordered;
import static org.junit.Assert.*;
import static org.mockito.Mockito.doReturn;

@RunWith(TestEnvRunner.class)
public class TypeTests {
    private static class BaseTestData<T> {
        private T id;

        private List<T> ids;

        public void setId(T id) {
            this.id = id;
        }

        public T getId() {
            return id;
        }
    }

    @Inject
    private EnvironmentHelper env;
    @Inject
    private SystemHelper sys;
    @Inject
    private TypeFactory typeFactory;
    @Inject
    private GenericHelper genericHelper;

    private Type type;

    @Before
    public void before() {
        doReturn(emptySet()).when(sys).getClassesInPackage(TypeFactory.PACKAGE_NAME);
        doReturn(emptySet()).when(sys).getClassesInPackage(FieldFactory.PACKAGE_NAME);
        doReturn(emptySet()).when(sys).getClassesInPackage(MethodFactory.PACKAGE_NAME);
        doReturn(emptySet()).when(sys).getClassesInPackage(ParameterFactory.PACKAGE_NAME);

    }

    private void setType(Class<?> clazz) {
        TypeElement typeElement = env.getTypeElement(clazz.getCanonicalName());
        type = typeFactory.get(typeElement, genericHelper.newGenericTypes(), Collections.emptyMap());

    }


    private static class TestData extends BaseTestData<Long> {
        private String name;

        private String getName() {
            return name;
        }
    }

    @Test
    public void getPassedGenericArgumentNames_superGenericClass_validGenericArguments() {
        setType(TestData.class);

        List<String> res = type.getPassedGenericArgumentNames(BaseTestData.class);

        assertCollectionEquals(res, Long.class.getCanonicalName());
    }

    @Test
    public void getGenericTypes_genericTypes_validGenericTypes() {
        setType(BaseTestData.class);

        List<String> res = type.getGenericTypes();

        assertCollectionEquals(res, "T");
    }

    @Test
    public void isAssignableFrom_isAssignable_true() {
        setType(TestData.class);

        boolean res = type.isAssignedFrom(BaseTestData.class);

        assertTrue(res);
    }

    @Test
    public void isAssignalbleFrom_isNotAssignable_false() {
        setType(TestData.class);

        boolean res = type.isAssignedFrom(Date.class);


        assertFalse(res);
    }

    @Test
    public void getSimpleName_noArg_typeSimpleName() {
        setType(TestData.class);

        String res = type.getSimpleName();

        assertEquals(TestData.class.getSimpleName(), res);
    }

    @Test
    public void getName_noArg_getClassName() {
        setType(TestData.class);

        String res = type.getName();

        assertEquals(TestData.class.getCanonicalName(), res);
    }

    @Test
    public void toString_noArg_getClassName() {
        setType(TestData.class);

        String res = type.toString();

        assertEquals(TestData.class.getCanonicalName(), res);
    }

    private static class AlwaysTrueFilter<T extends Element> extends TypeFactory.Filter<T> {
        @Override
        public boolean test(T element, GenericHelper.GenericTypes genericTypes) {
            return true;
        }

        @Override
        protected TypeFactory.Filter<T> clone() {
            return this;
        }
    }

    @Test
    public void getFields_noArg_validFields() {
        typeFactory.setFieldFilterPrototype(new AlwaysTrueFilter<>());
        setType(TestData.class);

        Set<String> fields = type.getFields().stream().map(Field::toString).collect(toSet());

        assertCollectionEqualsUnordered(fields, "java.lang.Long id",
                "java.util.List<java.lang.Long> ids",
                "java.lang.String name");
    }

    @Test
    public void getMethods_forClass_validMethods() {
        typeFactory.setMethodFilterPrototype(new AlwaysTrueFilter<>());
        setType(TestData.class);

        Set<String> methods = type.getMethods().stream().map(Object::toString).collect(toSet());

        assertCollectionEqualsUnordered(methods, "setId(java.lang.Long)", "getId()", "getName()");
    }

    private interface TestBaseInterface {
        void setId(Long id);

        Long getId();
    }

    @Test
    public void getMethods_forInterface_validMethods() {
        typeFactory.setMethodFilterPrototype(new AlwaysTrueFilter<>());
        setType(TestBaseInterface.class);

        Set<String> methods = type.getMethods().stream().map(Object::toString).collect(toSet());

        assertCollectionEqualsUnordered(methods, "setId(java.lang.Long)", "getId()");
    }

}
