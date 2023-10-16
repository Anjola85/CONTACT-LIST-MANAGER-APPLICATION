package com.example.listmanager.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class RateLimitConfig {
    @Bean
    public Bucket createNewBucket() {
        Refill refill = Refill.intervally(10, Duration.ofMinutes(1));
        Bandwidth limit = Bandwidth.classic(10, refill).withInitialTokens(1);
        return Bucket4j.builder().addLimit(limit).build();
    }
}
