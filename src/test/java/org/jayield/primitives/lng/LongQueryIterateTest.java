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

package org.jayield.primitives.lng;

import static org.jayield.primitives.lng.LongQuery.of;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;
import static org.testng.internal.junit.ArrayAsserts.assertArrayEquals;

import org.jayield.Query;
import org.jayield.boxes.IntBox;
import org.jayield.primitives.dbl.DoubleQuery;
import org.jayield.primitives.intgr.IntQuery;
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
public class LongQueryIterateTest {


    @Test
    public void testMapFilter() {
        long[] expected = {2, 4, 6};
        long[] source = {1, 1, 1, 2, 3, 4, 5, 6};
        LongQuery nrs = LongQuery.of(source);
        LongQuery pipe = nrs
                .map(n -> n * 2)
                .filter(n -> n <= 6)
                .skip(2);
        long[] actual = new long[expected.length];
        int index = 0;
        while (pipe.hasNext()) {
            assertTrue(index < expected.length);
            actual[index++] = pipe.next();
        }
        assertEquals(expected.length, index);
        assertArrayEquals(expected, actual);
    }

    @Test
    public void testBulkMapFilterOdd() {
        long[] expected = {1, 3, 5};
        long[] source = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        LongQuery nrs = LongQuery.of(source);
        LongQuery pipe = nrs
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
        long[] actual = new long[expected.length];
        int index = 0;
        while (pipe.hasNext()) {
            assertTrue(index < expected.length);
            actual[index++] = pipe.next();
        }
        assertEquals(expected.length, index);
        assertArrayEquals(expected, actual);
    }

    @Test
    public void testMapCollapse() {
        long[] expected = {7, 8, 9, 11, 7};
        long[] source = {7, 7, 8, 9, 9, 11, 11, 7};
        long[] actual = new long[expected.length];
        LongQuery pipe = LongQuery
                .of(source)
                .then(UserExt::collapse);
        int index = 0;
        while (pipe.hasNext()) {
            assertTrue(index < expected.length);
            actual[index++] = pipe.next();
        }
        assertEquals(expected.length, index);
        assertArrayEquals(expected, actual);
    }


    @Test
    public void testFlatMap() {
        long[] expected = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        long[] source = {2, 5, 8};
        long[] actual = new long[expected.length];
        LongQuery pipe = LongQuery
                .of(source)
                .flatMap(nr -> LongQuery.of(nr - 1, nr, nr + 1));
        int index = 0;
        while (pipe.hasNext()) {
            assertTrue(index < expected.length);
            actual[index++] = pipe.next();
        }
        assertEquals(expected.length, index);
        assertArrayEquals(expected, actual);
    }

    @Test
    public void testDistinct() {
        long[] expected = {7, 8, 9, 11};
        long[] source = {7, 7, 8, 9, 9, 11, 11, 7};
        long[] actual = new long[expected.length];
        LongQuery pipe = LongQuery
                .of(source)
                .distinct();
        int index = 0;
        while (pipe.hasNext()) {
            assertTrue(index < expected.length);
            actual[index++] = pipe.next();
        }
        assertEquals(expected.length, index);
        assertArrayEquals(expected, actual);
    }

    @Test
    public void testIterateLimit() {
        long[] expected = {1, 3, 5, 7, 9, 11, 13};
        long[] actual = new long[expected.length];
        LongQuery pipe = LongQuery
                .iterate(1, n -> n + 2)
                .limit(7);
        int index = 0;
        while (pipe.hasNext()) {
            assertTrue(index < expected.length);
            actual[index++] = pipe.next();
        }
        assertEquals(expected.length, index);
        assertArrayEquals(expected, actual);
    }


    @Test
    public void testPeekCount() {
        long[] expected = {2, 4, 6, 8};
        long[] source = {1, 2, 3, 4};
        long[] actual = new long[expected.length];
        IntBox index = new IntBox();
        index.setValue(0);
        LongQuery pipe = LongQuery
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
        assertArrayEquals(expected, actual);
    }

