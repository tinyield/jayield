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

import org.jayield.Yield;
import org.jayield.boxes.BoolBox;
import org.jayield.primitives.intgr.IntAdvancer;
import org.jayield.primitives.intgr.IntQuery;
import org.jayield.primitives.intgr.IntTraverser;
import org.jayield.primitives.intgr.IntYield;

import java.util.function.IntBinaryOperator;

public class IntZip implements IntAdvancer, IntTraverser {
    private final IntQuery upstream;
    private final IntQuery other;
    private final IntBinaryOperator zipper;

    public IntZip(IntQuery upstream, IntQuery other, IntBinaryOperator zipper) {
        this.upstream = upstream;
        this.other = other;
        this.zipper = zipper;
    }
    @Override
    public void traverse(IntYield yield) {
        upstream.shortCircuit(e1 -> {
            if(!other.tryAdvance(e2 -> yield.ret(zipper.applyAsInt(e1, e2))))
                Yield.bye();
        });
    }

    @Override
    public boolean tryAdvance(IntYield yield) {
        BoolBox consumed = new BoolBox();
        upstream.tryAdvance(e1 -> other.tryAdvance(e2 -> {
            yield.ret(zipper.applyAsInt(e1, e2));
            consumed.set();
        }));
        return consumed.isTrue();
    }
}
