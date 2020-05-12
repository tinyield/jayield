package org.jayield.primitives.lng;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertThrows;

import java.util.NoSuchElementException;

import org.jayield.boxes.BoolBox;
import org.jayield.primitives.intgr.IntAdvancer;
import org.testng.annotations.Test;

public class LongAdvancerTest {

    @Test()
    public void testEmptyNext() {
        assertThrows(NoSuchElementException.class, () -> LongAdvancer.empty().next());
    }

    @Test()
    public void testEmptyTraverse() {
        BoolBox box = new BoolBox();
        LongAdvancer.empty().traverse(i -> box.set());
        assertFalse(box.isTrue());
    }

}
