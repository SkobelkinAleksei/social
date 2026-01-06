package org.example.livechatmodule.mainView;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.livechatmodule.client.UserClient;
import org.example.livechatmodule.entity.MessageEntity;
import org.example.livechatmodule.repository.MessageRepository;
import org.example.livechatmodule.service.ChatService;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@Route("chat")
@RequiredArgsConstructor
public class ChatView extends VerticalLayout implements HasUrlParameter<Long> {

    private final ChatService chatService;
    private final MessageRepository messageRepository;
    private final UserClient userClient;

    private final Div messagesBox = new Div();
    private TextField inputField;
    private Long chatId;
    private List<MessageEntity> lastLoadedMessages;
    private ScheduledExecutorService scheduler;
    private final DateTimeFormatter timeFormatter =
            DateTimeFormatter.ofPattern("HH:mm:ss").withZone(ZoneId.systemDefault());

    @Override
    public void setParameter(BeforeEvent event, Long chatId) {
        this.chatId = chatId;
        buildLayout();
        loadMessages();
    }

    private void buildLayout() {
        removeAll();
        setSizeFull();
        setPadding(true);
        setSpacing(true);
        getStyle().set("background-color", "#edf0f3");

        messagesBox.removeAll();
        messagesBox.getStyle()
                .set("background-color", "white")
                .set("padding", "20px")
                .set("border-radius", "10px")
                .set("height", "70vh")
                .set("overflow-y", "auto")
                .set("flex-grow", "1")
                .set("display", "flex")
                .set("flex-direction", "column")
                .set("gap", "4px");
        messagesBox.setWidth("100%");

        Button back = new Button("← Назад", e -> {
            stopScheduler();
            getUI().ifPresent(ui -> ui.navigate("friends/" + getCurrentUserId()));
        });
        back.getStyle().set("font-weight", "500");

        inputField = new TextField();
        inputField.setPlaceholder("Введите сообщение...");
        inputField.setWidthFull();
        inputField.getStyle()
                .set("border-radius", "12px")
                .set("font-size", "15px")
                .set("padding", "12px 16px");

        inputField.addKeyPressListener(Key.ENTER, e -> sendMessageFromInput());

        Button sendBtn = new Button("📤", e -> sendMessageFromInput());
        sendBtn.getStyle()
                .set("background", "linear-gradient(135deg, #4a76a8, #3a5f88)")
                .set("color", "white")
                .set("border-radius", "12px")
                .set("font-weight", "600")
                .set("padding", "12px 20px")
                .set("box-shadow", "0 2px 8px rgba(74,118,168,0.3)");

        HorizontalLayout inputLayout = new HorizontalLayout(inputField, sendBtn);
        inputLayout.setWidthFull();
        inputLayout.setAlignItems(Alignment.END);
        inputLayout.setSpacing(true);
        inputLayout.setPadding(true);

        // ✅ Detach listener для очистки ресурсов
        addDetachListener(detachEvent -> stopScheduler());

        add(back, messagesBox, inputLayout);
        expand(messagesBox);
    }

    private Long getCurrentUserId() {
        try {
            return userClient.getMyProfile().getId();
        } catch (Exception e) {
            log.error("Не удалось получить текущего пользователя", e);
            return null;
        }
    }

    private void sendMessageFromInput() {
        String text = inputField.getValue();
        if (text != null && !text.isBlank()) {
            sendMessage(text.trim());
            inputField.clear();
        }
    }

    private void loadMessages() {
        try {
            List<MessageEntity> messages = chatService.loadMessages(chatId);
            messagesBox.removeAll();

            if (messages.isEmpty()) {
                Div emptyState = new Div(new Text("💬 Начните переписку!"));
                emptyState.getStyle()
                        .set("color", "#6b7b8a")
                        .set("font-size", "16px")
                        .set("text-align", "center")
                        .set("padding", "40px");
                messagesBox.add(emptyState);
                return;
            }

            messages.stream()
                    .sorted(Comparator.comparing(MessageEntity::getCreatedAt))
                    .forEach(this::addMessageToView);

            lastLoadedMessages = List.copyOf(messages);
        } catch (Exception e) {
            log.error("Ошибка загрузки сообщений для чата {}: {}", chatId, e.getMessage());
        }
    }

    private void addMessageToView(MessageEntity message) {
        boolean isMine = message.getUserId().equals(getCurrentUserId());
        String time = timeFormatter.format(message.getCreatedAt());

        Div messageContainer = new Div();
        messageContainer.getStyle()
                .set("display", "flex")
                .set("justify-content", isMine ? "flex-end" : "flex-start")
                .set("margin", "8px 0")
                .set("padding", "0 8px");

        Div bubble = new Div();
        Div content = new Div(new Text(message.getContent()));
        Div timeLabel = new Div(new Text(" " + time));

        content.getStyle()
                .set("font-size", "15px")
                .set("line-height", "1.4")
                .set("margin-bottom", "4px");

        timeLabel.getStyle()
                .set("font-size", "11px")
                .set("color", isMine ? "#e8f4fd" : "#6b7b8a")
                .set("text-align", isMine ? "right" : "left");

        bubble.add(content, timeLabel);
        bubble.getStyle()
                .set("padding", "14px 18px")
                .set("border-radius", "20px")
                .set("max-width", "75%")
                .set("white-space", "pre-wrap")
                .set("word-wrap", "break-word")
                .set("background", isMine ? "linear-gradient(135deg, #4a76a8, #5a86b8)" : "#f1f3f5")
                .set("color", isMine ? "white" : "#2c3e50")
                .set("box-shadow", "0 2px 8px rgba(0,0,0,0.1)");

        messageContainer.add(bubble);
        messagesBox.add(messageContainer);
        messagesBox.getElement().executeJs("this.scrollTop = this.scrollHeight;");
    }

    private void sendMessage(String text) {
        try {
            Long currentUserId = getCurrentUserId();
            if (currentUserId == null) {
                log.error("Текущий пользователь не найден!");
                return;
            }

            var chat = chatService.getChatById(chatId);
            MessageEntity message = new MessageEntity();
            message.setUserId(currentUserId);
            message.setChat(chat);
            message.setContent(text);
            message.setCreatedAt(Instant.now());

            messageRepository.save(message);
            addMessageToView(message);
        } catch (Exception e) {
            log.error("Ошибка при отправке сообщения: {}", e.getMessage(), e);
        }
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        startScheduler(attachEvent.getUI());
    }

    private void startScheduler(UI ui) {
        stopScheduler();
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> {
            ui.access(() -> {
                try {
                    List<MessageEntity> updated = chatService.loadMessages(chatId);
                    if (lastLoadedMessages == null || updated.size() > lastLoadedMessages.size()) {
                        loadMessages();
                    }
                } catch (Exception e) {
                    log.error("Ошибка автообновления чата {}: {}", chatId, e.getMessage());
                }
            });
        }, 2, 2, TimeUnit.SECONDS);
    }

    private void stopScheduler() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdownNow();
            scheduler = null;
        }
    }
}
