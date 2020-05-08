package org.jayield.primitives.intgr.advs;

import java.util.function.IntPredicate;

import org.jayield.boxes.BoolBox;
import org.jayield.primitives.intgr.IntQuery;
import org.jayield.primitives.intgr.IntYield;

public class IntAdvancerDropWhile extends AbstractIntAdvancer {

    private final IntQuery upstream;
    private final IntPredicate predicate;
    private final BoolBox dropped;

    public IntAdvancerDropWhile(IntQuery upstream, IntPredicate predicate) {
        this.upstream = upstream;
        this.predicate = predicate;
        this.dropped = new BoolBox();
    }

    @Override
    public void traverse(IntYield yield) {
        upstream.traverse(item -> {
            if (!dropped.isTrue() && !predicate.test(item)) {
                dropped.set();
            }
            if (dropped.isTrue()) {
                yield.ret(item);
            }
        });
    }

    @Override
    protected boolean move() {
        while (!dropped.isTrue() && this.upstream.hasNext()) {
            currInt = upstream.next();
            if (!predicate.test(currInt)) {
                this.dropped.set();
                return true;
            }
        }
        if (dropped.isTrue() && upstream.hasNext()) {
            currInt = upstream.next();
            return true;
        }
        return false;
    }
}
