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

package org.jayield.primitives.lng.advs;

import java.util.function.LongConsumer;

import org.jayield.primitives.lng.LongAdvancer;
import org.jayield.primitives.lng.LongYield;

public class LongAdvancerPeek implements LongAdvancer {
    private final LongAdvancer upstream;
    private final LongConsumer action;

    public LongAdvancerPeek(LongAdvancer adv, LongConsumer action) {
        this.upstream = adv;
        this.action = action;
    }

    @Override
    public boolean hasNext() {
        return upstream.hasNext();
    }

    @Override
    public long nextLong() {
        long curr = upstream.nextLong();
        action.accept(curr);
        return curr;
    }

    @Override
    public void traverse(LongYield yield) {
        upstream.traverse(item -> {
            action.accept(item);
            yield.ret(item);
        });
    }
}
