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

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

import static java.util.concurrent.CompletableFuture.supplyAsync;

/**
 * Asynchronous traversal.
 * Jayield uses traverse method as its first choice to
 * implement AsyncQuery operations.
 * This is a special kind of traversal that disallows individually access.
 */
public interface AsyncTraverser<T> {
    /**
     * Yields elements sequentially until all elements have been
     * processed or an exception is thrown.
     * The given consumer is invoked with the result (or null if none)
     * and the exception (or null if none).
     *
     * @return A CompletableFuture to signal finish to enable cancellation
     * through its cancel() method.
     */
    CompletableFuture<Void> subscribe(BiConsumer<? super T,? super Throwable> cons);
}
