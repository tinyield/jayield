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

package org.jayield.primitives.lng;

import org.testng.annotations.Test;

import java.util.PrimitiveIterator;
import java.util.Spliterator;
import java.util.stream.LongStream;

import static org.jayield.primitives.lng.LongQuery.fromStream;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;

/**
 * @author Miguel Gamboa
 */
public class LongQueryToLongStreamTest {

    @Test
    public void testFromStream() {
        long[] src = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        PrimitiveIterator.OfLong expected = LongStream.of(src).iterator();
        LongQuery nrs = fromStream(LongStream.of(src));
        while (nrs.tryAdvance(item -> assertEquals(item, expected.nextLong()))) {
        }
        assertFalse(expected.hasNext());
    }

    @Test
    public void testFromAndToStream() {
        long[] src = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        PrimitiveIterator.OfLong expected = LongStream.of(src).iterator();
        LongQuery nrs = fromStream(LongStream.of(src));
        Spliterator<Long> actual = nrs.toStream().spliterator();
        while (actual.tryAdvance(curr -> assertEquals(curr, expected.next()))) {
        }
        assertFalse(expected.hasNext());
    }
}
