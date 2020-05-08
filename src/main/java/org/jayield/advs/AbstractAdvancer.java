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

import java.util.NoSuchElementException;

import org.jayield.Advancer;

public abstract class AbstractAdvancer<T> implements Advancer<T> {
    boolean moved;
    boolean finished;
    T curr;

    @Override
    public final boolean hasNext() {
        if(finished) return false; // It has finished thus return false.
        if(moved) return true;     // It has not finished and has already moved forward, thus there is next.
        finished = !advance();     // If tryAdvance returns true then it has not finished yet.
        return !finished;
    }

    private final boolean advance() {
        moved = true;
        return move();
    }

    /**
     * Return true if it advances successfully and put the current item in curr field.
     */
    protected abstract boolean move();

    @Override
    public final T next() {
        if (!hasNext()) {
            throw new NoSuchElementException("No more elements available on iteration!");
        }
        prepareIteration();
        return curr;
    }

    protected void prepareIteration() {
        moved = false;
    }

}
