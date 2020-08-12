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

package org.jayield.async;

import org.jayield.AsyncQuery;
import org.jayield.Query;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

import static java.util.concurrent.CompletableFuture.runAsync;

public class AsyncQueryOf<U> extends AsyncQuery<U> {
    private final U[] data;

    public AsyncQueryOf(U[] data) {
        this.data = data;
    }

    @Override
    public CompletableFuture<Void> subscribe(BiConsumer<? super U, ? super Throwable> cons) {
        Query.of(data).traverse(item -> cons.accept(item, null));
        return CompletableFuture.completedFuture(null);
    }
}
