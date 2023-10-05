package com.basbase.service;

import com.basbase.record.Pair;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Comparator;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PictureService {
    @Value("${api.host}")
    private String host;
    @Value("${api.path.mars}")
    private String mars;
    @Value("${api.path.planetary}")
    private String planetary;
    @Value("${api.key}")
    private String apiKey;

    private final WebClient webClient;

    public ResponseEntity<byte[]> getLargestPicture(int sol) {
        return getLargestPictureInfo(sol)
                .map(Pair::second)
                .map(this::get)
                .orElseThrow();
    }

    public ResponseEntity<byte[]> getPictureOfTheDay() {
        return webClient.get()
                .uri(host, b -> b.path(planetary)
                        .queryParam("api_key", apiKey)
                        .build())
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(node -> node.findValuesAsText("hdurl"))
                .flatMapMany(Flux::fromIterable)
                .flatMap(url -> webClient.get()
                        .uri(url)
                        .exchangeToMono(resp -> resp.toEntity(byte[].class))
                ).blockFirst();
    }

    private Optional<Pair> getLargestPictureInfo(int sol) {
        return webClient.get()
                .uri(host, b -> b.path(mars)
                        .queryParam("sol", sol)
                        .queryParam("api_key", apiKey)
                        .build())
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(node -> node.findValuesAsText("img_src"))
                .flatMapMany(Flux::fromIterable)
                .flatMap(this::head)
                .collectList()
                .blockOptional()
                .flatMap(pairs -> pairs.stream().max(Comparator.comparing(Pair::first)));
    }

    private Mono<Pair> head(String url) {
        return webClient.head()
                .uri(url)
                .retrieve()
                .toBodilessEntity()
                .map(re -> new Pair(re.getHeaders().getContentLength(), url));
    }

    private ResponseEntity<byte[]> get(String url) {
        return webClient.get()
                .uri(url)
                .retrieve()
                .toEntity(byte[].class)
                .block();
    }
}