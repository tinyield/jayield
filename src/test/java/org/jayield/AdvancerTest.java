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

import java.util.*;
import java.util.function.Supplier;

import static java.util.Arrays.asList;
import static org.testng.Assert.*;

/**
 * @author Miguel Gamboa
 *         created on 06-07-2017
 */
public class AdvancerTest {

    @Test
    public void testIndividuallyMapFilter() {
        Integer[] arrange = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        Traversable<Integer> nrs = Traversable.of(arrange);
        String actual = nrs
                .filter(n -> n%2 != 0)
                .map(Object::toString)
                .skip(2)
                .iterator()
                .next();
        assertEquals(actual, "5");
    }

    @Test
    public void testIndividuallyMaxInt() {
        class Max {
            int actual = Integer.MIN_VALUE;
            void set(int n) {
                if(n > actual)
                    actual = n;
            }
        }
        Integer[] arrange = {7, 7, 8, 31, 9, 9, 11, 11, 7, 23, 31, 23};
        IntAdvancer adv = Traversable
                .of(arrange)
                .mapToInt(n -> n)
                .intAdvancer();
        Max max = new Max();
        IntYield y = max::set;
        while(adv.tryAdvance(y)){ }
        assertEquals(31, max.actual);
    }

    @Test
    public void testIndividuallyFlatMap() {
        Integer[] expected = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        Integer[] arrange = {2, 5, 8};
        Advancer<Integer> adv = Traversable
                .of(arrange)
                .flatMap(nr -> Traversable.of(nr - 1, nr, nr + 1))
                .advancer();
        List<Integer> res = new ArrayList<>();
        while(adv.tryAdvance(res::add)){ }
        Object[] actual = res.toArray();
        assertEquals(actual, expected);
    }

    @Test
    public void testIndividuallyMapFilterOdd() {
        Integer[] arrange = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        Traversable<Integer> nrs = Traversable.of(arrange);
        String actual = nrs
                .filter(n -> n%2 != 0)
                .map(Object::toString)
                .advancer()
                .<String>then(prev -> yield -> {
                    if(!prev.tryAdvance(item -> {})) return false;
                    return prev.tryAdvance(yield);
                })
                .iterator()
                .next();
        assertEquals(actual, "3");
    }

    @Test
    public void testIndividuallyIterateLimit() {
        final int LIMIT = 7;
        Iterator<Integer> iter = Traversable
                .iterate(1, n -> n + 2)
                .limit(LIMIT )
                .advancer()
                .iterator();
        for (int i = 0; i < LIMIT - 1; i++) iter.next();
        int actual =iter.next();
        assertEquals(13, actual);
    }

    @Test
    public void testIndividuallyFirstOnEmpty() {
        String[] arrange = {};
        boolean hasNext = Traversable
                .of(arrange)
                .iterator()
                .hasNext();
        assertTrue(!hasNext);
    }

    @Test
    public void testIndividuallyDistinctCount() {
        String [] expected = {"a", "x", "v", "d", "g", "j", "y", "r", "w", "e"};
        String [] arrange =
                {"a", "x", "v", "d","g", "x", "j", "x", "y","r", "y", "w", "y", "a", "e"};
        Supplier<Traversable<String>> sup = () -> Traversable.of(arrange).distinct();
        for (int i = 0; i < expected.length; i++) {
            assertEquals(sup.get().skip(i).iterator().next(), expected[i]);
        }
    }

    @Test
    public void testIndividuallyPeek() {
        Integer [] arrange = {1, 2, 3};
        List<Integer> actual = new ArrayList<>();
        int value = Traversable.of(arrange)
                .peek(item -> actual.add(item * 2))
                .iterator()
                .next();
        assertEquals(value, 1);
        assertEquals(actual.size(), 1);
        assertEquals(actual.get(0).intValue(), 2);
    }
    @Test
    public void testIndividuallyTakeWhileCount() {
        String [] arrange = {"a", "x", "v"};
        List<String> helper = Arrays.asList(arrange);
        List<String> actual= new ArrayList<>();
        Traversable<String> series = Traversable.of(arrange);
        Advancer<String> adv = series
                .takeWhile(item -> helper.indexOf(item) % 2 == 0)
                .peek(actual::add)
                .advancer();
        int count = 0;
        while(adv.tryAdvance(item -> {})) count++;
        assertEquals(count, 1);
        assertEquals(actual.size(), 1);
        assertFalse(actual.containsAll(asList("a", "x", "v")));
        assertEquals(actual.get(0), "a");
    }
}
