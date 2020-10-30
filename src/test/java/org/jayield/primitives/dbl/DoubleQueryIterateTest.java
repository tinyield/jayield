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

import org.jayield.Query;
import org.jayield.primitives.intgr.IntQuery;
import org.jayield.primitives.lng.LongQuery;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.jayield.primitives.dbl.DoubleQuery.of;
import static org.testng.Assert.assertEquals;

/**
 * These tests aim to evaluate only the execution of tryAdvance()
 * along the entire pipeline.
 * Each operation should forward the computation through the tryAdvance()
 * methods of the upstream.
 *
 * @author Miguel Gamboa
 * created on 03-06-2017
 */
public class DoubleQueryIterateTest {


    @Test
    public void testMapFilter() {
        List<Double> expected = asList(2d, 4d, 6d);
        double[] source = {1, 1, 1, 2, 3, 4, 5, 6};
        DoubleQuery nrs = DoubleQuery.of(source);
        DoubleQuery pipe = nrs
                .map(n -> n * 2)
                .filter(n -> n <= 6)
                .skip(2);
        List<Double> actual = new ArrayList<>();
        while (pipe.tryAdvance(item -> {
            Assert.assertTrue(actual.size() < expected.size());
            actual.add(item);
        })) {
        }
        assertEquals(expected.size(), actual.size());
        assertEquals(expected, actual);
    }

    @Test(expectedExceptions = { UnsupportedOperationException.class })
    public void testBulkMapFilterOddFail() {
        List<Double> expected = asList(1d, 3d, 5d);
        double[] source = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        DoubleQuery nrs = DoubleQuery.of(source);
        DoubleQuery pipe = nrs
                .filter(n -> n < 7)
                .map(n -> n - 1)
                .then(
                    prev -> yield -> { throw new AssertionError("This traverse should not be invoked for this pipeline!"); }
                );
        pipe.tryAdvance(item -> {});
    }


    @Test
    public void testBulkMapFilterOdd() {
        List<Double> expected = asList(1d, 3d, 5d);
        double[] source = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        DoubleQuery nrs = DoubleQuery.of(source);
        DoubleQuery pipe = nrs
                .filter(n -> n < 7)
                .map(n -> n - 1)
                .then(
                    UserExt::oddAdv,
                    prev -> yield -> { throw new AssertionError("This traverse should not be invoked for this pipeline!"); }
                );
        List<Double> actual = new ArrayList<>();
        while (pipe.tryAdvance(item -> {
            Assert.assertTrue(actual.size() < expected.size());
            actual.add(item);
        })) {
        }
        assertEquals(expected.size(), actual.size());
        assertEquals(expected, actual);
    }

    @Test(expectedExceptions = { UnsupportedOperationException.class })
    public void testMapCollapseFail() {
        double[] source = {7, 7, 8, 9, 9, 11, 11, 7};
        DoubleQuery pipe = DoubleQuery
                .of(source)
                .then(prev -> yield -> { throw new AssertionError("This traverse should not be invoked for this pipeline!"); });
        pipe.tryAdvance(item -> {});
    }

    @Test
    public void testMapCollapse() {
        List<Double> expected = asList(7d, 8d, 9d, 11d, 7d);
        double[] source = {7, 7, 8, 9, 9, 11, 11, 7};
        DoubleQuery pipe = DoubleQuery
                .of(source)
                .then(
                    UserExt::collapseAdv,
                    prev -> yield -> { throw new AssertionError("This traverse should not be invoked for this pipeline!"); }
                );
        List<Double> actual = new ArrayList<>();
        while (pipe.tryAdvance(item -> {
            Assert.assertTrue(actual.size() < expected.size());
            actual.add(item);
        })) {
        }
        assertEquals(expected.size(), actual.size());
        assertEquals(expected, actual);
    }


    @Test
    public void testFlatMap() {
        List<Double> expected = asList(1d, 2d, 3d, 4d, 5d, 6d, 7d, 8d, 9d);
        double[] source = {2, 5, 8};
        DoubleQuery pipe = DoubleQuery
                .of(source)
                .flatMap(nr -> DoubleQuery.of(nr - 1, nr, nr + 1));
        int index = 0;
        List<Double> actual = new ArrayList<>();
        while (pipe.tryAdvance(item -> {
            Assert.assertTrue(actual.size() < expected.size());
            actual.add(item);
        })) {
        }
        assertEquals(expected.size(), actual.size());
        assertEquals(expected, actual);
    }

    @Test
    public void testDistinct() {
        List<Double> expected = asList(7d, 8d, 9d, 11d);
        double[] source = {7, 7, 8, 9, 9, 11, 11, 7};
        DoubleQuery pipe = DoubleQuery
                .of(source)
                .distinct();
        List<Double> actual = new ArrayList<>();
        while (pipe.tryAdvance(item -> {
            Assert.assertTrue(actual.size() < expected.size());
            actual.add(item);
        })) {
        }
        assertEquals(expected.size(), actual.size());
        assertEquals(expected, actual);
    }

    @Test
    public void testIterateLimit() {
        List<Double> expected = asList(1d, 3d, 5d, 7d, 9d, 11d, 13d);
        DoubleQuery pipe = DoubleQuery
                .iterate(1, n -> n + 2)
                .limit(7);
        List<Double> actual = new ArrayList<>();
        while (pipe.tryAdvance(item -> {
            Assert.assertTrue(actual.size() < expected.size());
            actual.add(item);
        })) {
        }
        assertEquals(expected.size(), actual.size());
        assertEquals(expected, actual);
    }


