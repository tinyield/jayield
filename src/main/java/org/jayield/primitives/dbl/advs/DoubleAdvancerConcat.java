package org.jayield.primitives.dbl.advs;

import java.util.NoSuchElementException;

import org.jayield.primitives.dbl.DoubleAdvancer;
import org.jayield.primitives.dbl.DoubleQuery;
import org.jayield.primitives.dbl.DoubleYield;

public class DoubleAdvancerConcat implements DoubleAdvancer {
    private final DoubleQuery first;
    private final DoubleQuery second;

    public DoubleAdvancerConcat(DoubleQuery first, DoubleQuery second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public boolean hasNext() {
        return first.hasNext() || second.hasNext();
    }

    @Override
    public double nextDouble() {
        if (first.hasNext()) {
            return first.next();
        } else if (second.hasNext()) {
            return second.next();
        }
        throw new NoSuchElementException("No more elements available on iteration!");
    }

    @Override
    public void traverse(DoubleYield yield) {
        this.first.traverse(yield);
        this.second.traverse(yield);
    }
}
