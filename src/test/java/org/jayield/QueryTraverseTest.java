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

import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static org.jayield.Query.fromList;
import static org.jayield.Query.fromStream;
import static org.jayield.Query.iterate;
import static org.jayield.Query.of;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * These tests aim to evaluate only the execution of traverse()
 * along the entire pipeline.
 * Each operation should forward the computation through the traverse()
 * method of the upstream.
 *
 * @author Miguel Gamboa
 *         created on 03-06-2017
 */
public class QueryTraverseTest {

    @Test
    public void testBulkFromAndToStream() {
        Integer[] src = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        Iterator<Integer> expected = Stream.of(src).iterator();
        Query<Integer> nrs = fromStream(Stream.of(1, 2, 3, 4, 5, 6, 7, 8, 9));
        nrs
            .toStream()
            .forEach(actual -> assertEquals(actual, expected.next()));
        assertFalse(expected.hasNext());
    }


    @Test
    public void testBulkZip() {
        String[] expected = {"a1", "b2", "c3", "d4", "e5", "f6", "g7"};
        Query<Character> cs = of('a', 'b', 'c', 'd', 'e', 'f', 'g');
        Query<Integer> nrs = of(1, 2, 3, 4, 5, 6, 7, 8, 9);
        Object[] actual = cs
                .zip(nrs, (c,n) -> "" + c + n)
                .toArray();
        assertEquals(actual, expected);
    }


    @Test
    public void testBulkMapFilter() {
        String[] expected = {"5", "7", "9"};
        Integer[] arrange = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        Query<Integer> nrs = of(arrange);
        Object[] actual = nrs
                .filter(n -> n%2 != 0)
                .map(Object::toString)
                .skip(2)
                .toArray();
        assertEquals(actual, expected);
    }

    @Test
    public void testBulkMapFilterOdd() {
        String[] expected = {"3", "7"};
        Integer[] arrange = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        Query<Integer> nrs = of(arrange);
        Object[] actual = nrs
                .filter(n -> n%2 != 0)
                .map(Object::toString)
                .then(UserExt::oddTrav)
                .toArray();
        assertEquals(actual, expected);
    }

    @Test
    public void testBulkMapCollapse() {
        Integer[] expected= {7, 8, 9, 11, 7};
        Integer[] arrange = {7, 7, 8, 9, 9, 11, 11, 7};
        Object[] actual = of(arrange)
                .then(UserExt::collapseTrav)
                .toArray();
        assertEquals(actual, expected);
    }

    @Test
    public void testBulkMapFilterOddAndAnyMatch() {
        String[] expected = {"3", "7"};
        Integer[] arrange = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        Query<Integer> nrs = of(arrange);
        boolean actual = nrs
                .filter(n -> n%2 != 0)
                .map(Object::toString)
                .then(UserExt::oddTrav)
                .anyMatch(n ->
                        n.equals("7"));
        assertTrue(actual);
    }
    @Test
    public void testAllMatchForAllElements() {
        Integer[] arrange = {2, 4, 6, 8, 10, 12};
        boolean actual = of(arrange).allMatch(nr -> nr % 2 == 0);
        assertEquals(actual, true);
    }
    @Test
    public void testAllMatchFailOnIntruder() {
        Integer[] arrange = {2, 4, 6, 7, 10, 12};
        int[] count = {0};
        boolean actual = of(arrange).peek(__ -> count[0]++).allMatch(nr -> nr % 2 == 0);
        assertEquals(actual, false);
        assertEquals(count[0], 4);
    }
    @Test
    public void testBulkFlatMap() {
        Integer[] expected = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        Integer[] arrange = {2, 5, 8};
        Object[] actual = of(arrange)
                .flatMap(nr -> of(nr - 1, nr, nr + 1))
                .toArray();
        assertEquals(actual, expected);
    }

