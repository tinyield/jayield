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

import java.util.function.Supplier;

public class Generate<U> implements Advancer<U>, Traverser<U> {
    private final Supplier<U> s;

    public Generate(Supplier<U> s) {
        this.s = s;
    }

    @SuppressWarnings("java:S2189")
    @Override
    public void traverse(Yield<? super U> yield) {
        while(true) {
            yield.ret(s.get());
        }
    }

    @Override
    public boolean tryAdvance(Yield<? super U> yield) {
        yield.ret(s.get());
        return true;
    }
}
