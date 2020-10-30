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

import org.jayield.boxes.IntBox;
import org.testng.annotations.Test;

import java.util.DoubleSummaryStatistics;
import java.util.OptionalDouble;
import java.util.PrimitiveIterator;
import java.util.stream.DoubleStream;

import static org.jayield.primitives.dbl.DoubleQuery.fromStream;
import static org.jayield.primitives.dbl.DoubleQuery.iterate;
import static org.jayield.primitives.dbl.DoubleQuery.of;
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
public class DoubleQueryTraverseTest {

    @Test
    public void testBulkFromAndToStream() {
        double[] src = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        PrimitiveIterator.OfDouble expected = DoubleStream.of(src).iterator();
        DoubleQuery nrs = fromStream(DoubleStream.of(src));
        nrs.toStream()
           .forEach(actual -> assertEquals(actual, expected.nextDouble()));
        assertFalse(expected.hasNext());
    }


    @Test
    public void testBulkZip() {
        double[] expected = {4, 4, 4};
        double[] source1 = {1, 2, 3};
        double[] source2 = {3, 2, 1};
        double[] actual = of(source1)
                .zip(of(source2), Double::sum)
                .toArray();
        assertEquals(actual, expected);
    }


    @Test
    public void testBulkMapFilter() {
        double[] expected = {2, 4, 6};
        double[] source = {1, 1, 1, 2, 3, 4, 5, 6};
        DoubleQuery nrs = of(source);
        double[] actual = nrs
                .map(n -> n * 2)
                .filter(n -> n <= 6)
                .skip(2)
                .toArray();
        assertEquals(actual, expected);
    }

    @Test
    public void testBulkMapFilterOdd() {
        double[] expected = {1, 3, 5};
        double[] source = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        DoubleQuery nrs = of(source);
        double[] actual = nrs
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
        double[] expected = {7, 8, 9, 11, 7};
        double[] source = {7, 7, 8, 9, 9, 11, 11, 7};
        double[] actual = of(source)
                .then(UserExt::collapse)
                .toArray();
        assertEquals(actual, expected);
    }

    @Test
    public void testBulkMapFilterOddAndAnyMatch() {
        double[] source = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        DoubleQuery nrs = of(source);
        boolean actual = nrs
                .filter(n -> n < 7)
                .map(n -> n - 1)
                .then(UserExt::oddTrav)
                .anyMatch(n -> n == 5);
        assertTrue(actual);
    }

    @Test
    public void testAllMatchForAllElements() {
        double[] source = {2, 4, 6, 8, 10, 12};
        boolean actual = of(source).allMatch(nr -> nr % 2 == 0);
        assertTrue(actual);
    }

    @Test
    public void testAllMatchFailOnIntruder() {
        double[] arrange = {2, 4, 6, 7, 10, 12};
        double[] count = {0};
        double expectedCount = 4;
        boolean actual = of(arrange)
                .peek(__ -> count[0]++)
                .allMatch(nr -> nr % 2 == 0);
        assertFalse(actual);
        assertEquals(count[0], expectedCount);
    }

    @Test
    public void testBulkFlatMap() {
        double[] expected = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        double[] arrange = {2, 5, 8};
        double[] actual = of(arrange)
                .flatMap(nr -> of(nr - 1, nr, nr + 1))
                .toArray();
        assertEquals(actual, expected);
    }

    @Test
    public void testShortCircuitFlatMap() {
        double[] arrange = {2, 5, 8};
        double expected = 1;
        double actual = of(arrange)
                .flatMap(nr -> of(nr - 1, nr, nr + 1))
                .findFirst()
                .orElseThrow();
        assertEquals(actual, expected);
    }

    @Test
    public void testShortCircuitOnEmptySequence() {
        double[] arrange = {};
        OptionalDouble actual = of(arrange)
                .findFirst();
        assertTrue(actual.isEmpty());
    }

