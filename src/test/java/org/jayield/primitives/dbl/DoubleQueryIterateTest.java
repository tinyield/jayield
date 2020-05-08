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

package org.jayield.primitives.dbl;

import static org.jayield.primitives.dbl.DoubleQuery.of;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;
import static org.testng.internal.junit.ArrayAsserts.assertArrayEquals;

import org.jayield.Query;
import org.jayield.boxes.IntBox;
import org.jayield.primitives.intgr.IntQuery;
import org.jayield.primitives.lng.LongQuery;
import org.testng.annotations.Test;

/**
 * These tests aim to evaluate only the execution of hasNext() and next()
 * along the entire pipeline.
 * Each operation should forward the computation through the hasNext() and next()
 * methods of the upstream.
 *
 * @author Miguel Gamboa
 * created on 03-06-2017
 */
public class DoubleQueryIterateTest {


    @Test
    public void testMapFilter() {
        double[] expected = {2, 4, 6};
        double[] source = {1, 1, 1, 2, 3, 4, 5, 6};
        DoubleQuery nrs = DoubleQuery.of(source);
        DoubleQuery pipe = nrs
                .map(n -> n * 2)
                .filter(n -> n <= 6)
                .skip(2);
        double[] actual = new double[expected.length];
        int index = 0;
        while (pipe.hasNext()) {
            assertTrue(index < expected.length);
            actual[index++] = pipe.next();
        }
        assertEquals(expected.length, index);
        assertArrayEquals(expected, actual, 0);
    }

    @Test
    public void testBulkMapFilterOdd() {
        double[] expected = {1, 3, 5};
        double[] source = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        DoubleQuery nrs = DoubleQuery.of(source);
        DoubleQuery pipe = nrs
                .filter(n -> n < 7)
                .map(n -> n - 1)
                .then(prev -> yield -> {
                    final boolean[] isOdd = {false};
                    prev.traverse(item -> {
                        if (isOdd[0]) {
                            yield.ret(item);
                        }
                        isOdd[0] = !isOdd[0];
                    });
                });
        double[] actual = new double[expected.length];
        int index = 0;
        while (pipe.hasNext()) {
            assertTrue(index < expected.length);
            actual[index++] = pipe.next();
        }
        assertEquals(expected.length, index);
        assertArrayEquals(expected, actual, 0);
    }

    @Test
    public void testMapCollapse() {
        double[] expected = {7, 8, 9, 11, 7};
        double[] source = {7, 7, 8, 9, 9, 11, 11, 7};
        double[] actual = new double[expected.length];
        DoubleQuery pipe = DoubleQuery
                .of(source)
                .then(UserExt::collapse);
        int index = 0;
        while (pipe.hasNext()) {
            assertTrue(index < expected.length);
            actual[index++] = pipe.next();
        }
        assertEquals(expected.length, index);
        assertArrayEquals(expected, actual, 0);
    }


    @Test
    public void testFlatMap() {
        double[] expected = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        double[] source = {2, 5, 8};
        double[] actual = new double[expected.length];
        DoubleQuery pipe = DoubleQuery
                .of(source)
                .flatMap(nr -> DoubleQuery.of(nr - 1, nr, nr + 1));
        int index = 0;
        while (pipe.hasNext()) {
            assertTrue(index < expected.length);
            actual[index++] = pipe.next();
        }
        assertEquals(expected.length, index);
        assertArrayEquals(expected, actual, 0);
    }

    @Test
    public void testDistinct() {
        double[] expected = {7, 8, 9, 11};
        double[] source = {7, 7, 8, 9, 9, 11, 11, 7};
        double[] actual = new double[expected.length];
        DoubleQuery pipe = DoubleQuery
                .of(source)
                .distinct();
        int index = 0;
        while (pipe.hasNext()) {
            assertTrue(index < expected.length);
            actual[index++] = pipe.next();
        }
        assertEquals(expected.length, index);
        assertArrayEquals(expected, actual, 0);
    }

    @Test
    public void testIterateLimit() {
        double[] expected = {1, 3, 5, 7, 9, 11, 13};
        double[] actual = new double[expected.length];
        DoubleQuery pipe = DoubleQuery
                .iterate(1, n -> n + 2)
                .limit(7);
        int index = 0;
        while (pipe.hasNext()) {
            assertTrue(index < expected.length);
            actual[index++] = pipe.next();
        }
        assertEquals(expected.length, index);
        assertArrayEquals(expected, actual, 0);
    }


    @Test
    public void testPeekCount() {
        double[] expected = {2, 4, 6, 8};
        double[] source = {1, 2, 3, 4};
        double[] actual = new double[expected.length];
        IntBox index = new IntBox();
        index.setValue(0);
        DoubleQuery pipe = DoubleQuery
                .of(source)
                .peek(item -> {
                    actual[index.getValue()] = item * 2;
                    index.setValue(index.getValue() + 1);
                });
        int count = 0;
        while (pipe.hasNext()) {
            pipe.next();
            count++;
        }
        assertEquals(expected.length, count);
        assertEquals(expected.length, index.getValue());
        assertArrayEquals(expected, actual, 0);
    }

