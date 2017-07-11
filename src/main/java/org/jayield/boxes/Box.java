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

import java.util.function.UnaryOperator;

/**
 * @author Miguel Gamboa
 *         created on 06-07-2017
 */
public class Box<T> {
    private T value;
    private final UnaryOperator<T> inc;
    private boolean isPresent;

    public Box() {
        this(null, null, false);
    }

    public Box(T value) {
        this(value, null);
    }

    public Box(T value, UnaryOperator<T> inc) {
        this(value, inc, true);
    }

    protected Box(T value, UnaryOperator<T> inc, boolean isPresent) {
        this.value = value;
        this.inc = inc;
        this.isPresent = isPresent;
    }

    public boolean isPresent() {
        return isPresent;
    }

    public T getValue() {
        return value;
    }

    public T setValue(T value) {
        return this.value = value;
    }

    public final void inc() {
        value = inc.apply(value);
    }

    public void turnPresent(T e) {
        this.setValue(e);
        this.isPresent = true;
    }
}
