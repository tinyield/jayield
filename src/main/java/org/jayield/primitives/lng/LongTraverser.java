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

package org.jayield.primitives.lng;

import org.jayield.Traverser;
import org.jayield.Yield;
import org.jayield.primitives.dbl.DoubleTraverser;
import org.jayield.primitives.intgr.IntAdvancer;
import org.jayield.primitives.intgr.IntTraverser;

import java.util.function.DoubleToLongFunction;
import java.util.function.IntToLongFunction;
import java.util.function.ToLongFunction;

/**
 * Bulk traversal.
 * Jayield uses traverse method as its first choice to
 * implement Query operations.
 * This is a special kind of traversal that disallows individually access.
 */
public interface LongTraverser extends Traverser<Long> {
    /**
     * Default traverse implementation that calls the
     * primitive version of it
     */
    @Override
    default void traverse(Yield<? super Long> yield) {
        LongYield yld = yield::ret;
        this.traverse(yld);
    }

    /**
     * An LongTraverser object without elements.
     */
    static LongTraverser empty() {
        return yield -> {};
    }


    /**
     * Yields elements sequentially in the current thread,
     * until all elements have been processed or an
     * exception is thrown.
     */
    void traverse(LongYield yield);

    /**
     * An LongTraverser object from a generic {@link Traverser} mapped by a {@link ToLongFunction}.
     *
     * @param source
     *         {@link Traverser} with the source elements for this {@code LongTraverser}.
     * @param mapper
     *         {@link ToLongFunction} that specifies how to map the source elements longo long values.
     */
    static <T> LongTraverser from(Traverser<T> source, ToLongFunction<? super T> mapper) {
        return yield -> source.traverse(item -> yield.ret(mapper.applyAsLong(item)));
    }

    /**
     * An LongTraverser object from a {@link DoubleTraverser} mapped by a {@link DoubleToLongFunction}.
     *
     * @param source
     *         {@link DoubleTraverser} with the source elements for this {@code LongAdvancer}.
     * @param mapper
     *         {@link DoubleToLongFunction} that specifies how to map the source elements longo long values.
     */
    static LongTraverser from(DoubleTraverser source, DoubleToLongFunction mapper) {
        return from((Traverser<Double>) source, mapper::applyAsLong);
    }

    /**
     * An LongAdvancer object from a {@link IntTraverser} mapped by a {@link IntToLongFunction}.
     *
     * @param source
     *         {@link IntAdvancer} with the source elements for this {@code LongAdvancer}.
     * @param mapper
     *         {@link IntToLongFunction} that specifies how to map the source elements longo long values.
     */
    static LongTraverser from(IntTraverser source, IntToLongFunction mapper) {
        return from((Traverser<Integer>) source, mapper::applyAsLong);
    }
}
