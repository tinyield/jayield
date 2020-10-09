package org.jayield.primitives.lng.advs;

import org.jayield.primitives.lng.LongAdvancer;
import org.jayield.primitives.lng.LongQuery;
import org.jayield.primitives.lng.LongTraverser;
import org.jayield.primitives.lng.LongYield;

import java.util.function.LongPredicate;

public class LongAdvancerDropWhile implements LongAdvancer, LongTraverser {

    private final LongQuery upstream;
    private final LongPredicate predicate;
    private boolean dropped;

    public LongAdvancerDropWhile(LongQuery upstream, LongPredicate predicate) {
        this.upstream = upstream;
        this.predicate = predicate;
        this.dropped = false;
    }

    @Override
    public void traverse(LongYield yield) {
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
    public boolean tryAdvance(LongYield yield) {
        if (dropped) {
            return upstream.tryAdvance(yield);
        } else {
            LongYield takeWhile = item -> {
                if(!predicate.test(item)){
                    dropped = true;
                    yield.ret(item);
                }
            };
            while(upstream.tryAdvance(takeWhile) && !dropped) { }
            return dropped;
        }
    }
}
