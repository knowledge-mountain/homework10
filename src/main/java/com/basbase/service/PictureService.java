package com.basbase.service;

import com.basbase.record.Pair;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.util.Comparator;
import java.util.Optional;

@Service
public class PictureService {
    @Value("${api.host}")
    private String host;
    @Value("${api.path.mars}")
    private String mars;
    @Value("${api.path.planetary}")
    private String planetary;
    @Value("${api.key}")
    private String apiKey;

    private final WebClient webClient = WebClient.builder()
            .codecs(conf -> conf.defaultCodecs().maxInMemorySize(16 * 1024 * 1024))
            .clientConnector(new ReactorClientHttpConnector(HttpClient.create().followRedirect(true)))
            .build();

    public byte[] getLargestPicture(int sol) {
        return getLargestPictureInfo(sol)
                .map(Pair::second)
                .map(this::get)
                .orElseThrow();
    }

//    public byte[] getPictureOfTheDay(int sol) {
//        return webClient.get()
//                .uri(host, b -> b.path(planetary)
//                        .queryParam("api_key", apiKey)
//                        .build())
//                .retrieve()
//                .bodyToMono(JsonNode.class)
//                .map(node -> node.findValuesAsText("hdurl"))
//                .flatMapMany(Flux::fromIterable)
//                .
//    }

    public Optional<Pair> getLargestPictureInfo(int sol) {
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

    public Mono<Pair> head(String url) {
        return webClient.head()
                .uri(url)
                .retrieve()
                .toBodilessEntity()
                .map(re -> new Pair(re.getHeaders().getContentLength(), url));
    }

    public byte[] get(String url) {
        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(byte[].class)
                .block();
    }
}