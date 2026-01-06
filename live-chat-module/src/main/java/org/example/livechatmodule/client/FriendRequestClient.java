package org.example.livechatmodule.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.common.dto.RequestData;
import org.example.common.dto.friend.FriendRequestDto;
import org.example.httpcore.httpCore.SecuredHttpCore;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class FriendRequestClient {
    private final SecuredHttpCore httpCore;

    public List<FriendRequestDto> getOutgoing(String status, int page, int size) {
        String url = "http://localhost:8085/api/v1/social/friends/requests/outgoing?status=PENDING";
        RequestData rd = new RequestData(url, null);

        try {
            ResponseEntity<FriendRequestDto[]> resp = httpCore.get(rd, FriendRequestDto[].class);
            FriendRequestDto[] body = resp.getBody();

            List<FriendRequestDto> requests = body != null ? List.of(body) : List.of();
            log.info("[INFO] Получено исходящих заявок: {}", requests.size());
            return requests;
        } catch (Exception e) {
            log.error("[ERROR] Ошибка получения исходящих заявок: {}", e.getMessage());
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

            log.info("[INFO] ✅ Получено входящих заявок: {} (статус: {})",
                    requests.size(), resp.getStatusCode());
            return requests;
        } catch (Exception e) {
            log.error("[ERROR] Ошибка получения входящих заявок: {}", e.getMessage());
            e.printStackTrace();
            return List.of();
        }
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

    public CompletableFuture<Void> cancelRequest(Long addresseeId) {
        String url = String.format("http://localhost:8085/api/v1/social/friends/requests?addresseeId=%d", addresseeId);
        RequestData rd = new RequestData(url, null);

        log.info("[INFO] Отмена заявки для пользователя #{}", addresseeId);

        return CompletableFuture.supplyAsync(() -> {
            try {
                log.info("[INFO] Отправка DELETE /cancel для пользователя {}", addresseeId);
                httpCore.delete(rd);
                log.info("[INFO] Заявка для пользователя {} успешно отменена", addresseeId);
                return null;
            } catch (Exception e) {
                log.error("[ERROR] Ошибка отмены заявки для пользователя {}: {}", addresseeId, e.getMessage());
                throw new RuntimeException("Ошибка отмены заявки для пользователя #" + addresseeId);
            }
        });
    }
}

