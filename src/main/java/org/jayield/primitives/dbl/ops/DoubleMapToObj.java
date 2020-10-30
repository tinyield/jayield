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

package org.jayield.primitives.dbl.ops;

import org.jayield.Advancer;
import org.jayield.Traverser;
import org.jayield.Yield;
import org.jayield.primitives.dbl.DoubleQuery;

import java.util.function.DoubleFunction;

public class DoubleMapToObj<T> implements Advancer<T>, Traverser<T> {

    private final DoubleQuery upstream;
    private final DoubleFunction<? extends T> mapper;

    public DoubleMapToObj(DoubleQuery adv, DoubleFunction<? extends T> mapper) {
        this.upstream = adv;
        this.mapper = mapper;
    }

    @Override
    public void traverse(Yield<? super T> yield) {
        upstream.traverse(e -> yield.ret(mapper.apply(e)));
    }

    @Override
    public boolean tryAdvance(Yield<? super T> yield) {
        return upstream.tryAdvance(item -> yield.ret(mapper.apply(item)));
    }
}
