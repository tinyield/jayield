package org.jayield.primitives.lng.advs;

import java.util.NoSuchElementException;

import org.jayield.primitives.lng.LongAdvancer;
import org.jayield.primitives.lng.LongQuery;
import org.jayield.primitives.lng.LongYield;

public class LongAdvancerConcat implements LongAdvancer {
    private final LongQuery first;
    private final LongQuery second;

    public LongAdvancerConcat(LongQuery first, LongQuery second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public boolean hasNext() {
        return first.hasNext() || second.hasNext();
    }

    @Override
    public long nextLong() {
        if (first.hasNext()) {
            return first.next();
        } else if (second.hasNext()) {
            return second.next();
        }
        throw new NoSuchElementException("No more elements available on iteration!");
    }

    @Override
    public void traverse(LongYield yield) {
        this.first.traverse(yield);
        this.second.traverse(yield);
    }
}
