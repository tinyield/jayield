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

import org.jayield.Advancer;
import org.jayield.Query;
import org.jayield.Yield;

import java.util.function.Function;

public class AdvancerFlatMap<T, R> implements Advancer<R> {
    private final Query<T> upstream;
    private final Function<? super T, ? extends Query<? extends R>> mapper;
    Query<? extends R> curr;
    boolean moved;
    boolean finished;
    R item;

    public AdvancerFlatMap(Query query, Function<? super T, ? extends Query<? extends R>> mapper) {
        this.upstream = query;
        this.mapper = mapper;
        curr = new Query<>(Advancer.empty());
        moved = false;
        finished = false;
        item = null;
    }

    @Override
    public boolean tryAdvance(Yield<? super R> yield) {
        while (!curr.tryAdvance(yield)) {
            if(!upstream.tryAdvance(t -> curr = mapper.apply(t)))
                return false;
        }
        return true;
    }

    @Override
    public boolean hasNext() {
        if(finished) return false; // It has finished thus return false.
        if(moved) return true;     // It has not finished and has already moved forward, thus there is next.
        finished = !move();        // If tryAdvance returns true then it has not finished yet.
        return !finished;
    }

    @Override
    public R next() {
        if(!hasNext()) throw new IndexOutOfBoundsException("No more elements available on iteration!");
        moved = false;
        return item;
    }

    /**
     * Returns true if it moves successfully. Otherwise returns false
     * signaling it has finished.
     */
    public boolean move() {
        moved = true;
        while(!curr.hasNext()) {
            if(!upstream.hasNext()) return false;
            curr = mapper.apply(upstream.next());
        }
        item = curr.next();
        return true;
    }

    @Override
    public void traverse(Yield<? super R> yield) {
        upstream.traverse(item ->
                mapper.apply(item).traverse(yield));

    }
}
