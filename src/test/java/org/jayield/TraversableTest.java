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
import java.util.List;

import static java.util.Arrays.asList;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * @author Miguel Gamboa
 *         created on 03-06-2017
 */
public class TraversableTest {

    @Test
    public void testBulkMapFilter() {
        String[] expected = {"5", "7", "9"};
        Integer[] arrange = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        Traversable<Integer> nrs = Traversable.of(arrange);
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
        Traversable<Integer> nrs = Traversable.of(arrange);
        Object[] actual = nrs
                .filter(n -> n%2 != 0)
                .map(Object::toString)
                .then(prev -> yield -> {
                    final boolean[] isOdd = {false};
                    prev.traverse(item -> {
                        if(isOdd[0]) yield.ret(item);
                        isOdd[0] = !isOdd[0];
                    });
                })
                .toArray();
        assertEquals(actual, expected);
    }

    @Test
    public void testBulkMapCollapse() {
        Integer[] expected= {7, 8, 9, 11, 7};
        Integer[] arrange = {7, 7, 8, 9, 9, 11, 11, 7};
        Traversable<Integer> nrs = Traversable.of(arrange);
        Object[] actual = nrs
                .then(UserExt::collapse)
                .toArray();
        assertEquals(actual, expected);
    }

    @Test
    public void testBulkMapFilterOddAndAnyMatch() {
        String[] expected = {"3", "7"};
        Integer[] arrange = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        Traversable<Integer> nrs = Traversable.of(arrange);
        boolean actual = nrs
                .filter(n -> n%2 != 0)
                .map(Object::toString)
                .then(prev -> (yield) -> {
                    final boolean[] isOdd = {false};
                    prev.traverse(item -> {
                        if(isOdd[0]) yield.ret(item);
                        isOdd[0] = !isOdd[0];
                    });
                })
                .anyMatch(n ->
                        n.equals("7"));
        assertTrue(actual);
    }

    @Test
    public void testBulkFlatMap() {
        Integer[] expected = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        Integer[] arrange = {2, 5, 8};
        Object[] actual = Traversable
                .of(arrange)
                .flatMap(nr -> Traversable.of(nr - 1, nr, nr + 1))
                .toArray();
        assertEquals(actual, expected);
    }

    @Test
    public void testShortCircuitFlatMap() {
        Integer[] arrange = {2, 5, 8};
        int actual = Traversable
                .of(arrange)
                .flatMap(nr -> Traversable.of(nr - 1, nr, nr + 1))
                .findFirst()
                .get();
        assertEquals(1, actual);
    }


    @Test
    public void testBulkDistinctCount() {
        String [] arrange =
                {"a", "x", "v", "d","g", "x", "j", "x", "y","r", "y", "w", "y", "a", "e"};
        long total = Traversable
                .of(arrange)
                .distinct()
                .count();
        assertEquals(10, total);
    }

    @Test
    public void testBulkMax() {
        String [] arrange = {"a", "x", "v", "d","g","j","y","r","w","a","e"};
        String actual = Traversable
                .of(arrange)
                .max(String.CASE_INSENSITIVE_ORDER)
                .get();
        assertEquals("y", actual);
    }

    @Test
    public void testBulkMaxInt() {
        Integer[] arrange = {7, 7, 8, 31, 9, 9, 11, 11, 7, 23, 31, 23};
        int actual = Traversable
                .of(arrange)
                .mapToInt(n -> n)
                .max()
                .getAsInt();
        assertEquals(31, actual);
    }

    @Test
    public void testBulkIterateLimitMax() {
        int actual = Traversable
                .iterate(1, n -> n + 2)
                .limit(7)
                .max(Integer::compare)
                .get();
        assertEquals(13, actual);
    }


    @Test
    public void testBulkPeekCount() {
        Integer[] arrange = {1, 2, 3};
        List<Integer> actual = new ArrayList<>();
        long count = Traversable.of(arrange)
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
        Traversable<String> series = Traversable.of(arrange);
        long count = series.takeWhile(item -> helper.indexOf(item) % 2 == 0)
                .peek(actual::add)
                .count();
        assertEquals(count, 1);
        assertEquals(actual.size(), 1);
        assertFalse(actual.containsAll(asList("a", "x", "v")));
        assertEquals(actual.get(0), "a");
    }
}
