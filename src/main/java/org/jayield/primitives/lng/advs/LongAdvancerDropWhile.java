package org.jayield.primitives.lng.advs;

import java.util.function.LongPredicate;

import org.jayield.boxes.BoolBox;
import org.jayield.primitives.lng.LongQuery;
import org.jayield.primitives.lng.LongYield;

public class LongAdvancerDropWhile extends AbstractLongAdvancer {

    private final LongQuery upstream;
    private final LongPredicate predicate;
    private final BoolBox dropped;

    public LongAdvancerDropWhile(LongQuery upstream, LongPredicate predicate) {
        this.upstream = upstream;
        this.predicate = predicate;
        this.dropped = new BoolBox();
    }

    @Override
    public void traverse(LongYield yield) {
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
            currLong = upstream.next();
            if (!predicate.test(currLong)) {
                this.dropped.set();
                return true;
            }
        }
        if (dropped.isTrue() && upstream.hasNext()) {
            currLong = upstream.next();
            return true;
        }
        return false;
    }
}
