package org.jayield;

/**
 * @author Miguel Gamboa
 *         created on 06-07-2017
 */
public class UserExt {
    static <U> Traversable<U> collapse(Series<U> src) {
        return yield -> {
            final Object[] prev = {null};
            src.traverse(item -> {
                if (prev[0] == null || !prev[0].equals(item))
                    yield.ret((U) (prev[0] = item));
            });
        };
    }
}
