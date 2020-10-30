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

import org.jayield.Yield;
import org.jayield.primitives.dbl.DoubleAdvancer;
import org.jayield.primitives.dbl.DoubleQuery;
import org.jayield.primitives.dbl.DoubleTraverser;
import org.jayield.primitives.dbl.DoubleYield;

import java.util.function.DoublePredicate;

public class DoubleTakeWhile implements DoubleAdvancer, DoubleTraverser {
    private final DoubleQuery upstream;
    private final DoublePredicate predicate;
    private boolean hasNext;

    public DoubleTakeWhile(DoubleQuery upstream, DoublePredicate predicate) {
        this.upstream = upstream;
        this.predicate = predicate;
        this.hasNext = true;
    }

    @Override
    public void traverse(DoubleYield yield) {
        upstream.shortCircuit(item -> {
            if (!predicate.test(item)) {
                Yield.bye();
            }
            yield.ret(item);
        });
    }

    @Override
    public boolean tryAdvance(DoubleYield yield) {
        if(!hasNext) return false; // Once predicate is false it finishes the iteration
        DoubleYield takeWhile = item -> {
            if(predicate.test(item)){
                yield.ret(item);
            } else {
                hasNext = false;
            }
        };
        return upstream.tryAdvance(takeWhile) && hasNext;
    }
}
