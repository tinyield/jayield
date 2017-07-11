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

import org.jayield.boxes.IntBox;

import java.util.HashSet;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import java.util.function.UnaryOperator;

/**
 * @author Miguel Gamboa
 *         created on 06-07-2017
 */
public class TraversableExtensions {

    static <T> Traversable<T> iterate(T seed, UnaryOperator<T> f) {
        return yield -> {
            for(T i = seed; true; i = f.apply(i)){ yield.ret(i); }
        };
    }

    static <T> Traversable<T> of(T...data) {
        return yield -> {
            for (int i = 0; i < data.length; i++) { yield.ret(data[i]); }
        };
    }

    static <T, R> Traversable<R> map(Series<T> source, Function<T, R> mapper) {
        return yield -> {
            source.traverse(e -> yield.ret(mapper.apply(e)));
        };
    }

    static <T> IntTraversable mapToInt(Series<T> source, ToIntFunction<T> mapper) {
        return yield -> {
            source.traverse(e -> yield.ret(mapper.applyAsInt(e)));
        };
    }

    static <T, R> Traversable<R> flatMap(
            Series<T> source, Function<T, Series<R>> mapper)
    {
        return yield ->
                source.traverse(item -> mapper.apply(item).traverse(yield) );
    }

    static <T> Traversable<T> filter(Series<T> source, Predicate<T> p) {
        return yield -> {
            source.traverse(e -> { if (p.test(e)) yield.ret(e); });
        };
    }

    static <T> Traversable<T> skip(Series<T> source, int n) {
        return yield -> {
            final IntBox box = new IntBox(-1);
            source.traverse(item -> { if(box.inc() >= n) yield.ret(item); });
        };
    }

    static <T> Traversable<T> distinct(Series<T> source) {
        return yield -> {
            final HashSet<T> cache = new HashSet<>();
            source.traverse(item -> { if(cache.add(item)) yield.ret(item); });
        };
    }

}
