package org.jayield;

import org.jayield.boxes.BoolBox;
import org.testng.annotations.Test;

import static org.testng.Assert.assertFalse;

public class AdvancerTest {

    @Test()
    public void testEmptyNext() {
        assertFalse(Advancer.empty().tryAdvance(item -> { throw new AssertionError("Should not yield any item!"); }));
    }

    @Test()
    public void testEmptyTraverse() {
        BoolBox box = new BoolBox();
        Traverser.empty().traverse(i -> box.set());
        assertFalse(box.isTrue());
    }

}
