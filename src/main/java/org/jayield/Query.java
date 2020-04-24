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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Spliterator;
import java.util.Spliterators.AbstractSpliterator;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.jayield.advs.AdvancerArray;
import org.jayield.advs.AdvancerDistinct;
import org.jayield.advs.AdvancerFilter;
import org.jayield.advs.AdvancerFlatMap;
import org.jayield.advs.AdvancerGenerate;
import org.jayield.advs.AdvancerIterate;
import org.jayield.advs.AdvancerLimit;
import org.jayield.advs.AdvancerList;
import org.jayield.advs.AdvancerMap;
import org.jayield.advs.AdvancerPeek;
import org.jayield.advs.AdvancerSkip;
import org.jayield.advs.AdvancerStream;
import org.jayield.advs.AdvancerTakeWhile;
import org.jayield.advs.AdvancerThen;
import org.jayield.advs.AdvancerZip;
import org.jayield.boxes.BoolBox;
import org.jayield.boxes.Box;

/**
 * A sequence of elements supporting sequential operations.
 * Query operations are composed into a pipeline to perform
 * computation.
 *
 * @author Miguel Gamboa
 *         created on 04-06-2017
 */
public class Query<T> {

    private final Advancer<T> adv;

    public Query(Advancer<T> adv) {
        this.adv = adv;
    }

    /**
     * Yields elements sequentially in the current thread,
     * until all elements have been processed or an
     * exception is thrown.
     */
    public final void traverse(Yield<? super T> yield) {
        this.adv.traverse(yield);
    }
    /**
     * Returns {@code true} if the iteration has more elements.
     * (In other words, returns {@code true} if {@link #next} would
     * return an element rather than throwing an exception.)
     */
    public final boolean hasNext() {
        return this.adv.hasNext();
    }
    /**
     * Returns the next element in the iteration.
     */
    public final T next() {
        return this.adv.next();
    }

    /**
     * Yields elements sequentially in the current thread,
     * until all elements have been processed or the traversal
     * exited normally through the invocation of yield.bye().
     */
    public final void shortCircuit(Yield<T> yield) {
        try{
            this.adv.traverse(yield);
        }catch(TraversableFinishError e){
            /* Proceed */
        }
    }

    /**
     * Returns a sequential ordered query whose elements
     * are the specified values in data parameter.
     */
    public static <U> Query<U> of(U...data) {
        return new Query<>(new AdvancerArray<>(data));
    }

    /**
     * Returns a sequential ordered query with elements
     * from the provided List data.
     */
    public static <U> Query<U> fromList(List<U> data) {
        return new Query<>(new AdvancerList<>(data));
    }

    /**
     * Returns a sequential ordered query with elements
     * from the provided stream data.
     */
    public static <U> Query<U> fromStream(Stream<U> data) {
        return new Query<>(new AdvancerStream<>(data));
    }

    /**
     * Returns an infinite sequential ordered {@code Query} produced by iterative
     * application of a function {@code f} to an initial element {@code seed},
     * producing a {@code Query} consisting of {@code seed}, {@code f(seed)},
     * {@code f(f(seed))}, etc.
     *
    */
    public static <U> Query<U> iterate(U seed, UnaryOperator<U> f) {
        return new Query<>(new AdvancerIterate<>(seed, f));
    }

    /**
     * Returns a query consisting of the results of applying the given
     * function to the elements of this query.
     */
    public final <R> Query<R> map(Function<? super T,? extends R> mapper) {
        return new Query<>(new AdvancerMap<>(adv, mapper));
    }

    /**
     * Applies a specified function to the corresponding elements of two
     * sequences, producing a sequence of the results.
     */
    public final <U, R> Query<R> zip(Query<U> other, BiFunction<? super T, ? super U, ? extends R> zipper) {
        return new Query<>(new AdvancerZip<>(this.adv, other.adv, zipper));
    }

    /**
     * !!!! Deprecated !!!! Refactor it according to generic Advancer.
     */
    public final IntQuery mapToInt(ToIntFunction<? super T> mapper) {
        return new IntQuery(yield ->
                this.traverse(e -> yield.ret(mapper.applyAsInt(e))));
    }

    /**
     * Returns a query consisting of the elements of this query that match
     * the given predicate.
     */
    public final Query<T> filter(Predicate<? super T> p) {
        return new Query<>(new AdvancerFilter<>(adv, p));
    }

