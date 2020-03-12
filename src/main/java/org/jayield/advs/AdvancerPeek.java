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

package org.jayield.advs;

import org.jayield.Advancer;
import org.jayield.Yield;

import java.util.function.Consumer;

public class AdvancerPeek<T> implements Advancer<T> {
    private final Advancer<T> upstream;
    private final Consumer<? super T> action;

    public AdvancerPeek(Advancer<T> adv, Consumer<? super T> action) {
        this.upstream = adv;
        this.action = action;
    }

    @Override
    public boolean hasNext() {
        return upstream.hasNext();
    }

    @Override
    public T next() {
        T curr = upstream.next();
        action.accept(curr);
        return curr;
    }

    @Override
    public void traverse(Yield<? super T> yield) {
        upstream.traverse(item -> {
            action.accept(item);
            yield.ret(item);
        });
    }
}
