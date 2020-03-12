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
import org.jayield.Yield;

import java.util.function.Function;

public class AdvancerMap<T, R> implements Advancer<R> {

    private final Advancer<T> upstream;
    private final Function<? super T, ? extends R> mapper;

    public AdvancerMap(Advancer<T> adv, Function<? super T, ? extends R> mapper) {
        this.upstream = adv;
        this.mapper = mapper;
    }

    @Override
    public boolean hasNext() {
        return upstream.hasNext();
    }

    @Override
    public R next() {
        return mapper.apply(upstream.next());
    }

    @Override
    public void traverse(Yield<? super R> yield) {
        upstream.traverse(e -> yield.ret(mapper.apply(e)));
    }
}
