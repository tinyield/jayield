package org.jayield.primitives.intgr.advs;

import java.util.NoSuchElementException;

import org.jayield.primitives.intgr.IntAdvancer;
import org.jayield.primitives.intgr.IntQuery;
import org.jayield.primitives.intgr.IntYield;

public class IntAdvancerConcat implements IntAdvancer {
    private final IntQuery first;
    private final IntQuery second;

    public IntAdvancerConcat(IntQuery first, IntQuery second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public boolean hasNext() {
        return first.hasNext() || second.hasNext();
    }

    @Override
    public int nextInt() {
        if (first.hasNext()) {
            return first.next();
        } else if (second.hasNext()) {
            return second.next();
        }
        throw new NoSuchElementException("No more elements available on iteration!");
    }

    @Override
    public void traverse(IntYield yield) {
        this.first.traverse(yield);
        this.second.traverse(yield);
    }
}
