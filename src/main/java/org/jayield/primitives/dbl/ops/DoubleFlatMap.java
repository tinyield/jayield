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

import org.jayield.primitives.dbl.DoubleAdvancer;
import org.jayield.primitives.dbl.DoubleQuery;
import org.jayield.primitives.dbl.DoubleTraverser;
import org.jayield.primitives.dbl.DoubleYield;

import java.util.function.DoubleFunction;

public class DoubleFlatMap implements DoubleAdvancer, DoubleTraverser {
    private final DoubleQuery upstream;
    private final DoubleFunction<? extends DoubleQuery> mapper;
    DoubleQuery src;

    public DoubleFlatMap(DoubleQuery query, DoubleFunction<? extends DoubleQuery> mapper) {
        this.upstream = query;
        this.mapper = mapper;
        src = new DoubleQuery(DoubleAdvancer.empty(), DoubleTraverser.empty());
    }

    @Override
    public void traverse(DoubleYield yield) {
        upstream.traverse(elem -> mapper.apply(elem).traverse(yield));

    }

    @Override
    public boolean tryAdvance(DoubleYield yield) {
        while (!src.tryAdvance(yield)) {
            if(!upstream.tryAdvance(t -> src = mapper.apply(t)))
                return false;
        }
        return true;
    }
}
