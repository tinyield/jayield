package org.jayield.primitives.dbl;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertThrows;

import java.util.NoSuchElementException;

import org.jayield.boxes.BoolBox;
import org.testng.annotations.Test;

public class DoubleAdvancerTest {

    @Test()
    public void testEmptyNext() {
        assertThrows(NoSuchElementException.class, () -> DoubleAdvancer.empty().next());
    }

    @Test()
    public void testEmptyTraverse() {
        BoolBox box = new BoolBox();
        DoubleAdvancer.empty().traverse(i -> box.set());
        assertFalse(box.isTrue());
    }

}
