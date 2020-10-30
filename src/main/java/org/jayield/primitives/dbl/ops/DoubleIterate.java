/*
 * Copyright (c) 2020, Fernando Miguel Carvalho, mcarvalho@cc.isel.ipl.pt
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

package org.jayield.primitives.dbl.ops;

import org.jayield.primitives.dbl.DoubleAdvancer;
import org.jayield.primitives.dbl.DoubleTraverser;
import org.jayield.primitives.dbl.DoubleYield;

import java.util.function.DoubleUnaryOperator;

public class DoubleIterate implements DoubleAdvancer, DoubleTraverser {
    private final DoubleUnaryOperator f;
    private double prev;

    public DoubleIterate(double seed, DoubleUnaryOperator f) {
        this.f = f;
        this.prev = seed;
    }

    /**
     * Continues from the point where tryAdvance or next left the
     * internal iteration.
     *
     * @param yield
     */
    @Override
    public void traverse(DoubleYield yield) {
        for (double i = prev; true; i = f.applyAsDouble(i)) {
            yield.ret(i);
        }
    }

    @Override
    public boolean tryAdvance(DoubleYield yield) {
        double curr = prev;
        prev = f.applyAsDouble(prev);
        yield.ret(curr);
        return true;
    }
}