    @Test
    public void testBulkDistinctCount() {
        double[] arrange = {0, 9, 2, 9, 1, 3, 3, 7, 9, 5, 4, 0, 8, 1, 6};
        long expected = 10;
        long actual = of(arrange)
                .distinct()
                .count();
        assertEquals(actual, expected);
    }

    @Test
    public void testBulkMax() {
        double[] arrange = {0, 9, 2, 9, 1, 3, 3, 7, 9, 5, 4, 0, 8, 1, 6};
        double expected = 9;
        double actual = of(arrange)
                .max()
                .orElseThrow();
        assertEquals(actual, expected);
    }

    @Test
    public void testBulkMaxOnEmpty() {
        double[] arrange = {};
        assertFalse(of(arrange).max().isPresent());
    }

    @Test
    public void testBulkMinOnEmpty() {
        double[] arrange = {};
        assertFalse(of(arrange).min().isPresent());
    }

    @Test
    public void testBulkIterateLimitMax() {
        double expected = 13;
        double actual = iterate(1, n -> n + 2)
                .limit(7)
                .max()
                .orElseThrow();
        assertEquals(actual, expected);
    }

    @Test
    public void testBulkIterateTakeWhileMax() {
        double expected = 13;
        double actual = iterate(1, n -> n + 2)
                .takeWhile(n -> n < 14)
                .max()
                .orElseThrow();
        assertEquals(actual, expected);
    }

    @Test
    public void testBulkPeekCount() {
        double[] expected = {2, 4, 6, 8};
        double[] source = {1, 2, 3, 4};
        double[] actual = new double[expected.length];
        IntBox index = new IntBox();
        index.setValue(0);
        long count = DoubleQuery
                .of(source)
                .peek(item -> {
                    actual[index.getValue()] = item * 2;
                    index.setValue(index.getValue() + 1);
                }).count();
        assertEquals(count, expected.length);
        assertEquals(index.getValue(), expected.length);
        assertArrayEquals(expected, actual, 0);
    }

    @Test
    public void testBulkTakeWhileCount() {
        double[] expected = {4, 8};
        double[] source = {2, 4, 5, 8};
        double[] actual = new double[expected.length];
        IntBox index = new IntBox();
        index.setValue(0);
        long count = DoubleQuery
                .of(source)
                .takeWhile(i -> i % 2 == 0)
                .peek(item -> {
                    actual[index.getValue()] = item * 2;
                    index.setValue(index.getValue() + 1);
                }).count();
        assertEquals(count, expected.length);
        assertEquals(index.getValue(), expected.length);
        assertArrayEquals(expected, actual, 0);
    }

    @Test
    public void testForEach() {
        double[] input = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        PrimitiveIterator.OfDouble expected = DoubleStream.of(input).iterator();
        DoubleQuery nrs = fromStream(DoubleStream.of(1, 2, 3, 4, 5, 6, 7, 8, 9));
        nrs.forEach(actual -> assertEquals(actual, expected.nextDouble()));
        assertFalse(expected.hasNext());
    }


    @Test
    public void testReduce() {
        double[] input = {1, 1, 1};
        double expected = 3;
        double actual = of(input).reduce(Double::sum).orElseThrow();
        assertEquals(actual, expected);
    }

    @Test
    public void testReduceOnEmpty() {
        double[] input = {};
        assertTrue(of(input).reduce(Double::sum).isEmpty());
    }

    @Test
    public void testReduceOnEmptyWithIdentity() {
        double[] input = {};
        double expected = 3;
        double actual = of(input).reduce(expected, Double::sum);
        assertEquals(actual, expected);
    }

    @Test
    public void testNoneMatchFail() {
        boolean actual = DoubleQuery.generate(() -> 1).noneMatch(i -> i == 1);
        assertFalse(actual);
    }

    @Test
    public void testNoneMatchSuccess() {
        double[] input = {1, 1, 1};
        assertTrue(of(input).noneMatch(i -> i == 2));
    }

    @Test
    public void testFindAnySuccess() {
        double[] i = new double[]{0};
        double expected = 11;
        double actual = DoubleQuery.generate(() -> i[0]++)
                             .filter(integer -> integer > 10)
                             .findAny()
                             .orElseThrow();
        assertEquals(actual, expected);
    }

