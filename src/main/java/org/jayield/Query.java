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

import org.jayield.ops.FromArray;
import org.jayield.ops.Concat;
import org.jayield.ops.Distinct;
import org.jayield.ops.DropWhile;
import org.jayield.ops.Filter;
import org.jayield.ops.FlatMap;
import org.jayield.ops.Generate;
import org.jayield.ops.Iterate;
import org.jayield.ops.Limit;
import org.jayield.ops.FromList;
import org.jayield.ops.Mapping;
import org.jayield.ops.Peek;
import org.jayield.ops.Skip;
import org.jayield.ops.FromStream;
import org.jayield.ops.TakeWhile;
import org.jayield.ops.Zip;
import org.jayield.boxes.BoolBox;
import org.jayield.boxes.Box;
import org.jayield.primitives.dbl.DoubleAdvancer;
import org.jayield.primitives.dbl.DoubleQuery;
import org.jayield.primitives.dbl.DoubleTraverser;
import org.jayield.primitives.intgr.IntAdvancer;
import org.jayield.primitives.intgr.IntQuery;
import org.jayield.primitives.intgr.IntTraverser;
import org.jayield.primitives.lng.LongAdvancer;
import org.jayield.primitives.lng.LongQuery;
import org.jayield.primitives.lng.LongTraverser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators.AbstractSpliterator;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

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
    private final Traverser<T> trav;

    public Query(Advancer<T> adv, Traverser<T> trav) {
        this.adv = adv;
        this.trav = trav;
    }

    /**
     * Yields elements sequentially in the current thread,
     * until all elements have been processed or an
     * exception is thrown.
     */
    public final void traverse(Yield<? super T> yield) {
        this.trav.traverse(yield);
    }
    /**
     * If a remaining element exists, yields that element through
     * the given action.
     */
    public boolean tryAdvance(Yield<? super T> action) {
        return this.adv.tryAdvance(action);
    }


    /**
     * Yields elements sequentially in the current thread,
     * until all elements have been processed or the traversal
     * exited normally through the invocation of yield.bye().
     */
    public final void shortCircuit(Yield<T> yield) {
        try{
            this.trav.traverse(yield);
        }catch(TraversableFinishError e){
            /* Proceed */
        }
    }

    /**
     * Returns a sequential ordered query whose elements
     * are the specified values in data parameter.
     */
    public static <U> Query<U> of(U...data) {
        FromArray<U> adv = new FromArray<>(data);
        return new Query<>(adv, adv);
    }

    /**
     * Returns a sequential ordered query with elements
     * from the provided List data.
     */
    public static <U> Query<U> fromList(List<U> data) {
        FromList<U> adv = new FromList<>(data);
        return new Query<>(adv, adv);
    }

    /**
     * Returns a sequential ordered query with elements
     * from the provided stream data.
     */
    public static <U> Query<U> fromStream(Stream<U> data) {
        FromStream<U> adv = new FromStream<>(data);
        return new Query<>(adv, adv);
    }

    /**
     * Returns an infinite sequential ordered {@code Query} produced by iterative
     * application of a function {@code f} to an initial element {@code seed},
     * producing a {@code Query} consisting of {@code seed}, {@code f(seed)},
     * {@code f(f(seed))}, etc.
     *
    */
    public static <U> Query<U> iterate(U seed, UnaryOperator<U> f) {
        Iterate<U> iter = new Iterate<>(seed, f);
        return new Query<>(iter, iter);
    }

    /**
     * Returns a query consisting of the results of applying the given
     * function to the elements of this query.
     */
    public final <R> Query<R> map(Function<? super T,? extends R> mapper) {
        Mapping<T, R> map = new Mapping<>(this, mapper);
        return new Query<>(map, map);
    }

    /**
     * Applies a specified function to the corresponding elements of two
     * sequences, producing a sequence of the results.
     */
    public final <U, R> Query<R> zip(Query<U> other, BiFunction<? super T, ? super U, ? extends R> zipper) {
        Zip<T, U, R> zip = new Zip<>(this, other, zipper);
        return new Query<>(zip, zip);
    }

    /**
     * Returns a {@link IntQuery} with the elements of this {@code Query} mapped by
     * a {@link ToIntFunction}
     *
     * @param mapper
     *         ToIntFunction used to map elements of this {@code Query} to int
     */
    public final IntQuery mapToInt(ToIntFunction<? super T> mapper) {
        return new IntQuery(IntAdvancer.from(adv, mapper), IntTraverser.from(trav, mapper));
    }

    /**
     * Returns a {@link LongQuery} with the elements of this {@code Query} mapped by
     * a {@link ToLongFunction}
     *
     * @param mapper
     *         ToLongFunction used to map elements of this {@code Query} to long
     */
    public final LongQuery mapToLong(ToLongFunction<? super T> mapper) {
        return new LongQuery(LongAdvancer.from(adv, mapper), LongTraverser.from(trav, mapper));
    }

    /**
     * Returns a {@link DoubleQuery} with the elements of this {@code Query} mapped by
     * a {@link ToDoubleFunction}
     *
     * @param mapper
     *         ToLongFunction used to map elements of this {@code Query} to double
     */
    public final DoubleQuery mapToDouble(ToDoubleFunction<? super T> mapper) {
        return new DoubleQuery(DoubleAdvancer.from(adv, mapper), DoubleTraverser.from(trav, mapper));
    }

    /**
     * Returns a query consisting of the elements of this query that match
     * the given predicate.
     */
    public final Query<T> filter(Predicate<? super T> p) {
        Filter<T> filter = new Filter<>(this, p);
        return new Query<>(filter, filter);
    }

    /**
     * Returns a query consisting of the remaining elements of this query
     * after discarding the first {@code n} elements of the query.
     */
    public final Query<T> skip(int n){
        Skip<T> skip = new Skip<>(this, n);
        return new Query<>(skip, skip);
    }

    /**
     * Returns a query consisting of the elements of this query, truncated
     * to be no longer than {@code n} in length.
     */
    public final Query<T> limit(int n){
        Limit<T> limit = new Limit<>(this, n);
        return new Query<>(limit, limit);
    }

    /**
     * Returns a query consisting of the distinct elements (according to
     * {@link Object#equals(Object)}) of this query.
     */
    public final Query<T> distinct(){
        Distinct<T> dis = new Distinct<>(this);
        return new Query<>(dis, dis);
    }

    /**
     * Returns a query consisting of the results of replacing each element of
     * this query with the contents of a mapped query produced by applying
     * the provided mapping function to each element.
     */
    public final <R> Query<R> flatMap(Function<? super T,? extends Query<? extends R>> mapper){
        FlatMap<T, R> map = new FlatMap<>(this, mapper);
        return new Query<>(map, map);
    }

    /**
     * Returns a query consisting of the elements of this query, additionally
     * performing the provided action on each element as elements are consumed
     * from the resulting query.
     */
    public final Query<T> peek(Consumer<? super T> action) {
        Peek<T> peek = new Peek<>(this, action);
        return new Query<>(peek, peek);
    }

    /**
     * Returns a query consisting of the longest prefix of elements taken from
     * this query that match the given predicate.
     */
    public final Query<T> takeWhile(Predicate<? super T> predicate){
        TakeWhile<T> take = new TakeWhile<>(this, predicate);
        return new Query<>(take, take);
    }

    /**
     * The {@code then} operator lets you encapsulate a piece of an operator
     * chain into a function.
     * That function {@code next} is applied to this query to produce a new
     * {@code Traverser} object that is encapsulated in the resulting query.
     * On the other hand, the {@code nextAdv} is applied to this query to produce a new
     * {@code Advancer} object that is encapsulated in the resulting query.
     */
    public final <R> Query<R> then(Function<Query<T>, Advancer<R>> nextAdv, Function<Query<T>, Traverser<R>> next) {
        return new Query<>(nextAdv.apply(this), next.apply(this));
    }
    /**
     * The {@code then} operator lets you encapsulate a piece of an operator
     * chain into a function.
     * That function {@code next} is applied to this query to produce a new
     * {@code Traverser} object that is encapsulated in the resulting query.
     */
    public final <R> Query<R> then(Function<Query<T>, Traverser<R>> next) {
        Advancer<R> nextAdv = item -> { throw new UnsupportedOperationException(
            "Missing tryAdvance() implementation! Use the overloaded then() providing both Advancer and Traverser!");
        };
        return new Query<>(nextAdv, next.apply(this));
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
        return this.toArray(Object[]::new);
    }

    public final Stream<T> toStream() {
        Spliterator<T> iter = new AbstractSpliterator<T>(Long.MAX_VALUE, Spliterator.ORDERED) {
            @Override
            public boolean tryAdvance(Consumer<? super T> action) {
                return adv.tryAdvance(action::accept);
            }

            @Override
            public void forEachRemaining(Consumer<? super T> action) {
                trav.traverse(action::accept);
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
        this.tryAdvance(box::turnPresent);
        return box.isPresent()
                ? Optional.of(box.getValue())
                : Optional.empty();
    }

    /**
     * Returns the maximum element of this query according to the provided
     * {@code Comparator}.  This is a special case of a reduction.
     */
    public final Optional<T> max(Comparator<? super T> cmp){
        class BoxMax extends Box<T> implements Yield<T> {
            @Override
            public final void ret(T item) {
                if(!isPresent()) turnPresent(item);
                else if(cmp.compare(item, value) > 0) value = item;
            }
        }
        BoxMax b = new BoxMax();
        this.traverse(b);
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
     * Returns whether all elements of this query match the provided
     * predicate. May not evaluate the predicate on all elements if not
     * necessary for determining the result. If the query is empty then
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
     * Returns an {@link Optional} with the resulting reduction of the elements of this {@code Query},
     * if a reduction can be made, using the provided accumulator.
     */
    public Optional<T> reduce(BinaryOperator<T> accumulator) {
        Box<T> box = new Box<>();
        if(this.tryAdvance(box::setValue)) {
            return Optional.of(this.reduce(box.getValue(), accumulator));
        } else {
            return Optional.empty();
        }
    }

    /**
     * Returns the result of the reduction of the elements of this query,
     * using the provided identity value and accumulator.
     */
    public T reduce(T identity, BinaryOperator<T> accumulator) {
        class BoxAccumulator extends Box<T> implements Yield<T> {
            public BoxAccumulator(T identity) {
                super(identity);
            }
            @Override
            public final void ret(T item) {
                this.value = accumulator.apply(value, item);
            }
        }
        BoxAccumulator box = new BoxAccumulator(identity);
        this.traverse(box);
        return box.getValue();
    }

    /**
     * Yields elements sequentially in the current thread,
     * until all elements have been processed or an
     * exception is thrown.
     */
    public final void forEach(Yield<? super T> yield) {
        this.traverse(yield);
    }

    /**
     * Returns a {@link Set} containing the elements of this query.
     */
    public final Set<T> toSet() {
        Set<T> data = new HashSet<>();
        this.traverse(data::add);
        return data;
    }

    /**
     * Returns an array containing the elements of this query.
     */
    public final <U> U[] toArray(IntFunction<U[]> generator) {
        return this.toList().toArray(generator);
    }

    /**
     * Returns the concatenation of the input elements into a String, in encounter order.
     */
    public final String join() {
        return this.map(String::valueOf)
                   .collect(StringBuilder::new, StringBuilder::append)
                   .toString();
    }

    /**
     * Returns an {@link Optional} describing any element of this query,
     * or an empty {@code Optional} if this query is empty.
     */
    public final Optional<T> findAny(){
        return this.findFirst();
    }

    /**
     * Returns the minimum element of this query according to the provided
     * {@code Comparator}.  This is a special case of a reduction.
     */
    public final Optional<T> min(Comparator<? super T> cmp) {
        return this.max((a, b) -> cmp.compare(a, b) * -1);
    }

    /**
     * Returns whether no elements of this query match the provided
     * predicate. May not evaluate the predicate on all elements if not
     * necessary for determining the result. If the query is empty then
     * {@code true} is returned and the predicate is not evaluated.
     */
    public final boolean noneMatch(Predicate<? super T> p) {
        return !this.anyMatch(p);
    }

    /**
     * Returns an infinite sequential unordered {@code Query}
     * where each element is generated by the provided Supplier.
     */
    public static <U> Query<U> generate(Supplier<U> s) {
        Generate<U> gen = new Generate<>(s);
        return new Query<>(gen, gen);
    }

    /**
     * Performs a mutable reduction operation on the elements of this {@code Query}.
     * A mutable reduction is one in which the reduced value is a mutable result container, such as an ArrayList,
     * and elements are incorporated by updating the state of the result rather than by replacing the result.
     */
    public <R> R collect(Supplier<R> supplier, BiConsumer<R, ? super T> accumulator) {
        R result = supplier.get();
        this.traverse(elem -> accumulator.accept(result, elem));
        return result;
    }

    /**
     * Creates a concatenated {@code Query} in which the elements are
     * all the elements of this {@code Query} followed by all the
     * elements of the other {@code Query}.
     */
    public final Query<T> concat(Query<T> other) {
        Concat<T> con = new Concat<>(this, other);
        return new Query<>(con, con);
    }

    /**
     * Returns a {@code Query} consisting of the elements of this {@code Query},
     * sorted according to the provided Comparator.
     *
     * This is a stateful intermediate operation.
     */
    public final Query<T> sorted(Comparator<T> comparator) {
        T[] state = (T[]) this.toArray();
        Arrays.sort(state, comparator);
        FromArray<T> sorted = new FromArray<>(state);
        return new Query<>(sorted, sorted);
    }

    /**
     * Returns a {@code Query} consisting of the remaining elements of this query
     * after discarding the first sequence of elements that match the given Predicate.
     */
    public final Query<T> dropWhile(Predicate<T> predicate) {
        DropWhile<T> drop = new DropWhile<>(this, predicate);
        return new Query<>(drop, drop);
    }

}
