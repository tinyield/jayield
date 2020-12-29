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

import org.jayield.async.AsyncQueryDistinct;
import org.jayield.async.AsyncQueryFilter;
import org.jayield.async.AsyncQueryFlatMapConcat;
import org.jayield.async.AsyncQueryFlatMapMerge;
import org.jayield.async.AsyncQueryFork;
import org.jayield.async.AsyncQueryMap;
import org.jayield.async.AsyncQueryOf;
import org.jayield.async.AsyncQueryOfCompletableFuture;
import org.jayield.async.AsyncQueryOfIterator;
import org.jayield.async.AsyncQueryOnNext;
import org.jayield.async.AsyncQuerySkip;
import org.jayield.async.AsyncQueryTakeWhile;

import java.util.Iterator;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * An asynchronous sequence of elements supporting sequential operations.
 * Query operations are composed into a pipeline to perform computation.
 *
 * @author Miguel Gamboa
 *         created on 07-07-2020
 */
public abstract class AsyncQuery<T> implements AsyncTraverser<T>{

    /**
     * Returns an asynchronous sequential ordered query whose elements
     * are the specified values in data parameter.
     */
    public static <U> AsyncQuery<U> of(U...data) {
        return new AsyncQueryOf<>(data);
    }
    /**
     * Returns an asynchronous sequential ordered query whose elements
     * are the specified values in the Iterator parameter.
     */
    public static <U> AsyncQuery<U> of(Iterator<U> iter) {
        return new AsyncQueryOfIterator<>(iter);
    }
    /**
     * Creates an AsyncQuery, producing a single value from the provided CompletableFuture.
     */
    public static <U> AsyncQuery<U> of(CompletableFuture<U> from) {
        return new AsyncQueryOfCompletableFuture<>(from);
    }

    /**
     * Returns an asynchronous sequential ordered query whose elements
     * are the specified values in data parameter running on thread pool.
     */
    public static <U> AsyncQuery<U> fork(U...data) {
        return new AsyncQueryFork<>(data);
    }

    /**
     * Returns a new asynchronous query emitting the same items of this query,
     * additionally performing the provided action on each element as elements are consumed
     * from the resulting query.
     */
    public final AsyncQuery<T> onNext(BiConsumer<? super T, ? super Throwable> action) {
        return new AsyncQueryOnNext<>(this, action);
    }

    /**
     * Returns a new asynchronous query consisting of the remaining elements of
     * this query after discarding the first {@code n} elements of the query.
     */
    public final AsyncQuery<T> skip(int n) {
        return new AsyncQuerySkip<>(this, n);
    }

    /**
     * Returns an asynchronous query consisting of the elements of this query that match
     * the given predicate.
     */
    public final AsyncQuery<T> filter(Predicate<? super T> p) {
        return new AsyncQueryFilter<>(this, p);
    }

    /**
     * Returns an asynchronous query consisting of the results of applying the given
     * function to the elements of this query.
     */
    public final <R> AsyncQuery<R> map(Function<? super T,? extends R> mapper) {
        return new AsyncQueryMap<>(this, mapper);
    }

    /**
     * Returns a query consisting of the distinct elements (according to
     * {@link Object#equals(Object)}) of this query.
     */
    public final AsyncQuery<T> distinct() {
        return new AsyncQueryDistinct<>(this);
    }

    /**
     * Returns a query consisting of the longest prefix of elements taken from
     * this query that match the given predicate.
     */
    public final AsyncQuery<T> takeWhile(Predicate<? super T> predicate){
        return new AsyncQueryTakeWhile<>(this, predicate);
    }
    /**
     * Returns an asynchronous query consisting of the results of replacing each element of
     * this query with the contents of a mapped query produced by applying
     * the provided mapping function to each element.
     * It waits for the inner flow to complete before starting to collect the next one.
     */
    public final <R> AsyncQuery<R> flatMapConcat(Function<? super T,? extends AsyncQuery<? extends R>> mapper) {
        return new AsyncQueryFlatMapConcat<>(this, mapper);
    }

    public final <R> AsyncQuery<R> flatMapMerge(Function<? super T,? extends AsyncQuery<? extends R>> mapper) {
        return new AsyncQueryFlatMapMerge<>(this, mapper);
    }

    public final void blockingSubscribe() {
        this
            .subscribe((item, err) -> { })
            .join(); // In both previous cases cf will raise an exception.
    }
}
