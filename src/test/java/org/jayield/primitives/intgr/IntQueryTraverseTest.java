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

import org.jayield.boxes.IntBox;
import org.testng.annotations.Test;

import java.util.IntSummaryStatistics;
import java.util.OptionalInt;
import java.util.PrimitiveIterator;
import java.util.stream.IntStream;

import static org.jayield.primitives.intgr.IntQuery.fromStream;
import static org.jayield.primitives.intgr.IntQuery.iterate;
import static org.jayield.primitives.intgr.IntQuery.of;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.AssertJUnit.assertTrue;
import static org.testng.internal.junit.ArrayAsserts.assertArrayEquals;

/**
 * These tests aim to evaluate only the execution of traverse()
 * along the entire pipeline.
 * Each operation should forward the computation through the traverse()
 * method of the upstream.
 *
 * @author Miguel Gamboa
 * created on 03-06-2017
 */
public class IntQueryTraverseTest {

    @Test
    public void testBulkFromAndToStream() {
        int[] src = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        PrimitiveIterator.OfInt expected = IntStream.of(src).iterator();
        IntQuery nrs = fromStream(IntStream.of(src));
        nrs.toStream()
           .forEach(actual -> assertEquals(actual, expected.nextInt()));
        assertFalse(expected.hasNext());
    }


    @Test
    public void testBulkZip() {
        int[] expected = {4, 4, 4};
        int[] source1 = {1, 2, 3};
        int[] source2 = {3, 2, 1};
        int[] actual = of(source1)
                .zip(of(source2), Integer::sum)
                .toArray();
        assertEquals(actual, expected);
    }


    @Test
    public void testBulkMapFilter() {
        int[] expected = {2, 4, 6};
        int[] source = {1, 1, 1, 2, 3, 4, 5, 6};
        IntQuery nrs = of(source);
        int[] actual = nrs
                .map(n -> n * 2)
                .filter(n -> n <= 6)
                .skip(2)
                .toArray();
        assertEquals(actual, expected);
    }

