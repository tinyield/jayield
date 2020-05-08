package org.jayield.primitives.intgr;

import static org.testng.AssertJUnit.assertTrue;

import org.jayield.boxes.BoolBox;
import org.testng.annotations.Test;

/**
 * Test that generic ret calls primitive ret
 *
 * @author Miguel Gamboa
 * created on 03-06-2017
 */
public class IntYieldTest {

    @Test
    public void testGenericRetCallsPrimitive() {
        Integer input = 1;
        BoolBox called = new BoolBox();
        IntYield yld = i -> called.set();
        yld.ret(input);
        assertTrue(called.isTrue());
    }
}
