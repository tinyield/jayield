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

public class DoubleBox {
    protected double value;
    private boolean isPresent;

    public DoubleBox() {
        this(Double.MIN_VALUE, false);
    }


    public DoubleBox(double value, boolean isPresent) {
        this.value = value;
        this.isPresent = isPresent;
    }

    public DoubleBox(double identity) {
        this.value = identity;
        this.isPresent = true;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public boolean isPresent() {
        return isPresent;
    }

    public void turnPresent(double value) {
        this.value = value;
        isPresent = true;
    }
}
