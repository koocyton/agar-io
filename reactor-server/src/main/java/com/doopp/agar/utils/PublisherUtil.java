package com.doopp.agar.utils;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class PublisherUtil {

    public static <T> Flux<T> fluxCallable(Callable<? extends List<T>> supplier) {
        return Mono.fromCallable(supplier)
                .subscribeOn(Schedulers.elastic())
                .flatMapMany(Flux::fromIterable);
    }

    public static <T> Flux<T> fluxFuture(Supplier<? extends List<T>> supplier) {
        return Mono.fromFuture(CompletableFuture.supplyAsync(supplier))
                .subscribeOn(Schedulers.elastic())
                .flatMapMany(Flux::fromIterable);
    }

    public static <T> Mono<T> monoCallable(Callable<T> supplier) {
        return Mono.fromCallable(supplier)
                .subscribeOn(Schedulers.elastic());
    }

    public static <T> Mono<T> monoFuture(Supplier<T> supplier) {
        return Mono.fromFuture(CompletableFuture.supplyAsync(supplier))
                .subscribeOn(Schedulers.elastic());
    }
}
