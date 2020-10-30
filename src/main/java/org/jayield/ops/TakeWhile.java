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

package org.jayield.ops;

import org.jayield.Advancer;
import org.jayield.Query;
import org.jayield.Traverser;
import org.jayield.Yield;

import java.util.function.Predicate;

public class TakeWhile<T> implements Advancer<T>, Traverser<T> {
    private final Query<T> upstream;
    private final Predicate<? super T> predicate;
    private boolean hasNext;

    public TakeWhile(Query<T> upstream, Predicate<? super T> predicate) {
        this.upstream = upstream;
        this.predicate = predicate;
        this.hasNext = true;
    }

    @Override
    public boolean tryAdvance(Yield<? super T> yield) {
        if(!hasNext) return false; // Once predicate is false it finishes the iteration
        Yield<T> takeWhile = item -> {
            if(predicate.test(item)){
                yield.ret(item);
            } else {
                hasNext = false;
            }
        };
        return upstream.tryAdvance(takeWhile) && hasNext;
    }

    @Override
    public void traverse(Yield<? super T> yield) {
        upstream.shortCircuit(item -> {
            if(!predicate.test(item)) Yield.bye();
            yield.ret(item);
        });
    }
}
