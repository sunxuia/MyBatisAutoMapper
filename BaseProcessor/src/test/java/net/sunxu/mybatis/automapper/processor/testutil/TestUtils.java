package net.sunxu.mybatis.automapper.processor.testutil;

import org.junit.Assert;

import java.util.Collection;
import java.util.Map;

import static java.lang.String.format;
import static net.sunxu.mybatis.automapper.processor.testutil.TestHelpUtils.convertToArray;
import static org.junit.Assert.assertArrayEquals;


public class TestUtils {

    public static void assertArrayEqualsUnordered(Object[] expects, Object[] actual) {
        if (isAddressEquals(expects, actual)) return;
        if (expects.length != actual.length) {
            fail("length not equal , expect : %d, actual : %d", expects.length, actual.length);
        }
        final int length = expects.length;
        boolean[] visited = new boolean[length];
        for (int i = 0; i < length; i++) {
            for (int j = 0; j < length; j++) {
                if (!visited[j] && isEqual(expects[i], actual[j])) {
                    visited[j] = true;
                    break;
                }
            }
        }
        StringBuilder errorMsgBuilder = new StringBuilder(1);
        for (int i = 0; i < length; i++) {
            if (!visited[i]) {
                errorMsgBuilder.append(format("[%d][%s], ", i, actual[i]));
            }
        }
        if (errorMsgBuilder.length() == 0) return;
        errorMsgBuilder.insert(0, "Actual not exist in expects : ");
        fail(reduceEnd(errorMsgBuilder, 2).toString());
    }

    private static boolean isAddressEquals(Object expected, Object actual) {
        if (expected == actual) {
            return true;
        }
        if (expected == null) {
            fail("Expect null, actual is not null.");
            return true;
        }
        if (expected == null) {
            fail("Expect not null, actual is null.");
            return true;
        }
        return false;
    }

    private static void fail(String failMessage, Object... paras) {
        if (paras.length > 0) {
            failMessage = format(failMessage, paras);
        }
        Assert.fail(failMessage);
    }

    private static boolean isEqual(Object expeced, Object actual) {
        return expeced == actual ||
                (!(expeced == null || actual == null) &&
                        expeced.equals(actual));
    }

    private static StringBuilder reduceEnd(StringBuilder stringBuilder, int length) {
        stringBuilder.setLength(stringBuilder.length() - length);
        return stringBuilder;
    }

    public static <T extends Exception> void assertThrowException(Runnable runnable,
                                                                  Class<T> expectedException) {
        try {
            runnable.run();
        } catch (Exception err) {
            if (!expectedException.isAssignableFrom(err.getClass())) {
                fail("expected exception [%s], actual is [%s]",
                        expectedException.getName(), err.getClass().getName());
            }
            return;
        }
        fail("expected exception [%s] not thrown", expectedException.getName());
    }

    public static <T extends Exception> void assertThrowException(TestException.RunnableWithException runnable,
                                                                  Class<T> expectedException,
                                                                  String expectedMessage,
                                                                  Object... paras) {
        try {
            runnable.run();
        } catch (Exception err) {
            if (!expectedException.isAssignableFrom(err.getClass())) {
                fail("exception expect [%s], actual [%s]",
                        expectedException.getName(),
                        err.getClass().getName());
            }
            if (paras.length > 0) {
                expectedMessage = String.format(expectedMessage, paras);
            }
            if (!err.getMessage().equals(expectedMessage)) {
                fail("exception message expected [%s], actual [%s]",
                        expectedMessage,
                        err.getMessage());
            }
            return;
        }
        fail("expected exception [%s] not thrown", expectedException.getName());
    }

    public static void assertSame(Object expected, Object actual) {
        if (!isAddressEquals(expected, actual)) {
            fail("expect [%s] and actual [%s] is not one", expected, actual);
        }
    }

    public static <T extends Comparable> void assertSorted(T[] values) {
        for (int i = 1; i < values.length; i++) {
            if (values[i - 1].compareTo(values[i]) > 0) {
                StringBuilder stringBuilder = new StringBuilder();
                for (T value : values) stringBuilder.append(value).append("; ");
                fail("array not sorted. [%d][%s] : [%d][%s]\n%s",
                        i - 1, values[i - 1], i, values[i], reduceEnd(stringBuilder, 2).toString());
            }
        }
    }

    public static <T> void assertInCollection(T[] expects, T actual) {
        for (T expect : expects) {
            if (expect == actual ||
                    actual != null && actual.equals(expect)) {
                return;
            }
        }
        fail("cannot find [%s] in expects.", actual);
    }

    public static void assertMapEquals(Map expect, Map actual) {
        assertMapEquals("", expect, actual);
    }

    public static void assertMapEquals(String message, Map expect, Map actual) {
        if (isAddressEquals(expect, actual)) {
            return;
        }
        if (expect.size() != actual.size()) {
            fail(message + "expect [%d] and actual [%d] size not equal", expect.size(), actual.size());
        }
        expect.forEach((key, value1) -> {
            Object value2 = actual.get(key);
            if (!isEqual(value1, value2)) {
                fail(message + "key [%s] not equal expect [%s] actual [%s]", key, value1, value2);
            }
        });
    }

    public static void assertCollectionEqualsUnordered(Collection actual, Object... expected) {
        Object[] actualArray = convertToArray(actual);
        assertArrayEqualsUnordered(expected, actualArray);
    }

    public static void assertCollectionEquals(Collection actual, Object... expected) {
        if (actual == null) {
            fail("actual collection is null");
        }
        Object[] actualArray = convertToArray(actual);
        assertArrayEquals("Collection not equal.", expected, actualArray);
    }
}
