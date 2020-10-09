/*
 * Copyright (c) 2017, Fernando Miguel Carvalho, mcarvalho@cc.isel.ipl.pt
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

/**
 * @author Miguel Gamboa
 *         created on 06-07-2017
 */
public class UserExt {
    static <U> Traverser<U> collapseTrav(Query<U> src) {
        return yield -> {
            final Object[] prev = {null};
            src.traverse(item -> {
                if (prev[0] == null || !prev[0].equals(item))
                    yield.ret((U) (prev[0] = item));
            });
        };
    }
    static <U> Advancer<U> collapseAdv(Query<U> src) {
        final Box<U> prev = new Box<>();
        return yield -> {
            BoolBox found = new BoolBox();
            while(found.isFalse() && src.tryAdvance(item -> {
                if(!item.equals(prev.getValue())) {
                    found.set();
                    prev.setValue(item);
                    yield.ret(item);
                }
            })) {}
            return found.isTrue();
        };
    }
    static <U> Traverser<U> oddTrav(Query<U> src) {
        return yield -> {
            final boolean[] isOdd = {false};
            src.traverse(item -> {
                if(isOdd[0]) yield.ret(item);
                isOdd[0] = !isOdd[0];
            });
        };
    }
    static <U> Advancer<U> oddAdv(Query<U> src) {
        return yield -> {
            if(src.tryAdvance(item -> {}))
                return src.tryAdvance(yield);
            return false;
        };
    }
}