    @Test
    public void testBulkMapFilterOdd() {
        int[] expected = {1, 3, 5};
        int[] source = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        IntQuery nrs = of(source);
        int[] actual = nrs
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
                })
                .toArray();
        assertEquals(actual, expected);
    }

    @Test
    public void testBulkMapCollapse() {
        int[] expected = {7, 8, 9, 11, 7};
        int[] source = {7, 7, 8, 9, 9, 11, 11, 7};
        int[] actual = of(source)
                .then(UserExt::collapse)
                .toArray();
        assertEquals(actual, expected);
    }

    @Test
    public void testBulkMapFilterOddAndAnyMatch() {
        int[] source = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        IntQuery nrs = of(source);
        boolean actual = nrs
                .filter(n -> n < 7)
                .map(n -> n - 1)
                .then(UserExt::oddTrav)
                .anyMatch(n -> n == 5);
        assertTrue(actual);
    }

    @Test
    public void testAllMatchForAllElements() {
        int[] source = {2, 4, 6, 8, 10, 12};
        boolean actual = of(source).allMatch(nr -> nr % 2 == 0);
        assertTrue(actual);
    }

    @Test
    public void testAllMatchFailOnIntruder() {
        int[] arrange = {2, 4, 6, 7, 10, 12};
        int[] count = {0};
        int expectedCount = 4;
        boolean actual = of(arrange)
                .peek(__ -> count[0]++)
                .allMatch(nr -> nr % 2 == 0);
        assertFalse(actual);
        assertEquals(count[0], expectedCount);
    }

    @Test
    public void testBulkFlatMap() {
        int[] expected = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        int[] arrange = {2, 5, 8};
        int[] actual = of(arrange)
                .flatMap(nr -> of(nr - 1, nr, nr + 1))
                .toArray();
        assertEquals(actual, expected);
    }

    @Test
    public void testShortCircuitFlatMap() {
        int[] arrange = {2, 5, 8};
        int expected = 1;
        int actual = of(arrange)
                .flatMap(nr -> of(nr - 1, nr, nr + 1))
                .findFirst()
                .orElseThrow();
        assertEquals(actual, expected);
    }

    @Test
    public void testShortCircuitOnEmptySequence() {
        int[] arrange = {};
        OptionalInt actual = of(arrange)
                .findFirst();
        assertTrue(actual.isEmpty());
    }

    @Test
    public void testBulkDistinctCount() {
        int[] arrange = {0, 9, 2, 9, 1, 3, 3, 7, 9, 5, 4, 0, 8, 1, 6};
        int expected = 10;
        long total = of(arrange)
                .distinct()
                .count();
        assertEquals(total, expected);
    }

    @Test
    public void testBulkMax() {
        int[] arrange = {0, 9, 2, 9, 1, 3, 3, 7, 9, 5, 4, 0, 8, 1, 6};
        int expected = 9;
        int actual = of(arrange)
                .max()
                .orElseThrow();
        assertEquals(actual, expected);
    }


    @Test
    public void testBulkMaxOnEmpty() {
        int[] arrange = {};
        assertFalse(of(arrange).max().isPresent());
    }

    @Test
    public void testBulkMinOnEmpty() {
        int[] arrange = {};
        assertFalse(of(arrange).min().isPresent());
    }

    @Test
    public void testBulkIterateLimitMax() {
        int expected = 13;
        int actual = iterate(1, n -> n + 2)
                .limit(7)
                .max()
                .orElseThrow();
        assertEquals(actual, expected);
    }

    @Test
    public void testBulkIterateTakeWhileMax() {
        int expected = 13;
        int actual = iterate(1, n -> n + 2)
                .takeWhile(n -> n < 14)
                .max()
                .orElseThrow();
        assertEquals(actual, expected);
    }

    @Test
    public void testBulkPeekCount() {
        int[] expected = {2, 4, 6, 8};
        int[] source = {1, 2, 3, 4};
        int[] actual = new int[expected.length];
        IntBox index = new IntBox();
        index.setValue(0);
        long count = IntQuery
                .of(source)
                .peek(item -> {
                    actual[index.getValue()] = item * 2;
                    index.setValue(index.getValue() + 1);
                }).count();
        assertEquals(count, expected.length);
        assertEquals(index.getValue(), expected.length);
        assertArrayEquals(expected, actual);
    }

    @Test
    public void testBulkTakeWhileCount() {
        int[] expected = {4, 8};
        int[] source = {2, 4, 5, 8};
        int[] actual = new int[expected.length];
        IntBox index = new IntBox();
        index.setValue(0);
        long count = IntQuery
                .of(source)
                .takeWhile(i -> i % 2 == 0)
                .peek(item -> {
                    actual[index.getValue()] = item * 2;
                    index.setValue(index.getValue() + 1);
                }).count();
        assertEquals(count, expected.length);
        assertEquals(index.getValue(), expected.length);
        assertArrayEquals(expected, actual);
    }

    @Test
    public void testForEach() {
        int[] input = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        PrimitiveIterator.OfInt expected = IntStream.of(input).iterator();
        IntQuery nrs = fromStream(IntStream.of(1, 2, 3, 4, 5, 6, 7, 8, 9));
        nrs.forEach(actual -> assertEquals(actual, expected.nextInt()));
        assertFalse(expected.hasNext());
    }


    @Test
    public void testReduce() {
        int[] input = {1, 1, 1};
        int expected = 3;
        int actual = of(input).reduce(Integer::sum).orElseThrow();
        assertEquals(actual, expected);
    }

    @Test
    public void testReduceOnEmpty() {
        int[] input = {};
        assertTrue(of(input).reduce(Integer::sum).isEmpty());
    }

    @Test
    public void testReduceOnEmptyWithIdentity() {
        int[] input = {};
        int expected = 3;
        int actual = of(input).reduce(expected, Integer::sum);
        assertEquals(actual, expected);
    }

    @Test
    public void testNoneMatchFail() {
        boolean actual = IntQuery.generate(() -> 1).noneMatch(i -> i == 1);
        assertFalse(actual);
    }

    @Test
    public void testNoneMatchSuccess() {
        int[] input = {1, 1, 1};
        assertTrue(of(input).noneMatch(i -> i == 2));
    }

    @Test
    public void testFindAnySuccess() {
        int[] i = new int[]{0};
        int expected = 11;
        int actual = IntQuery.generate(() -> i[0]++)
                             .filter(integer -> integer > 10)
                             .findAny()
                             .orElseThrow();
        assertEquals(actual, expected);
    }

    @Test
    public void testFindAnyFailure() {
        int[] input = {1, 1, 1};
        OptionalInt actual = of(input)
                .filter(integer -> integer > 10)
                .findAny();
        assertFalse(actual.isPresent());
    }

    @Test
    public void testBulkMin() {
        int[] arrange = {9, 2, 9, 0, 1, 3, 3, 7, 9, 5, 4, 0, 8, 1, 6};
        int expected = 0;
        int actual = of(arrange)
                .min()
                .orElseThrow();
        assertEquals(actual, expected);
    }

    @Test
    public void testBulkAverage() {
        int[] arrange = {1, 2, 3, 3, 2, 1};
        double expected = 2;
        double actual = of(arrange)
                .average()
                .orElseThrow();
        assertEquals(actual, expected);
    }

    @Test
    public void testBulkAverageOnEmpty() {
        int[] arrange = {};
        assertFalse(of(arrange).average().isPresent());
    }

    @Test
    public void testBulkIntSummaryStatistics() {
        int[] arrange = {1, 2, 3, 3, 2, 1};
        int expectedCount = arrange.length;
        int expectedMin = 1;
        int expectedMax = 3;
        int expectedSum = 12;
        double expectedAverage = 2;
        IntSummaryStatistics actual = of(arrange)
                .summaryStatistics();

        assertEquals(actual.getCount(), expectedCount);
        assertEquals(actual.getMax(), expectedMax);
        assertEquals(actual.getMin(), expectedMin);
        assertEquals(actual.getAverage(), expectedAverage);
        assertEquals(actual.getSum(), expectedSum);
    }

    @Test
    public void testConcat() {
        int[] expected = {1, 2, 3, 4};
        int[] source1 = {1, 2};
        int[] source2 = {3, 4};
        int[] actual = of(source1).concat(of(source2)).toArray();
        assertEquals(actual, expected);
    }

    @Test
    public void testSorted() {
        int[] expected = {7, 8, 9, 11};
        int[] source = {11, 7, 9, 8};
        int[] actual = IntQuery.of(source)
                               .sorted()
                               .toArray();
        assertEquals(actual, expected);
    }

    @Test
    public void testBoxed() {
        Integer[] expected = {7, 8, 9, 11};
        int[] source = {11, 7, 9, 8};
        Integer[] actual = IntQuery.of(source)
                               .sorted()
                               .boxed()
                               .toArray(Integer[]::new);
        assertEquals(actual, expected);
    }

    @Test
    public void testDropWhile() {
        int delimiter = 9;
        int[] expected = {delimiter, 11};
        int[] source = {8, 7, delimiter, 11};
        int[] actual = IntQuery.of(source)
                               .dropWhile(i -> delimiter != i)
                               .toArray();
        assertEquals(actual, expected);
    }


    @Test
    public void testMapToObj() {
        String[] expected = {"4", "4", "4"};
        int[] source1 = {1, 2, 3};
        int[] source2 = {3, 2, 1};
        String[] actual = of(source1)
                .zip(of(source2), Integer::sum)
                .mapToObj(String::valueOf)
                .toArray(String[]::new);
        assertArrayEquals(expected, actual);
    }

    @Test
    public void testBulkMapToDouble() {
        double[] expected = {7, 8, 9, 11};
        int[] source = {11, 7, 9, 8};
        double[] actual = IntQuery
                .of(source)
                .sorted()
                .asDoubleQuery()
                .toArray();
        assertArrayEquals(expected, actual, 0);
    }

    @Test
    public void testBulkMapToLong() {
        long[] expected = {7, 8, 9, 11};
        int[] source = {11, 7, 9, 8};
        long[] actual = IntQuery.of(source)
                                .sorted()
                                .asLongQuery()
                                .toArray();
        assertArrayEquals(expected, actual);
    }
}
