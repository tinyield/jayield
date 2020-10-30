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

import java.util.Spliterator;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;

public class FromIntStream implements IntAdvancer, IntTraverser {
    private final Spliterator.OfInt upstream;

    public FromIntStream(IntStream data) {
        this.upstream = data.spliterator();
    }

    @Override
    public void traverse(IntYield yield) {
        IntConsumer cons = yield::ret;
        upstream.forEachRemaining(cons);
    }

    @Override
    public boolean tryAdvance(IntYield yield) {
        IntConsumer cons = yield::ret;
        return upstream.tryAdvance(cons);
    }
}
