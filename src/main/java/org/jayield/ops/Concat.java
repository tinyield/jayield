package org.jayield.ops;

import org.jayield.Advancer;
import org.jayield.Query;
import org.jayield.Traverser;
import org.jayield.Yield;

public class Concat<T> implements Advancer<T>, Traverser<T> {
    private final Query<T> first;
    private final Query<T> second;

    public Concat(Query<T> first, Query<T> second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public void traverse(Yield<? super T> yield) {
        this.first.traverse(yield);
        this.second.traverse(yield);
    }

    @Override
    public boolean tryAdvance(Yield<? super T> yield) {
        return first.tryAdvance(yield) || second.tryAdvance(yield);
    }
}
