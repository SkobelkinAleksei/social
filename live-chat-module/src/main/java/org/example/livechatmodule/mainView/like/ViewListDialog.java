package org.example.livechatmodule.mainView.like;  // Тот же пакет!

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import lombok.extern.slf4j.Slf4j;
import org.example.common.dto.user.UserDto;
import org.example.livechatmodule.client.UserClient;
import org.example.livechatmodule.utils.CustomDateTimeFormatter;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
public class ViewListDialog extends Dialog {

    public ViewListDialog(List<Long> viewUserIds, UserClient userClient) {
        log.info("[INFO] Конструктор ViewListDialog: {} просмотров", viewUserIds.size());

        if (viewUserIds.isEmpty()) {
            H3 title = new H3("👁 Нет просмотров");
            Button closeBtn = new Button("Закрыть", e -> close());
            VerticalLayout content = new VerticalLayout(title, closeBtn);
            content.setPadding(true);
            add(content);
            return;
        }

        H3 title = new H3(viewUserIds.size() + " просмотров");
        title.getStyle().set("margin", "0 0 20px 0");

        Grid<ViewRow> grid = new Grid<>(ViewRow.class, false);
        grid.addClassName("view-grid");
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);

        grid.addColumn(ViewRow::getUserName).setHeader("👤 Кто смотрел").setFlexGrow(2);
        grid.addColumn(ViewRow::getViewedAt).setHeader("📅 Когда").setFlexGrow(1)
                .setTextAlign(ColumnTextAlign.CENTER);

        grid.setItems(List.of(new ViewRow("⏳ Загрузка...", "")));

        loadViewsData(viewUserIds, userClient, grid);

        Button closeBtn = new Button("Закрыть", e -> close());

        VerticalLayout content = new VerticalLayout(title, grid, closeBtn);
        content.setPadding(true);
        content.setSpacing(true);
        content.setSizeFull();
        content.addClassNames("view-dialog-content");

        add(content);
        setWidth("480px");
        setHeight("380px");
        setResizable(true);
        setDraggable(true);
        open();
    }

    private void loadViewsData(List<Long> viewUserIds, UserClient userClient, Grid<ViewRow> grid) {
        CompletableFuture.supplyAsync(() ->
                viewUserIds.stream()
                        .map(userId -> {
                            try {
                                UserDto user = userClient.getUserById(userId);
                                String name = user != null
                                        ? user.getFirstName() + " " + (user.getLastName() != null ? user.getLastName() : "")
                                        : "#" + userId;
                                // FIXME: Нет точной даты просмотра — используем createdAt поста или now
                                return new ViewRow(name, CustomDateTimeFormatter.formatCommentDate(java.time.LocalDateTime.now()));
                            } catch (Exception e) {
                                log.warn("[WARN] Ошибка загрузки userId {}: {}", userId, e.getMessage());
                                return new ViewRow("#" + userId, "недавно");
                            }
                        })
                        .collect(Collectors.toList())
        ).thenAccept(rows -> getUI().ifPresent(ui -> ui.access(() -> {
            grid.setItems(rows);
            log.info("[INFO] ViewGrid заполнен: {} строк", rows.size());
        })));
    }

    public static class ViewRow {
        private final String userName;
        private final String viewedAt;

        public ViewRow(String userName, String viewedAt) {
            this.userName = userName;
            this.viewedAt = viewedAt;
        }

        public String getUserName() { return userName; }
        public String getViewedAt() { return viewedAt; }
    }
}
