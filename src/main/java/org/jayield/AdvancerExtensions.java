package org.jayield;

import org.jayield.boxes.BoolBox;
import org.jayield.boxes.Box;
import org.jayield.boxes.IntBox;

import java.util.HashSet;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import java.util.function.UnaryOperator;

/**
 * @author Miguel Gamboa
 *         created on 06-07-2017
 */
public class AdvancerExtensions {
    static <T> Advancer<T> iterate(T seed, UnaryOperator<T> f) {
        Box<T> box = new Box<>(seed, f);
        return yield -> {
            yield.ret(box.getValue());
            box.inc();
            return true;
        };
    }

    static <T> Advancer<T> of(T...data) {
        IntBox index = new IntBox(-1);
        return yield -> {
            int i;
            if((i = index.inc()) < data.length) yield.ret(data[i]);
            return i < data.length;
        };
    }

    static <T, R> Advancer<R> map(Series<T> source, Function<T, R> mapper) {
        return yield -> {
            return source.tryAdvance(item -> yield.ret(mapper.apply(item)));
        };
    }

    static <T> IntAdvancer mapToInt(Series<T> source, ToIntFunction<T> mapper) {
        return yield -> {
            return source.tryAdvance(item -> yield.ret(mapper.applyAsInt(item)));
        };
    }

    static <T, R> Advancer<R> flatMap(
            Series<T> source, Function<T, Series<R>> mapper)
    {
        final Box<Series<R>> curr = new Box<>(Series.<R>empty());
        return yield -> {
            while (!curr.getValue().tryAdvance(yield)) {
                if(!source.tryAdvance((t) -> curr.setValue(mapper.apply(t))))
                    return false;
            }
            return true;
        };
    }

    static <T> Advancer<T> filter(Series<T> source, Predicate<T> p) {
        return yield -> {
            BoolBox found = new BoolBox();
            while(found.isFalse()) {
                boolean hasNext = source.tryAdvance(item -> {
                    if(p.test(item)) {
                        yield.ret(item);
                        found.set();
                    }
                });
                if(!hasNext) break;
            }
            return found.isTrue();
        };
    }

    static <T> Advancer<T> skip(Series<T> source, int n) {
        IntBox index = new IntBox(0);
        return yield -> {
            for (; index.getValue() < n; index.inc())
                source.tryAdvance(item -> {});
            return source.tryAdvance(yield);
        };
    }

    static <T> Advancer<T> limit(Series source, int n) {
        IntBox index = new IntBox(-1);
        return yield -> index.inc() < n ? source.tryAdvance(yield) : false;
    }

    static <T> Advancer<T> distinct(Series<T> source) {
        final HashSet<T> mem = new HashSet<>();
        final BoolBox found = new BoolBox();
        return yield -> {
            found.reset();
            while(found.isFalse() && source.tryAdvance(item -> {
                if(mem.add(item)) {
                    yield.ret(item);
                    found.set();
                }
            }));
            return found.isTrue();
        };
    }
}
