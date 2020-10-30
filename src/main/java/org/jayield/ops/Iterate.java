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
import org.jayield.Traverser;
import org.jayield.Yield;

import java.util.function.UnaryOperator;

public class Iterate<U> implements Advancer<U>, Traverser<U> {
    private final UnaryOperator<U> f;
    private U prev;

    public Iterate(U seed, UnaryOperator<U> f) {
        this.f = f;
        this.prev = seed;
    }

    @Override
    public void traverse(Yield<? super U> yield) {
        for(U curr = prev; true; curr = f.apply(curr))
            yield.ret(curr);
    }

    @Override
    public boolean tryAdvance(Yield<? super U> yield) {
        U curr = prev;
        prev = f.apply(prev);
        yield.ret(curr);
        return true;
    }
}