    @Test
    public void testShortCircuitFlatMap() {
        Integer[] arrange = {2, 5, 8};
        int actual = of(arrange)
                .flatMap(nr -> of(nr - 1, nr, nr + 1))
                .findFirst()
                .get();
        assertEquals(actual, 1);
    }

    @Test
    public void testShortCircuitOnEmptySequence() {
        Integer[] arrange = {};
        Optional<Integer> actual = of(arrange)
                .findFirst();
        assertTrue(actual.isEmpty());
    }

    @Test
    public void testBulkDistinctCount() {
        String [] arrange =
                {"a", "x", "v", "d","g", "x", "j", "x", "y","r", "y", "w", "y", "a", "e"};
        long total = of(arrange)
                .distinct()
                .count();
        assertEquals(total, 10);
    }

    @Test
    public void testBulkMax() {
        String [] arrange = {"a", "x", "v", "d","g","j","y","r","w","a","e"};
        String actual = of(arrange)
                .max(String.CASE_INSENSITIVE_ORDER)
                .get();
        assertEquals(actual, "y");
    }

    @Test
    public void testBulkMaxInt() {
        int expected = 31;
        Integer[] arrange = {7, 7, 8, expected, 9, 9, 11, 11, 7, 23, expected, 23};
        int actual = of(arrange)
                .mapToInt(n -> n)
                .max()
                .orElseThrow();
        assertEquals(actual, expected);
    }

    @Test
    public void testBulkMaxLong() {
        int expected = 31;
        Integer[] arrange = {7, 7, 8, expected, 9, 9, 11, 11, 7, 23, expected, 23};
        long actual = of(arrange)
                .mapToLong(n -> n)
                .max()
                .orElseThrow();
        assertEquals(actual, expected);
    }

    @Test
    public void testBulkMaxDouble() {
        double expected = 31.0;
        Double[] arrange = {7D, 7D, 8D, expected, 9D, 9D, 11D, 11D, 7D, 23D, expected, 23D};
        double actual = of(arrange)
                .mapToDouble(n -> n)
                .max()
                .orElseThrow();
        assertEquals(actual, expected);
    }

    @Test
    public void testBulkIterateLimitMax() {
        int actual = iterate(1, n -> n + 2)
                .limit(7)
                .max(Integer::compare)
                .get();
        assertEquals(actual, 13);
    }

    @Test
    public void testBulkIterateTakeWhileMax() {
        int actual = iterate(1, n -> n + 2)
                .takeWhile(n -> n < 14)
                .max(Integer::compare)
                .get();
        assertEquals(actual, 13);
    }

    @Test
    public void testBulkPeekCount() {
        Integer[] arrange = {1, 2, 3};
        List<Integer> actual = new ArrayList<>();
        long count = of(arrange)
                .peek(item -> actual.add(item * 2))
                .count();
        assertEquals(count, 3);
        assertEquals(actual.size(), 3);
        assertTrue(actual.containsAll(asList(2,4,6)));
    }

    @Test
    public void testBulkTakeWhileCount() {
        String [] arrange = {"a", "x", "v"};
        List<String> helper = Arrays.asList(arrange);
        List<String> actual= new ArrayList<>();
        Query<String> series = of(arrange);
        long count = series.takeWhile(item -> helper.indexOf(item) % 2 == 0)
                .peek(actual::add)
                .count();
        assertEquals(count, 1);
        assertEquals(actual.size(), 1);
        assertFalse(actual.containsAll(asList("a", "x", "v")));
        assertEquals(actual.get(0), "a");
    }

    @Test
    public void testForEach() {
        Integer[] input = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        Iterator<Integer> expected = Stream.of(input).iterator();
        Query<Integer> nrs = fromStream(Stream.of(1, 2, 3, 4, 5, 6, 7, 8, 9));
        nrs.forEach(actual -> assertEquals(actual, expected.next()));
        assertFalse(expected.hasNext());
    }

    @Test
    public void testToSet() {
        List<String> input = asList("a", "x", "v", "d", "g", "x", "j", "x", "y", "r", "y", "w", "y", "a", "e");
        long actual = fromList(input).toSet().size();
        assertEquals(actual, 10);
    }

