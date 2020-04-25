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

package org.jayield.advs;

import java.util.function.Function;

import org.jayield.Advancer;
import org.jayield.Query;
import org.jayield.Yield;

public class AdvancerFlatMap<T, R> extends AbstractAdvancer<R> {
    private final Query<T> upstream;
    private final Function<? super T, ? extends Query<? extends R>> mapper;
    Query<? extends R> src;

    public AdvancerFlatMap(Query<T> query, Function<? super T, ? extends Query<? extends R>> mapper) {
        this.upstream = query;
        this.mapper = mapper;
        src = new Query<>(Advancer.empty());
    }

    /**
     * Returns true if it moves successfully. Otherwise returns false
     * signaling it has finished.
     */
    public boolean move() {
        while(!src.hasNext()) {
            if(!upstream.hasNext()) return false;
            src = mapper.apply(upstream.next());
        }
        curr = src.next();
        return true;
    }

    @Override
    public void traverse(Yield<? super R> yield) {
        upstream.traverse(elem ->
                mapper.apply(elem).traverse(yield));

    }
}
