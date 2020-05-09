package org.jayield.primitives.intgr;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertThrows;

import java.util.NoSuchElementException;

import org.jayield.boxes.BoolBox;
import org.jayield.primitives.dbl.DoubleAdvancer;
import org.testng.annotations.Test;

public class IntAdvancerTest {

    @Test()
    public void testEmptyNext() {
        assertThrows(NoSuchElementException.class, () -> IntAdvancer.empty().next());
    }

    @Test()
    public void testEmptyTraverse() {
        BoolBox box = new BoolBox();
        IntAdvancer.empty().traverse(i -> box.set());
        assertFalse(box.isTrue());
    }

}
