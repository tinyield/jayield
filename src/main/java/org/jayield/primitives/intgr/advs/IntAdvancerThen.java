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

import java.util.ArrayList;
import java.util.function.Function;

import org.jayield.primitives.intgr.IntAdvancer;
import org.jayield.primitives.intgr.IntIterator;
import org.jayield.primitives.intgr.IntQuery;
import org.jayield.primitives.intgr.IntTraverser;
import org.jayield.primitives.intgr.IntYield;

public class IntAdvancerThen implements IntAdvancer {
    private IntQuery upstream;
    private Function<IntQuery, IntTraverser> next;
    private IntIterator curr;
    private boolean inMem = false;

    public IntAdvancerThen(IntQuery upstream, Function<IntQuery, IntTraverser> next) {
        this.upstream = upstream;
        this.next = next;
    }

    @Override
    public boolean hasNext() {
        return curr().hasNext();
    }

    public IntIterator curr() {
        if (inMem) {
            return curr;
        }
        ArrayList<Integer> mem = new ArrayList<>();
        next.apply(upstream).traverse(mem::add);
        inMem = true;
        curr = IntIterator.from(mem.iterator());
        return curr;
    }

    @Override
    public int nextInt() {
        return curr().nextInt();
    }

    @Override
    public void traverse(IntYield yield) {
        next.apply(upstream).traverse(yield);
    }
}
