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

import org.jayield.boxes.BoolBox;
import org.jayield.boxes.Box;
import org.jayield.operations.TraversableDistinct;
import org.jayield.operations.TraversableFilter;
import org.jayield.operations.TraversableFlatMap;
import org.jayield.operations.TraversableIterate;
import org.jayield.operations.TraversableLimit;
import org.jayield.operations.TraversableMap;
import org.jayield.operations.TraversableMapToInt;
import org.jayield.operations.TraversableOf;
import org.jayield.operations.TraversablePeek;
import org.jayield.operations.TraversableSkip;
import org.jayield.operations.TraversableTakeWhile;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import java.util.function.UnaryOperator;

/**
 * Traverse all elements sequentially in bulk
 * in the current thread, until all elements have
 * been processed or throws an exception.
 *
 * @author Miguel Gamboa
 *         created on 04-06-2017
 */
public interface Traversable<T> {
    void traverse(Yield<T> yield);

    default void shortCircuit(Yield<T> yield) {
        try{
            this.traverse(yield);
        }catch(TraversableFinishError e){
            /* Proceed */
        }
    }

    default Advancer<T> advancer() {
        throw new UnsupportedOperationException();
    }

    default Iterator<T> iterator() {
        return new AdvancerIterator<T>(this.advancer());
    }

    public static <U> Traversable<U> of(U...data) {
        return new TraversableOf<>(data);
    }

    public static <U> Traversable<U> iterate(U seed, UnaryOperator<U> f) {
        return new TraversableIterate<U>(seed, f);
    }

    default public <R> Traversable<R> map(Function<T, R> mapper) {
        return new TraversableMap<T, R>(this, mapper);
    }

    default IntTraversable mapToInt(ToIntFunction<T> mapper) {
        return new TraversableMapToInt(this, mapper);
    }

    default Traversable<T> filter(Predicate<T> p) {
        return new TraversableFilter<T>(this, p);
    }

    default Traversable<T> skip(int n){
        return new TraversableSkip<T>(this, n);
    }

    default Traversable<T> limit(int n){
        return new TraversableLimit<T>(this, n);
    }

    default Traversable<T> distinct(){
        return new TraversableDistinct<T>(this);
    }

    default <R> Traversable<R> flatMap(Function<T, Traversable<R>> mapper){
        return new TraversableFlatMap<T, R>(this, mapper);
    }

    default Traversable<T> peek(Consumer<T> action) {
        return new TraversablePeek<T>(this, action);
    }

    default Traversable<T> takeWhile(Predicate<T> predicate){
        return new TraversableTakeWhile<T>(this, predicate);
    }

    default <R> Traversable<R> then(Function<Traversable<T>, Traversable<R>> next) {
        return next.apply(this);
    }

    default Object[] toArray() {
        List<Object> data = new ArrayList<>();
        this.traverse(data::add);
        return data.toArray();
    }

    default Optional<T> findFirst(){
        Box<T> box = new Box<>();
        this.shortCircuit(item -> {
            box.turnPresent(item);
            Yield.bye();
        });
        return box.isPresent()
                ? Optional.of(box.getValue())
                : Optional.empty();
    }

    default Optional<T> max(Comparator<T> cmp){
        Box<T> b = new Box<>();
        this.traverse(e -> {
            if(!b.isPresent()) b.turnPresent(e);
            else if(cmp.compare(e, b.getValue()) > 0) b.setValue(e);
        });
        return b.isPresent() ? Optional.of(b.getValue()) : Optional.empty();
    }

    default boolean anyMatch(Predicate<T> p) {
        BoolBox found = new BoolBox();
        shortCircuit(item -> {
            if(p.test(item)) {
                found.set();
                Yield.bye();
            }
        });
        return found.isTrue();
    }

    default long count() {
        class Counter implements Yield<T> {
            long n = 0;

            @Override
            public void ret(T item) {
                ++n;
            }
        }
        Counter c = new Counter();
        this.traverse(c);
        return c.n;
    }
}
