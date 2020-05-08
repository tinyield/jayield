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

import java.util.NoSuchElementException;
import java.util.function.LongSupplier;

import org.jayield.primitives.lng.LongAdvancer;
import org.jayield.primitives.lng.LongYield;

public class LongAdvancerGenerate implements LongAdvancer {
    private final LongSupplier s;

    public LongAdvancerGenerate(LongSupplier s) {
        this.s = s;
    }

    @Override
    public void traverse(LongYield yield) {
        while (hasNext()) {
            yield.ret(s.getAsLong());
        }
    }

    @Override
    public boolean hasNext() {
        return true;
    }

    @Override
    public long nextLong() {
        if (!hasNext()) {
            throw new NoSuchElementException("No more elements available on iteration!");
        }
        return s.getAsLong();
    }


}
