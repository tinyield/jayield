package org.jayield.primitives.dbl.advs;

import org.jayield.primitives.dbl.DoubleAdvancer;
import org.jayield.primitives.dbl.DoubleQuery;
import org.jayield.primitives.dbl.DoubleTraverser;
import org.jayield.primitives.dbl.DoubleYield;

import java.util.function.DoublePredicate;

public class DoubleAdvancerDropWhile implements DoubleAdvancer, DoubleTraverser {

    private final DoubleQuery upstream;
    private final DoublePredicate predicate;
    private boolean dropped;

    public DoubleAdvancerDropWhile(DoubleQuery upstream, DoublePredicate predicate) {
        this.upstream = upstream;
        this.predicate = predicate;
        this.dropped = false;
    }

    @Override
    public void traverse(DoubleYield yield) {
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
    public boolean tryAdvance(DoubleYield yield) {
        if (dropped) {
            return upstream.tryAdvance(yield);
        } else {
            DoubleYield takeWhile = item -> {
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
