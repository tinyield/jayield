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

import org.jayield.Query;
import org.jayield.boxes.IntBox;
import org.jayield.primitives.dbl.DoubleQuery;
import org.jayield.primitives.lng.LongQuery;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.jayield.primitives.intgr.IntQuery.of;
import static org.testng.AssertJUnit.assertEquals;
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
public class IntQueryIterateTest {


    @Test
    public void testMapFilter() {
        List<Integer> expected = asList(2, 4, 6);
        int[] source = {1, 1, 1, 2, 3, 4, 5, 6};
        IntQuery nrs = IntQuery.of(source);
        IntQuery pipe = nrs
                .map(n -> n * 2)
                .filter(n -> n <= 6)
                .skip(2);
        List<Integer> actual = new ArrayList<>();
        while (pipe.tryAdvance(item -> {
            Assert.assertTrue(actual.size() < expected.size());
            actual.add(item);
        })) {
        }
        Assert.assertEquals(expected.size(), actual.size());
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testBulkMapFilterOdd() {
        List<Integer> expected = asList(1, 3, 5);
        int[] source = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        IntQuery nrs = IntQuery.of(source);
        IntQuery pipe = nrs
                .filter(n -> n < 7)
                .map(n -> n - 1)
                .then(
                    UserExt::oddAdv,
                    prev -> yield -> { throw new AssertionError("This traverse should not be invoked for this pipeline!");}
                );
        List<Integer> actual = new ArrayList<>();
        while (pipe.tryAdvance(item -> {
            Assert.assertTrue(actual.size() < expected.size());
            actual.add(item);
        })) {
        }
        Assert.assertEquals(expected.size(), actual.size());
        Assert.assertEquals(expected, actual);
    }

    @Test(expectedExceptions = { UnsupportedOperationException.class })
    public void testBulkMapFilterOddFail() {
        int[] source = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        IntQuery nrs = IntQuery.of(source);
        IntQuery pipe = nrs
                .filter(n -> n < 7)
                .map(n -> n - 1)
                .then(prev -> yield -> { throw new AssertionError("This traverse should not be invoked for this pipeline!");});
        pipe.tryAdvance(item -> {});
    }

    @Test
    public void testMapCollapse() {
        List<Integer> expected = asList(7, 8, 9, 11, 7);
        int[] source = {7, 7, 8, 9, 9, 11, 11, 7};
        IntQuery pipe = IntQuery
                .of(source)
                .then(
                    UserExt::collapseAdv,
                    prev -> yield -> { throw new AssertionError("This traverse should not be invoked for this pipeline!");}
                );
        List<Integer> actual = new ArrayList<>();
        while (pipe.tryAdvance(item -> {
            Assert.assertTrue(actual.size() < expected.size());
            actual.add(item);
        })) {
        }
        Assert.assertEquals(expected.size(), actual.size());
        Assert.assertEquals(expected, actual);
    }

    @Test(expectedExceptions = { UnsupportedOperationException.class })
    public void testMapCollapseFail() {
        int[] source = {7, 7, 8, 9, 9, 11, 11, 7};
        IntQuery pipe = IntQuery
                .of(source)
                .then(prev -> yield -> { throw new AssertionError("This traverse should not be invoked for this pipeline!");});
        pipe.tryAdvance(item -> {});
    }


    @Test
    public void testFlatMap() {
        List<Integer> expected = asList(1, 2, 3, 4, 5, 6, 7, 8, 9);
        int[] source = {2, 5, 8};
        IntQuery pipe = IntQuery
                .of(source)
                .flatMap(nr -> IntQuery.of(nr - 1, nr, nr + 1));
        List<Integer> actual = new ArrayList<>();
        while (pipe.tryAdvance(item -> {
            Assert.assertTrue(actual.size() < expected.size());
            actual.add(item);
        })) {
        }
        Assert.assertEquals(expected.size(), actual.size());
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testDistinct() {
        List<Integer> expected = asList(7, 8, 9, 11);
        int[] source = {7, 7, 8, 9, 9, 11, 11, 7};
        IntQuery pipe = IntQuery
                .of(source)
                .distinct();
        List<Integer> actual = new ArrayList<>();
        while (pipe.tryAdvance(item -> {
            Assert.assertTrue(actual.size() < expected.size());
            actual.add(item);
        })) {
        }
        Assert.assertEquals(expected.size(), actual.size());
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testIterateLimit() {
        List<Integer> expected = asList(1, 3, 5, 7, 9, 11, 13);
        IntQuery pipe = IntQuery
                .iterate(1, n -> n + 2)
                .limit(7);
        List<Integer> actual = new ArrayList<>();
        while (pipe.tryAdvance(item -> {
            Assert.assertTrue(actual.size() < expected.size());
            actual.add(item);
        })) {
        }
        Assert.assertEquals(expected.size(), actual.size());
        Assert.assertEquals(expected, actual);
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
        int []count = {0};
        while (pipe.tryAdvance(ignore -> count[0]++)) {
        }
        assertEquals(expected.length, count[0]);
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
        int []count = {0};
        while (pipe.tryAdvance(ignore -> count[0]++)) {
        }
        assertEquals(expected.length, count[0]);
        assertEquals(expected.length, index.getValue());
        assertArrayEquals(expected, actual);
    }

    @Test
    public void testGenerateLimitLast() {
        List<Integer> expected = asList(3, 4, 5, 6, 7, 8, 9);
        int[] n = new int[]{1};
        IntQuery pipe = IntQuery
                .generate(() -> 2 + n[0]++)
                .limit(7);
        List<Integer> actual = new ArrayList<>();
        while (pipe.tryAdvance(item -> {
            Assert.assertTrue(actual.size() < expected.size());
            actual.add(item);
        })) {
        }
        Assert.assertEquals(expected.size(), actual.size());
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testConcat() {
        List<Integer> expected = asList(1, 2, 3, 4);
        int[] source1 = {1, 2};
        int[] source2 = {3, 4};
        IntQuery pipe = IntQuery.of(source1).concat(IntQuery.of(source2));
        List<Integer> actual = new ArrayList<>();
        while (pipe.tryAdvance(item -> {
            Assert.assertTrue(actual.size() < expected.size());
            actual.add(item);
        })) {
        }
        Assert.assertEquals(expected.size(), actual.size());
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testSorted() {
        List<Integer> expected = asList(7, 8, 9, 11);
        int[] source = {11, 7, 9, 8};
        IntQuery pipe = IntQuery
                .of(source)
                .sorted();
        List<Integer> actual = new ArrayList<>();
        while (pipe.tryAdvance(item -> {
            Assert.assertTrue(actual.size() < expected.size());
            actual.add(item);
        })) {
        }
        Assert.assertEquals(expected.size(), actual.size());
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testDropWhile() {
        int delimiter = 9;
        List<Integer> expected = asList(delimiter, 11);
        int[] source = {8, 7, delimiter, 11};
        IntQuery pipe = IntQuery
                .of(source)
                .dropWhile(i -> delimiter != i);
        List<Integer> actual = new ArrayList<>();
        while (pipe.tryAdvance(item -> {
            Assert.assertTrue(actual.size() < expected.size());
            actual.add(item);
        })) {
        }
        Assert.assertEquals(expected.size(), actual.size());
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testZip() {
        List<Integer> expected = asList(4, 4, 4);
        int[] source1 = {1, 2, 3};
        int[] source2 = {3, 2, 1};
        IntQuery pipe = of(source1)
                .zip(of(source2), Integer::sum);
        List<Integer> actual = new ArrayList<>();
        while (pipe.tryAdvance(item -> {
            Assert.assertTrue(actual.size() < expected.size());
            actual.add(item);
        })) {
        }
        Assert.assertEquals(expected.size(), actual.size());
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testMapToObj() {
        List<String> expected = asList("4", "4", "4");
        int[] source1 = {1, 2, 3};
        int[] source2 = {3, 2, 1};
        Query<String> pipe = of(source1)
                .zip(of(source2), Integer::sum)
                .mapToObj(String::valueOf);
        List<String> actual = new ArrayList<>();
        while (pipe.tryAdvance(item -> {
            Assert.assertTrue(actual.size() < expected.size());
            actual.add(item);
        })) {
        }
        Assert.assertEquals(expected.size(), actual.size());
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testMapToDouble() {
        List<Double> expected = asList(7d, 8d, 9d, 11d);
        int[] source = {11, 7, 9, 8};
        DoubleQuery pipe = IntQuery
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
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testMapToLong() {
        List<Long> expected = asList(7L, 8L, 9L, 11L);
        int[] source = {11, 7, 9, 8};
        LongQuery pipe = IntQuery
                .of(source)
                .sorted()
                .asLongQuery();
        List<Long> actual = new ArrayList<>();
        while (pipe.tryAdvance(item -> {
            Assert.assertTrue(actual.size() < expected.size());
            actual.add(item);
        })) {
        }
        Assert.assertEquals(expected.size(), actual.size());
        Assert.assertEquals(expected, actual);
    }
}
