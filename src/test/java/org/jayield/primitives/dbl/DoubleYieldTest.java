package org.jayield.primitives.dbl;

import static org.testng.AssertJUnit.assertTrue;

import org.jayield.boxes.BoolBox;
import org.testng.annotations.Test;

/**
 * Test that generic ret calls primitive ret
 *
 * @author Miguel Gamboa
 * created on 03-06-2017
 */
public class DoubleYieldTest {

    @Test
    public void testGenericRetCallsPrimitive() {
        Double input = 1.0;
        BoolBox called = new BoolBox();
        DoubleYield yld = i -> called.set();
        yld.ret(input);
        assertTrue(called.isTrue());
    }
}
