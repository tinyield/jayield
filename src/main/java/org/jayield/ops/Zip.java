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
import org.jayield.Query;
import org.jayield.Traverser;
import org.jayield.Yield;
import org.jayield.boxes.BoolBox;

import java.util.function.BiFunction;

public class Zip<T, U, R> implements Advancer<R>, Traverser<R> {
    private final Query<T> upstream;
    private final Query<U> other;
    private final BiFunction<? super T, ? super U, ? extends R> zipper;

    public Zip(Query<T> upstream, Query<U> other, BiFunction<? super T, ? super U, ? extends R> zipper) {
        this.upstream = upstream;
        this.other = other;
        this.zipper = zipper;
    }

    @Override
    public boolean tryAdvance(Yield<? super R> yield) {
        BoolBox consumed = new BoolBox();
        upstream.tryAdvance(e1 -> other.tryAdvance(e2 -> {
            yield.ret(zipper.apply(e1, e2));
            consumed.set();
        }));
        return consumed.isTrue();
    }


    @Override
    public void traverse(Yield<? super R> yield) {
        upstream.shortCircuit(e1 -> {
            if(!other.tryAdvance(e2 -> yield.ret(zipper.apply(e1, e2))))
                Yield.bye();
        });
    }
}
