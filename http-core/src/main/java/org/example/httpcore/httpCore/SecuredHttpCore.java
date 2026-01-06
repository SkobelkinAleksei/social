package org.example.httpcore.httpCore;

import lombok.RequiredArgsConstructor;
import org.example.common.dto.RequestData;
import org.example.common.security.JwtHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class  SecuredHttpCore {

    private final IHttpCore iHttpCore;
    private final JwtHolder jwtHolder;

    public <T> ResponseEntity<T> get(RequestData requestData, Class<T> responseType) {
        HttpHeaders headers = buildAuthHeaders();
        return iHttpCore.get(requestData, headers, responseType);
    }

    public <T> ResponseEntity<T> post(RequestData requestData, Class<T> responseType) {
        HttpHeaders headers = buildAuthHeaders();
        return iHttpCore.post(requestData, headers, responseType);
    }

    public void delete(RequestData requestData) {
        HttpHeaders headers = buildAuthHeaders();
        iHttpCore.delete(requestData, headers);
    }

    public <T> ResponseEntity<T> put(RequestData requestData, Class<T> responseType) {
        HttpHeaders headers = buildAuthHeaders();
        return iHttpCore.put(requestData, headers, responseType);
    }

    public ResponseEntity<Void> putNoContent(RequestData requestData) {
        HttpHeaders headers = buildAuthHeaders();
        return iHttpCore.put(requestData, headers, Void.class);
    }

    private HttpHeaders buildAuthHeaders() {
        HttpHeaders headers = new HttpHeaders();
        String token = jwtHolder.getToken();
        if (token != null && !token.isBlank()) {
            headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        }
        return headers;
    }
}