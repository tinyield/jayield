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

package org.jayield.primitives.intgr.ops;

import org.jayield.primitives.intgr.IntAdvancer;
import org.jayield.primitives.intgr.IntQuery;
import org.jayield.primitives.intgr.IntTraverser;
import org.jayield.primitives.intgr.IntYield;

public class IntSkip implements IntAdvancer, IntTraverser {
    private final IntQuery upstream;
    private final int n;
    int index;

    public IntSkip(IntQuery adv, int n) {
        this.upstream = adv;
        this.n = n;
        index = 0;
    }
    /**
     * Continues from the point where tryAdvance or next left the
     * internal iteration.
     *
     * @param yield
     */
    @Override
    public void traverse(IntYield yield) {
        upstream.traverse(item -> {
            if (index++ >= n) {
                yield.ret(item);
            }
        });
    }

    @Override
    public boolean tryAdvance(IntYield yield) {
        for (; index < n; index++)
            upstream.tryAdvance(item -> {});
        return upstream.tryAdvance(yield);
    }
}
