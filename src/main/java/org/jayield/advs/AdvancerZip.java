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

import java.util.function.BiFunction;

public class AdvancerZip<T, U, R> implements Advancer<R> {
    private final Advancer<T> upstream;
    private final Advancer<U> other;
    private final BiFunction<? super T, ? super U, ? extends R> zipper;

    public AdvancerZip(Advancer<T> upstream, Advancer<U> other, BiFunction<? super T, ? super U, ? extends R> zipper) {
        this.upstream = upstream;
        this.other = other;
        this.zipper = zipper;
    }

    @Override
    public boolean hasNext() {
        return upstream.hasNext() && other.hasNext();
    }

    @Override
    public R next() {
        return zipper.apply(upstream.next(), other.next());
    }

    @Override
    public void traverse(Yield<? super R> yield) {
        upstream.traverse(e -> {
            if (!other.hasNext()) return;
            yield.ret(zipper.apply(e, other.next()));
        });
    }
}
