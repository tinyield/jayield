package org.jayield;

import org.jayield.boxes.IntBox;

import java.util.Comparator;
import java.util.Optional;
import java.util.OptionalInt;

/**
 * @author Miguel Gamboa
 *         created on 03-07-2017
 */
public class IntSeries {
    private final IntTraversable bulk;
    private final IntAdvancer advancer;

    public IntSeries(IntTraversable bulk, IntAdvancer advancer) {
        this.bulk = bulk;
        this.advancer = advancer;
    }

    public OptionalInt max(){
        IntBox b = new IntBox();
        this.bulk.traverse(e -> {
            if(!b.isPresent()) b.turnPresent(e);
            else if(e > b.getValue()) b.setValue(e);
        });
        return b.isPresent()
                ? OptionalInt.of(b.getValue())
                : OptionalInt.empty();
    }
}
