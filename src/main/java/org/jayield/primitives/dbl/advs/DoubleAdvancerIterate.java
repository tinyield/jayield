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

package org.jayield.primitives.dbl.advs;

import java.util.NoSuchElementException;
import java.util.function.DoubleUnaryOperator;

import org.jayield.primitives.dbl.DoubleAdvancer;
import org.jayield.primitives.dbl.DoubleYield;

public class DoubleAdvancerIterate implements DoubleAdvancer {
    private final DoubleUnaryOperator f;
    private double prev;

    public DoubleAdvancerIterate(double seed, DoubleUnaryOperator f) {
        this.f = f;
        this.prev = seed;
    }

    @Override
    public double nextDouble() {
        if (!hasNext()) {
            throw new NoSuchElementException("No more elements available on iteration!");
        }
        double curr = prev;
        prev = f.applyAsDouble(prev);
        return curr;
    }

    @Override
    public boolean hasNext() {
        return true;
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
}
