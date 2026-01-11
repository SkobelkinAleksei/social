package org.example.livechatmodule.mainView;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.notification.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.common.NotificationBroadcaster;
import org.example.livechatmodule.client.UserClient;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationListenerComponent {
    private final NotificationBroadcaster broadcaster;
    private final UserClient userClient;

    private static final Map<Long, UI> USER_UI_MAP = new ConcurrentHashMap<>();

    private Consumer<NotificationBroadcaster.NotificationEvent> listener;

    public void init() {
        Long userId = userClient.getMyProfile().getId();
        UI ui = UI.getCurrent();
        USER_UI_MAP.put(userId, ui);
        log.info("[LISTENER] UI registered userId={} ui={}", userId, ui.hashCode());

        listener = event -> {
            log.info("[LISTENER] Получено: {} userId={}", event.getType(), event.getUserId());

            if (!event.getUserId().equals(userId)) return;

            UI targetUi = USER_UI_MAP.get(event.getUserId());
            if (targetUi == null) {
                log.warn("[LISTENER] Нет UI для userId={}", event.getUserId());
                return;
            }

            targetUi.access(() -> {
                Notification.show("🔔 " + event.getMessage(), 10000, Notification.Position.TOP_END);
                log.info("[SHOW] ✅ userId={} ui={}", userId, targetUi.hashCode());
            });
        };
        broadcaster.register(listener);
        log.info("[LISTENER] ✅ Active для userId={}", userId);
    }

    public void destroy() {
        Long userId = userClient.getMyProfile().getId();
        USER_UI_MAP.remove(userId);
        if (listener != null) broadcaster.unregister(listener);
        log.info("[LISTENER] Destroyed userId={}", userId);
    }
}
