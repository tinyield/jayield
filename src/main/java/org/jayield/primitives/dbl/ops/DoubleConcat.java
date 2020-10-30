package org.jayield.primitives.dbl.ops;

import org.jayield.primitives.dbl.DoubleAdvancer;
import org.jayield.primitives.dbl.DoubleQuery;
import org.jayield.primitives.dbl.DoubleTraverser;
import org.jayield.primitives.dbl.DoubleYield;

public class DoubleConcat implements DoubleAdvancer, DoubleTraverser {
    private final DoubleQuery first;
    private final DoubleQuery second;

    public DoubleConcat(DoubleQuery first, DoubleQuery second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public void traverse(DoubleYield yield) {
        this.first.traverse(yield);
        this.second.traverse(yield);
    }

    @Override
    public boolean tryAdvance(DoubleYield yield) {
        return first.tryAdvance(yield) || second.tryAdvance(yield);
    }
}
