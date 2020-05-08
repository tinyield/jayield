package org.jayield.advs;

import java.util.NoSuchElementException;

import org.jayield.Advancer;
import org.jayield.Query;
import org.jayield.Yield;

public class AdvancerConcat<T> implements Advancer<T> {
    private final Query<T> first;
    private final Query<T> second;

    public AdvancerConcat(Query<T> first, Query<T> second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public boolean hasNext() {
        return first.hasNext() || second.hasNext();
    }

    @Override
    public T next() {
        if(first.hasNext()) {
            return first.next();
        } else if(second.hasNext()) {
            return second.next();
        }
        throw new NoSuchElementException("No more elements available on iteration!");
    }

    @Override
    public void traverse(Yield<? super T> yield) {
        this.first.traverse(yield);
        this.second.traverse(yield);
    }
}
