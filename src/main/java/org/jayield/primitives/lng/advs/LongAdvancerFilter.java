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

import java.util.function.LongPredicate;

import org.jayield.primitives.lng.LongAdvancer;
import org.jayield.primitives.lng.LongYield;

public class LongAdvancerFilter extends AbstractLongAdvancer {
    private final LongAdvancer upstream;
    private final LongPredicate p;

    public LongAdvancerFilter(LongAdvancer adv, LongPredicate p) {
        this.upstream = adv;
        this.p = p;
    }

    /**
     * Returns true if it moves successfully. Otherwise returns false
     * signaling it has finished.
     */
    public boolean move() {
        while (upstream.hasNext()) {
            currLong = upstream.nextLong();
            if (p.test(currLong)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void traverse(LongYield yield) {
        upstream.traverse(e -> {
            if (p.test(e)) {
                yield.ret(e);
            }
        });
    }
}