    @Test
    public void testJoin() {
        String[] input = {"a", "b", "c"};
        String expected = "abc";
        String actual = of(input).join();
        assertEquals(actual, expected);
    }

    @Test
    public void testReduce() {
        String[] input = {"a", "b", "c"};
        String expected = "abc";
        String actual = of(input).reduce((p, c) -> p + c).orElseThrow();
        assertEquals(actual, expected);
    }

   @Test
    public void testFlatMapAndReduce() {
        List<Query<String>> input = new ArrayList<>();
        input.add(Query.of("a"));
        input.add(Query.of("b"));
        input.add(Query.of("c"));
        String expected = "abc";
        String actual = fromList(input).flatMap(s -> s).reduce((p, c) -> p + c).orElseThrow();
        assertEquals(actual, expected);
    }

    @Test
    public void testFromListFlatMapAndReduce() {
        List<Query<String>> input = new ArrayList<>();
        input.add(fromList(List.of("a")));
        input.add(fromList(List.of("b")));
        input.add(fromList(List.of("c")));
        String expected = "abc";
        String actual = fromList(input).flatMap(s -> s).reduce((p, c) -> p + c).orElseThrow();
        assertEquals(actual, expected);
    }

    @Test
    public void testReduceOnEmpty() {
        String[] input = {};
        assertTrue(of(input).reduce((p, c) -> p + c).isEmpty());
    }

    @Test
    public void testReduceOnEmptyWithIdentity() {
        String[] input = {};
        String expected = "a";
        String actual = of(input).reduce(expected, (p, c) -> p + c);
        assertEquals(actual, expected);
    }

    @Test
    public void testNoneMatchFail() {
        boolean actual = Query.generate(() -> 1).noneMatch(integer -> integer == 1);
        assertFalse(actual);
    }

    @Test
    public void testNoneMatchSuccess() {
        String[] input = {"a", "b", "c"};
        assertTrue(of(input).noneMatch("d"::equals));
    }

    @Test
    public void testFindAnySuccess() {
        int[] i = new int[]{0};
        Integer expected = 11;
        Integer actual = Query.generate(() -> i[0]++)
                              .filter(integer -> integer > 10)
                              .findAny()
                              .orElseThrow();
        assertEquals(actual, expected);
    }

    @Test
    public void testFindAnyFailure() {
        String[] input = {"a", "b", "c"};
        Optional<String> actual = of(input)
                .filter("d"::equals)
                .findAny();
        assertFalse(actual.isPresent());
    }

    @Test
    public void testBulkMin() {
        String [] arrange = {"a", "x", "v", "d","g","j","y","r","w","a","e"};
        String expected = "a";
        String actual = of(arrange)
                .min(String.CASE_INSENSITIVE_ORDER)
                .orElseThrow();
        assertEquals(actual, expected);
    }

    @Test
    public void testConcat() {
        String[] input1 = new String[]{"a", "b"};
        String[] input2 = new String[]{"c", "d"};
        String[] expected = new String[]{"a", "b", "c", "d"};
        String[] actual = of(input1).concat(of(input2)).toArray(String[]::new);
        assertEquals(actual, expected);
    }

    @Test
    public void testSorted() {
        String[] input = new String[]{"b", "d", "a", "c"};
        String[] expected = new String[]{"a", "b", "c", "d"};
        String[] actual = Query.of(input)
                               .sorted(String::compareTo)
                               .toArray(String[]::new);
        assertEquals(actual, expected);
    }

    @Test
    public void testDropWhile() {
        String delimiter = "c";
        String[] input = new String[]{"a", "b", delimiter, "d", "e"};
        String[] expected = new String[]{delimiter, "d", "e"};
        String[] actual = Query.of(input)
                               .dropWhile(s -> !delimiter.equals(s))
                               .toArray(String[]::new);
        assertEquals(actual, expected);
    }
}
