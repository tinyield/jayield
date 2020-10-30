package org.jayield.ops;

import org.jayield.Advancer;
import org.jayield.Query;
import org.jayield.Traverser;
import org.jayield.Yield;

import java.util.function.Predicate;

public class DropWhile<T> implements Advancer<T>, Traverser<T> {

    private final Query<T> upstream;
    private final Predicate<T> predicate;
    private boolean dropped;

    public DropWhile(Query<T> upstream, Predicate<T> predicate) {
        this.upstream = upstream;
        this.predicate = predicate;
    }

    @Override
    public void traverse(Yield<? super T> yield) {
        upstream.traverse(item -> {
            if (!dropped && !predicate.test(item)) {
                dropped = true;
            }
            if (dropped) {
                yield.ret(item);
            }
        });
    }

    @Override
    public boolean tryAdvance(Yield<? super T> yield) {
        if (dropped) {
            return upstream.tryAdvance(yield);
        } else {
            while(!dropped && dropNext(yield)) {
                // Intentionally empty. Action specified on yield statement of tryAdvance().
            }
            return dropped;
        }
    }

    private boolean dropNext(Yield<? super T> yield) {
        return upstream.tryAdvance(item -> {
            if(!predicate.test(item)){
                dropped = true;
                yield.ret(item);
            }
        });
    }
}
