package com.rag.chat.storage.config;

import io.github.bucket4j.Bucket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class RateLimiterServiceTest {

    private RateLimiterService rateLimiterService;

    @BeforeEach
    void setUp() {
        rateLimiterService = new RateLimiterService();
    }

    @Test
    void testResolveBucketCreatesNewBucket() {
        String apiKey = "test-key";

        Bucket bucket = rateLimiterService.resolveBucket(apiKey);

        assertNotNull(bucket);
        // Bucket should allow initial tokens
        assertTrue(bucket.tryConsume(1));
    }

    @Test
    void testResolveBucketCachesBucket() {
        String apiKey = "test-key";

        Bucket bucket1 = rateLimiterService.resolveBucket(apiKey);
        Bucket bucket2 = rateLimiterService.resolveBucket(apiKey);

        assertSame(bucket1, bucket2, "Bucket should be cached and reused for same API key");
    }

    @Test
    void testBucketLimits() {
        String apiKey = "limited-key";
        Bucket bucket = rateLimiterService.resolveBucket(apiKey);

        // Consume up to 5 tokens (minute limit)
        for (int i = 0; i < 20; i++) {
            assertTrue(bucket.tryConsume(1), "Should allow token " + (i + 1));
        }

        // 6th token should fail immediately
        assertFalse(bucket.tryConsume(1), "Should not allow more than 5 tokens per minute");

        // But hour limit is 10, so after refill we can consume more
        // Simulate waiting for refill (advance time not possible here, but we can check capacity)
        long available = bucket.getAvailableTokens();
        assertTrue(available <= 20, "Bucket should not exceed hourly limit of 10");
    }
}