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

package org.jayield.primitives.intgr;

import static org.jayield.primitives.intgr.IntQuery.of;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;
import static org.testng.internal.junit.ArrayAsserts.assertArrayEquals;

import org.jayield.Query;
import org.jayield.boxes.IntBox;
import org.jayield.primitives.dbl.DoubleQuery;
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
public class IntQueryIterateTest {


    @Test
    public void testMapFilter() {
        int[] expected = {2, 4, 6};
        int[] source = {1, 1, 1, 2, 3, 4, 5, 6};
        IntQuery nrs = IntQuery.of(source);
        IntQuery pipe = nrs
                .map(n -> n * 2)
                .filter(n -> n <= 6)
                .skip(2);
        int[] actual = new int[expected.length];
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
        int[] expected = {1, 3, 5};
        int[] source = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        IntQuery nrs = IntQuery.of(source);
        IntQuery pipe = nrs
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
        int[] actual = new int[expected.length];
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
        int[] expected = {7, 8, 9, 11, 7};
        int[] source = {7, 7, 8, 9, 9, 11, 11, 7};
        int[] actual = new int[expected.length];
        IntQuery pipe = IntQuery
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
        int[] expected = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        int[] source = {2, 5, 8};
        int[] actual = new int[expected.length];
        IntQuery pipe = IntQuery
                .of(source)
                .flatMap(nr -> IntQuery.of(nr - 1, nr, nr + 1));
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
        int[] expected = {7, 8, 9, 11};
        int[] source = {7, 7, 8, 9, 9, 11, 11, 7};
        int[] actual = new int[expected.length];
        IntQuery pipe = IntQuery
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
        int[] expected = {1, 3, 5, 7, 9, 11, 13};
        int[] actual = new int[expected.length];
        IntQuery pipe = IntQuery
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
        int[] expected = {2, 4, 6, 8};
        int[] source = {1, 2, 3, 4};
        int[] actual = new int[expected.length];
        IntBox index = new IntBox();
        index.setValue(0);
        IntQuery pipe = IntQuery
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
        int[] expected = {4, 8};
        int[] source = {2, 4, 5, 8};
        int[] actual = new int[expected.length];
        IntBox index = new IntBox();
        index.setValue(0);
        IntQuery pipe = IntQuery
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
        int[] expected = {3, 4, 5, 6, 7, 8, 9};
        int[] actual = new int[expected.length];
        int[] n = new int[]{1};
        IntQuery pipe = IntQuery
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
        int[] expected = {1, 2, 3, 4};
        int[] source1 = {1, 2};
        int[] source2 = {3, 4};
        int[] actual = new int[expected.length];
        IntQuery pipe = IntQuery.of(source1).concat(IntQuery.of(source2));
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
        int[] expected = {7, 8, 9, 11};
        int[] source = {11, 7, 9, 8};
        int[] actual = new int[expected.length];
        IntQuery pipe = IntQuery
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
        int[] expected = {delimiter, 11};
        int[] source = {8, 7, delimiter, 11};
        int[] actual = new int[expected.length];
        IntQuery pipe = IntQuery
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
        int[] expected = {4, 4, 4};
        int[] source1 = {1, 2, 3};
        int[] source2 = {3, 2, 1};
        int[] actual = new int[expected.length];
        IntQuery pipe = of(source1)
                .zip(of(source2), Integer::sum);
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
        int[] source1 = {1, 2, 3};
        int[] source2 = {3, 2, 1};
        String[] actual = new String[expected.length];
        Query<String> pipe = of(source1)
                .zip(of(source2), Integer::sum)
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
        int[] source = {11, 7, 9, 8};
        double[] actual = new double[expected.length];
        DoubleQuery pipe = IntQuery
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
    public void testMapToLong() {
        long[] expected = {7, 8, 9, 11};
        int[] source = {11, 7, 9, 8};
        long[] actual = new long[expected.length];
        LongQuery pipe = IntQuery
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
