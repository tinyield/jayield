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

package org.jayield.primitives.lng.ops;

import org.jayield.primitives.lng.LongAdvancer;
import org.jayield.primitives.lng.LongQuery;
import org.jayield.primitives.lng.LongTraverser;
import org.jayield.primitives.lng.LongYield;

import java.util.function.LongUnaryOperator;

public class LongMap implements LongAdvancer, LongTraverser {

    private final LongQuery upstream;
    private final LongUnaryOperator mapper;

    public LongMap(LongQuery adv, LongUnaryOperator mapper) {
        this.upstream = adv;
        this.mapper = mapper;
    }

    @Override
    public void traverse(LongYield yield) {
        upstream.traverse(e -> yield.ret(mapper.applyAsLong(e)));
    }

    @Override
    public boolean tryAdvance(LongYield yield) {
        return upstream.tryAdvance(item -> yield.ret(mapper.applyAsLong(item)));
    }
}
