package net.sunxu.mybatis.automapper.processor.util;

import javax.lang.model.element.*;
import javax.lang.model.type.TypeMirror;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.*;
import java.util.function.*;

import static com.google.common.base.Strings.isNullOrEmpty;


public class HelpUtils {
    private HelpUtils() { }

    public static <K, V> void forEach(Map<K, V> map, BiConsumer<K, V> action) {
        if (map == null) return;
        map.forEach(action);
    }

    public static <K, V> void forEach(Map<K, V> map, BiPredicate<K, V> action) {
        if (map == null) return;
        for (Map.Entry<K, V> pair : map.entrySet()) {
            boolean isContinue = action.test(pair.getKey(), pair.getValue());
            if (!isContinue) return;
        }
    }

    public static <T> void forEach(Iterable<T> iterable, Consumer<T> action) {
        if (iterable == null) return;
        iterable.forEach(action::accept);
    }

    public static <T> void forEach(Iterable<T> iterable, Predicate<T> action) {
        if (iterable == null) return;
        for (T item : iterable) {
            if (!action.test(item)) {
                return;
            }
        }
    }

    public static <T> void forEach(Iterable<T> iterable, BiConsumer<Integer, T> action) {
        if (iterable == null) return;
        int i = 0;
        for (Iterator<T> it = iterable.iterator(); it.hasNext(); i++) {
            action.accept(i, it.next());
        }
    }

    public static boolean isEquals(Object val1, Object val2) {
        if (val1 == val2) return true;
        if (val1 == null || val2 == null) return false;
        return val1.equals(val2);
    }

    public static String convertToString(Object obj) {
        return obj == null ? null : obj.toString();
    }

    public static String nvl(String text, String backup) {
        return isNullOrEmpty(text) ? backup : text;
    }

    public static String nvl(String str, Supplier<String> backup) {
        return isNullOrEmpty(str) ? backup.get() : str;
    }

    public static <T> T nvl(T val, T backup) {
        return val == null ? backup : val;
    }

    public static <T> T nvl(T val, Supplier<T> backup) {
        return val == null ? backup.get() : val;
    }

    public static <T, R> R ifNotNull(T val, Function<T, R> function) {
        if (val == null) return null;
        return function.apply(val);
    }

    public static StringBuilder reduceEnd(StringBuilder stringBuilder, int len) {
        stringBuilder.setLength(stringBuilder.length() - len);
        return stringBuilder;
    }

    public static String concat(Iterable<String> texts, String interval) {
        StringBuilder sb = new StringBuilder();
        for (String text : texts) {
            sb.append(text).append(interval);
        }
        if (sb.length() > 0) {
            sb.setLength(sb.length() - interval.length());
        }
        return sb.toString();
    }

    public static <T> String concat(Iterable<T> iterable, String interval, Function<T, String> convertor) {
        StringBuilder stringBuilder = new StringBuilder();
        for (T val : iterable) {
            String text = convertor.apply(val);
            stringBuilder.append(text).append(interval);
        }
        if (stringBuilder.length() > 0) {
            reduceEnd(stringBuilder, interval.length());
        }
        return stringBuilder.toString();
    }

    public static <T> boolean existIn(T expected, T... candidates) {
        for (T candidate : candidates) {
            if (candidate == expected ||
                    expected != null && expected.equals(candidate)) {
                return true;
            }
        }
        return false;
    }

    public static String convertToUpperCaseSplitByUnderLine(String name) {
        StringBuilder stringBuilder = new StringBuilder(name.length() + 1);
        boolean isLastCharacterUpperCase = true;
        for (char c : name.toCharArray()) {
            if (Character.isUpperCase(c) && !isLastCharacterUpperCase) {
                stringBuilder.append('_').append(c);
                isLastCharacterUpperCase = true;
            } else {
                isLastCharacterUpperCase = false;
                stringBuilder.append(Character.toUpperCase(c));
            }
        }
        return stringBuilder.toString();
    }

    public static String convertToLowerCaseSplitByUnderLine(String name) {
        StringBuilder stringBuilder = new StringBuilder(name.length() + 1);
        boolean isLastCharacterUpperCase = true;
        for (char c : name.toCharArray()) {
            if (Character.isUpperCase(c) && !isLastCharacterUpperCase) {
                stringBuilder.append('_').append(Character.toLowerCase(c));
                isLastCharacterUpperCase = true;
            } else {
                isLastCharacterUpperCase = false;
                stringBuilder.append(c);
            }
        }
        return stringBuilder.toString();
    }

