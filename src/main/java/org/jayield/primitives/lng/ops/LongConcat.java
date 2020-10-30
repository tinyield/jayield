package org.jayield.primitives.lng.ops;

import org.jayield.primitives.lng.LongAdvancer;
import org.jayield.primitives.lng.LongQuery;
import org.jayield.primitives.lng.LongTraverser;
import org.jayield.primitives.lng.LongYield;

public class LongConcat implements LongAdvancer, LongTraverser {
    private final LongQuery first;
    private final LongQuery second;

    public LongConcat(LongQuery first, LongQuery second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public void traverse(LongYield yield) {
        this.first.traverse(yield);
        this.second.traverse(yield);
    }

    @Override
    public boolean tryAdvance(LongYield yield) {
        return first.tryAdvance(yield) || second.tryAdvance(yield);
    }
}
