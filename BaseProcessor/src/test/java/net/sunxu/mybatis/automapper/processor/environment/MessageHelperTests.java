package net.sunxu.mybatis.automapper.processor.environment;

import org.junit.Before;
import org.junit.Test;

import javax.annotation.processing.Messager;
import javax.tools.Diagnostic;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;


public class MessageHelperTests {
    private Messager messager;
    private MessageHelper helper;

    @Before
    public void setup() {
        messager = mock(Messager.class);
        helper = new MessageHelper(messager);
    }

    @Test
    public void info_message_passedToMessager() {
        helper.info("test %s", "a");

        verify(messager).printMessage(Diagnostic.Kind.NOTE, "test a");
    }

    @Test
    public void warning_message_passedToMessager(){
        helper.warning("test %s", "a");

        verify(messager).printMessage(Diagnostic.Kind.WARNING, "test a");
    }

    @Test
    public void error_message_passedToMessager(){
        helper.error("test %s", "a");

        verify(messager).printMessage(Diagnostic.Kind.ERROR, "test a");
    }
}
