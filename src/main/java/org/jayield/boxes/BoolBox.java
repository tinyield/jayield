package org.jayield.boxes;

/**
 * @author Miguel Gamboa
 *         created on 06-07-2017
 */
public class BoolBox {
    private boolean value;

    public boolean isTrue() {
        return value;
    }

    public boolean isFalse() {
        return !value;
    }

    public void set() {
        value = true;
    }

    public void reset() {
        value = false;
    }
}
