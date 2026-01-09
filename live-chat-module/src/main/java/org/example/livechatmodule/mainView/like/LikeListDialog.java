package org.example.livechatmodule.mainView.like;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import lombok.extern.slf4j.Slf4j;
import org.example.common.dto.post.LikePostDto;
import org.example.common.dto.user.UserDto;
import org.example.livechatmodule.client.UserClient;
import org.example.livechatmodule.utils.CustomDateTimeFormatter;

import java.util.List;
import java.util.concurrent.CompletableFuture;
@Slf4j
public class LikeListDialog extends Dialog {

    private Grid<LikeRow> grid;  // ✅ Делаем поле

    public LikeListDialog(List<LikePostDto> likes, UserClient userClient) {
        log.info("[INFO] Конструктор LikeListDialog: {} лайков", likes.size());

        H3 title = new H3(likes.size() + " лайкнули");
        title.getStyle().set("margin", "0 0 20px 0");

        grid = new Grid<>(LikeRow.class, false);
        grid.addClassName("like-grid");
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);

        grid.addColumn(LikeRow::getUserName).setHeader("👤 Имя").setFlexGrow(2);
        grid.addColumn(LikeRow::getLikedAt).setHeader("📅 Когда").setFlexGrow(1)
                .setTextAlign(ColumnTextAlign.CENTER);

        grid.setItems(List.of(new LikeRow("⏳ Загрузка пользователей...", "")));

        loadLikesData(likes, userClient, grid);

        Button closeBtn = new Button("Закрыть", e -> close());

        VerticalLayout content = new VerticalLayout(title, grid, closeBtn);
        content.setPadding(true);
        content.setSpacing(true);
        content.setSizeFull();
        content.addClassNames("like-dialog-content");

        add(content);

        setWidth("480px");
        setHeight("380px");
        setResizable(true);
        setDraggable(true);
    }

    private void loadLikesData(List<LikePostDto> likes, UserClient userClient, Grid<LikeRow> grid) {
        CompletableFuture.supplyAsync(() ->
                        likes.stream()
                                .map(like -> {
                                    try {
                                        UserDto user = userClient.getUserById(like.getAuthorId());
                                        String name = user != null ? user.getFirstName() + " " +
                                                (user.getLastName() != null ? user.getLastName() : "") : "Unknown";
                                        return new LikeRow(name, CustomDateTimeFormatter.formatCommentDate(like.getCreatedAt()));
                                    } catch (Exception e) {
                                        log.warn("[WARN] Ошибка загрузки пользователя {}: {}", like.getAuthorId(), e.getMessage());
                                        return new LikeRow("Unknown", CustomDateTimeFormatter.formatCommentDate(like.getCreatedAt()));
                                    }
                                })
                                .toList()
                )
                .thenAccept(rows -> {
                    getUI().ifPresent(ui ->
                            ui.access(() -> {
                                grid.setItems(rows);
                                log.info("[INFO] Grid заполнен: {} строк", rows.size());
                            })
                    );
                });
    }

    public static class LikeRow {
        private final String userName;
        private final String likedAt;

        public LikeRow(String userName, String likedAt) {
            this.userName = userName;
            this.likedAt = likedAt;
        }

        public String getUserName() { return userName; }
        public String getLikedAt() { return likedAt; }
    }
}
