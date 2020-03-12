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
import org.jayield.Traverser;
import org.jayield.Yield;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.function.Function;

public class AdvancerThen<T, R> implements Advancer<R> {
    private Query<T> upstream;
    private Function<Query<T>, Traverser<R>> next;
    private Iterator<R> curr;
    private boolean inMem = false;

    public AdvancerThen(Query<T> upstream, Function<Query<T>, Traverser<R>> next) {
        this.upstream = upstream;
        this.next = next;
    }

    public Iterator<R> curr() {
        if(inMem) return curr;
        ArrayList<R> mem = new ArrayList<>();
        next.apply(upstream).traverse(mem::add);
        inMem = true;
        return curr = mem.iterator();
    }

    @Override
    public boolean tryAdvance(Yield<? super R> yield) {
        if(!curr().hasNext()) return false;
        yield.ret(curr().next());
        return true;
    }

    @Override
    public boolean hasNext() {
        return curr().hasNext();
    }

    @Override
    public R next() {
        return curr().next();
    }

    @Override
    public void traverse(Yield<? super R> yield) {
        next.apply(upstream).traverse(yield);
    }
}
