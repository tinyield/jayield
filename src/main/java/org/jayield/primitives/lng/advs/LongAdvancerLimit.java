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

import java.util.NoSuchElementException;

import org.jayield.Yield;
import org.jayield.primitives.lng.LongAdvancer;
import org.jayield.primitives.lng.LongQuery;
import org.jayield.primitives.lng.LongYield;

public class LongAdvancerLimit implements LongAdvancer {
    private final LongQuery upstream;
    private final int n;
    int count;

    public LongAdvancerLimit(LongQuery upstream, int n) {
        this.upstream = upstream;
        this.n = n;
        count = 0;
    }

    @Override
    public boolean hasNext() {
        return count < n && upstream.hasNext();
    }

    @Override
    public long nextLong() {
        if (count >= n) {
            throw new NoSuchElementException("Nor more elements available!");
        }
        count++;
        return upstream.next();
    }

    @Override
    public void traverse(LongYield yield) {
        upstream.shortCircuit(item -> {
            if (count >= n) {
                Yield.bye();
            }
            count++;
            yield.ret(item);
        });
    }
}
