package org.example.httpcore.httpCore;

import org.example.common.dto.RequestData;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import java.util.concurrent.CompletableFuture;

public sealed interface HttpCore permits IHttpCore {
    <T>ResponseEntity<T> get(RequestData requestData, HttpHeaders headers, Class<T> responseType);
    <T>ResponseEntity<T> post(RequestData requestData, HttpHeaders headers, Class<T> responseType);
    <T>CompletableFuture<ResponseEntity<T>> asyncPost(RequestData requestData, HttpHeaders headers, Class<T> responseType);
}
