package com.hegel.core.functions;

import java.util.function.Function;

@FunctionalInterface
public interface ExceptionalVarConsumer<T, E extends Exception> extends VarConsumer<T> {

    static <T, E extends Exception> VarConsumer<T> toUnchecked(ExceptionalVarConsumer<T, E> exceptionalConsumer) {
        return exceptionalConsumer;
    }

    @SafeVarargs
    @SuppressWarnings("unused")
    static <T, E extends Exception> void call(ExceptionalVarConsumer<T, E> exceptionalConsumer, T... params) {
        call(exceptionalConsumer, RuntimeException::new, params);
    }

    @SafeVarargs
    static <T, E extends Exception, E1 extends Exception> void call(ExceptionalVarConsumer<T, E> teExceptionalVarConsumer,
                                                                    Function<E, E1> exceptionTransformer,
                                                                    T... params) throws E1 {
        try {
            teExceptionalVarConsumer.call(params);
        } catch (Exception e) {
            //noinspection unchecked
            throw exceptionTransformer.apply((E) e);
        }
    }

    @SafeVarargs
    static <T, E extends Exception> ExceptionalRunnable<E> supply(ExceptionalVarConsumer<T, E> exceptionalVarConsumer,
                                                                  T... params) {
        return () -> exceptionalVarConsumer.accept(params);
    }

    static <T, E extends Exception> Runnable supplyUnchecked(ExceptionalVarConsumer<T, E> exceptionalVarConsumer,
                                                             T... params) {
        return supply(exceptionalVarConsumer, params);
    }

    @SuppressWarnings("unchecked")
    void call(T... params) throws E;

    @SuppressWarnings("unchecked")
    @Override
    default void accept(T... params) {
        try {
            call(params);
        } catch (Exception e) {
            //noinspection unchecked
            ifException((E) e);
        }
    }

    default void ifException(E e) {
        Exceptional.throwAsUnchecked(e);
    }
}
