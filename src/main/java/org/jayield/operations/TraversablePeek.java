/*
 * Copyright (c) 2018, Fernando Miguel Carvalho, mcarvalho@cc.isel.ipl.pt
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

package org.jayield.operations;

import org.jayield.Advancer;
import org.jayield.AdvancerExtensions;
import org.jayield.Traversable;
import org.jayield.Yield;

import java.util.function.Consumer;

/**
 * @author Miguel Gamboa
 *         created on 06-02-2018
 */
public class TraversablePeek<T> implements Traversable<T> {

    private final Consumer<T> action;
    private final Traversable<T> src;

    public TraversablePeek(Traversable<T> src, Consumer<T> action) {
        this.src = src;
        this.action = action;
    }

    @Override
    public void traverse(Yield<T> yield) {
        src.traverse(item -> {
            action.accept(item);
            yield.ret(item);
        });
    }

    @Override
    public Advancer<T> advancer() {
        return AdvancerExtensions.peek(src.advancer(), action);
    }
}
