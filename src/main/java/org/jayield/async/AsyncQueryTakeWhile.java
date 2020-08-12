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
import java.util.function.Predicate;

public class AsyncQueryTakeWhile<T> extends AsyncQuery<T> {
    private final AsyncQuery<T> upstream;
    private final Predicate<? super T> p;
    private CompletableFuture<Void> subscription;
    /**
     * After cancellation of upstream subscription we may still receive updates on consumer.
     * To avoid propagation we must check if we have already cancelled the subscription.
     * But we need a different flag from the CF subscription because this field may not be
     * initialized yet on first check of the subscribe callback.
     */
    private boolean finished = false;

    public AsyncQueryTakeWhile(AsyncQuery<T> upstream, Predicate<? super T> p) {
        this.upstream = upstream;
        this.p = p;
    }

    @Override
    public CompletableFuture<Void> subscribe(BiConsumer<? super T, ? super Throwable> cons) {
        subscription = upstream.subscribe((item, err) -> {
            /**
             * After cancellation of upstream subscription we may still receive updates on consumer.
             * To avoid propagation we must check if we have already cancelled the subscription.
             */
            if(finished) {
                if(subscription != null && !subscription.isDone())
                    subscription.complete(null);
                return;
            }
            if(err != null) {
                cons.accept(null, err);
                return;
            }
            if(p.test(item)) cons.accept(item, null);
            else {
                if(!finished) {
                    finished = true;
                    // We need this guard because we could meet conditions
                    // to finish processing, yet the outer subscribe() invocation
                    // has not returned and the subscription is still null.
                    if(subscription != null)
                        subscription.complete(null);
                }
            }
        });
        return subscription;
    }
}
