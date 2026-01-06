package org.example.livechatmodule.mainView.comment;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.common.dto.comment.CommentDto;
import org.example.common.dto.comment.NewCommentDto;
import org.example.livechatmodule.client.CommentClient;

@Slf4j
@RequiredArgsConstructor
public class EditComment extends Dialog {

    private final CommentClient commentClient;
    private final Long commentId;
    private final Long postId;
    private final Runnable onSuccess;
    private final String originalContent;

    public EditComment(CommentClient commentClient, Long commentId, Long postId, Runnable onSuccess) {
        this.commentClient = commentClient;
        this.commentId = commentId;
        this.postId = postId;
        this.onSuccess = onSuccess;
        this.originalContent = ""; // Загрузим позже
        init();
    }

    private void init() {
        setHeaderTitle("✏️ Редактировать комментарий");
        setWidth("500px");
        setMaxHeight("90vh");
        addClassName("edit-comment-dialog");

        // Форма редактирования
        TextArea textArea = new TextArea();
        textArea.setValue(originalContent);
        textArea.setPlaceholder("Введите новый текст...");
        textArea.setMaxHeight("200px");
        textArea.addClassName("edit-comment-textarea");
        textArea.focus();

        // Кнопки
        Button saveBtn = new Button("💾 Сохранить");
        saveBtn.addClassName("edit-comment-save-btn");
        saveBtn.addClickListener(e -> saveComment(textArea.getValue().trim(), textArea));

        Button cancelBtn = new Button("❌ Отмена");
        cancelBtn.addClassName("edit-comment-cancel-btn");
        cancelBtn.addClickListener(e -> close());

        HorizontalLayout buttons = new HorizontalLayout(saveBtn, cancelBtn);
        buttons.setSpacing(true);
        buttons.setJustifyContentMode(FlexComponent.JustifyContentMode.END);

        VerticalLayout content = new VerticalLayout(textArea, buttons);
        content.setSpacing(true);
        content.setPadding(true);
        content.setAlignItems(FlexComponent.Alignment.STRETCH);

        add(content);
    }

    private void saveComment(String content, TextArea textArea) {
        if (content.trim().isEmpty()) {
            textArea.setInvalid(true);
            textArea.setErrorMessage("⚠️ Текст не может быть пустым");
            return;
        }

        log.info("💾 Сохраняем комментарий {}", commentId);
        try {
            NewCommentDto updateDto = new NewCommentDto();
            updateDto.setContent(content);

            CommentClient commentClient = this.commentClient; // Для PUT private API
            CommentDto updated = commentClient.updateComment(commentId, updateDto); // Новый метод

            log.info("✅ Комментарий обновлен: {}", updated.getId());
            onSuccess.run(); // reloadComments()
            close();
            // Notification.show("✅ Комментарий обновлен!", 2000, Notification.Position.TOP_CENTER);
        } catch (Exception e) {
            log.error("❌ Ошибка обновления: {}", e.getMessage(), e);
            textArea.setInvalid(true);
            textArea.setErrorMessage("❌ Ошибка сохранения");
        }
    }
}