    /**
     * Returns a query consisting of the remaining elements of this query
     * after discarding the first {@code n} elements of the query.
     */
    public final Query<T> skip(int n){
        return new Query<>(new AdvancerSkip<>(adv, n));
    }

    /**
     * Returns a query consisting of the elements of this query, truncated
     * to be no longer than {@code n} in length.
     */
    public final Query<T> limit(int n){
        return new Query<>(new AdvancerLimit<>(this, n));
    }

    /**
     * Returns a query consisting of the distinct elements (according to
     * {@link Object#equals(Object)}) of this query.
     */
    public final Query<T> distinct(){
        return new Query<>(new AdvancerDistinct<>(adv));
    }

    /**
     * Returns a query consisting of the results of replacing each element of
     * this query with the contents of a mapped query produced by applying
     * the provided mapping function to each element.
     */
    public final <R> Query<R> flatMap(Function<? super T,? extends Query<? extends R>> mapper){
        return new Query<>(new AdvancerFlatMap<>(this, mapper));
    }

    /**
     * Returns a query consisting of the elements of this query, additionally
     * performing the provided action on each element as elements are consumed
     * from the resulting query.
     */
    public final Query<T> peek(Consumer<? super T> action) {
        return new Query<>(new AdvancerPeek<>(adv, action));
    }

    /**
     * Returns a query consisting of the longest prefix of elements taken from
     * this query that match the given predicate.
     */
    public final Query<T> takeWhile(Predicate<? super T> predicate){
        return new Query<>(new AdvancerTakeWhile<>(this, predicate));
    }

    /**
     * The {@code then} operator lets you encapsulate a piece of an operator
     * chain into a function.
     * That function {@code next} is applied to this query to produce a new
     * {@code Traverser} object that is encapsulated in the resulting query.
     */
    public final <R> Query<R> then(Function<Query<T>, Traverser<R>> next) {
        return new Query<>(new AdvancerThen<>(this, next));
    }

    /**
     * Returns a list containing the elements of this query.
     */
    public final List<T> toList() {
        List<T> data = new ArrayList<>();
        this.traverse(data::add);
        return data;
    }

    /**
     * Returns an array containing the elements of this query.
     */
    public final Object[] toArray() {
        return this.toList().toArray();
    }

    public final Stream<T> toStream() {
        Spliterator<T> iter = new AbstractSpliterator<T>(Long.MAX_VALUE, Spliterator.ORDERED) {
            @Override
            public boolean tryAdvance(Consumer<? super T> action) {
                if(!adv.hasNext()) return false;
                action.accept(adv.next());
                return true;
            }

            @Override
            public void forEachRemaining(Consumer<? super T> action) {
                adv.traverse(action::accept);
            }
        };
        return StreamSupport.stream(iter, false);
    }

    /**
     * Returns an {@link Optional} describing the first element of this query,
     * or an empty {@code Optional} if this query is empty.
     */
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

    /**
     * Returns the maximum element of this query according to the provided
     * {@code Comparator}.  This is a special case of a reduction.
     */
    public final Optional<T> max(Comparator<? super T> cmp){
        Box<T> b = new Box<>();
        this.traverse(e -> {
            if(!b.isPresent()) b.turnPresent(e);
            else if(cmp.compare(e, b.getValue()) > 0) b.setValue(e);
        });
        return b.isPresent() ? Optional.of(b.getValue()) : Optional.empty();
    }

    /**
     * Returns whether any elements of this query match the provided
     * predicate.  May not evaluate the predicate on all elements if not
     * necessary for determining the result.  If the query is empty then
     * {@code false} is returned and the predicate is not evaluated.
     */
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
    /**
     * Returns whether all elements of this stream match the provided
     * predicate. May not evaluate the predicate on all elements if not
     * necessary for determining the result. If the stream is empty then
     * {@code true} is returned and the predicate is not evaluated.
     */
    public final boolean allMatch(Predicate<? super T> p) {
        BoolBox succeed = new BoolBox(true);
        shortCircuit(item -> {
            if(!p.test(item)) {
                succeed.set(false);
                Yield.bye();
            }
        });
        return succeed.isTrue();
    }

    /**
     * Returns the count of elements in this query.
     */
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

    /**
     * Returns an infinite sequential unordered {@code Query}
     * where each element is generated by the provided Supplier.
     */
    public static <U> Query<U> generate(Supplier<U> s) {
        return new Query<>(new AdvancerGenerate<>(s));
    }

}
