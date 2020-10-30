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

import org.jayield.boxes.IntBox;
import org.testng.annotations.Test;

import java.util.LongSummaryStatistics;
import java.util.OptionalLong;
import java.util.PrimitiveIterator;
import java.util.stream.LongStream;

import static org.jayield.primitives.lng.LongQuery.fromStream;
import static org.jayield.primitives.lng.LongQuery.iterate;
import static org.jayield.primitives.lng.LongQuery.of;
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
public class LongQueryTraverseTest {

    @Test
    public void testBulkFromAndToStream() {
        long[] src = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        PrimitiveIterator.OfLong expected = LongStream.of(src).iterator();
        LongQuery nrs = fromStream(LongStream.of(src));
        nrs.toStream()
           .forEach(actual -> assertEquals(actual, expected.nextLong()));
        assertFalse(expected.hasNext());
    }


    @Test
    public void testBulkZip() {
        long[] expected = {4, 4, 4};
        long[] source1 = {1, 2, 3};
        long[] source2 = {3, 2, 1};
        long[] actual = of(source1)
                .zip(of(source2), Long::sum)
                .toArray();
        assertEquals(actual, expected);
    }


    @Test
    public void testBulkMapFilter() {
        long[] expected = {2, 4, 6};
        long[] source = {1, 1, 1, 2, 3, 4, 5, 6};
        LongQuery nrs = of(source);
        long[] actual = nrs
                .map(n -> n * 2)
                .filter(n -> n <= 6)
                .skip(2)
                .toArray();
        assertEquals(actual, expected);
    }

    @Test
    public void testBulkMapFilterOdd() {
        long[] expected = {1, 3, 5};
        long[] source = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        LongQuery nrs = of(source);
        long[] actual = nrs
                .filter(n -> n < 7)
                .map(n -> n - 1)
                .then(UserExt::oddTrav)
                .toArray();
        assertEquals(actual, expected);
    }

    @Test
    public void testBulkMapCollapse() {
        long[] expected = {7, 8, 9, 11, 7};
        long[] source = {7, 7, 8, 9, 9, 11, 11, 7};
        long[] actual = of(source)
                .then(UserExt::collapse)
                .toArray();
        assertEquals(actual, expected);
    }

    @Test
    public void testBulkMapFilterOddAndAnyMatch() {
        long[] source = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        LongQuery nrs = of(source);
        boolean actual = nrs
                .filter(n -> n < 7)
                .map(n -> n - 1)
                .then(UserExt::oddTrav)
                .anyMatch(n -> n == 5);
        assertTrue(actual);
    }

    @Test
    public void testAllMatchForAllElements() {
        long[] source = {2, 4, 6, 8, 10, 12};
        boolean actual = of(source).allMatch(nr -> nr % 2 == 0);
        assertTrue(actual);
    }

    @Test
    public void testAllMatchFailOnIntruder() {
        long[] arrange = {2, 4, 6, 7, 10, 12};
        long[] count = {0};
        int expectedCount = 4;
        boolean actual = of(arrange)
                .peek(__ -> count[0]++)
                .allMatch(nr -> nr % 2 == 0);
        assertFalse(actual);
        assertEquals(count[0], expectedCount);
    }

    @Test
    public void testBulkFlatMap() {
        long[] expected = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        long[] arrange = {2, 5, 8};
        long[] actual = of(arrange)
                .flatMap(nr -> of(nr - 1, nr, nr + 1))
                .toArray();
        assertEquals(actual, expected);
    }

    @Test
    public void testShortCircuitFlatMap() {
        long[] arrange = {2, 5, 8};
        long expected = 1;
        long actual = of(arrange)
                .flatMap(nr -> of(nr - 1, nr, nr + 1))
                .findFirst()
                .orElseThrow();
        assertEquals(actual, expected);
    }

    @Test
    public void testShortCircuitOnEmptySequence() {
        long[] arrange = {};
        OptionalLong actual = of(arrange)
                .findFirst();
        assertTrue(actual.isEmpty());
    }

    @Test
    public void testBulkDistinctCount() {
        long[] arrange = {0, 9, 2, 9, 1, 3, 3, 7, 9, 5, 4, 0, 8, 1, 6};
        int expected = 10;
        long total = of(arrange)
                .distinct()
                .count();
        assertEquals(total, expected);
    }

    @Test
    public void testBulkMax() {
        long[] arrange = {0, 9, 2, 9, 1, 3, 3, 7, 9, 5, 4, 0, 8, 1, 6};
        long expected = 9;
        long actual = of(arrange)
                .max()
                .orElseThrow();
        assertEquals(actual, expected);
    }

    @Test
    public void testBulkMaxOnEmpty() {
        long[] arrange = {};
        assertFalse(of(arrange).max().isPresent());
    }

    @Test
    public void testBulkMinOnEmpty() {
        long[] arrange = {};
        assertFalse(of(arrange).min().isPresent());
    }

    @Test
    public void testBulkIterateTakeWhileMax() {
        long expected = 13;
        long actual = iterate(1, n -> n + 2)
                .takeWhile(n -> n < 14)
                .max()
                .orElseThrow();
        assertEquals(actual, expected);
    }

    @Test
    public void testBulkIterateLimitMax() {
        long expected = 13;
        long actual = iterate(1, n -> n + 2)
                .limit(7)
                .max()
                .orElseThrow();
        assertEquals(actual, expected);
    }


