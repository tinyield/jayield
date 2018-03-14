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

package org.jayield;

import org.jayield.boxes.BoolBox;
import org.jayield.boxes.Box;

import java.util.Iterator;
import java.util.function.Consumer;

/**
 * @author Miguel Gamboa
 *         created on 06-02-2018
 */
public class AdvancerIterator<T> implements Iterator<T>, Yield<T> {
    private final Advancer<T> src;
    private T curr;
    private BoolBox hasNext = new BoolBox();

    public AdvancerIterator(Advancer<T> src) {
        this.src = src;
    }

    @Override
    public boolean hasNext() {
        if(!hasNext.isPresent()) moveNext();
        return hasNext.isTrue();
    }

    @Override
    public T next() {
        hasNext();
        hasNext.reset();
        return curr;
    }

    private void moveNext() {
        boolean res = src.tryAdvance(this);
        hasNext.set(res);
    }

    @Override
    public void ret(T item) {
        curr = item;
    }
}
