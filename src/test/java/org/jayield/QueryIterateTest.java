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
import java.util.Spliterator;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static org.jayield.Query.fromStream;
import static org.jayield.UserExt.collapse;
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
        Object[] expected = {"a1", "b2", "c3", "d4", "e5", "f6", "g7"};
        Query<Integer> nrs = Query.of(1, 2, 3, 4, 5, 6, 7, 8, 9);
        List<Object> actual = new ArrayList<>();
        Query<String> pipe = Query
            .of('a', 'b', 'c', 'd', 'e', 'f', 'g')
            .zip(nrs, (c, n) -> "" + c + n);
        while(pipe.hasNext()) { actual.add(pipe.next()); }
        assertEquals(actual.toArray(), expected);
    }


    @Test
    public void testMapFilter() {
        Object[] expected = {"5", "7", "9"};
        List<Integer> arrange = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9);
        Query<Integer> nrs = Query.fromList(arrange);
        Query<String> pipe = nrs
            .filter(n -> n % 2 != 0)
            .map(Object::toString)
            .skip(2);
        List<Object> actual = new ArrayList<>();
        while(pipe.hasNext()) { actual.add(pipe.next()); }
        assertEquals(actual.toArray(), expected);
    }

    @Test
    public void testBulkMapFilterOdd() {
        Object[] expected = {"3", "7"};
        List<Integer> arrange = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9);
        Query<Integer> nrs = Query.fromList(arrange);
        List<Object> actual = new ArrayList<>();
        Query<String> pipe = nrs
                .filter(n -> n%2 != 0)
                .map(Object::toString)
                .then(prev -> yield -> {
                    final boolean[] isOdd = {false};
                    prev.traverse(item -> {
                        if(isOdd[0]) yield.ret(item);
                        isOdd[0] = !isOdd[0];
                    });
                });
        while(pipe.hasNext()) { actual.add(pipe.next()); }
        assertEquals(actual.toArray(), expected);
    }

    @Test
    public void testMapCollapse() {
        Object[] expected= {7, 8, 9, 11, 7};
        Integer[] arrange = {7, 7, 8, 9, 9, 11, 11, 7};
        List<Object> actual = new ArrayList<>();
        Query<Integer> pipe = Query
                .of(arrange)
                .then(n -> collapse(n));
        while(pipe.hasNext()) { actual.add(pipe.next()); }
        assertEquals(actual.toArray(), expected);
    }


    @Test
    public void testFlatMap() {
        Object[] expected = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        Integer[] arrange = {2, 5, 8};
        List<Object> actual = new ArrayList<>();
        Query<Integer> pipe = Query
                .of(arrange)
                .flatMap(nr -> Query.of(nr - 1, nr, nr + 1));
        while(pipe.hasNext()) { actual.add(pipe.next()); }
        assertEquals(actual.toArray(), expected);
    }

    @Test
    public void testDistinctCount() {
        String [] arrange =
                {"a", "x", "v", "d","g", "x", "j", "x", "y","r", "y", "w", "y", "a", "e"};
        Query<String> pipe = Query
                .of(arrange)
                .distinct();
        int total = 0;
        while(pipe.hasNext()) { pipe.next(); total++; }
        assertEquals(10, total);
    }

    @Test
    public void testIterateLimitLast() {
        Query<Integer> pipe = Query
            .iterate(1, n -> n + 2)
            .limit(7);
        int actual = 0;
        while(pipe.hasNext()) { actual = pipe.next(); }
        assertEquals(13, actual);
    }


    @Test
    public void testPeekCount() {
        Integer[] arrange = {1, 2, 3};
        List<Integer> actual = new ArrayList<>();
        Query<Integer> pipe = Query.of(arrange)
            .peek(item -> actual.add(item * 2));
        int count = 0;
        while(pipe.hasNext()) { pipe.next(); count++; }
        assertEquals(count, 3);
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
        int count = 0;
        while(pipe.hasNext()) { pipe.next(); count++; }
        assertEquals(count, 1);
        assertEquals(actual.size(), 1);
        assertFalse(actual.containsAll(asList("a", "x", "v")));
        assertEquals(actual.get(0), "a");
    }
}
