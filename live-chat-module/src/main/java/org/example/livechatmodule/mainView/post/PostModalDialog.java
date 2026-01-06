package org.example.livechatmodule.mainView.post;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import lombok.extern.slf4j.Slf4j;
import org.example.common.dto.post.NewPostDto;
import org.example.livechatmodule.client.PostClient;

@Slf4j
@CssImport("./styles/post-modal.css")
public class PostModalDialog extends Dialog {

    private final PostClient postClient;
    private Button submitBtn;

    public PostModalDialog(PostClient postClient) {
        this.postClient = postClient;

        // ✅ Авто-размер без скролла
        setWidth("500px");
        setMaxHeight("90vh");
        setResizable(true);
        addClassName("post-modal");

        // ✅ Кастомный header без setHeaderTitle
        H3 header = new H3("✍️ Создать пост");
        header.addClassNames("post-header");

        // ✅ TextArea авто-высота
        TextArea content = new TextArea();
        content.setWidthFull();
        content.setMinHeight("150px");
        content.setMaxHeight("300px");
        content.setPlaceholder("Расскажите о своих мыслях... (5-100 символов)");
        content.addClassNames("post-content");
        content.setRequired(true);
        content.setRequiredIndicatorVisible(true);

        // ✅ Buttons
        Button cancelBtn = new Button("❌ Отмена", e -> close());
        submitBtn = new Button("📤 Опубликовать", e -> submitPost(content));
        submitBtn.addClassNames("primary");
        cancelBtn.addClassNames("secondary");

        HorizontalLayout buttons = new HorizontalLayout(submitBtn, cancelBtn);
        buttons.setWidthFull();
        buttons.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        buttons.setPadding(true);

        // ✅ Layout без скролла
        VerticalLayout form = new VerticalLayout(header, content, buttons);
        form.setSpacing(true);
        form.setPadding(false);
        form.setAlignItems(FlexComponent.Alignment.STRETCH);
        form.getElement().getStyle()
                .set("overflow", "visible")
                .set("height", "auto")
                .set("max-height", "70vh");

        add(form);
    }

    private void submitPost(TextArea content) {
        String text = content.getValue().trim();
        log.info("Submit пост, длина: {}", text.length());

        if (text.length() < 5 || text.length() > 100) {
            Notification.show("Текст: 5-100 символов!", 3000, Notification.Position.MIDDLE);
            return;
        }

        NewPostDto dto = new NewPostDto(text);

        submitBtn.setText("⏳ Отправка...");
        submitBtn.setEnabled(false);

        postClient.submitPost(dto)
                .thenAccept(postId -> {
                    log.info("✅ Пост создан ID: {}", postId);

                    getUI().ifPresent(ui -> ui.access(() -> {
                        Notification.show("✅ Пост #" + postId + " отправлен на модерацию!",
                                3000, Notification.Position.TOP_CENTER);
                        content.clear();
                        close();
                    }));
                })
                .exceptionally(t -> {
                    log.error("❌ Ошибка поста: ", t);
                    getUI().ifPresent(ui -> ui.access(() -> {
                        String msg = t.getMessage() != null ? t.getMessage() : "Неизвестная ошибка";
                        Notification.show("❌ " + msg, 5000, Notification.Position.MIDDLE);
                    }));
                    return null;
                })
                .whenComplete((result, throwable) -> {
                    submitBtn.setText("📤 Опубликовать");
                    submitBtn.setEnabled(true);
                });
    }
}
