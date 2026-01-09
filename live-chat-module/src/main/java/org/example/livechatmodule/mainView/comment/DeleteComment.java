package org.example.livechatmodule.mainView.comment;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import lombok.extern.slf4j.Slf4j;
import org.example.livechatmodule.client.CommentClient;

import java.util.function.Consumer;

@Slf4j
public class DeleteComment extends Dialog {

    private final CommentClient commentClient;
    private final Long commentId;
    private final Long postId;
    private final Consumer<Void> onSuccessCallback; // колбэк для перезагрузки списка

    public DeleteComment(CommentClient commentClient, Long commentId, Long postId, Consumer<Void> onSuccessCallback) {
        super();
        this.commentClient = commentClient;
        this.commentId = commentId;
        this.postId = postId;
        this.onSuccessCallback = onSuccessCallback;

        initDialog();
    }

    private void initDialog() {
        setHeaderTitle("Подтверждение удаления");
        setWidth("400px");
        setHeight("200px");

        // Контент диалога
        Paragraph message = new Paragraph("Удалить комментарий навсегда?");
        message.getStyle().set("text-align", "center").set("color", "#6b7b8a");

        // Кнопки
        HorizontalLayout buttonsLayout = new HorizontalLayout();
        buttonsLayout.setWidthFull();
        buttonsLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        buttonsLayout.setSpacing(true);

        Button yesBtn = new Button("🗑 Да, удалить", e -> confirmDelete());
        Button noBtn = new Button("❌ Отмена", e -> close());

        // Стилизация кнопок
        yesBtn.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_PRIMARY);
        noBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        buttonsLayout.add(yesBtn, noBtn);

        // Сборка диалога
        VerticalLayout content = new VerticalLayout(message, buttonsLayout);
        content.setPadding(true);
        content.setSpacing(true);
        content.setAlignItems(FlexComponent.Alignment.CENTER);

        add(content);

        // Фокус на "Отмена" по умолчанию (безопаснее)
        noBtn.focus();
    }

    private void confirmDelete() {
        log.info("[INFO] ПОДТВЕРЖДЕНИЕ удаления commentId={}", commentId);

        try {
            commentClient.deleteComment(commentId);
            log.info("[INFO] API delete успешен для commentId={}", commentId);

            Notification.show("✅ Комментарий удалён!", 2000, Notification.Position.TOP_CENTER);
            close();

            // Вызываем колбэк для перезагрузки списка комментариев
            if (onSuccessCallback != null) {
                onSuccessCallback.accept(null);
            }

        } catch (Exception ex) {
            log.error("❌ Ошибка API delete: {}", ex.getMessage(), ex);
            Notification.show("❌ Ошибка: " + ex.getMessage(), 3000, Notification.Position.TOP_CENTER);
        }
    }
}
