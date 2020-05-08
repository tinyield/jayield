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

import java.util.function.IntFunction;

import org.jayield.primitives.intgr.IntAdvancer;
import org.jayield.primitives.intgr.IntQuery;
import org.jayield.primitives.intgr.IntYield;

public class IntAdvancerFlatMap extends AbstractIntAdvancer {
    private final IntQuery upstream;
    private final IntFunction<? extends IntQuery> mapper;
    IntQuery src;

    public IntAdvancerFlatMap(IntQuery query, IntFunction<? extends IntQuery> mapper) {
        this.upstream = query;
        this.mapper = mapper;
        src = new IntQuery(IntAdvancer.empty());
    }

    /**
     * Returns true if it moves successfully. Otherwise returns false
     * signaling it has finished.
     */
    public boolean move() {
        while (!src.hasNext()) {
            if (!upstream.hasNext()) {
                return false;
            }
            src = mapper.apply(upstream.next());
        }
        currInt = src.next();
        return true;
    }

    @Override
    public void traverse(IntYield yield) {
        upstream.traverse(elem -> mapper.apply(elem).traverse(yield));

    }
}
