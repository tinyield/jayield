package org.jayield.advs;

import java.util.function.Predicate;

import org.jayield.Query;
import org.jayield.Yield;
import org.jayield.boxes.BoolBox;

public class AdvancerDropWhile<T> extends AbstractAdvancer<T> {

    private final Query<T> upstream;
    private final Predicate<T> predicate;
    private final BoolBox dropped;

    public AdvancerDropWhile(Query<T> upstream, Predicate<T> predicate) {
        this.upstream = upstream;
        this.predicate = predicate;
        this.dropped = new BoolBox();
    }

    @Override
    public void traverse(Yield<? super T> yield) {
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
            curr = upstream.next();
            if (!predicate.test(curr)) {
                this.dropped.set();
                return true;
            }
        }
        if (dropped.isTrue() && upstream.hasNext()) {
            curr = upstream.next();
            return true;
        }
        return false;
    }
}
