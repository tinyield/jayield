package org.jayield.primitives.intgr;

import org.jayield.boxes.BoolBox;
import org.testng.annotations.Test;

import static org.testng.Assert.assertFalse;

public class IntAdvancerTest {

    @Test()
    public void testEmptyNext() {
        assertFalse(IntAdvancer.empty().tryAdvance(item -> { throw new AssertionError("Should not yield any item!"); }));
    }

    @Test()
    public void testEmptyTraverse() {
        BoolBox box = new BoolBox();
        IntTraverser.empty().traverse(i -> box.set());
        assertFalse(box.isTrue());
    }

}
