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

package org.jayield.primitives.lng.advs;

import java.util.function.LongBinaryOperator;

import org.jayield.primitives.lng.LongAdvancer;
import org.jayield.primitives.lng.LongYield;

public class LongAdvancerZip implements LongAdvancer {
    private final LongAdvancer upstream;
    private final LongAdvancer other;
    private final LongBinaryOperator zipper;

    public LongAdvancerZip(LongAdvancer upstream, LongAdvancer other, LongBinaryOperator zipper) {
        this.upstream = upstream;
        this.other = other;
        this.zipper = zipper;
    }

    @Override
    public boolean hasNext() {
        return upstream.hasNext() && other.hasNext();
    }

    @Override
    public long nextLong() {
        return zipper.applyAsLong(upstream.next(), other.next());
    }

    @Override
    public void traverse(LongYield yield) {
        upstream.traverse(e -> {
            if (!other.hasNext()) return;
            yield.ret(zipper.applyAsLong(e, other.next()));
        });
    }
}
