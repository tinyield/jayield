package org.jayield.primitives.intgr.advs;

import org.jayield.primitives.intgr.IntAdvancer;
import org.jayield.primitives.intgr.IntQuery;
import org.jayield.primitives.intgr.IntTraverser;
import org.jayield.primitives.intgr.IntYield;

import java.util.function.IntPredicate;

public class IntAdvancerDropWhile  implements IntAdvancer, IntTraverser {

    private final IntQuery upstream;
    private final IntPredicate predicate;
    private boolean dropped;

    public IntAdvancerDropWhile(IntQuery upstream, IntPredicate predicate) {
        this.upstream = upstream;
        this.predicate = predicate;
        this.dropped = false;
    }

    @Override
    public void traverse(IntYield yield) {
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
    public boolean tryAdvance(IntYield yield) {
        if (dropped) {
            return upstream.tryAdvance(yield);
        } else {
            IntYield takeWhile = item -> {
                if(!predicate.test(item)){
                    dropped = true;
                    yield.ret(item);
                }
            };
            while(upstream.tryAdvance(takeWhile) && !dropped) {
                // Intentionally empty. Action specified on yield statement of tryAdvance().
            }
            return dropped;
        }

    }
}
