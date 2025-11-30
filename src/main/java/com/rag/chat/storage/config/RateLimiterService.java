package com.rag.chat.storage.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimiterService {

    private final Map<String, Bucket> cache = new ConcurrentHashMap<>();

    public Bucket resolveBucket(String apiKey) {
        return cache.computeIfAbsent(apiKey, this::newBucket);
    }

    private Bucket newBucket(String apiKey) {

        Bandwidth limitPerMinute = Bandwidth.classic(
                20, Refill.greedy(20, Duration.ofMinutes(1))
        );

        Bandwidth limitPerHour = Bandwidth.classic(
                200, Refill.greedy(200, Duration.ofHours(1))
        );

        return Bucket.builder()
                .addLimit(limitPerMinute)
                .addLimit(limitPerHour)
                .build();
    }
}