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
import org.jayield.primitives.intgr.IntTraverser;
import org.jayield.primitives.intgr.IntYield;

import java.util.function.IntSupplier;

public class IntGenerate implements IntAdvancer, IntTraverser {
    private final IntSupplier s;

    public IntGenerate(IntSupplier s) {
        this.s = s;
    }

    @SuppressWarnings("java:S2189")
    @Override
    public void traverse(IntYield yield) {
        while (true) {
            yield.ret(s.getAsInt());
        }
    }

    @Override
    public boolean tryAdvance(IntYield yield) {
        yield.ret(s.getAsInt());
        return true;
    }
}
