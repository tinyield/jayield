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

import java.util.HashSet;

import org.jayield.primitives.dbl.DoubleAdvancer;
import org.jayield.primitives.dbl.DoubleYield;

public class DoubleAdvancerDistinct extends AbstractDoubleAdvancer {
    final HashSet<Double> mem = new HashSet<>();
    private final DoubleAdvancer upstream;

    public DoubleAdvancerDistinct(DoubleAdvancer adv) {
        this.upstream = adv;
    }

    /**
     * Returns true if it moves successfully. Otherwise returns false
     * signaling it has finished.
     */
    public boolean move() {
        while (upstream.hasNext()) {
            currDouble = upstream.nextDouble();
            if (mem.add(currDouble)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void traverse(DoubleYield yield) {
        upstream.traverse(item -> {
            if (mem.add(item)) {
                yield.ret(item);
            }
        });
    }
}
