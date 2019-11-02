package net.sunxu.mybatis.automapper.processor.util;


public class ObjectHolder<T> {
    public ObjectHolder() {}

    public ObjectHolder(T value) {
        this.value = value;
    }

    private T value;

    private boolean valueSetted;

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        valueSetted = true;
        this.value = value;
    }

    public boolean isValueSetted() {
        return valueSetted;
    }

    public String toString() {
        return "" + value;
    }
}
