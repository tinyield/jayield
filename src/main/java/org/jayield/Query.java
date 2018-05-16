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
 * Traverser all elements sequentially in bulk
 * in the current thread, until all elements have
 * been processed or throws an exception.
 *
 * @author Miguel Gamboa
 *         created on 04-06-2017
 */
public class Query<T> {

    private final Traverser<T> traverser;

    public Query(Traverser<T> traverser) {
        this.traverser = traverser;
    }

    public final void traverse(Yield<? super T> yield) {
        this.traverser.traverse(yield);
    }

    /**
     * Returns a traverser for the elements of this query.
     */
    public Traverser<T> getTraverser() {
        return traverser;
    }

    public final void shortCircuit(Yield<T> yield) {
        try{
            this.traverser.traverse(yield);
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

    public static <U> Query<U> of(U...data) {
        return new Query<>(yield -> {
            for (int i = 0; i < data.length; i++) {
                yield.ret(data[i]);
            }
        });
    }

    public static <U> Query<U> iterate(U seed, UnaryOperator<U> f) {
        return new Query<>(yield -> {
            for(U i = seed; true; i = f.apply(i)){
                yield.ret(i);
            }
        });
    }

    public final <R> Query<R> map(Function<? super T,? extends R> mapper) {
        return new Query<>(yield ->
                this.traverse(e ->
                        yield.ret(mapper.apply(e)))
        );
    }

    public final IntTraversable mapToInt(ToIntFunction<? super T> mapper) {
        return new TraversableMapToInt(this, mapper);
    }

    public final Query<T> filter(Predicate<? super T> p) {
        return new Query<>(yield ->
                this.traverse(e -> {
                    if (p.test(e))
                        yield.ret(e);
                })
        );
    }

    public final Query<T> skip(int n){
        return new Query<>(yield -> {
                int[] count = {0};
                this.traverse(item -> {
                    if(count[0]++ >= n)
                        yield.ret(item);
                });
        });
    }

    public final Query<T> limit(int n){
        return new Query<>(yield -> {
            int[] count = {0};
            this.shortCircuit(item -> {
                if(count[0]++ >= n) Yield.bye();
                yield.ret(item);
            });
        });
    }

    public final Query<T> distinct(){
        final HashSet<T> cache = new HashSet<>();
        return new Query<>(yield ->
                this.traverse(item -> {
                    if(cache.add(item)) yield.ret(item);
                })
        );
    }

    public final <R> Query<R> flatMap(Function<? super T,? extends Query<? extends R>> mapper){
        return new Query<>(yield ->
                this.traverse(item ->
                        mapper.apply(item).traverse(yield))
                );
    }

    public final Query<T> peek(Consumer<? super T> action) {
        return new Query<>(yield ->
                this.traverse(item -> {
                    action.accept(item);
                    yield.ret(item);
                })
        );
    }

    public final Query<T> takeWhile(Predicate<? super T> predicate){
        return new Query<>(yield -> {
            this.shortCircuit(item -> {
                if(!predicate.test(item)) Yield.bye();
                yield.ret(item);
            });
        });
    }

    public final <R> Query<R> then(Function<Query<T>, Traverser<R>> next) {
        return new Query<>(next.apply(this));
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

    public final Optional<T> max(Comparator<? super T> cmp){
        Box<T> b = new Box<>();
        this.traverse(e -> {
            if(!b.isPresent()) b.turnPresent(e);
            else if(cmp.compare(e, b.getValue()) > 0) b.setValue(e);
        });
        return b.isPresent() ? Optional.of(b.getValue()) : Optional.empty();
    }

    public final boolean anyMatch(Predicate<? super T> p) {
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
