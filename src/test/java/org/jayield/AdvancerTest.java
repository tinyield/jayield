/*
 * Copyright (c) 2017, Fernando Miguel Carvalho, mcarvalho@cc.isel.ipl.pt
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jayield;

import org.testng.annotations.Test;

import java.util.Optional;
import java.util.function.Supplier;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * @author Miguel Gamboa
 *         created on 06-07-2017
 */
public class AdvancerTest {
    @Test
    public void testIndividuallyMapFilter() {
        Integer[] arrange = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        Series<Integer> nrs = Series.of(arrange);
        String actual = nrs
                .filter(n -> n%2 != 0)
                .map(Object::toString)
                .skip(2)
                .findFirst()
                .get();
        assertEquals(actual, "5");
    }

    @Test
    public void testIndividuallyMapFilterOdd() {
        Integer[] arrange = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        Series<Integer> nrs = Series.of(arrange);
        String actual = nrs
                .filter(n -> n%2 != 0)
                .map(Object::toString)
                .<String>advanceWith(prev -> yield -> {
                    if(!prev.tryAdvance(item -> {})) return false;
                    return prev.tryAdvance(yield);
                })
                .findFirst()
                .get();
        assertEquals(actual, "3");
    }

    @Test
    public void testIndividuallyFilterMapAnyMatch() {
        Integer[] arrange = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        Series<Integer> nrs = Series.of(arrange);
        boolean actual = nrs
                .filter(n -> n%2 != 0)
                .map(Object::toString)
                .anyMatch(n -> n.equals("9"));
        assertTrue(actual);
    }

    @Test
    public void testIndividuallyFlatMap() {
        Integer[] arrange = {2, 5, 8};
        int actual = Series
                .of(arrange)
                .flatMap(nr -> Series.of(nr - 1, nr, nr + 1))
                .findFirst()
                .get();
        assertEquals(1, actual);
    }

    @Test
    public void testIndividuallyFirstOnEmpty() {
        String[] arrange = {};
        Optional<String> actual = Series
                .of(arrange)
                .findFirst();
        assertTrue(!actual.isPresent());
    }

    @Test
    public void testIndividuallyDistinctCount() {
        String [] expected = {"a", "x", "v", "d", "g", "j", "y", "r", "w", "e"};
        String [] arrange =
                {"a", "x", "v", "d","g", "x", "j", "x", "y","r", "y", "w", "y", "a", "e"};
        Supplier<Series<String>> sup = () -> Series.of(arrange).distinct();
        for (int i = 0; i < expected.length; i++) {
            assertEquals(sup.get().skip(i).findFirst().get(), expected[i]);
        }
    }

}
