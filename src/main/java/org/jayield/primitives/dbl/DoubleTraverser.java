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
}
