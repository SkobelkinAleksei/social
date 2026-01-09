package org.example.livechatmodule.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.common.dto.RequestData;
import org.example.common.dto.friend.FriendRequestDto;
import org.example.httpcore.httpCore.SecuredHttpCore;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class FriendRequestClient {
    private final SecuredHttpCore httpCore;

    public void addFriend(Long addresseeId) {
        String url = "http://localhost:8085/api/v1/social/friends/requests?addresseeId=" + addresseeId;
        RequestData rd = new RequestData(url, null);

        try {
            log.info("[INFO] Отправка заявки в друзья пользователю id={}", addresseeId);
            httpCore.post(rd, Void.class);
            log.info("[INFO] Заявка успешно отправлена пользователю id={}", addresseeId);
        } catch (Exception e) {
            log.error("[ERROR] Не удалось отправить заявку пользователю id={}: {}", addresseeId, e.getMessage(), e);
            throw new RuntimeException("Не удалось отправить заявку в друзья", e);
        }
    }

    public List<FriendRequestDto> getOutgoing(int page, int size) {
        List<FriendRequestDto> result = new ArrayList<>();
        result.addAll(getByStatus("PENDING", page / 2, size));
        result.addAll(getOutgoingRejected(page / 2, size));
        log.info("[INFO] Активных исходящих (PENDING+REJECTED): {}", result.size());
        return result;
    }

    public List<FriendRequestDto> getByStatus(String status, int page, int size) {
        StringBuilder urlBuilder = new StringBuilder("http://localhost:8085/api/v1/social/friends/requests/outgoing?page=");
        urlBuilder.append(page).append("&size=").append(size).append("&status=").append(status);

        String url = urlBuilder.toString();
        RequestData rd = new RequestData(url, null);

        try {
            log.info("[INFO] GET outgoing {}: {}", status, url);
            ResponseEntity<FriendRequestDto[]> resp = httpCore.get(rd, FriendRequestDto[].class);
            FriendRequestDto[] body = resp.getBody();
            return body != null ? Arrays.asList(body) : List.of();
        } catch (Exception e) {
            log.error("[ERROR] Ошибка getByStatus({}): {}", status, e.getMessage(), e);
            return List.of();
        }
    }

    public List<FriendRequestDto> getIncoming(int page, int size) {
        String url = "http://localhost:8085/api/v1/social/friends/requests/incoming";
        RequestData rd = new RequestData(url, null);

        log.info("[INFO] Запрос входящих заявок: {}", url);

        try {
            ResponseEntity<FriendRequestDto[]> resp = httpCore.get(rd, FriendRequestDto[].class);
            FriendRequestDto[] body = resp.getBody();
            List<FriendRequestDto> requests = body != null ? List.of(body) : List.of();

            log.info("[INFO] Получено входящих заявок: {} (статус: {})",
                    requests.size(), resp.getStatusCode());
            return requests;
        } catch (Exception e) {
            log.error("[ERROR] Ошибка получения входящих заявок: {}", e.getMessage());
            e.printStackTrace();
            return List.of();
        }
    }

    public List<FriendRequestDto> getOutgoingRejected(int page, int size) {
        return getByStatus("REJECTED", page, size);
    }

    public CompletableFuture<Void> acceptRequest(Long requestId) {
        String url = String.format("http://localhost:8085/api/v1/social/friends/requests/%d?status=ACCEPTED", requestId);
        RequestData rd = new RequestData(url, null);

        log.info("[INFO] Принятие заявки #{}", requestId);

        return CompletableFuture.supplyAsync(() -> {
            try {
                log.info("[INFO] Отправка PUT /accept для заявки #{}", requestId);
                ResponseEntity<String> resp = httpCore.put(rd, String.class);

                if (resp.getStatusCode().is2xxSuccessful()) {
                    log.info("[INFO] Заявка #{} успешно принята", requestId);
                    return null;
                } else {
                    log.error("[ERROR] Ошибка принятия заявки #{}: HTTP {} - {}",
                            requestId, resp.getStatusCode(), resp.getBody());
                    throw new RuntimeException("Ошибка принятия заявки");
                }
            } catch (Exception e) {
                log.error("[ERROR] Исключение при принятии заявки {}: {}", requestId, e.getMessage(), e);
                throw new RuntimeException("Ошибка принятия заявки " + requestId, e);
            }
        });
    }

    public CompletableFuture<Void> rejectRequest(Long requestId) {
        String url = String.format(
                "http://localhost:8085/api/v1/social/friends/requests/%d?status=REJECTED", requestId
        );
        RequestData rd = new RequestData(url, null);

        return CompletableFuture.supplyAsync(() -> {
            try {
                log.info("[INFO] Отправка PUT /reject для заявки {}", requestId);
                httpCore.put(rd, String.class);
                log.info("[INFO] Заявка {} успешно отклонена", requestId);
                return null;
            } catch (Exception e) {
                log.error("[ERROR] Ошибка отклонения заявки {}: {}", requestId, e.getMessage(), e);
                throw new RuntimeException("Ошибка отклонения заявки " + requestId, e);
            }
        });
    }

    public CompletableFuture<Void> cancelRequest(Long requestId, Long addresseeId) {
        if (requestId == null || addresseeId == null) {
            return CompletableFuture.failedFuture(new IllegalArgumentException("requestId и addresseeId обязательны"));
        }

        // DELETE /requests?addresseeId=X
        String url = String.format("http://localhost:8085/api/v1/social/friends/requests?addresseeId=%d", addresseeId);
        RequestData rd = new RequestData(url, null);

        log.info("[INFO] Отмена заявки requestId={} addresseeId={} (DELETE ?addresseeId)", requestId, addresseeId);

        return CompletableFuture.supplyAsync(() -> {
            try {
                log.info("[INFO] Отправка DELETE /requests?addresseeId={} для заявки {}", addresseeId, requestId);
                httpCore.delete(rd);
                log.info("[INFO] Заявка requestId={} отменена", requestId);
                return null;
            } catch (Exception e) {
                log.error("[ERROR] Ошибка отмены заявки {}: {}", requestId, e.getMessage(), e);
                throw new RuntimeException("Ошибка отмены заявки requestId=" + requestId, e);
            }
        });
    }




}

