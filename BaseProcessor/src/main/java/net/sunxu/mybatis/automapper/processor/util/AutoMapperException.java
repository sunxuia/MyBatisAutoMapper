package net.sunxu.mybatis.automapper.processor.util;


public class AutoMapperException extends RuntimeException {
    private AutoMapperException(String message) {
        super(message);
    }

    private AutoMapperException(Throwable cause) {
        super(cause);
    }

    public static AutoMapperException newException(String message, Object... paras) {
        if (paras.length > 0)
            message = String.format(message, paras);
        return new AutoMapperException(message);
    }

    public static AutoMapperException newException(Throwable cause) {
        return new AutoMapperException(cause);
    }

    @FunctionalInterface
    public interface RunnableWithException {
        void run() throws Exception;
    }

    public static void wrapException(RunnableWithException runnable) {
        try {
            runnable.run();
        } catch (Exception err) {
            throw new AutoMapperException(err);
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
            throw new AutoMapperException(err);
        }
    }

    public static <T> T wrapException(SupplierWithException<T> function, String failMessage, Object... paras) {
        try {
            return function.get();
        } catch (Exception err) {
            throw newException(failMessage, paras);
        }
    }
}
