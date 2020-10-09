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

package org.jayield.primitives.dbl;

import org.jayield.Traverser;
import org.jayield.Yield;
import org.jayield.primitives.intgr.IntTraverser;
import org.jayield.primitives.lng.LongTraverser;

import java.util.function.DoubleToLongFunction;
import java.util.function.IntToDoubleFunction;
import java.util.function.LongToDoubleFunction;
import java.util.function.ToDoubleFunction;

/**
 * Bulk traversal.
 * Jayield uses traverse method as its first choice to
 * implement Query operations.
 * This is a special kind of traversal that disallows individually access.
 */
public interface DoubleTraverser extends Traverser<Double> {
    /**
     * Default traverse implementation that calls the
     * primitive version of it
     */
    @Override
    default void traverse(Yield<? super Double> yield) {
        DoubleYield yld = yield::ret;
        this.traverse(yld);
    }

    /**
     * Yields elements sequentially in the current thread,
     * until all elements have been processed or an
     * exception is thrown.
     */
    void traverse(DoubleYield yield);
    /**
     * An DoubleTraverser object without elements.
     */
    static DoubleTraverser empty() {
        return action -> {};
    }
    /**
     * A DoubleTraverser object from a generic {@link Traverser} mapped by a {@link ToDoubleFunction}.
     *
     * @param source
     *         {@link Traverser} with the source elements for this {@code DoubleTraverser}.
     * @param mapper
     *         {@link ToDoubleFunction} that specifies how to map the source elements double values.
     */
    static <T> DoubleTraverser from(Traverser<T> source, ToDoubleFunction<? super T> mapper) {
        return yield -> source.traverse(item -> yield.ret(mapper.applyAsDouble(item)));
    }

    /**
     * A DoubleTraverser object from a {@link LongTraverser} mapped by a {@link LongToDoubleFunction}.
     *
     * @param source
     *         {@link LongTraverser} with the source elements for this {@code LongTraverser}.
     * @param mapper
     *         {@link DoubleToLongFunction} that specifies how to map the source elements into double values.
     */
    static DoubleTraverser from(LongTraverser source, LongToDoubleFunction mapper) {
        return from((Traverser<Long>) source, mapper::applyAsDouble);
    }

    /**
     * A DoubleTraverser object from a {@link IntTraverser} mapped by a {@link IntToDoubleFunction}.
     *
     * @param source
     *         {@link IntTraverser} with the source elements for this {@code LongTraverser}.
     * @param mapper
     *         {@link IntToDoubleFunction} that specifies how to map the source elements into double values.
     */
    static DoubleTraverser from(IntTraverser source, IntToDoubleFunction mapper) {
        return from((Traverser<Integer>) source, mapper::applyAsDouble);
    }
}
