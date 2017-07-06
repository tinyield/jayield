/*
 * Copyright (c) 2017, jasync.org
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.jayield;

import org.testng.annotations.Test;

import java.util.function.Function;
import java.util.function.ToIntFunction;

import static org.testng.Assert.assertEquals;
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
        Series<Integer> nrs = Series.of(arrange);
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
        Series<Integer> nrs = Series.of(arrange);
        Object[] actual = nrs
                .filter(n -> n%2 != 0)
                .map(Object::toString)
                .traverseWith(prev -> yield -> {
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
        Series<Integer> nrs = Series.of(arrange);
        Object[] actual = nrs
                .traverseWith(UserExt::collapse)
                .toArray();
        assertEquals(actual, expected);
    }

    @Test(expectedExceptions = UnsupportedOperationException.class)
    public void testBulkMapFilterOddAndAnyMatch() {
        String[] expected = {"3", "7"};
        Integer[] arrange = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        Series<Integer> nrs = Series.of(arrange);
        boolean actual = nrs
                .filter(n -> n%2 != 0)
                .map(Object::toString)
                .traverseWith(prev -> yield -> {
                    final boolean[] isOdd = {false};
                    prev.traverse(item -> {
                        if(isOdd[0]) yield.ret(item);
                        isOdd[0] = !isOdd[0];
                    });
                })
                .anyMatch(n -> n.equals("7"));
        assertTrue(actual);
    }

    @Test
    public void testBulkFlatMap() {
        Integer[] expected = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        Integer[] arrange = {2, 5, 8};
        Object[] actual = Series
                .of(arrange)
                .flatMap(nr -> Series.of(nr - 1, nr, nr + 1))
                .toArray();
        assertEquals(actual, expected);
    }

    @Test
    public void testBulkDistinctCount() {
        String [] arrange =
                {"a", "x", "v", "d","g", "x", "j", "x", "y","r", "y", "w", "y", "a", "e"};
        long total = Series
                .of(arrange)
                .distinct()
                .count();
        assertEquals(10, total);
    }

    @Test
    public void testBulkMax() {
        String [] arrange = {"a", "x", "v", "d","g","j","y","r","w","a","e"};
        String actual = Series
                .of(arrange)
                .max(String.CASE_INSENSITIVE_ORDER)
                .get();
        assertEquals("y", actual);
    }

    @Test
    public void testBulkMaxInt() {
        Integer[] arrange = {7, 7, 8, 31, 9, 9, 11, 11, 7, 23, 31, 23};
        int actual = Series
                .of(arrange)
                .mapToInt(n -> n)
                .max()
                .getAsInt();
        assertEquals(31, actual);
    }

    @Test
    public void testBulkIterateLimitMax() {
        int actual = Series
                .iterate(1, n -> n + 2)
                .limit(7)
                .max(Integer::compare)
                .get();
        assertEquals(13, actual);
    }
}
