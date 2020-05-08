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

import java.util.ArrayList;
import java.util.function.Function;

import org.jayield.primitives.lng.LongAdvancer;
import org.jayield.primitives.lng.LongIterator;
import org.jayield.primitives.lng.LongQuery;
import org.jayield.primitives.lng.LongTraverser;
import org.jayield.primitives.lng.LongYield;

public class LongAdvancerThen implements LongAdvancer {
    private LongQuery upstream;
    private Function<LongQuery, LongTraverser> next;
    private LongIterator curr;
    private boolean inMem = false;

    public LongAdvancerThen(LongQuery upstream, Function<LongQuery, LongTraverser> next) {
        this.upstream = upstream;
        this.next = next;
    }

    @Override
    public boolean hasNext() {
        return curr().hasNext();
    }

    public LongIterator curr() {
        if (inMem) {
            return curr;
        }
        ArrayList<Long> mem = new ArrayList<>();
        next.apply(upstream).traverse(mem::add);
        inMem = true;
        curr = LongIterator.from(mem.iterator());
        return curr;
    }

    @Override
    public long nextLong() {
        return curr().nextLong();
    }

    @Override
    public void traverse(LongYield yield) {
        next.apply(upstream).traverse(yield);
    }
}
