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

import java.util.function.Predicate;

public class AdvancerTakeWhile<T> implements Advancer<T> {
    private final Query<T> upstream;
    private final Predicate<? super T> predicate;
    boolean moved = false;
    boolean finished = false;
    T curr = null;

    public AdvancerTakeWhile(Query<T> upstream, Predicate<? super T> predicate) {
        this.upstream = upstream;
        this.predicate = predicate;
    }

    @Override
    public boolean tryAdvance(Yield<? super T> yield) {
        upstream.tryAdvance(item -> {
            if(!predicate.test(item)) finished = true;
            else yield.ret(item);
        });
        return !finished;
    }

    @Override
    public boolean hasNext() {
        if(finished) return false; // It has finished thus return false.
        if(moved) return true;     // It has not finished and has already moved forward, thus there is next.
        finished = !move();        // If tryAdvance returns true then it has not finished yet.
        return !finished;
    }

    @Override
    public T next() {
        if(!hasNext()) throw new IndexOutOfBoundsException("No more elements available on iteration!");
        moved = false;
        return curr;
    }
    /**
     * Returns true if it moves successfully. Otherwise returns false
     * signaling it has finished.
     */
    public boolean move() {
        moved = true;
        if(upstream.hasNext() && predicate.test(curr = upstream.next()))
            return true;
        return false;
    }

    @Override
    public void traverse(Yield<? super T> yield) {
        upstream.shortCircuit(item -> {
            if(!predicate.test(item)) Yield.bye();
            yield.ret(item);
        });
    }
}
