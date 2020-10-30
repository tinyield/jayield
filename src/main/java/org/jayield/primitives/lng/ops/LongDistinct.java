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

import java.util.HashSet;

public class LongDistinct implements LongAdvancer, LongTraverser {
    final HashSet<Long> mem = new HashSet<>();
    private final LongQuery upstream;

    public LongDistinct(LongQuery adv) {
        this.upstream = adv;
    }

    @Override
    public void traverse(LongYield yield) {
        upstream.traverse(item -> {
            if (mem.add(item)) {
                yield.ret(item);
            }
        });
    }

    @Override
    public boolean tryAdvance(LongYield yield) {
        final BoolBox found = new BoolBox();
        while(found.isFalse() && upstream.tryAdvance(item -> {
            if(mem.add(item)) {
                yield.ret(item);
                found.set();
            }
        }));
        return found.isTrue();
    }
}