    @Test
    public void testPeekCount() {
        List<Double> expected = asList(2d, 4d, 6d, 8d);
        double[] source = {1, 2, 3, 4};
        ArrayList<Double> actual = new ArrayList<Double>();

        DoubleQuery pipe = DoubleQuery
                .of(source)
                .peek(item -> actual.add(item * 2));
        int []count = {0};
        while (pipe.tryAdvance(ignore -> count[0]++)) {
        }
        assertEquals(expected.size(), count[0]);
        assertEquals(expected.size(), actual.size());
        assertEquals(expected, actual);
    }

    @Test
    public void testTakeWhileCount() {
        List<Double> expected = asList(4d, 8d);
        double[] source = {2, 4, 5, 8};
        ArrayList<Double> actual = new ArrayList<Double>();
        DoubleQuery pipe = DoubleQuery
                .of(source)
                .takeWhile(i -> i % 2 == 0)
                .peek(item -> actual.add(item * 2));
        int []count = {0};
        while (pipe.tryAdvance(ignore -> count[0]++)) {
        }
        assertEquals(expected.size(), count[0]);
        assertEquals(expected.size(), actual.size());
        assertEquals(expected, actual);
    }

    @Test
    public void testGenerateLimitLast() {
        List<Double> expected = asList(3d, 4d, 5d, 6d, 7d, 8d, 9d);
        double[] n = new double[]{1};
        DoubleQuery pipe = DoubleQuery
                .generate(() -> 2 + n[0]++)
                .limit(7);
        List<Double> actual = new ArrayList<>();
        while (pipe.tryAdvance(item -> {
            Assert.assertTrue(actual.size() < expected.size());
            actual.add(item);
        })) {
        }
        assertEquals(expected.size(), actual.size());
        assertEquals(expected, actual);
    }

    @Test
    public void testConcat() {
        List<Double> expected = asList(1d, 2d, 3d, 4d);
        double[] source1 = {1, 2};
        double[] source2 = {3, 4};
        DoubleQuery pipe = DoubleQuery.of(source1).concat(DoubleQuery.of(source2));
        List<Double> actual = new ArrayList<>();
        while (pipe.tryAdvance(item -> {
            Assert.assertTrue(actual.size() < expected.size());
            actual.add(item);
        })) {
        }
        assertEquals(expected.size(), actual.size());
        assertEquals(expected, actual);
    }

    @Test
    public void testSorted() {
        List<Double> expected = asList(7d, 8d, 9d, 11d);
        double[] source = {11, 7, 9, 8};
        DoubleQuery pipe = DoubleQuery
                .of(source)
                .sorted();
        List<Double> actual = new ArrayList<>();
        while (pipe.tryAdvance(item -> {
            Assert.assertTrue(actual.size() < expected.size());
            actual.add(item);
        })) {
        }
        assertEquals(expected.size(), actual.size());
        assertEquals(expected, actual);
    }

    @Test
    public void testDropWhile() {
        double delimiter = 9;
        List<Double> expected = asList(delimiter, 11d);
        double[] source = {8, 7, delimiter, 11};
        DoubleQuery pipe = DoubleQuery
                .of(source)
                .dropWhile(i -> delimiter != i);
        List<Double> actual = new ArrayList<>();
        while (pipe.tryAdvance(item -> {
            Assert.assertTrue(actual.size() < expected.size());
            actual.add(item);
        })) {
        }
        assertEquals(expected.size(), actual.size());
        assertEquals(expected, actual);
    }

    @Test
    public void testZip() {
        List<Double> expected = asList(4d, 4d, 4d);
        double[] source1 = {1, 2, 3};
        double[] source2 = {3, 2, 1};
        DoubleQuery pipe = of(source1)
                .zip(of(source2), Double::sum);
        List<Double> actual = new ArrayList<>();
        while (pipe.tryAdvance(item -> {
            Assert.assertTrue(actual.size() < expected.size());
            actual.add(item);
        })) {
        }
        assertEquals(expected.size(), actual.size());
        assertEquals(expected, actual);
    }

    @Test
    public void testMapToObj() {
        List<String> expected = asList("4.0", "4.0", "4.0");
        double[] source1 = {1, 2, 3};
        double[] source2 = {3, 2, 1};
        Query<String> pipe = of(source1)
                .zip(of(source2), Double::sum)
                .mapToObj(String::valueOf);
        List<String> actual = new ArrayList<>();
        while (pipe.tryAdvance(item -> {
            Assert.assertTrue(actual.size() < expected.size());
            actual.add(item);
        })) {
        }
        assertEquals(expected.size(), actual.size());
        assertEquals(expected, actual);
    }

    @Test
    public void testMapToInt() {
        List<Integer> expected = asList(7, 8, 9, 11);
        double[] source = {11, 7, 9, 8};
        IntQuery pipe = DoubleQuery
                .of(source)
                .sorted()
                .asIntQuery();
        List<Integer> actual = new ArrayList<>();
        while (pipe.tryAdvance(item -> {
            Assert.assertTrue(actual.size() < expected.size());
            actual.add(item);
        })) {
        }
        assertEquals(expected.size(), actual.size());
        assertEquals(expected, actual);
    }


    @Test
    public void testMapToLong() {
        List<Long> expected = asList(7L, 8L, 9L, 11L);
        double[] source = {11, 7, 9, 8};
        LongQuery pipe = DoubleQuery
                .of(source)
                .sorted()
                .asLongQuery();
        List<Long> actual = new ArrayList<>();
        while (pipe.tryAdvance(item -> {
            Assert.assertTrue(actual.size() < expected.size());
            actual.add(item);
        })) {
        }
        assertEquals(expected.size(), actual.size());
        assertEquals(expected, actual);
    }
}
