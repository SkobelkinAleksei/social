package org.example.httpcore.httpCore;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.common.dto.RequestData;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.concurrent.CompletableFuture;

@Slf4j
@RequiredArgsConstructor
@Component
public non-sealed class IHttpCore implements HttpCore {

    private final RestClient restClient;

    @Override
    public <T>ResponseEntity<T> get(
            RequestData requestData, HttpHeaders headers, Class<T> responseType) {
        HttpHeaders finalHeaders = (headers == null) ? new HttpHeaders() : headers;

        return restClient.get()
                .uri(requestData.url())
                .headers(h -> h.putAll(finalHeaders))
                .retrieve()
                .toEntity(responseType);
    }

    @Override
    public <T>ResponseEntity<T> post(RequestData requestData, HttpHeaders headers, Class<T> responseType) {

        HttpHeaders finalHeaders = (headers == null) ? new HttpHeaders() : headers;

        return restClient.post().uri(requestData.url())
                .headers(h -> h.putAll(finalHeaders))
                .body(requestData.data())
                .retrieve()
                .toEntity(responseType);
    }

    @Override
    public <T>CompletableFuture<ResponseEntity<T>> asyncPost(RequestData requestData, HttpHeaders headers, Class<T> responseType) {
        return null;
    }
}
