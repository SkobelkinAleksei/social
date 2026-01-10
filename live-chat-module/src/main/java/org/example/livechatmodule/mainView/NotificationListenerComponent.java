package org.example.livechatmodule.mainView;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.notification.Notification;
import lombok.extern.slf4j.Slf4j;
import org.example.common.NotificationBroadcaster;
import org.example.common.NotificationBroadcaster.NotificationEvent;

import java.util.Optional;
import java.util.function.Consumer;

@Slf4j
public class NotificationListenerComponent {

    private final NotificationBroadcaster broadcaster;
    private Consumer<NotificationEvent> listener;

    public NotificationListenerComponent(NotificationBroadcaster broadcaster) {
        this.broadcaster = broadcaster;
    }

    public void init() {
        listener = event -> {
            log.info("📢 [СЛУШАТЕЛЬ] Получено: {}", event.getMessage());

            // 🔥 ИСПРАВЛЕНО
            UI ui = UI.getCurrent();
            if (ui != null) {
                ui.access(() -> showNotification(event.getMessage()));
            }
        };
        broadcaster.register(listener);
        log.info("✅ Слушатель уведомлений зарегистрирован");
    }


    private void showNotification(String message) {
        log.info("🔔 [ПОКАЗ] {}", message);
        Notification.show(message, 10000, Notification.Position.TOP_END);
    }

    public void destroy() {
        if (listener != null) {
            broadcaster.unregister(listener);
            log.info("🔕 Слушатель уведомлений удален");
        }
    }
}
