package net.sunxu.mybatis.automapper.processor.testutil;

import com.google.inject.Module;
import com.google.inject.util.Providers;

import javax.lang.model.element.*;
import java.lang.reflect.*;
import java.util.*;
import java.util.function.Function;

import static net.sunxu.mybatis.automapper.processor.testutil.TestException.wrapException;


public final class TestHelpUtils {
    private TestHelpUtils() { }

    public static Name getNameWithName(String name) {
        return new Name() {
            @Override
            public int length() {
                return name.length();
            }

            @Override
            public char charAt(int index) {
                return name.charAt(index);
            }

            @Override
            public CharSequence subSequence(int start, int end) {
                return name.substring(start, end);
            }

            @Override
            public boolean contentEquals(CharSequence cs) {
                if (cs == null) return false;
                return name.equals(cs.toString());
            }

            @Override
            public String toString() {
                return name;
            }
        };
    }

    public static <R, E> R[] convertToArray(Collection<E> collection, Class<R> returnType, Function<E, R> convert) {
        R[] values = (R[]) Array.newInstance(returnType, collection.size());
        int i = 0;
        for (E value : collection) {
            values[i++] = convert.apply(value);
        }
        return values;
    }

    public static <E> Object[] convertToArray(Collection<E> collection, Function<E, Object> convert) {
        return convertToArray(collection, Object.class, convert);
    }

    public static <E, R> R[] convertToArray(E[] array, Class<R> returnType, Function<E, R> convert) {
        return convertToArray(Arrays.asList(array), returnType, convert);
    }

    public static <E> E[] convertToArray(Collection<E> collection, Class<E> componentType) {
        return convertToArray(collection, componentType, Function.identity());
    }

    public static Object[] convertToArray(Collection collection) {
        return convertToArray(collection, Object.class);
    }

    public static String[] getMemberName(Member[] members) {
        return convertToArray(members, String.class, Member::getName);
    }

    public static <T> Set<T> convertToSet(T... values) {
        Set<T> set = new HashSet<>(values.length);
        for (T value : values) {
            set.add(value);
        }
        return set;
    }


    public static List<Class<?>> getPassedTargetClass(Class thisClass, Class targetClass) {
        Map<Type, Type> types = new HashMap<>();
        for (Class clazz = thisClass;
             clazz != targetClass;
             clazz = clazz.getSuperclass()) {
            Type superClass = clazz.getGenericSuperclass();
            if (superClass instanceof Class) continue;

            ParameterizedType parameterized = (ParameterizedType) superClass;
            Class superClazz = clazz.getSuperclass();
            for (int i = 0; i < superClazz.getTypeParameters().length; i++) {
                types.put(superClazz.getTypeParameters()[i], parameterized.getActualTypeArguments()[i]);
            }
        }

        List<Class<?>> res = new ArrayList<>();
        for (TypeVariable typeVariable : targetClass.getTypeParameters()) {
            Type realType = types.get(typeVariable);
            while (realType != null && !(realType instanceof Class))
                realType = types.get(realType);
            res.add((Class) realType);
        }
        return res;
    }

    public static Module getModule(Object tester, Object... fieldValues) {
        return binder -> {
            for (Object fieldValue : fieldValues) {
                Field field = getFieldByValue(tester, fieldValue);
                if (field == null) {
                    binder.bind((Class) fieldValue.getClass()).toProvider(Providers.of(fieldValue));
                } else {
                    binder.bind((Class) field.getType()).toProvider(Providers.of(fieldValue));
                }
            }
        };
    }

    public static <T> Field getFieldByValue(T tester, Object value) {
        for (Field field : tester.getClass().getDeclaredFields()) {
            boolean isAccessable = field.isAccessible();
            field.setAccessible(true);
            Object fieldValue = wrapException(() -> field.get(tester));
            field.setAccessible(isAccessable);
            if (fieldValue == value) {
                return field;
            }
        }
        return null;
    }

    public static void setFieldValue(Object tester, Field field, Object value) {
        boolean isAccessable = field.isAccessible();
        field.setAccessible(true);
        wrapException(() -> field.set(tester, value));
        field.setAccessible(isAccessable);
    }

    public static VariableElement getFieldElement(TypeElement typeElement, String fieldName) {
        VariableElement fieldElement = null;
        for (Element element : typeElement.getEnclosedElements()) {
            if (element instanceof VariableElement && element.getSimpleName().toString().equals(fieldName)) {
                fieldElement = (VariableElement) element;
                break;
            }
        }
        return fieldElement;
    }

    public static ExecutableElement getMethodElement(TypeElement typeElement, String methodName) {
        ExecutableElement methodElement = null;
        for (Element element : typeElement.getEnclosedElements()) {
            if (element instanceof ExecutableElement && element.getSimpleName().toString().equals(methodName)) {
                methodElement = (ExecutableElement) element;
                break;
            }
        }
        return methodElement;
    }

    public static VariableElement getParameterElement(TypeElement typeElement, String methodName, int paraIndex) {
        ExecutableElement method = getMethodElement(typeElement, methodName);
        assert method != null;
        return method.getParameters().get(paraIndex);
    }
}
