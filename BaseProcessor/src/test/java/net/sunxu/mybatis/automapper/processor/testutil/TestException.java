package net.sunxu.mybatis.automapper.processor.testutil;


public class TestException extends RuntimeException {
    public TestException(String message) {
        super(message);
    }

    public TestException(Throwable cause) {
        super(cause);
    }

    public static RuntimeException newException(String message, Object... paras) {
        if (paras.length > 0)
            message = String.format(message, paras);
        return new TestException(message);
    }

    @FunctionalInterface
    public interface RunnableWithException {
        void run() throws Exception;
    }

    public static void wrapException(RunnableWithException runnable) {
        try {
            runnable.run();
        } catch (Exception err) {
            throw new TestException(err);
        }
    }

    @FunctionalInterface
    public interface SupplierWithException<T> {
        T get() throws Exception;
    }

    public static <T> T wrapException(SupplierWithException<T> function) {
        try {
            return function.get();
        } catch (Exception err) {
            throw new TestException(err);
        }
    }
}
