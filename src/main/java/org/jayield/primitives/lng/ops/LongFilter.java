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

import org.jayield.boxes.BoolBox;
import org.jayield.primitives.lng.LongAdvancer;
import org.jayield.primitives.lng.LongQuery;
import org.jayield.primitives.lng.LongTraverser;
import org.jayield.primitives.lng.LongYield;

import java.util.function.LongPredicate;

public class LongFilter implements LongAdvancer, LongTraverser {
    private final LongQuery upstream;
    private final LongPredicate p;

    public LongFilter(LongQuery adv, LongPredicate p) {
        this.upstream = adv;
        this.p = p;
    }

    @Override
    public void traverse(LongYield yield) {
        upstream.traverse(e -> {
            if (p.test(e)) {
                yield.ret(e);
            }
        });
    }

    @Override
    public boolean tryAdvance(LongYield yield) {
        BoolBox found = new BoolBox();
        while(found.isFalse()) {
            boolean hasNext = upstream.tryAdvance(item -> {
                if(p.test(item)) {
                    yield.ret(item);
                    found.set();
                }
            });
            if(!hasNext) break;
        }
        return found.isTrue();
    }
}
