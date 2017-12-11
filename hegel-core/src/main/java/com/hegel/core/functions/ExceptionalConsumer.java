package com.hegel.core.functions;

import java.util.function.Consumer;
import java.util.function.Function;

@FunctionalInterface
public interface ExceptionalConsumer<T, E extends Exception> extends Consumer<T> {

    static <T, E extends Exception> Consumer<T> toUncheckedConsumer(
            ExceptionalConsumer<T, E> exceptionalConsumer) {
        return exceptionalConsumer;
    }

    @SuppressWarnings("unused")
    static <T, E extends Exception> void put(ExceptionalConsumer<T, E> exceptionalConsumer, T param) {
        put(exceptionalConsumer, param, RuntimeException::new);
    }

    static <T, E extends Exception, E1 extends Exception> void put(
            ExceptionalConsumer<T, E> exceptionalConsumer,
            T param,
            Function<E, E1> exceptionTransformer) throws E1 {
        try {
            exceptionalConsumer.put(param);
        } catch (Exception e) {
            //noinspection unchecked
            throw exceptionTransformer.apply((E) e);
        }
    }

    static <T, E extends Exception> ExceptionalRunnable<E> supply(ExceptionalConsumer<T, E> exceptionalConsumer,
                                                                  T param) {
        return () -> exceptionalConsumer.accept(param);
    }

    static <T, E extends Exception> Runnable supplyUnchecked(ExceptionalConsumer<T, E> exceptionalConsumer,
                                                             T param) {
        return supply(exceptionalConsumer, param);
    }

    void put(T t) throws E;

    @Override
    default void accept(T t) {
        try {
            put(t);
        } catch (Exception e) {
            //noinspection unchecked
            ifException((E) e);
        }
    }

    default void ifException(E e) {
        Exceptional.throwAsUnchecked(e);
    }
}
