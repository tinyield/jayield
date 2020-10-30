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
public class Box<T> {
    protected T value;
    private boolean isPresent;

    public Box(T identity) {
        this.value = identity;
        this.isPresent = true;
    }

    public Box() {
    }


    public final boolean isPresent() {
        return isPresent;
    }

    public final T getValue() {
        return value;
    }

    public final T setValue(T value) {
        this.value = value;
        return value;
    }

    public final void turnPresent(T e) {
        this.setValue(e);
        this.isPresent = true;
    }
}
