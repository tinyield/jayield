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

package org.jayield.primitives.intgr.ops;

import org.jayield.primitives.intgr.IntAdvancer;
import org.jayield.primitives.intgr.IntQuery;
import org.jayield.primitives.intgr.IntTraverser;
import org.jayield.primitives.intgr.IntYield;

import java.util.function.IntUnaryOperator;

public class IntMapping implements IntAdvancer, IntTraverser {

    private final IntQuery upstream;
    private final IntUnaryOperator mapper;

    public IntMapping(IntQuery adv, IntUnaryOperator mapper) {
        this.upstream = adv;
        this.mapper = mapper;
    }

    @Override
    public void traverse(IntYield yield) {
        upstream.traverse(e -> yield.ret(mapper.applyAsInt(e)));
    }

    @Override
    public boolean tryAdvance(IntYield yield) {
        return upstream.tryAdvance(item -> yield.ret(mapper.applyAsInt(item)));
    }
}
