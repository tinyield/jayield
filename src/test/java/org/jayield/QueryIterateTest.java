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

import org.jayield.primitives.dbl.DoubleQuery;
import org.jayield.primitives.intgr.IntQuery;
import org.jayield.primitives.lng.LongQuery;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.Arrays.asList;
import static org.jayield.Query.of;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * These tests aim to evaluate only the execution of hasNext() and next()
 * along the entire pipeline.
 * Each operation should forward the computation through the hasNext() and next()
 * methods of the upstream.
 *
 * @author Miguel Gamboa
 *         created on 03-06-2017
 */
public class QueryIterateTest {

    @Test
    public void testZip() {
        List<String> expected = asList("a1", "b2", "c3", "d4", "e5", "f6", "g7");
        Query<Integer> nrs = Query.of(1, 2, 3, 4, 5, 6, 7, 8, 9);
        Query<String> pipe = Query
            .of('a', 'b', 'c', 'd', 'e', 'f', 'g')
            .zip(nrs, (c, n) -> "" + c + n);
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
    public void testMapFilter() {
        List<String> expected = asList("5", "7", "9");
        List<Integer> arrange = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9);
        Query<Integer> nrs = Query.fromList(arrange);
        Query<String> pipe = nrs
            .filter(n -> n % 2 != 0)
            .map(Object::toString)
            .skip(2);
        List<String> actual = new ArrayList<>();
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
        List<Integer> arrange = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9);
        Query<Integer> nrs = Query.fromList(arrange);
        Query<String> pipe = nrs
                .filter(n -> n%2 != 0)
                .map(Object::toString)
                .then(prev -> yield -> { throw new AssertionError("This traverse should not be invoked for this pipeline!"); });
        pipe.tryAdvance(item -> {});
    }

    @Test
    public void testBulkMapFilterOdd() {
        Object[] expected = {"3", "7"};
        List<Integer> arrange = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9);
        List<Object> actual = new ArrayList<>();
        Query<Integer> nrs = Query.fromList(arrange);
        Query<String> pipe = nrs
                .filter(n -> n%2 != 0)
                .map(Object::toString)
                .then(
                    UserExt::oddAdv,
                    prev -> yield -> { throw new AssertionError("This traverse should not be invoked for this pipeline!"); }
                );
        while(pipe.tryAdvance(actual::add)) { }
        assertEquals(actual.toArray(), expected);
    }


    @Test(expectedExceptions = { UnsupportedOperationException.class })
    public void testMapCollapseFail() {
        Integer[] arrange = {7, 7, 8, 9, 9, 11, 11, 7};
        Query<Integer> pipe = Query
                .of(arrange)
                .then(
                    prev -> yield -> { throw new AssertionError("This traverse should not be invoked for this pipeline!"); }
                );
        pipe.tryAdvance(item -> {});
    }

    @Test
    public void testMapCollapse() {
        List<Integer> expected= asList(7, 8, 9, 11, 7);
        Integer[] arrange = {7, 7, 8, 9, 9, 11, 11, 7};
        Query<Integer> pipe = Query
                .of(arrange)
                .then(
                    UserExt::collapseAdv,
                    prev -> yield -> { throw new AssertionError("This traverse should not be invoked for this pipeline!"); }
                );
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
    public void testFlatMap() {
        List<Integer> expected = asList(1, 2, 3, 4, 5, 6, 7, 8, 9);
        Integer[] arrange = {2, 5, 8};
        Query<Integer> pipe = Query
                .of(arrange)
                .flatMap(nr -> Query.of(nr - 1, nr, nr + 1));
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
    public void testDistinctCount() {
        String [] arrange =
                {"a", "x", "v", "d","g", "x", "j", "x", "y","r", "y", "w", "y", "a", "e"};
        Query<String> pipe = Query
                .of(arrange)
                .distinct();
        int [] count = {0};
        while (pipe.tryAdvance(ignore -> count[0]++)) {
        }
        assertEquals(count[0], 10);
    }

    @Test
    public void testIterateLimitLast() {
        Query<Integer> pipe = Query
            .iterate(1, n -> n + 2)
            .limit(7);
        int [] last = {-1};
        while (pipe.tryAdvance(item -> last[0] = item)) {
        }
        assertEquals(last[0], 13);
    }


    @Test
    public void testPeekCount() {
        Integer[] arrange = {1, 2, 3};
        List<Integer> actual = new ArrayList<>();
        Query<Integer> pipe = Query.of(arrange)
            .peek(item -> actual.add(item * 2));
        int [] count = {0};
        while (pipe.tryAdvance(ignore -> count[0]++)) {
        }
        assertEquals(count[0], 3);
        assertEquals(actual.size(), 3);
        assertTrue(actual.containsAll(asList(2,4,6)));
    }

    @Test
    public void testTakeWhileCount() {
        String [] arrange = {"a", "x", "v"};
        List<String> helper = Arrays.asList(arrange);
        List<String> actual= new ArrayList<>();
        Query<String> pipe = Query.of(arrange)
            .takeWhile(item -> helper.indexOf(item) % 2 == 0)
            .peek(actual::add);
        int [] count = {0};
        while (pipe.tryAdvance(ignore -> count[0]++)) {
        }
        assertEquals(count[0], 1);
        assertEquals(actual.size(), 1);
        assertFalse(actual.containsAll(asList("a", "x", "v")));
        assertEquals(actual.get(0), "a");
    }

    @Test
    public void testGenerateLimitLast() {
        int[] n = new int[]{1};
        int expected = 9;
        Query<Integer> pipe = Query
                .generate(() -> 2 + n[0]++)
                .takeWhile(nr -> nr < 10);
        int [] last = {-1};
        while (pipe.tryAdvance(item -> last[0] = item)) {
        }
        assertEquals(last[0], expected);
    }

    @Test
    public void testConcat() {
        String[] input1 = new String[]{"a", "b"};
        String[] input2 = new String[]{"c", "d"};
        List<String> expected = asList("a", "b", "c", "d");
        Query<String> pipe = Query.of(input1).concat(Query.of(input2));
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
    public void testSorted() {
        String[] input = new String[]{"b", "d", "a", "c"};
        List<String> expected = asList("a", "b", "c", "d");
        Query<String> pipe = Query.of(input).sorted(String::compareTo);
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
    public void testDropWhile() {
        String delimiter = "c";
        String[] input = new String[]{"a", "b", delimiter, "d", "e"};
        List<String> expected = asList(delimiter, "d", "e");
        Query<String> pipe = Query.of(input).dropWhile(s -> !delimiter.equals(s));
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
        List<Integer> expected = asList(1, 2, 3);
        String[] arrange = {"1", "2", "3"};
        IntQuery pipe = of(arrange)
                .mapToInt(Integer::valueOf);
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
        List<Long> expected = asList(1L, 2L, 3L);
        String[] arrange = {"1", "2", "3"};
        LongQuery pipe = of(arrange)
                .mapToLong(Long::valueOf);
        List<Long> actual = new ArrayList<>();
        while (pipe.tryAdvance(item -> {
            Assert.assertTrue(actual.size() < expected.size());
            actual.add(item);
        })) {
        }
        assertEquals(expected.size(), actual.size());
        assertEquals(expected, actual);
    }

    @Test
    public void testMapToDouble() {
        List<Double> expected = asList(1d, 2d, 3d);
        String[] arrange = {"1", "2", "3"};
        DoubleQuery pipe = of(arrange)
                .mapToDouble(Double::valueOf);
        List<Double> actual = new ArrayList<>();
        while (pipe.tryAdvance(item -> {
            Assert.assertTrue(actual.size() < expected.size());
            actual.add(item);
        })) {
        }
        assertEquals(expected.size(), actual.size());
        assertEquals(expected, actual);
    }
}
