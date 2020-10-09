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

import org.jayield.Query;
import org.jayield.boxes.IntBox;
import org.jayield.primitives.dbl.DoubleQuery;
import org.jayield.primitives.intgr.IntQuery;
import org.testng.Assert;
import org.testng.AssertJUnit;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.jayield.primitives.lng.LongQuery.of;
import static org.testng.Assert.assertEquals;
import static org.testng.internal.junit.ArrayAsserts.assertArrayEquals;

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
        List<Long> expected = asList(2L, 4L, 6L);
        long[] source = {1, 1, 1, 2, 3, 4, 5, 6};
        LongQuery nrs = LongQuery.of(source);
        LongQuery pipe = nrs
                .map(n -> n * 2)
                .filter(n -> n <= 6)
                .skip(2);
        List<Long> actual = new ArrayList<>();
        while (pipe.tryAdvance(item -> {
            Assert.assertTrue(actual.size() < expected.size());
            actual.add(item);
        })) {
        }
        Assert.assertEquals(expected.size(), actual.size());
        assertEquals(expected, actual);
    }

    @Test(expectedExceptions = { UnsupportedOperationException.class })
    public void testBulkMapFilterOddFail() {
        long[] source = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        LongQuery nrs = LongQuery.of(source);
        LongQuery pipe = nrs
                .filter(n -> n < 7)
                .map(n -> n - 1)
                .then(prev -> yield -> { throw new AssertionError("This traverse should not be invoked for this pipeline!"); });
        pipe.tryAdvance(item -> {});
    }

    @Test
    public void testBulkMapFilterOdd() {
        List<Long> expected = asList(1L, 3L, 5L);
        long[] source = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        LongQuery nrs = LongQuery.of(source);
        LongQuery pipe = nrs
                .filter(n -> n < 7)
                .map(n -> n - 1)
                .then(
                    UserExt::oddAdv,
                    prev -> yield -> { throw new AssertionError("This traverse should not be invoked for this pipeline!"); }
                );
        List<Long> actual = new ArrayList<>();
        while (pipe.tryAdvance(item -> {
            Assert.assertTrue(actual.size() < expected.size());
            actual.add(item);
        })) {
        }
        Assert.assertEquals(expected.size(), actual.size());
        assertEquals(expected, actual);
    }

    @Test
    public void testMapCollapse() {
        List<Long> expected = asList(7L, 8L, 9L, 11L, 7L);
        long[] source = {7, 7, 8, 9, 9, 11, 11, 7};
        LongQuery pipe = LongQuery
                .of(source)
                .then(
                    UserExt::collapseAdv,
                    prev -> yield -> { throw new AssertionError("This traverse should not be invoked for this pipeline!");}
                );
        List<Long> actual = new ArrayList<>();
        while (pipe.tryAdvance(item -> {
            Assert.assertTrue(actual.size() < expected.size());
            actual.add(item);
        })) {
        }
        Assert.assertEquals(expected.size(), actual.size());
        assertEquals(expected, actual);
    }

    @Test(expectedExceptions = { UnsupportedOperationException.class })
    public void testMapCollapseFail() {
        long[] source = {7, 7, 8, 9, 9, 11, 11, 7};
        LongQuery pipe = LongQuery
                .of(source)
                .then(prev -> yield -> { throw new AssertionError("This traverse should not be invoked for this pipeline!");});
        pipe.tryAdvance(item -> {});
    }


    @Test
    public void testFlatMap() {
        List<Long> expected = asList(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L);
        long[] source = {2, 5, 8};
        LongQuery pipe = LongQuery
                .of(source)
                .flatMap(nr -> LongQuery.of(nr - 1, nr, nr + 1));
        List<Long> actual = new ArrayList<>();
        while (pipe.tryAdvance(item -> {
            Assert.assertTrue(actual.size() < expected.size());
            actual.add(item);
        })) {
        }
        Assert.assertEquals(expected.size(), actual.size());
        assertEquals(expected, actual);
    }

    @Test
    public void testDistinct() {
        List<Long> expected = asList(7L, 8L, 9L, 11L);
        long[] source = {7, 7, 8, 9, 9, 11, 11, 7};
        LongQuery pipe = LongQuery
                .of(source)
                .distinct();
        List<Long> actual = new ArrayList<>();
        while (pipe.tryAdvance(item -> {
            Assert.assertTrue(actual.size() < expected.size());
            actual.add(item);
        })) {
        }
        Assert.assertEquals(expected.size(), actual.size());
        assertEquals(expected, actual);
    }

    @Test
    public void testIterateLimit() {
        List<Long> expected = asList(1L, 3L, 5L, 7L, 9L, 11L, 13L);
        LongQuery pipe = LongQuery
                .iterate(1, n -> n + 2)
                .limit(7);
        List<Long> actual = new ArrayList<>();
        while (pipe.tryAdvance(item -> {
            Assert.assertTrue(actual.size() < expected.size());
            actual.add(item);
        })) {
        }
        Assert.assertEquals(expected.size(), actual.size());
        assertEquals(expected, actual);
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
        int []count = {0};
        while (pipe.tryAdvance(ignore -> count[0]++)) {
        }
        AssertJUnit.assertEquals(expected.length, count[0]);
        AssertJUnit.assertEquals(expected.length, index.getValue());
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
        int []count = {0};
        while (pipe.tryAdvance(ignore -> count[0]++)) {
        }
        AssertJUnit.assertEquals(expected.length, count[0]);
        AssertJUnit.assertEquals(expected.length, index.getValue());
        assertArrayEquals(expected, actual);
    }

    @Test
    public void testGenerateLimitLast() {
        List<Long> expected = asList(3L, 4L, 5L, 6L, 7L, 8L, 9L);
        long[] n = new long[]{1};
        LongQuery pipe = LongQuery
                .generate(() -> 2 + n[0]++)
                .limit(7);
        List<Long> actual = new ArrayList<>();
        while (pipe.tryAdvance(item -> {
            Assert.assertTrue(actual.size() < expected.size());
            actual.add(item);
        })) {
        }
        Assert.assertEquals(expected.size(), actual.size());
        assertEquals(expected, actual);
    }

    @Test
    public void testConcat() {
        List<Long> expected = asList(1L, 2L, 3L, 4L);
        long[] source1 = {1, 2};
        long[] source2 = {3, 4};
        LongQuery pipe = LongQuery.of(source1).concat(LongQuery.of(source2));
        List<Long> actual = new ArrayList<>();
        while (pipe.tryAdvance(item -> {
            Assert.assertTrue(actual.size() < expected.size());
            actual.add(item);
        })) {
        }
        Assert.assertEquals(expected.size(), actual.size());
        assertEquals(expected, actual);
    }

    @Test
    public void testSorted() {
        List<Long> expected = asList(7L, 8L, 9L, 11L);
        long[] source = {11, 7, 9, 8};
        LongQuery pipe = LongQuery
                .of(source)
                .sorted();
        List<Long> actual = new ArrayList<>();
        while (pipe.tryAdvance(item -> {
            Assert.assertTrue(actual.size() < expected.size());
            actual.add(item);
        })) {
        }
        Assert.assertEquals(expected.size(), actual.size());
        assertEquals(expected, actual);
    }

    @Test
    public void testDropWhile() {
        long delimiter = 9;
        List<Long> expected = asList(delimiter, 11L);
        long[] source = {8, 7, delimiter, 11};
        LongQuery pipe = LongQuery
                .of(source)
                .dropWhile(i -> delimiter != i);
        List<Long> actual = new ArrayList<>();
        while (pipe.tryAdvance(item -> {
            Assert.assertTrue(actual.size() < expected.size());
            actual.add(item);
        })) {
        }
        Assert.assertEquals(expected.size(), actual.size());
        assertEquals(expected, actual);
    }

    @Test
    public void testZip() {
        List<Long> expected = asList(4L, 4L, 4L);
        long[] source1 = {1, 2, 3};
        long[] source2 = {3, 2, 1};
        LongQuery pipe = of(source1)
                .zip(of(source2), Long::sum);
        List<Long> actual = new ArrayList<>();
        while (pipe.tryAdvance(item -> {
            Assert.assertTrue(actual.size() < expected.size());
            actual.add(item);
        })) {
        }
        Assert.assertEquals(expected.size(), actual.size());
        assertEquals(expected, actual);
    }

    @Test
    public void testMapToObj() {
        List<String> expected = asList("4", "4", "4");
        long[] source1 = {1, 2, 3};
        long[] source2 = {3, 2, 1};
        Query<String> pipe = of(source1)
                .zip(of(source2), Long::sum)
                .mapToObj(String::valueOf);
        List<String> actual = new ArrayList<>();
        while (pipe.tryAdvance(item -> {
            Assert.assertTrue(actual.size() < expected.size());
            actual.add(item);
        })) {
        }
        Assert.assertEquals(expected.size(), actual.size());
        assertEquals(expected, actual);
    }

    @Test
    public void testMapToDouble() {
        List<Double> expected = asList(7d, 8d, 9d, 11d);
        long[] source = {11, 7, 9, 8};
        DoubleQuery pipe = LongQuery
                .of(source)
                .sorted()
                .asDoubleQuery();
        List<Double> actual = new ArrayList<>();
        while (pipe.tryAdvance(item -> {
            Assert.assertTrue(actual.size() < expected.size());
            actual.add(item);
        })) {
        }
        Assert.assertEquals(expected.size(), actual.size());
        assertEquals(expected, actual);
    }

    @Test
    public void testMapToInt() {
        List<Integer> expected = asList(7, 8, 9, 11);
        long[] source = {11, 7, 9, 8};
        IntQuery pipe = LongQuery
                .of(source)
                .sorted()
                .asIntQuery();
        List<Integer> actual = new ArrayList<>();
        while (pipe.tryAdvance(item -> {
            Assert.assertTrue(actual.size() < expected.size());
            actual.add(item);
        })) {
        }
        Assert.assertEquals(expected.size(), actual.size());
        assertEquals(expected, actual);
    }
}
