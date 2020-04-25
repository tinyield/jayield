/*
 * Copyright (c) 2020, Fernando Miguel Carvalho, mcarvalho@cc.isel.ipl.pt
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
 * @author Miguel Gamboa
 */
public class QueryToStreamTest {

    @Test
    public void testFromStream() {
        Integer[] src = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        Iterator<Integer> expected = Stream.of(src).iterator();
        Query<Integer> nrs = fromStream(Stream.of(1, 2, 3, 4, 5, 6, 7, 8, 9));
        while(nrs.hasNext()) {
            assertEquals(nrs.next(), expected.next());
        }
        assertFalse(expected.hasNext());
    }
    @Test
    public void testFromAndToStream() {
        Integer[] src = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        Iterator<Integer> expected = Stream.of(src).iterator();
        Query<Integer> nrs = fromStream(Stream.of(1, 2, 3, 4, 5, 6, 7, 8, 9));
        Spliterator<Integer> actual = nrs.toStream().spliterator();
        while(actual.tryAdvance(curr -> assertEquals(curr, expected.next()))) {}
        assertFalse(expected.hasNext());
    }
}
