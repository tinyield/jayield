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

package org.jayield.async;

import org.jayield.AsyncQuery;
import org.jayield.lastfm.LastfmWebApi;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;

import static java.lang.System.out;
import static java.util.Arrays.asList;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

public class AsyncQueryTest {

    public void exampleForReadme() {
        AsyncQuery
            .of("muse", "cure", "radiohead")                   // AsyncQuery<String>
            .map(artist -> LastfmWebApi.topTracks(artist, 3))  // AsyncQuery<CF<Track[]>>
            .flatMapMerge(AsyncQuery::of)                      // AsyncQuery<Track[]>
            .flatMapMerge(AsyncQuery::of)                      // AsyncQuery<Track>
            .subscribe((track, err) -> {
                if(err != null) out.println(err);
                else out.println(track.getName());
            })
            .join(); // block if you want to wait for completion
        // Removing former join this unit test will finish before AsyncQuery processing completion.
    }

    @Test
    public void testOfArrayAndBlockingSubscribe() {
        Iterator<String> expected = asList("a", "b", "c", "d").iterator();
        AsyncQuery
            .of("a b c d".split(" "))
            .onNext((item, err) -> {
                assertNull(err);
                assertEquals(expected.next(), item);
            })
            .blockingSubscribe();
        assertFalse(expected.hasNext());
    }
    @Test
    public void testOfIteratorAndBlockingSubscribe() {
        Iterable<String> source = asList("a", "b", "c", "d");
        Iterator<String> expected = source.iterator();
        AsyncQuery
            .of(source.iterator())
            .onNext((item, err) -> {
                assertNull(err);
                assertEquals(expected.next(), item);
            })
            .blockingSubscribe();
        assertFalse(expected.hasNext());
    }

    static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testOfCompletableFutureAndBlockingSubscribe() {
        List<Integer> source = new ArrayList<>(asList(50, 40, 70, 20, 35));
        AsyncQuery
            .of(source.iterator())
            .map(ms -> CompletableFuture.supplyAsync(() -> {
                sleep(ms);
                return ms;
            }))
            .flatMapMerge(AsyncQuery::of)
            .onNext((item, err) -> {
                out.println(item);
                assertNull(err);
                assertTrue(source.remove(item), "Missing item: " + item);
            })
            .blockingSubscribe();
        assertTrue(source.isEmpty());
    }

    @Test
    public void testSkip() {
        Iterator<Integer> expected = asList(4, 5, 6, 7).iterator();
        AsyncQuery
            .fork(1, 2, 3, 4, 5, 6, 7)
            .skip(3)
            .subscribe((item, err) -> {
                assertNull(err);
                assertEquals(expected.next(), item);
            })
            .join();
        assertFalse(expected.hasNext());
    }

    @Test
    public void testFilter() {
        Iterator<Integer> expected = asList(1, 3, 5, 7, 9).iterator();
        AsyncQuery
            .fork(1, 2, 3, 4, 5, 6, 7, 8, 9)
            .filter(n -> n % 2 != 0)
            .subscribe((item, err) -> {
                assertNull(err);
                assertEquals(expected.next(), item);
            })
            .join();
        assertFalse(expected.hasNext());
    }

    @Test
    public void testFilterAndMap() {
        Iterator<Integer> expected = asList(3, 3, 5).iterator();
        AsyncQuery
            .fork("abc", "abcd", "ab", "bad", "super", "isel")
            .map(String::length)
            .filter(n -> n % 2 != 0)
            .subscribe((item, err) -> {
                assertNull(err);
                assertEquals(expected.next(), item);
            })
            .join();
        assertFalse(expected.hasNext());
    }

    @Test
    public void testDistinct() {
        Iterator<String> expected = asList("ana", "jose", "maria", "joana").iterator();
        AsyncQuery
            .fork("ana", "jose", "maria", "jose", "maria", "joana", "ana")
            .distinct()
            .subscribe((item, err) -> {
                assertNull(err);
                assertEquals(expected.next(), item);
            })
            .join();
        assertFalse(expected.hasNext());
    }

    @Test
    public void testTakeWhile() {
        Iterator<Integer> expected1 = asList(1, 2, 3, 4, 5).iterator();
        Iterator<Integer> expected2 = asList(1, 2, 3, 4).iterator();
        AsyncQuery
            .fork(1, 2, 3, 4, 5, 6, 7, 8, 9)
            .onNext((item, err) -> {
                assertNull(err);
                assertEquals(expected1.next(), item);
            })
            .takeWhile(n -> n < 5)
            .subscribe((item, err) -> {
                assertNull(err);
                assertEquals(expected2.next(), item);
            })
            .join();
        assertFalse(expected1.hasNext());
        assertFalse(expected2.hasNext());
    }
    @Test
    public void testFlatMapConcat() {
        Iterator<Integer> expected = asList(1, 2, 3, 4, 5, 6, 7, 8, 9).iterator();
        AsyncQuery
            .fork(2, 5, 8)
            .flatMapConcat(nr -> AsyncQuery.fork(nr - 1, nr, nr + 1))
            .subscribe((item, err) -> {
                assertNull(err);
                Integer e = expected.next();
                assertEquals(e , item);
            })
            .join();
        assertFalse(expected.hasNext());
    }
    @Test
    public void testFlatMapMerge() {
        Queue<Integer> expected = new ConcurrentLinkedQueue<>();
        expected.addAll(asList(1, 2, 3, 4, 5, 6, 7, 8, 9));
        AsyncQuery
            .fork(2, 5, 8)
            .flatMapMerge(nr -> AsyncQuery.of(nr - 1, nr, nr + 1))
            .subscribe((item, err) -> {
                assertNull(err);
                assertTrue(expected.remove(item), "Missing item " + item + "!");
            })
            .join();
        assertTrue(expected.isEmpty());
    }
}
