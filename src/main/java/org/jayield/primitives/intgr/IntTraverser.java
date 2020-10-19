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

package org.jayield.primitives.intgr;

import org.jayield.Traverser;
import org.jayield.Yield;
import org.jayield.primitives.dbl.DoubleTraverser;
import org.jayield.primitives.lng.LongTraverser;

import java.util.function.DoubleToIntFunction;
import java.util.function.LongToIntFunction;
import java.util.function.ToIntFunction;

/**
 * Bulk traversal.
 * Jayield uses traverse method as its first choice to
 * implement Query operations.
 * This is a special kind of traversal that disallows individually access.
 */
public interface IntTraverser extends Traverser<Integer> {
    /**
     * Yields elements sequentially in the current thread,
     * until all elements have been processed or an
     * exception is thrown.
     */
    void traverse(IntYield yield);

    static IntTraverser empty() {
        return yield -> { };
    }
    /**
     * Default traverse implementation that calls the
     * primitive version of it
     */
    @Override
    default void traverse(Yield<? super Integer> yield) {
        IntYield yld = yield::ret;
        this.traverse(yld);
    }
    /**
     * An IntTraverser object from a generic {@link Traverser} mapped by a {@link ToIntFunction}.
     *
     * @param source
     *         {@link Traverser} with the source elements for this {@code IntTraverser}.
     * @param mapper
     *         {@link ToIntFunction} that specifies how to map the source elements into int values.
     */
    static <T> IntTraverser from(Traverser<T> source, ToIntFunction<? super T> mapper) {
        return yield -> source.traverse(item -> yield.ret(mapper.applyAsInt(item)));
    }

    /**
     * An IntTraverser object from a {@link DoubleTraverser} mapped by a {@link DoubleToIntFunction}.
     *
     * @param source
     *         {@link DoubleTraverser} with the source elements for this {@code IntTraverser}.
     * @param mapper
     *         {@link DoubleToIntFunction} that specifies how to map the source elements into int values.
     */
    static IntTraverser from(DoubleTraverser source, DoubleToIntFunction mapper) {
        return from((Traverser<Double>) source, mapper::applyAsInt);
    }

    /**
     * An IntTraverser object from a {@link LongTraverser} mapped by a {@link LongToIntFunction}.
     *
     * @param source
     *         {@link LongTraverser} with the source elements for this {@code DoubleTraverser}.
     * @param mapper
     *         {@link LongToIntFunction} that specifies how to map the source elements into int values.
     */
    static IntTraverser from(LongTraverser source, LongToIntFunction mapper) {
        return from((Traverser<Long>) source, mapper::applyAsInt);
    }
}
