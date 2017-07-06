package org.jayield.boxes;

/**
 * @author Miguel Gamboa
 *         created on 06-07-2017
 */
public class IntBox {
    private int value;
    private boolean isPresent;

    public IntBox() {
        this(Integer.MIN_VALUE, false);
    }

    public IntBox(int value) {
        this(value, true);
    }

    public IntBox(int value, boolean isPresent) {
        this.value = value;
        this.isPresent = isPresent;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public final int inc() {
        return ++value;
    }

    public boolean isPresent() {
        return isPresent;
    }

    public void turnPresent(int value) {
        this.value = value;
        isPresent = true;
    }
}
