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

import java.util.function.DoubleConsumer;

import org.jayield.primitives.dbl.DoubleAdvancer;
import org.jayield.primitives.dbl.DoubleYield;

public class DoubleAdvancerPeek implements DoubleAdvancer {
    private final DoubleAdvancer upstream;
    private final DoubleConsumer action;

    public DoubleAdvancerPeek(DoubleAdvancer adv, DoubleConsumer action) {
        this.upstream = adv;
        this.action = action;
    }

    @Override
    public boolean hasNext() {
        return upstream.hasNext();
    }

    @Override
    public double nextDouble() {
        double curr = upstream.nextDouble();
        action.accept(curr);
        return curr;
    }

    @Override
    public void traverse(DoubleYield yield) {
        upstream.traverse(item -> {
            action.accept(item);
            yield.ret(item);
        });
    }
}