    @Test
    public void testBulkPeekCount() {
        long[] expected = {2, 4, 6, 8};
        long[] source = {1, 2, 3, 4};
        long[] actual = new long[expected.length];
        IntBox index = new IntBox();
        index.setValue(0);
        long count = of(source)
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
        long[] expected = {4, 8};
        long[] source = {2, 4, 5, 8};
        long[] actual = new long[expected.length];
        IntBox index = new IntBox();
        index.setValue(0);
        long count = of(source)
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
        long[] input = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        PrimitiveIterator.OfLong expected = LongStream.of(input).iterator();
        LongQuery nrs = fromStream(LongStream.of(1, 2, 3, 4, 5, 6, 7, 8, 9));
        nrs.forEach(actual -> assertEquals(actual, expected.nextLong()));
        assertFalse(expected.hasNext());
    }


    @Test
    public void testReduce() {
        long[] input = {1, 1, 1};
        long expected = 3;
        long actual = of(input).reduce(Long::sum).orElseThrow();
        assertEquals(actual, expected);
    }

    @Test
    public void testReduceOnEmpty() {
        long[] input = {};
        assertTrue(of(input).reduce(Long::sum).isEmpty());
    }

    @Test
    public void testReduceOnEmptyWithIdentity() {
        long[] input = {};
        long expected = 3;
        long actual = of(input).reduce(expected, Long::sum);
        assertEquals(actual, expected);
    }

    @Test
    public void testNoneMatchFail() {
        boolean actual = LongQuery.generate(() -> 1).noneMatch(i -> i == 1);
        assertFalse(actual);
    }

    @Test
    public void testNoneMatchSuccess() {
        long[] input = {1, 1, 1};
        assertTrue(of(input).noneMatch(i -> i == 2));
    }

    @Test
    public void testFindAnySuccess() {
        long[] i = new long[]{0};
        long expected = 11;
        long actual = LongQuery.generate(() -> i[0]++)
                               .filter(integer -> integer > 10)
                               .findAny()
                               .orElseThrow();
        assertEquals(actual, expected);
    }

    @Test
    public void testFindAnyFailure() {
        long[] input = {1, 1, 1};
        OptionalLong actual = of(input)
                .filter(integer -> integer > 10)
                .findAny();
        assertFalse(actual.isPresent());
    }

    @Test
    public void testBulkMin() {
        long[] arrange = {9, 2, 9, 0, 1, 3, 3, 7, 9, 5, 4, 0, 8, 1, 6};
        long expected = 0;
        long actual = of(arrange)
                .min()
                .orElseThrow();
        assertEquals(actual, expected);
    }

    @Test
    public void testBulkAverage() {
        long[] arrange = {1, 2, 3, 3, 2, 1};
        double expected = 2;
        double actual = of(arrange)
                .average()
                .orElseThrow();
        assertEquals(actual, expected);
    }

    @Test
    public void testBulkAverageOnEmpty() {
        long[] arrange = {};
        assertFalse(of(arrange).average().isPresent());
    }

    @Test
    public void testBulkLongSummaryStatistics() {
        long[] arrange = {1, 2, 3, 3, 2, 1};
        int expectedCount = arrange.length;
        int expectedMin = 1;
        int expectedMax = 3;
        int expectedSum = 12;
        double expectedAverage = 2;
        LongSummaryStatistics actual = of(arrange)
                .summaryStatistics();

        assertEquals(actual.getCount(), expectedCount);
        assertEquals(actual.getMax(), expectedMax);
        assertEquals(actual.getMin(), expectedMin);
        assertEquals(actual.getAverage(), expectedAverage);
        assertEquals(actual.getSum(), expectedSum);
    }

    @Test
    public void testConcat() {
        long[] expected = {1, 2, 3, 4};
        long[] source1 = {1, 2};
        long[] source2 = {3, 4};
        long[] actual = of(source1).concat(of(source2)).toArray();
        assertEquals(actual, expected);
    }

    @Test
    public void testSorted() {
        long[] expected = {7, 8, 9, 11};
        long[] source = {11, 7, 9, 8};
        long[] actual = of(source)
                .sorted()
                .toArray();
        assertEquals(actual, expected);
    }

    @Test
    public void testBoxed() {
        Long[] expected = {7L, 8L, 9L, 11L};
        long[] source = {11, 7, 9, 8};
        Long[] actual = of(source)
                .sorted()
                .boxed()
                .toArray(Long[]::new);
        assertEquals(actual, expected);
    }

    @Test
    public void testDropWhile() {
        int delimiter = 9;
        long[] expected = {delimiter, 11};
        long[] source = {8, 7, delimiter, 11};
        long[] actual = of(source)
                .dropWhile(i -> delimiter != i)
                .toArray();
        assertEquals(actual, expected);
    }


    @Test
    public void testMapToObj() {
        String[] expected = {"4", "4", "4"};
        long[] source1 = {1, 2, 3};
        long[] source2 = {3, 2, 1};
        String[] actual = of(source1)
                .zip(of(source2), Long::sum)
                .mapToObj(String::valueOf)
                .toArray(String[]::new);
        assertArrayEquals(expected, actual);
    }

    @Test
    public void testBulkMapToDouble() {
        double[] expected = {7, 8, 9, 11};
        long[] source = {11, 7, 9, 8};
        double[] actual = of(source)
                .sorted()
                .asDoubleQuery()
                .toArray();
        assertArrayEquals(expected, actual, 0);
    }

    @Test
    public void testBulkMapToInt() {
        int[] expected = {7, 8, 9, 11};
        long[] source = {11, 7, 9, 8};
        int[] actual = of(source)
                .sorted()
                .asIntQuery()
                .toArray();
        assertArrayEquals(expected, actual);
    }
}