    public static <R> R visitAnnotationValue(AnnotationMirror annotationMirror,
                                             String methodName,
                                             Supplier<R> defaultValue,
                                             AnnotationVisitor<R> visitor) {
        ObjectHolder<R> res = new ObjectHolder<>();
        forEach(annotationMirror.getElementValues(), (method, value) -> {
            if (!method.getSimpleName().toString().equals(methodName)) {
                return true;
            }
            res.setValue(value.accept(visitor, method));
            return false;
        });
        if (!res.isValueSetted()) {
            res.setValue(defaultValue.get());
        }
        return res.getValue();
    }

    public static abstract class AnnotationVisitor<R> implements AnnotationValueVisitor<R, ExecutableElement> {

        @Override
        public R visit(AnnotationValue av, ExecutableElement method) {
            throw new UnsupportedOperationException("annotation value not supported");
        }

        @Override
        public R visit(AnnotationValue av) {
            throw new UnsupportedOperationException("annotation value not supported");
        }

        @Override
        public R visitBoolean(boolean b, ExecutableElement method) {
            throw new UnsupportedOperationException("annotation value not supported");
        }

        @Override
        public R visitByte(byte b, ExecutableElement method) {
            throw new UnsupportedOperationException("annotation value not supported");
        }

        @Override
        public R visitChar(char c, ExecutableElement method) {
            throw new UnsupportedOperationException("annotation value not supported");
        }

        @Override
        public R visitDouble(double d, ExecutableElement method) {
            throw new UnsupportedOperationException("annotation value not supported");
        }

        @Override
        public R visitFloat(float f, ExecutableElement method) {
            throw new UnsupportedOperationException("annotation value not supported");
        }

        @Override
        public R visitInt(int i, ExecutableElement method) {
            throw new UnsupportedOperationException("annotation value not supported");
        }

        @Override
        public R visitLong(long i, ExecutableElement method) {
            throw new UnsupportedOperationException("annotation value not supported");
        }

        @Override
        public R visitShort(short s, ExecutableElement method) {
            throw new UnsupportedOperationException("annotation value not supported");
        }

        @Override
        public R visitString(String s, ExecutableElement method) {
            throw new UnsupportedOperationException("annotation value not supported");
        }

        @Override
        public R visitType(TypeMirror t, ExecutableElement method) {
            throw new UnsupportedOperationException("annotation value not supported");
        }

        @Override
        public R visitEnumConstant(VariableElement c, ExecutableElement method) {
            throw new UnsupportedOperationException("annotation value not supported");
        }

        @Override
        public R visitAnnotation(AnnotationMirror a, ExecutableElement method) {
            throw new UnsupportedOperationException("annotation value not supported");
        }

        @Override
        public R visitArray(List<? extends AnnotationValue> vals, ExecutableElement method) {
            throw new UnsupportedOperationException("annotation value not supported");
        }

        @Override
        public R visitUnknown(AnnotationValue av, ExecutableElement method) {
            throw new UnsupportedOperationException("annotation value not supported");
        }
    }

    public static String getAnnotationTypeValue(AnnotationMirror annotationMirror,
                                                String methodName,
                                                Supplier<String> defaultValue) {
        return visitAnnotationValue(annotationMirror, methodName, defaultValue,
                new AnnotationVisitor<String>() {
                    @Override
                    public String visitType(TypeMirror t, ExecutableElement method) {
                        return t.toString();
                    }
                });
    }

    public static String getAnnotationTypeValue(AnnotationMirror annotationMirror, String methodName) {
        return getAnnotationTypeValue(annotationMirror, methodName, () -> "");
    }

    public static List<Type> getGenericArgumentPassedToSuperClass(Class<?> childClass, Class<?> baseClass) {
        Map<Type, Type> types = new HashMap<>();
        for (Class clazz = childClass; clazz != baseClass && clazz != null; clazz = clazz.getSuperclass()) {
            Type superType = clazz.getGenericSuperclass();
            if (superType instanceof Class) continue;
            if (!(superType instanceof ParameterizedType)) {
                throw new RuntimeException("unsupported type : " + superType);
            }
            ParameterizedType parameterized = (ParameterizedType) superType;
            Class superClazz = clazz.getSuperclass();
            for (int i = 0; i < superClazz.getTypeParameters().length; i++) {
                types.put(superClazz.getTypeParameters()[i], parameterized.getActualTypeArguments()[i]);
            }
        }

        List<Type> res = new ArrayList<>(baseClass.getTypeParameters().length);
        for (TypeVariable<? extends Class<?>> typeVariable : baseClass.getTypeParameters()) {
            for (Type type = typeVariable; type != null; type = types.get(type)) {
                if (type instanceof Class) {
                    res.add(type);
                    break;
                }
            }
        }
        return res;
    }
}
