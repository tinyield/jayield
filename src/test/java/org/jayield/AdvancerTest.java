package org.jayield;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertThrows;

import java.util.NoSuchElementException;

import org.jayield.boxes.BoolBox;
import org.jayield.primitives.lng.LongAdvancer;
import org.testng.annotations.Test;

public class AdvancerTest {

    @Test()
    public void testEmptyNext() {
        assertThrows(NoSuchElementException.class, () -> Advancer.empty().next());
    }

    @Test()
    public void testEmptyTraverse() {
        BoolBox box = new BoolBox();
        Advancer.empty().traverse(i -> box.set());
        assertFalse(box.isTrue());
    }

}
