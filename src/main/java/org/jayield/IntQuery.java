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

import org.jayield.boxes.IntBox;

import java.util.OptionalInt;

/**
 * A sequence of primitive int-valued elements supporting sequential
 * operations. This is the int primitive specialization of Query.
 */
public class IntQuery {

    private final IntTraverser traverser;

    public IntQuery(IntTraverser  traverser) {
        this.traverser = traverser;
    }

    /**
     * Yields elements sequentially in the current thread,
     * until all elements have been processed or an
     * exception is thrown.
     */
    public final void traverse(IntYield yield) {
        this.traverser.traverse(yield);
    }

    /**
     * Returns a traverser for the elements of this query.
     */
    public IntTraverser getTraverser() {
        return traverser;
    }

    public OptionalInt max(){
        IntBox b = new IntBox();
        IntYield iy =  e -> {
            if(!b.isPresent()) b.turnPresent(e);
            else if(e > b.getValue()) b.setValue(e);
        };
        this.traverse(iy);
        return b.isPresent()
                ? OptionalInt.of(b.getValue())
                : OptionalInt.empty();
    }
}
