package org.jayield.primitives.lng;

import org.jayield.boxes.BoolBox;
import org.testng.annotations.Test;

import static org.testng.Assert.assertFalse;

public class LongAdvancerTest {

    @Test()
    public void testEmptyNext() {
        assertFalse(LongAdvancer.empty().tryAdvance(item -> { throw new AssertionError("Should not yield any item!"); }));
    }

    @Test()
    public void testEmptyTraverse() {
        BoolBox box = new BoolBox();
        LongTraverser.empty().traverse(i -> box.set());
        assertFalse(box.isTrue());
    }

}
