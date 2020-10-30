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
import org.jayield.primitives.dbl.DoubleQuery;
import org.jayield.primitives.dbl.DoubleTraverser;
import org.jayield.primitives.dbl.DoubleYield;

public class DoubleLimit implements DoubleAdvancer, DoubleTraverser {
    private final DoubleQuery upstream;
    private final int n;
    int count;

    public DoubleLimit(DoubleQuery upstream, int n) {
        this.upstream = upstream;
        this.n = n;
        count = 0;
    }

    @Override
    public void traverse(DoubleYield yield) {
        if(count >= n)
            throw new IllegalStateException("Traverser has already been operated on or closed!");
        while(this.tryAdvance(yield)) {
            // Intentionally empty. Action specified on yield statement of tryAdvance().
        }
    }

    @Override
    public boolean tryAdvance(DoubleYield yield) {
        if(count >= n) return false;
        count++;
        return upstream.tryAdvance(yield);
    }
}
