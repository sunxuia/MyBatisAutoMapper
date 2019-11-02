package net.sunxu.mybatis.automapper.processor.environment;

import javax.annotation.processing.Messager;
import javax.tools.Diagnostic;


public class MessageHelper {
    private final Messager messager;

    public MessageHelper(Messager messager) {
        this.messager = messager;
    }

    public void info(String message, Object... paras) {
        messager.printMessage(Diagnostic.Kind.NOTE, format(message, paras));
    }

    private String format(String message, Object... paras) {
        if (paras.length > 0)
            message = String.format(message, paras);
        return message;
    }

    public void warning(String message, Object... paras) {
        messager.printMessage(Diagnostic.Kind.WARNING, format(message, paras));
    }

    public void error(String message, Object... paras) {
        message = format(message, paras);
        messager.printMessage(Diagnostic.Kind.ERROR, message);
    }
}
