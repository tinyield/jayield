package org.jayield.primitives.dbl.advs;

import java.util.function.DoublePredicate;

import org.jayield.boxes.BoolBox;
import org.jayield.primitives.dbl.DoubleQuery;
import org.jayield.primitives.dbl.DoubleYield;

public class DoubleAdvancerDropWhile extends AbstractDoubleAdvancer {

    private final DoubleQuery upstream;
    private final DoublePredicate predicate;
    private final BoolBox dropped;

    public DoubleAdvancerDropWhile(DoubleQuery upstream, DoublePredicate predicate) {
        this.upstream = upstream;
        this.predicate = predicate;
        this.dropped = new BoolBox();
    }

    @Override
    public void traverse(DoubleYield yield) {
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
            currDouble = upstream.next();
            if (!predicate.test(currDouble)) {
                this.dropped.set();
                return true;
            }
        }
        if (dropped.isTrue() && upstream.hasNext()) {
            currDouble = upstream.next();
            return true;
        }
        return false;
    }
}
