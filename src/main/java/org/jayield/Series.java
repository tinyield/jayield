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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import java.util.function.UnaryOperator;

/**
 * @author Miguel Gamboa
 *         created on 02-06-2017
 */
public class Series<T> {

    private static final Series EMPTY = new Series(
            yield -> {},
            yield -> false
    );

    private final Traversable<T> bulk;
    private final Advancer<T> advancer;

    public final void traverse(Yield<T> yield) {
        bulk.traverse(yield);
    }

    public final boolean tryAdvance(Yield<T> yield) {
        return advancer.tryAdvance(yield);
    }

    public Series(Traversable<T> bulk, Advancer<T> advancer) {
        this.bulk = bulk;
        this.advancer = advancer;
    }

    public static <U> Series<U> empty() {
        return EMPTY;
    }

    public static <U> Series<U> iterate(U seed, UnaryOperator<U> f) {
        Traversable<U> b = TraversableExtensions.iterate(seed, f);
        Advancer<U> a = AdvancerExtensions.iterate(seed, f);
        return new Series<>(b, a);
    }

    public static <T> Series<T> of(T...data) {
        Traversable<T> b = TraversableExtensions.of(data);
        Advancer<T> a = AdvancerExtensions.of(data);
        return new Series<T>(b, a);
    }

    public <R> Series<R> map(Function<T, R> mapper) {
        Traversable<R> b = TraversableExtensions.map(this, mapper);
        Advancer<R> a = AdvancerExtensions.map(this, mapper);
        return new Series<R>(b, a);
    }

    public  IntSeries mapToInt(ToIntFunction<T> mapper) {
        IntTraversable b = TraversableExtensions.mapToInt(this, mapper);
        IntAdvancer a = AdvancerExtensions.mapToInt(this, mapper);
        return new IntSeries(b, a);
    }

    public Series<T> filter(Predicate<T> p) {
        Traversable<T> b = TraversableExtensions.filter(this, p);
        Advancer<T> ta = AdvancerExtensions.filter(this, p);
        return new Series<T>(b, ta);
    }

    public Series<T> skip(int n){
        Traversable<T> b = TraversableExtensions.skip(this, n);
        Advancer<T> a = AdvancerExtensions.skip(this, n);
        return new Series<T>(b, a);
    }

    public Series<T> distinct(){
        Traversable<T> b = TraversableExtensions.distinct(this);
        Advancer<T> a = AdvancerExtensions.distinct(this);
        return new Series<T>(b, a);
    }

    public <R> Series<R> flatMap(Function<T, Series<R>> mapper){
        Traversable<R> b = TraversableExtensions.flatMap(this, mapper);
        Advancer<R> a = AdvancerExtensions.flatMap(this, mapper);
        return new Series<>(b, a);
    }

    public Series<T> limit(int n){
        return advanceWith(src -> AdvancerExtensions.limit(this, n));
    }


    public <R> Series<R> advanceWith(Function<Series<T>, Advancer<R>> then) {
        Advancer<R> a = then.apply(this);
        Traversable<R> b = yield -> {while(a.tryAdvance(yield)){}};
        return new Series<>(b, a);
    }

    public <R> Series<R> traverseWith(Function<Series<T>, Traversable<R>> then) {
        Traversable<R> b = then.apply(this);
        Advancer<R> ta = yield -> {
            throw new UnsupportedOperationException();
        };
        return new Series<>(b, ta);
    }

    public Object[] toArray() {
        List<Object> data = new ArrayList<>();
        this.bulk.traverse(data::add);
        return data.toArray();
    }

    public Optional<T> findFirst(){
        Object[] first = {null};
        return advancer.tryAdvance(item -> first[0] = item)
            ? Optional.of((T) first[0])
            : Optional.empty();
    }

    public Optional<T> max(Comparator<T> cmp){
        Box<T> b = new Box<>();
        this.bulk.traverse(e -> {
            if(!b.isPresent()) b.turnPresent(e);
            else if(cmp.compare(e, b.getValue()) > 0) b.setValue(e);
        });
        return b.isPresent() ? Optional.of(b.getValue()) : Optional.empty();
    }

    public boolean anyMatch(Predicate<T> p) {
        BoolBox found = new BoolBox();
        while(found.isFalse() && tryAdvance(item -> {
            if(p.test(item))
                found.set();
        })){ }
        return found.isTrue();
    }

    public long count() {
        class Counter implements Yield<T>{
            long n = 0;

            @Override
            public void ret(T item) { ++n; }
        }
        Counter c = new Counter();
        this.traverse(c);
        return c.n;
    }
}