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

import java.util.function.DoublePredicate;

import org.jayield.primitives.dbl.DoubleAdvancer;
import org.jayield.primitives.dbl.DoubleYield;

public class DoubleAdvancerFilter extends AbstractDoubleAdvancer {
    private final DoubleAdvancer upstream;
    private final DoublePredicate p;

    public DoubleAdvancerFilter(DoubleAdvancer adv, DoublePredicate p) {
        this.upstream = adv;
        this.p = p;
    }

    /**
     * Returns true if it moves successfully. Otherwise returns false
     * signaling it has finished.
     */
    public boolean move() {
        while (upstream.hasNext()) {
            currDouble = upstream.nextDouble();
            if (p.test(currDouble)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void traverse(DoubleYield yield) {
        upstream.traverse(e -> {
            if (p.test(e)) {
                yield.ret(e);
            }
        });
    }
}
