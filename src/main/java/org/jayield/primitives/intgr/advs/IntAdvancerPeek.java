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

package org.jayield.primitives.intgr.advs;

import java.util.function.IntConsumer;

import org.jayield.primitives.intgr.IntAdvancer;
import org.jayield.primitives.intgr.IntYield;

public class IntAdvancerPeek implements IntAdvancer {
    private final IntAdvancer upstream;
    private final IntConsumer action;

    public IntAdvancerPeek(IntAdvancer adv, IntConsumer action) {
        this.upstream = adv;
        this.action = action;
    }

    @Override
    public boolean hasNext() {
        return upstream.hasNext();
    }

    @Override
    public int nextInt() {
        int curr = upstream.nextInt();
        action.accept(curr);
        return curr;
    }

    @Override
    public void traverse(IntYield yield) {
        upstream.traverse(item -> {
            action.accept(item);
            yield.ret(item);
        });
    }
}
