package vn.aeoc.base.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

@Component
public class QueryExecutor {

    @Autowired
    private Executor dbExecutor;

    public <T> CompletableFuture<T> async(Supplier<T> supplier) {
        return CompletableFuture.supplyAsync(supplier, dbExecutor);
    }

    public <A, B> Tuple2<A, B> parallel2(
            Supplier<A> q1,
            Supplier<B> q2
    ) {

        CompletableFuture<A> f1 = async(q1);
        CompletableFuture<B> f2 = async(q2);

        return new Tuple2<>(
                f1.join(),
                f2.join()
        );
    }

    public <A, B, C> Tuple3<A, B, C> parallel3(
            Supplier<A> s1,
            Supplier<B> s2,
            Supplier<C> s3
    ) {

        CompletableFuture<A> f1 = CompletableFuture.supplyAsync(s1);
        CompletableFuture<B> f2 = CompletableFuture.supplyAsync(s2);
        CompletableFuture<C> f3 = CompletableFuture.supplyAsync(s3);

        return new Tuple3<>(
                f1.join(),
                f2.join(),
                f3.join()
        );
    }

    public <A, B, C, D> Tuple4<A,B,C,D> parallel4(
            Supplier<A> s1,
            Supplier<B> s2,
            Supplier<C> s3,
            Supplier<D> s4
    ) {

        CompletableFuture<A> f1 = CompletableFuture.supplyAsync(s1);
        CompletableFuture<B> f2 = CompletableFuture.supplyAsync(s2);
        CompletableFuture<C> f3 = CompletableFuture.supplyAsync(s3);
        CompletableFuture<D> f4 = CompletableFuture.supplyAsync(s4);

        return new Tuple4<>(
                f1.join(),
                f2.join(),
                f3.join(),
                f4.join()
        );
    }

    public <A, B, C, D, E> Tuple5<A,B,C,D, E> parallel5(
            Supplier<A> s1,
            Supplier<B> s2,
            Supplier<C> s3,
            Supplier<D> s4,
            Supplier<E> s5
    ) {

        CompletableFuture<A> f1 = CompletableFuture.supplyAsync(s1);
        CompletableFuture<B> f2 = CompletableFuture.supplyAsync(s2);
        CompletableFuture<C> f3 = CompletableFuture.supplyAsync(s3);
        CompletableFuture<D> f4 = CompletableFuture.supplyAsync(s4);
        CompletableFuture<E> f5 = CompletableFuture.supplyAsync(s5);

        return new Tuple5<>(
                f1.join(),
                f2.join(),
                f3.join(),
                f4.join(),
                f5.join()
        );
    }

}