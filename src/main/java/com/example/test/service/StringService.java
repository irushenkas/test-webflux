package com.example.test.service;

import reactor.core.publisher.Mono;

public interface StringService {
    Mono<String> getStringNonBlocked();
    String getStringBlocked();
}
