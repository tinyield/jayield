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

import java.util.HashSet;

public class Distinct<T> implements Advancer<T>, Traverser<T> {
    private final Query<T> upstream;
    final HashSet<T> mem = new HashSet<>();

    public Distinct(Query<T> adv) {
        this.upstream = adv;
    }

    @Override
    public void traverse(Yield<? super T> yield) {
        upstream.traverse(item -> {
            if(mem.add(item)) yield.ret(item);
        });
    }

    @Override
    public boolean tryAdvance(Yield<? super T> yield) {
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
