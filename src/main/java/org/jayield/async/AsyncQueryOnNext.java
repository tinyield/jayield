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

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public class AsyncQueryOnNext<T> extends AsyncQuery<T> {

    private final AsyncQuery<T> upstream;
    private final BiConsumer<? super T, ? super Throwable> action;

    public AsyncQueryOnNext(AsyncQuery<T> upstream, BiConsumer<? super T, ? super Throwable> action) {
        this.upstream = upstream;
        this.action = action;
    }

    @Override
    public CompletableFuture<Void> subscribe(BiConsumer<? super T, ? super Throwable> cons) {
        return upstream.subscribe((item, err) -> {
            action.accept(item, err);
            cons.accept(item, err);
        });
    }
}
