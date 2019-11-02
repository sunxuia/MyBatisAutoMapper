package net.sunxu.mybatis.automapper.processor.util;

import org.junit.Test;

import static net.sunxu.mybatis.automapper.processor.util.AutoMapperException.newException;
import static net.sunxu.mybatis.automapper.processor.util.AutoMapperException.wrapException;
import static org.junit.Assert.*;


public class AutoMapperExceptionTests {
    @Test
    public void newException_nonNullMessage_getMessage() {
        RuntimeException exception = newException("testMessage %s", "parameter");

        assertEquals("testMessage parameter", exception.getMessage());
    }

    @Test
    public void wrapException_runnableThrowException_throwWrappedException() {
        Exception newException = new Exception();
        AutoMapperException.RunnableWithException runnable = () -> {
            throw newException;
        };

        try {
            wrapException(runnable);
        } catch (RuntimeException err) {
            assertEquals(newException, err.getCause());
        } catch (Exception e) {
            fail("failed to throw a RuntimeException.");
        }
    }

    @Test
    public void wrapException_supplierThrowException_throwWrappedException() {
        Exception newException = new Exception();
        AutoMapperException.SupplierWithException supplier = () -> {
            throw newException;
        };

        try {
            wrapException(supplier);
        } catch (RuntimeException err) {
            assertEquals(newException, err.getCause());
        } catch (Exception e) {
            fail("failed to throw a RuntimeException.");
        }
    }

    @Test
    public void wrapException_supplierNoThrowException_getValue() {
        Integer value = Integer.valueOf(128);
        AutoMapperException.SupplierWithException<Integer> supplier = () -> value;

        Integer res = wrapException(supplier);

        assertTrue(value == res);
    }
}