    @Test
    public void testTakeWhileCount() {
        double[] expected = {4, 8};
        double[] source = {2, 4, 5, 8};
        double[] actual = new double[expected.length];
        IntBox index = new IntBox();
        index.setValue(0);
        DoubleQuery pipe = DoubleQuery
                .of(source)
                .takeWhile(i -> i % 2 == 0)
                .peek(item -> {
                    actual[index.getValue()] = item * 2;
                    index.setValue(index.getValue() + 1);
                });
        int count = 0;
        while (pipe.hasNext()) {
            pipe.next();
            count++;
        }
        assertEquals(expected.length, count);
        assertEquals(expected.length, index.getValue());
        assertArrayEquals(expected, actual, 0);
    }

    @Test
    public void testGenerateLimitLast() {
        double[] expected = {3, 4, 5, 6, 7, 8, 9};
        double[] actual = new double[expected.length];
        double[] n = new double[]{1};
        DoubleQuery pipe = DoubleQuery
                .generate(() -> 2 + n[0]++)
                .limit(7);
        int index = 0;
        while (pipe.hasNext()) {
            assertTrue(index < expected.length);
            actual[index++] = pipe.next();
        }
        assertEquals(expected.length, index);
        assertArrayEquals(expected, actual, 0);
    }

    @Test
    public void testConcat() {
        double[] expected = {1, 2, 3, 4};
        double[] source1 = {1, 2};
        double[] source2 = {3, 4};
        double[] actual = new double[expected.length];
        DoubleQuery pipe = DoubleQuery.of(source1).concat(DoubleQuery.of(source2));
        int index = 0;
        while (pipe.hasNext()) {
            assertTrue(index < expected.length);
            actual[index++] = pipe.next();
        }
        assertEquals(expected.length, index);
        assertArrayEquals(expected, actual, 0);
    }

    @Test
    public void testSorted() {
        double[] expected = {7, 8, 9, 11};
        double[] source = {11, 7, 9, 8};
        double[] actual = new double[expected.length];
        DoubleQuery pipe = DoubleQuery
                .of(source)
                .sorted();
        int index = 0;
        while (pipe.hasNext()) {
            assertTrue(index < expected.length);
            actual[index++] = pipe.next();
        }
        assertEquals(expected.length, index);
        assertArrayEquals(expected, actual, 0);
    }

    @Test
    public void testDropWhile() {
        int delimiter = 9;
        double[] expected = {delimiter, 11};
        double[] source = {8, 7, delimiter, 11};
        double[] actual = new double[expected.length];
        DoubleQuery pipe = DoubleQuery
                .of(source)
                .dropWhile(i -> delimiter != i);
        int index = 0;
        while (pipe.hasNext()) {
            assertTrue(index < expected.length);
            actual[index++] = pipe.next();
        }
        assertEquals(expected.length, index);
        assertArrayEquals(expected, actual, 0);
    }

    @Test
    public void testZip() {
        double[] expected = {4, 4, 4};
        double[] source1 = {1, 2, 3};
        double[] source2 = {3, 2, 1};
        double[] actual = new double[expected.length];
        DoubleQuery pipe = of(source1)
                .zip(of(source2), Double::sum);
        int index = 0;
        while (pipe.hasNext()) {
            assertTrue(index < expected.length);
            actual[index++] = pipe.next();
        }
        assertEquals(expected.length, index);
        assertArrayEquals(expected, actual, 0);
    }

    @Test
    public void testMapToObj() {
        String[] expected = {"4.0", "4.0", "4.0"};
        double[] source1 = {1, 2, 3};
        double[] source2 = {3, 2, 1};
        String[] actual = new String[expected.length];
        Query<String> pipe = of(source1)
                .zip(of(source2), Double::sum)
                .mapToObj(String::valueOf);
        int index = 0;
        while (pipe.hasNext()) {
            assertTrue(index < expected.length);
            actual[index++] = pipe.next();
        }
        assertEquals(expected.length, index);
        assertArrayEquals(expected, actual);
    }

    @Test
    public void testMapToInt() {
        int[] expected = {7, 8, 9, 11};
        double[] source = {11, 7, 9, 8};
        int[] actual = new int[expected.length];
        IntQuery pipe = DoubleQuery
                .of(source)
                .sorted()
                .asIntQuery();
        int index = 0;
        while (pipe.hasNext()) {
            assertTrue(index < expected.length);
            actual[index++] = pipe.next();
        }
        assertEquals(expected.length, index);
        assertArrayEquals(expected, actual);
    }


    @Test
    public void testMapToLong() {
        long[] expected = {7, 8, 9, 11};
        double[] source = {11, 7, 9, 8};
        long[] actual = new long[expected.length];
        LongQuery pipe = DoubleQuery
                .of(source)
                .sorted()
                .asLongQuery();
        int index = 0;
        while (pipe.hasNext()) {
            assertTrue(index < expected.length);
            actual[index++] = pipe.next();
        }
        assertEquals(expected.length, index);
        assertArrayEquals(expected, actual);
    }
}
