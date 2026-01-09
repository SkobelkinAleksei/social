package org.example.livechatmodule.mainView.post;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import lombok.extern.slf4j.Slf4j;
import org.example.common.dto.post.UpdatePostDto;
import org.example.livechatmodule.client.PostClient;

import java.util.function.Consumer;

@Slf4j
@CssImport("./styles/edit-post-dialog.css")
public class EditPostDialog extends Dialog {
    private final PostClient postClient;
    private final Long postId;
    private final String originalContent;
    private final Consumer<String> onSuccess; // Callback для обновления

    private TextArea contentField;

    public EditPostDialog(PostClient postClient, Long postId, String originalContent, Consumer<String> onSuccess) {
        this.postClient = postClient;
        this.postId = postId;
        this.originalContent = originalContent;
        this.onSuccess = onSuccess;

        setHeaderTitle("⚙️ Редактировать пост");
        setWidth("500px");
        addClassName("edit-post-dialog");

        contentField = new TextArea("Текст поста", originalContent);
        contentField.setWidthFull();
        contentField.setMinHeight("150px");

        Button saveBtn = new Button("💾 Сохранить", e -> savePost());
        Button cancelBtn = new Button("❌ Отмена", e -> close());

        HorizontalLayout buttons = new HorizontalLayout(saveBtn, cancelBtn);
        buttons.setJustifyContentMode(FlexComponent.JustifyContentMode.END);

        add(contentField, buttons);
    }

    private void savePost() {
        String newContent = contentField.getValue().trim();
        if (newContent.length() < 5 || newContent.length() > 100) {
            Notification.show("5-100 символов!", 3000, Notification.Position.MIDDLE);
            return;
        }

        UpdatePostDto dto = new UpdatePostDto();
        dto.setContent(newContent);

        close();
        onSuccess.accept(newContent);

        Notification.show("✅ Пост отправлен на модерацию...", 1500, Notification.Position.TOP_CENTER);

        postClient.updatePost(postId, dto)
                .thenRun(() -> {
                    log.info("[INFO] UpdatePost завершён в фоне");
                })
                .exceptionally(t -> {
                    log.error("[ERROR] UpdatePost ошибка (игнорируем): {}", t.getMessage());
                    return null;
                });

        UI.getCurrent().getPage().executeJs("setTimeout(() => { window.location.href = '/profile'; }, 500);");
    }
}
