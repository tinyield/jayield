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

import java.util.function.IntUnaryOperator;

import org.jayield.primitives.intgr.IntAdvancer;
import org.jayield.primitives.intgr.IntYield;

public class IntAdvancerMap implements IntAdvancer {

    private final IntAdvancer upstream;
    private final IntUnaryOperator mapper;

    public IntAdvancerMap(IntAdvancer adv, IntUnaryOperator mapper) {
        this.upstream = adv;
        this.mapper = mapper;
    }

    @Override
    public boolean hasNext() {
        return upstream.hasNext();
    }

    @Override
    public int nextInt() {
        return mapper.applyAsInt(upstream.nextInt());
    }

    @Override
    public void traverse(IntYield yield) {
        upstream.traverse(e -> yield.ret(mapper.applyAsInt(e)));
    }
}