    @Test
    public void testFindAnyFailure() {
        double[] input = {1, 1, 1};
        OptionalDouble actual = of(input)
                .filter(integer -> integer > 10)
                .findAny();
        assertFalse(actual.isPresent());
    }

    @Test
    public void testBulkMin() {
        double[] arrange = {9, 2, 9, 0, 1, 3, 3, 7, 9, 5, 4, 0, 8, 1, 6};
        double expected = 0;
        double actual = of(arrange)
                .min()
                .orElseThrow();
        assertEquals(actual, expected);
    }

    @Test
    public void testBulkAverage() {
        double[] arrange = {1, 2, 3, 3, 2, 1};
        double expected = 2;
        double actual = of(arrange)
                .average()
                .orElseThrow();
        assertEquals(actual, expected);
    }

    @Test
    public void testBulkAverageOnEmpty() {
        double[] arrange = {};
        assertFalse(of(arrange).average().isPresent());
    }

    @Test
    public void testBulkDoubleSummaryStatistics() {
        double[] arrange = {1, 2, 3, 3, 2, 1};
        long expectedCount = arrange.length;
        double expectedMin = 1.0;
        double expectedMax = 3.0;
        double expectedSum = 12.0;
        double expectedAverage = 2.0;
        DoubleSummaryStatistics actual = of(arrange)
                .summaryStatistics();

        assertEquals(actual.getCount(), expectedCount);
        assertEquals(actual.getMax(), expectedMax);
        assertEquals(actual.getMin(), expectedMin);
        assertEquals(actual.getAverage(), expectedAverage);
        assertEquals(actual.getSum(), expectedSum);
    }

    @Test
    public void testConcat() {
        double[] expected = {1, 2, 3, 4};
        double[] source1 = {1, 2};
        double[] source2 = {3, 4};
        double[] actual = of(source1).concat(of(source2)).toArray();
        assertEquals(actual, expected);
    }

    @Test
    public void testSorted() {
        double[] expected = {7, 8, 9, 11};
        double[] source = {11, 7, 9, 8};
        double[] actual = DoubleQuery.of(source)
                               .sorted()
                               .toArray();
        assertEquals(actual, expected);
    }

    @Test
    public void testBoxed() {
        Double[] expected = {7.0, 8.0, 9.0, 11.0};
        double[] source = {11.0, 7.0, 9.0, 8.0};
        Double[] actual = DoubleQuery.of(source)
                               .sorted()
                               .boxed()
                               .toArray(Double[]::new);
        assertEquals(actual, expected);
    }

    @Test
    public void testDropWhile() {
        double delimiter = 9;
        double[] expected = {delimiter, 11};
        double[] source = {8, 7, delimiter, 11};
        double[] actual = DoubleQuery.of(source)
                               .dropWhile(i -> delimiter != i)
                               .toArray();
        assertEquals(actual, expected);
    }


    @Test
    public void testMapToObj() {
        String[] expected = {"4.0", "4.0", "4.0"};
        double[] source1 = {1, 2, 3};
        double[] source2 = {3, 2, 1};
        String[] actual = of(source1)
                .zip(of(source2), Double::sum)
                .mapToObj(String::valueOf)
                .toArray(String[]::new);
        assertArrayEquals(expected, actual);
    }

    @Test
    public void testBulkMapToInt() {
        int[] expected = {7, 8, 9, 11};
        double[] source = {11, 7, 9, 8};
        int[] actual = DoubleQuery
                .of(source)
                .sorted()
                .asIntQuery()
                .toArray();
        assertArrayEquals(expected, actual);
    }

    @Test
    public void testBulkMapToLong() {
        long[] expected = {7, 8, 9, 11};
        double[] source = {11, 7, 9, 8};
        long[] actual = DoubleQuery.of(source)
                                .sorted()
                                .asLongQuery()
                                .toArray();
        assertArrayEquals(expected, actual);
    }
}