    @Test
    public void testTakeWhileCount() {
        long[] expected = {4, 8};
        long[] source = {2, 4, 5, 8};
        long[] actual = new long[expected.length];
        IntBox index = new IntBox();
        index.setValue(0);
        LongQuery pipe = LongQuery
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
        assertArrayEquals(expected, actual);
    }

    @Test
    public void testGenerateLimitLast() {
        long[] expected = {3, 4, 5, 6, 7, 8, 9};
        long[] actual = new long[expected.length];
        long[] n = new long[]{1};
        LongQuery pipe = LongQuery
                .generate(() -> 2 + n[0]++)
                .limit(7);
        int index = 0;
        while (pipe.hasNext()) {
            assertTrue(index < expected.length);
            actual[index++] = pipe.next();
        }
        assertEquals(expected.length, index);
        assertArrayEquals(expected, actual);
    }

    @Test
    public void testConcat() {
        long[] expected = {1, 2, 3, 4};
        long[] source1 = {1, 2};
        long[] source2 = {3, 4};
        long[] actual = new long[expected.length];
        LongQuery pipe = LongQuery.of(source1).concat(LongQuery.of(source2));
        int index = 0;
        while (pipe.hasNext()) {
            assertTrue(index < expected.length);
            actual[index++] = pipe.next();
        }
        assertEquals(expected.length, index);
        assertArrayEquals(expected, actual);
    }

    @Test
    public void testSorted() {
        long[] expected = {7, 8, 9, 11};
        long[] source = {11, 7, 9, 8};
        long[] actual = new long[expected.length];
        LongQuery pipe = LongQuery
                .of(source)
                .sorted();
        int index = 0;
        while (pipe.hasNext()) {
            assertTrue(index < expected.length);
            actual[index++] = pipe.next();
        }
        assertEquals(expected.length, index);
        assertArrayEquals(expected, actual);
    }

    @Test
    public void testDropWhile() {
        int delimiter = 9;
        long[] expected = {delimiter, 11};
        long[] source = {8, 7, delimiter, 11};
        long[] actual = new long[expected.length];
        LongQuery pipe = LongQuery
                .of(source)
                .dropWhile(i -> delimiter != i);
        int index = 0;
        while (pipe.hasNext()) {
            assertTrue(index < expected.length);
            actual[index++] = pipe.next();
        }
        assertEquals(expected.length, index);
        assertArrayEquals(expected, actual);
    }

    @Test
    public void testZip() {
        long[] expected = {4, 4, 4};
        long[] source1 = {1, 2, 3};
        long[] source2 = {3, 2, 1};
        long[] actual = new long[expected.length];
        LongQuery pipe = of(source1)
                .zip(of(source2), Long::sum);
        int index = 0;
        while (pipe.hasNext()) {
            assertTrue(index < expected.length);
            actual[index++] = pipe.next();
        }
        assertEquals(expected.length, index);
        assertArrayEquals(expected, actual);
    }

    @Test
    public void testMapToObj() {
        String[] expected = {"4", "4", "4"};
        long[] source1 = {1, 2, 3};
        long[] source2 = {3, 2, 1};
        String[] actual = new String[expected.length];
        Query<String> pipe = of(source1)
                .zip(of(source2), Long::sum)
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
    public void testMapToDouble() {
        double[] expected = {7, 8, 9, 11};
        long[] source = {11, 7, 9, 8};
        double[] actual = new double[expected.length];
        DoubleQuery pipe = LongQuery
                .of(source)
                .sorted()
                .asDoubleQuery();
        int index = 0;
        while (pipe.hasNext()) {
            assertTrue(index < expected.length);
            actual[index++] = pipe.next();
        }
        assertEquals(expected.length, index);
        assertArrayEquals(expected, actual, 0);
    }

    @Test
    public void testMapToInt() {
        int[] expected = {7, 8, 9, 11};
        long[] source = {11, 7, 9, 8};
        int[] actual = new int[expected.length];
        IntQuery pipe = LongQuery
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
}
