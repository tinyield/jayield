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

package org.jayield.ops;

import org.jayield.Advancer;
import org.jayield.Traverser;
import org.jayield.Yield;

public class FromArray<U> implements Advancer<U>, Traverser<U> {
    private final U[] data;
    private int current;

    public FromArray(U... data) {
        this.data = data;
        this.current = 0;
    }

    public boolean hasNext() {
        return current < data.length;
    }

    /**
     * Continues from the point where tryAdvance or next left the
     * internal iteration.
     * @param yield
     */
    @Override
    public void traverse(Yield<? super U> yield) {
        for (int i = current; i < data.length; i++) {
            yield.ret(data[i]);
        }
    }

    @Override
    public boolean tryAdvance(Yield<? super U> yield) {
        if(!hasNext()) return false;
        yield.ret(data[current++]);
        return true;
    }
}