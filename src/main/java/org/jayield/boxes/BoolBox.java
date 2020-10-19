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

package org.jayield.boxes;

/**
 * @author Miguel Gamboa
 *         created on 06-07-2017
 */
public class BoolBox {
    private boolean value;

    public BoolBox(boolean value) {
        this.value = value;
    }

    public BoolBox() {
        this(false);
    }

    public boolean isTrue() {
        return value;
    }

    public boolean isFalse() {
        return !value;
    }

    public void set() {
        value = true;
    }
    public void set(boolean val) {
        value = val;
    }
}
