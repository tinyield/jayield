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

package org.jayield.primitives.lng.ops;

import org.jayield.primitives.lng.LongAdvancer;
import org.jayield.primitives.lng.LongTraverser;
import org.jayield.primitives.lng.LongYield;

import java.util.function.LongUnaryOperator;

public class LongIterate implements LongAdvancer, LongTraverser {
    private final LongUnaryOperator f;
    private long prev;

    public LongIterate(long seed, LongUnaryOperator f) {
        this.f = f;
        this.prev = seed;
    }

    /**
     * Continues from the polong where tryAdvance or next left the
     * longernal iteration.
     *
     * @param yield
     */
    @Override
    public void traverse(LongYield yield) {
        for (long i = prev; true; i = f.applyAsLong(i)) {
            yield.ret(i);
        }
    }

    @Override
    public boolean tryAdvance(LongYield yield) {
        long curr = prev;
        prev = f.applyAsLong(prev);
        yield.ret(curr);
        return true;
    }
}
