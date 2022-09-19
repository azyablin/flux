package com.magnit.flux.client.service;

import com.magnit.flux.model.entity.Operation;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
import javax.annotation.PostConstruct;
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
        BaseSubscriber<Operation> subscriber = new BaseSubscriber<Operation>() {
            @Override
            protected void hookOnSubscribe(Subscription subscription) {

            }

            @Override
            protected void hookOnNext(Operation value) {
                System.out.println(counter.incrementAndGet() + " " + value);
            }
        };

        val flux = client.get().retrieve().bodyToFlux(Operation.class);
        flux.subscribe(subscriber);
        Mono.fromFuture(bucket.asScheduler().consume(rate, service))
            .subscribe(value -> subscriber.request(1));
    }

    @PostConstruct
    public void init() {
        executeBlocking(50);
    }


}
