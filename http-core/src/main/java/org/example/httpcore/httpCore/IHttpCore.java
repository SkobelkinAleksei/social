package org.example.httpcore.httpCore;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.common.dto.RequestData;
import org.springframework.http.HttpHeaders;
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
    public <T> ResponseEntity<T> get(RequestData requestData, HttpHeaders headers, Class<T> responseType) {
        HttpHeaders finalHeaders = (headers == null) ? new HttpHeaders() : headers;
        return restClient.get()
                .uri(requestData.url())
                .headers(h -> h.putAll(finalHeaders))
                .retrieve()
                .toEntity(responseType);
    }

    @Override
    public <T> ResponseEntity<T> post(RequestData requestData, HttpHeaders headers, Class<T> responseType) {
        HttpHeaders finalHeaders = (headers == null) ? new HttpHeaders() : headers;

        var spec = restClient.post()
                .uri(requestData.url())
                .headers(h -> h.putAll(finalHeaders));

        Object data = requestData.data();
        if (data != null) {
            spec = spec.body(data);
        }

        return spec.retrieve().toEntity(responseType);
    }

    public <T> ResponseEntity<T> put(RequestData requestData, HttpHeaders headers, Class<T> responseType) {
        HttpHeaders finalHeaders = (headers == null) ? new HttpHeaders() : headers;

        var spec = restClient.put()
                .uri(requestData.url())
                .headers(h -> h.putAll(finalHeaders));

        Object data = requestData.data();
        if (data != null) {
            spec = spec.body(data);
            log.debug("PUT {} с body", requestData.url());
        } else {
            log.warn("PUT {} БЕЗ body (data==null)", requestData.url());
            finalHeaders.setContentLength(0);
        }

        return spec.retrieve().toEntity(responseType);
    }

    public void delete(RequestData requestData, HttpHeaders headers) {
        HttpHeaders finalHeaders = (headers == null) ? new HttpHeaders() : headers;
        restClient.delete()
                .uri(requestData.url())
                .headers(h -> h.putAll(finalHeaders))
                .retrieve()
                .toBodilessEntity();
    }

    @Override
    public <T> CompletableFuture<ResponseEntity<T>> asyncPost(RequestData requestData, HttpHeaders headers, Class<T> responseType) {
        // TODO: реализовать
        return CompletableFuture.completedFuture(null);
    }
}
