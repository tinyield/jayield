package org.jayield.primitives.intgr.ops;

import org.jayield.primitives.intgr.IntAdvancer;
import org.jayield.primitives.intgr.IntQuery;
import org.jayield.primitives.intgr.IntTraverser;
import org.jayield.primitives.intgr.IntYield;

public class IntConcat implements IntAdvancer, IntTraverser {
    private final IntQuery first;
    private final IntQuery second;

    public IntConcat(IntQuery first, IntQuery second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public void traverse(IntYield yield) {
        this.first.traverse(yield);
        this.second.traverse(yield);
    }

    @Override
    public boolean tryAdvance(IntYield yield) {
        return first.tryAdvance(yield) || second.tryAdvance(yield);
    }
}
