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
import org.jayield.operations.TraversableMapToInt;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
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
public class Traversable<T> {

    private final Consumer<Yield<T>> traverse;

    public Traversable(Consumer<Yield<T>> traverse) {
        this.traverse = traverse;
    }

    public final void traverse(Yield<T> yield) {
        this.traverse.accept(yield);
    }

    public final void shortCircuit(Yield<T> yield) {
        try{
            this.traverse.accept(yield);
        }catch(TraversableFinishError e){
            /* Proceed */
        }
    }

    public final Advancer<T> advancer() {
        throw new UnsupportedOperationException();
    }

    public final Iterator<T> iterator() {
        return new AdvancerIterator<T>(this.advancer());
    }

    public static <U> Traversable<U> of(U...data) {
        return new Traversable<>(yield -> {
            for (int i = 0; i < data.length; i++) {
                yield.ret(data[i]);
            }
        });
    }

    public static <U> Traversable<U> iterate(U seed, UnaryOperator<U> f) {
        return new Traversable<>(yield -> {
            for(U i = seed; true; i = f.apply(i)){
                yield.ret(i);
            }
        });
    }

    public final <R> Traversable<R> map(Function<T, R> mapper) {
        return new Traversable<>(yield ->
                this.traverse(e ->
                        yield.ret(mapper.apply(e)))
        );
    }

    public final IntTraversable mapToInt(ToIntFunction<T> mapper) {
        return new TraversableMapToInt(this, mapper);
    }

    public final Traversable<T> filter(Predicate<T> p) {
        return new Traversable<>(yield ->
                this.traverse(e -> {
                    if (p.test(e))
                        yield.ret(e);
                })
        );
    }

    public final Traversable<T> skip(int n){
        return new Traversable<>(yield -> {
                int[] count = {0};
                this.traverse(item -> {
                    if(count[0]++ >= n)
                        yield.ret(item);
                });
        });
    }

    public final Traversable<T> limit(int n){
        return new Traversable<>(yield -> {
            int[] count = {0};
            this.shortCircuit(item -> {
                if(count[0]++ >= n) Yield.bye();
                yield.ret(item);
            });
        });
    }

    public final Traversable<T> distinct(){
        final HashSet<T> cache = new HashSet<>();
        return new Traversable<>(yield ->
                this.traverse(item -> {
                    if(cache.add(item)) yield.ret(item);
                })
        );
    }

    public final <R> Traversable<R> flatMap(Function<T, Traversable<R>> mapper){
        return new Traversable<>(yield ->
                this.traverse(item ->
                        mapper.apply(item).traverse(yield))
                );
    }

    public final Traversable<T> peek(Consumer<T> action) {
        return new Traversable<>(yield ->
                this.traverse(item -> {
                    action.accept(item);
                    yield.ret(item);
                })
        );
    }

    public final Traversable<T> takeWhile(Predicate<T> predicate){
        return new Traversable<>(yield -> {
            this.shortCircuit(item -> {
                if(!predicate.test(item)) Yield.bye();
                yield.ret(item);
            });
        });
    }

    public final <R> Traversable<R> then(Function<Traversable<T>, Consumer<Yield<R>>> next) {
        return new Traversable<>(next.apply(this));
    }

    public final Object[] toArray() {
        List<Object> data = new ArrayList<>();
        this.traverse(data::add);
        return data.toArray();
    }

    public final Optional<T> findFirst(){
        Box<T> box = new Box<>();
        this.shortCircuit(item -> {
            box.turnPresent(item);
            Yield.bye();
        });
        return box.isPresent()
                ? Optional.of(box.getValue())
                : Optional.empty();
    }

    public final Optional<T> max(Comparator<T> cmp){
        Box<T> b = new Box<>();
        this.traverse(e -> {
            if(!b.isPresent()) b.turnPresent(e);
            else if(cmp.compare(e, b.getValue()) > 0) b.setValue(e);
        });
        return b.isPresent() ? Optional.of(b.getValue()) : Optional.empty();
    }

    public final boolean anyMatch(Predicate<T> p) {
        BoolBox found = new BoolBox();
        shortCircuit(item -> {
            if(p.test(item)) {
                found.set();
                Yield.bye();
            }
        });
        return found.isTrue();
    }

    public final long count() {
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
