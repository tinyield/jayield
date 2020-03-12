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

public class AdvancerLimit<T> implements Advancer<T> {
    private final Query<T> upstream;
    private final int n;
    int count;

    public AdvancerLimit(Query<T> upstream, int n) {
        this.upstream = upstream;
        this.n = n;
        count = 0;
    }

    @Override
    public boolean hasNext() {
        return count < n && upstream.hasNext();
    }

    @Override
    public T next() {
        if(count >= n) throw new IndexOutOfBoundsException("Nor more elements available!");
        count++;
        return upstream.next();
    }

    @Override
    public void traverse(Yield<? super T> yield) {
        upstream.shortCircuit(item -> {
            if(count >= n) Yield.bye();
            count++;
            yield.ret(item);
        });
    }
}
