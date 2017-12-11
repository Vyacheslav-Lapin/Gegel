package com.hegel.core.functions;

import java.util.function.Function;
import java.util.function.Supplier;

@FunctionalInterface
public interface ExceptionalFunction<T, R, E extends Exception> extends Function<T, Exceptional<R, E>> {

    static <T, R, E extends Exception> R getOrThrowUnchecked(ExceptionalFunction<T, R, E> exceptionalFunction,
                                                             T param) {
        return exceptionalFunction.apply(param).getOrThrowUnchecked();
    }

    static <T, R, E extends Exception> Function<T, R> toUncheckedFunction(ExceptionalFunction<T, R, E> exceptionalFunction) {
        return t -> getOrThrowUnchecked(exceptionalFunction, t);
    }

    static <T, R, E extends Exception> ExceptionalSupplier<R, E> carry(ExceptionalFunction<T, R, E> exceptionalFunction,
                                                                       T param) {
        return () -> exceptionalFunction.map(param);
    }

    static <T, R, E extends Exception> Supplier<R> carryUnchecked(ExceptionalFunction<T, R, E> exceptionalFunction,
                                                                  T param) {
        return carry(exceptionalFunction, param)::getOrThrowUnchecked;
    }

    R map(T t) throws E;

    @Override
    default Exceptional<R, E> apply(T t) {
        try {
            return Exceptional.withValue(map(t));
        } catch (Exception e) {
            //noinspection unchecked
            return Exceptional.withException((E) e);
        }
    }
}
