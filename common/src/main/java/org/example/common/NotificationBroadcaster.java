package org.example.common;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

@Getter
@Slf4j
@Component
public class NotificationBroadcaster {
    private final CopyOnWriteArrayList<Consumer<NotificationEvent>> listeners = new CopyOnWriteArrayList<>();

    public void register(Consumer<NotificationEvent> listener) {
        listeners.add(listener);
        log.info("✅ Listener зарегистрирован. Всего: {}", listeners.size());
    }

    public void unregister(Consumer<NotificationEvent> listener) {
        listeners.remove(listener);
        log.info("✅ Listener удалён. Осталось: {}", listeners.size());
    }

    public void broadcast(NotificationEvent event) {
        log.info("🔥 BROADCAST → {} слушателей: {} для userId={}",
                listeners.size(), event.getType(), event.getUserId());
        listeners.forEach(listener -> listener.accept(event));
    }

    @Data
    @RequiredArgsConstructor
    @AllArgsConstructor
    public static class NotificationEvent {
        private String type;
        private Long userId;
        private String message;
    }
}


