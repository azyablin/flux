package com.magnit.flux.client.service;

import com.magnit.flux.model.entity.Operation;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import javax.annotation.PostConstruct;
import lombok.SneakyThrows;
import lombok.val;
import org.reactivestreams.Subscription;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Mono;

/**
 * Использование backe4j совместно с webflux
 */
@Service
public class RateLimitService {

    private static final WebClient client = WebClient
        .create("http://localhost:8095/operations-stream");

    /**
     * Выводит на конмсоль данные, полученные из REST с заданной скоросью rate в минуту Использует
     * блокирующий API для ожидания токенов из bucket,
     */
    public void executeBlocking(long rate) {
        AtomicLong counter = new AtomicLong(0);
        val limit = Bandwidth.simple(rate, Duration.ofMinutes(1));
        val bucket = Bucket.builder().addLimit(limit).build();
        val flux = client.get().retrieve().bodyToFlux(Operation.class);
        flux.delayUntil(operation -> {
            try {
                bucket.asBlocking().consume(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return Mono.just("two");
        }).subscribe(value -> System.out.println(counter.incrementAndGet() + " " + value));
    }

    /**
     * Выводит на конмсоль данные, полученные из REST с заданной скоросью rate в минуту Использует
     * шедулинг для ожидания токенов из bucket
     */
    public void executeScheduling(long rate) {
        AtomicLong counter = new AtomicLong(0);
        val service = Executors.newSingleThreadScheduledExecutor();
        val limit = Bandwidth.simple(rate, Duration.ofMinutes(1));
        val bucket = Bucket.builder().addLimit(limit).build();
        final long watTime = 50;

        BaseSubscriber<Operation> subscriber = new BaseSubscriber<Operation>() {

            private void handleFutureResult(Boolean result) {
                if (result) {
                    this.request(1);
                } else {
                    service.schedule(() -> asyncRequest(), watTime, TimeUnit.NANOSECONDS);
                }
            }

            @SneakyThrows
            private void asyncRequest() {
                val future = bucket.asScheduler().tryConsume(1, watTime, service);
                if (future.isDone()) {
                    handleFutureResult(future.get());
                } else {
                    future.thenAcceptAsync(value -> {
                        handleFutureResult(value);
                    });
                }

            }

            @Override
            protected void hookOnSubscribe(Subscription subscription) {
                asyncRequest();
            }

            @Override
            protected void hookOnNext(Operation value) {
                System.out.println(counter.incrementAndGet() + " " + value);
                asyncRequest();
            }

        };
        val flux = client.get().retrieve().bodyToFlux(Operation.class);
        flux.subscribe(subscriber);

    }


    @PostConstruct
    public void init() {
        executeScheduling(50);
    }


}
