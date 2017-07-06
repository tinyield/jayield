package org.jayield.boxes;

import java.util.function.UnaryOperator;

/**
 * @author Miguel Gamboa
 *         created on 06-07-2017
 */
public class Box<T> {
    private T value;
    private final UnaryOperator<T> inc;
    private boolean isPresent;

    public Box() {
        this(null, null, false);
    }

    public Box(T value) {
        this(value, null);
    }

    public Box(T value, UnaryOperator<T> inc) {
        this(value, inc, true);
    }

    protected Box(T value, UnaryOperator<T> inc, boolean isPresent) {
        this.value = value;
        this.inc = inc;
        this.isPresent = isPresent;
    }

    public boolean isPresent() {
        return isPresent;
    }

    public T getValue() {
        return value;
    }

    public T setValue(T value) {
        return this.value = value;
    }

    public final void inc() {
        value = inc.apply(value);
    }

    public void turnPresent(T e) {
        this.setValue(e);
        this.isPresent = true;
    }
}
