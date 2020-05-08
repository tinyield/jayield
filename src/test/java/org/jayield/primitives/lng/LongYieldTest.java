package org.jayield.primitives.lng;

import static org.testng.AssertJUnit.assertTrue;

import org.jayield.boxes.BoolBox;
import org.testng.annotations.Test;

/**
 * Test that generic ret calls primitive ret
 *
 * @author Miguel Gamboa
 * created on 03-06-2017
 */
public class LongYieldTest {

    @Test
    public void testGenericRetCallsPrimitive() {
        Long input = 1L;
        BoolBox called = new BoolBox();
        LongYield yld = i -> called.set();
        yld.ret(input);
        assertTrue(called.isTrue());
    }
}
