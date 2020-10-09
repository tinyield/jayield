package org.jayield.primitives.dbl;

import org.jayield.boxes.BoolBox;
import org.testng.annotations.Test;

import static org.testng.Assert.assertFalse;

public class DoubleAdvancerTest {

    @Test()
    public void testEmptyNext() {
        assertFalse(DoubleAdvancer.empty().tryAdvance(item -> { throw new AssertionError("Should not yield any item!"); }));
    }

    @Test()
    public void testEmptyTraverse() {
        BoolBox box = new BoolBox();
        DoubleTraverser.empty().traverse(i -> box.set());
        assertFalse(box.isTrue());
    }

}
