package com.example.test.service;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Service
public class StringServiceImpl implements StringService {

    @Override
    public Mono<String> getStringNonBlocked() {
        return Mono.just("waited").delayElement(Duration.ofSeconds(10));
    }

    @Override
    public String getStringBlocked() {
        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "blocked";
    }
}
