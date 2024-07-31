package com.example.test.controller;

import com.example.test.service.StringService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@RestController
public class HomeController {

    private final StringService stringService;

    public HomeController(StringService stringService) {
        this.stringService = stringService;
    }

    @GetMapping("/test1")
    public String getStrSynchronously() {
        return "test1";
    }

    @GetMapping("/test2")
    public Mono<String> getStrReactive() {
        return Mono.just("test2");
    }

    @GetMapping("/test3")
    public Mono<String> getStrReactiveBlocked() {
        return Mono.just(stringService.getStringBlocked()).publishOn(Schedulers.boundedElastic());
    }

    @GetMapping("/test4")
    public Mono<String> getStrReactiveMerged() {
        Mono<String> resultTest1 = getStrReactive();
        Mono<String> resultTest2 = getStrReactive();

        return Mono.zip(resultTest1, resultTest2)
                .map(t -> t.getT1() + t.getT2());
    }

    @GetMapping("/test5")
    public Mono<String> getStrReactiveParallel() {
        WebClient webClient = WebClient.create("http://localhost:8080");

        return Mono.zip(getStrReactiveBlockedService(webClient), getStrReactiveBlockedService(webClient),
                 getStrReactiveBlockedService(webClient), getStrReactiveBlockedService(webClient),
                 getStrReactiveBlockedService(webClient))
                .map(t -> t.getT1() + t.getT2() + t.getT3() + t.getT4() + t.getT5());
    }

    private Mono<String> getStrReactiveBlockedService(WebClient webClient) {
        return webClient.get()
                .uri("/test3")
                .retrieve()
                .bodyToMono(String.class)
                .subscribeOn(Schedulers.parallel());
    }

    @GetMapping("/test6")
    public Mono<String> getStrFromSecondService() {
        WebClient webClient = WebClient.create("http://localhost:8080");
        return webClient.get()
                .uri("/test2")
                .retrieve()
                .bodyToMono(String.class);
    }
}
