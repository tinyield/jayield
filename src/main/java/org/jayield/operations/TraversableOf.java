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

/**
 * @author Miguel Gamboa
 *         created on 06-02-2018
 */
public class TraversableOf<T> implements Traversable<T>{
    private final T[] data;

    public TraversableOf(T[] data) {
        this.data = data;
    }

    @Override
    public void traverse(Yield<T> yield) {
        for (int i = 0; i < data.length; i++) {
            yield.ret(data[i]);
        }
    }

    @Override
    public Advancer<T> advancer() {
        return AdvancerExtensions.of(data);
    }
}
