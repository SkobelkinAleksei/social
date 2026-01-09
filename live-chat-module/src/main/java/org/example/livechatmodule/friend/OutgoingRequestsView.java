package org.example.livechatmodule.friend;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import lombok.RequiredArgsConstructor;
import org.example.common.dto.friend.FriendRequestDto;
import org.example.common.dto.friend.FriendRequestStatus;
import org.example.common.dto.user.UserDto;
import org.example.livechatmodule.client.FriendRequestClient;
import org.example.livechatmodule.client.UserClient;
import org.example.livechatmodule.mainView.MainLayout;

import java.util.List;

@Route(value = "friend-requests/outgoing", layout = MainLayout.class)
@RequiredArgsConstructor
@CssImport("./styles/friends-view-styles.css")
public class OutgoingRequestsView extends VerticalLayout implements BeforeEnterObserver {

    private FriendRequestStatus currentFilter = null;
    private final FriendRequestClient friendRequestClient;
    private final UserClient userClient;

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        loadRequests(currentFilter);
    }

    private void loadRequests(FriendRequestStatus filter) {
        this.currentFilter = filter;

        removeAll();
        setSizeFull();
        setPadding(true);
        setSpacing(true);
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);

        HorizontalLayout filterLayout = createFilterButtons(filter);
        add(filterLayout);

        List<FriendRequestDto> allRequests = friendRequestClient.getOutgoing(0, 50);

        List<FriendRequestDto> requests = filterRequests(allRequests, filter);

        H3 title = new H3("Отправленные заявки (" + requests.size() + ")");
        title.getStyle().set("color", "#2c3e50");

        if (requests.isEmpty()) {
            Paragraph empty = getEmptyText(filter);
            add(title, empty);
            return;
        }

        Div grid = new Div();
        grid.getStyle()
                .set("display", "grid")
                .set("grid-template-columns", "repeat(auto-fill, minmax(280px, 1fr))")
                .set("gap", "16px")
                .set("width", "100%")
                .set("max-width", "900px");

        for (FriendRequestDto req : requests) {
            Long targetId = req.getAddresseeId();
            Long requestId = req.getId();
            UserDto user = userClient.getUserById(targetId);
            grid.add(buildUserCard(user, targetId, requestId, req.getStatus()));
        }

        add(title, grid);
    }

    private List<FriendRequestDto> filterRequests(List<FriendRequestDto> requests, FriendRequestStatus filter) {
        if (filter == null) return requests;

        return requests.stream()
                .filter(req -> req.getStatus() == filter)
                .toList();
    }

    private HorizontalLayout createFilterButtons(FriendRequestStatus activeFilter) {
        HorizontalLayout filters = new HorizontalLayout();
        filters.setSpacing(true);
        filters.addClassName("filter-buttons");

        // Все (PENDING + REJECTED)
        Button allBtn = new Button("Все", e -> loadRequests(null));
        allBtn.addClassNames(activeFilter == null ? "vk-button active" : "vk-button");

        // Только PENDING
        Button pendingBtn = new Button("⏳ Ожидание", e -> loadRequests(FriendRequestStatus.PENDING));
        pendingBtn.addClassNames(activeFilter == FriendRequestStatus.PENDING ? "vk-button active" : "vk-button");

        // Только REJECTED
        Button rejectedBtn = new Button("❌ Отклонено", e -> loadRequests(FriendRequestStatus.REJECTED));
        rejectedBtn.addClassNames(activeFilter == FriendRequestStatus.REJECTED ? "vk-button active" : "vk-button");

        filters.add(allBtn, pendingBtn, rejectedBtn);
        return filters;
    }

    private Paragraph getEmptyText(FriendRequestStatus filter) {
        String text = switch (filter) {
            case null -> "Отправленных заявок пока нет 😔";
            case PENDING -> "Заявок на ожидании нет 😌";
            case REJECTED -> "Отклонённых заявок нет 🙂";
            default -> "Заявок нет 😔";
        };

        Paragraph empty = new Paragraph(text);
        empty.getStyle().set("color", "#6b7b8a").set("font-size", "16px");
        return empty;
    }

    private Component buildUserCard(UserDto user, Long userId, Long requestId, FriendRequestStatus status) {
        UI currentUI = UI.getCurrent();
        Div cardContainer = new Div();
        cardContainer.addClassName("friend-card-container");

        String fullName = user != null && user.getFirstName() != null
                ? user.getFirstName() + (user.getLastName() != null ? " " + user.getLastName() : "")
                : "Пользователь #" + userId;

        String email = user != null && user.getEmail() != null ? user.getEmail() : "—";

        VerticalLayout cardContent = new VerticalLayout();
        cardContent.setPadding(true);
        cardContent.setSpacing(false);
        cardContent.setAlignItems(Alignment.CENTER);

        Avatar avatar = new Avatar(fullName);
        avatar.setColorIndex((int) (userId % 10));
        avatar.setWidth("60px");
        avatar.setHeight("60px");

        H3 name = new H3(fullName);
        name.getStyle().set("margin", "8px 0 4px 0").set("font-size", "17px");

        Paragraph emailText = new Paragraph(email);
        emailText.getStyle().set("color", "#6b7b8a").set("font-size", "14px").set("margin", "0 0 8px 0");

        // Показываем статус заявки
        Paragraph statusText = new Paragraph(status != null ? status.name() : "UNKNOWN");
        statusText.getStyle().set("color", status == FriendRequestStatus.PENDING ? "#f39c12" : "#e74c3c")
                .set("font-weight", "bold").set("font-size", "12px");

        Button cancelBtn = new Button("❌ Отменить");
        cancelBtn.addClassNames("vk-button", "cancel-btn");
        cancelBtn.getElement().addEventListener("click", e -> {
            friendRequestClient.cancelRequest(requestId, userId)
                    .thenRun(() -> {
                        currentUI.access(() -> {
                            Notification.show("✅ Заявка отменена", 2000, Notification.Position.TOP_CENTER);
                            loadRequests(currentFilter);
                        });
                    })
                    .exceptionally(t -> {
                        Notification.show("❌ Ошибка: " + t.getMessage(), 4000, Notification.Position.MIDDLE);
                        return null;
                    });
        });

        HorizontalLayout buttonLayout = new HorizontalLayout(cancelBtn);
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

        cardContent.add(avatar, name, emailText, statusText, buttonLayout);
        cardContainer.add(cardContent);

        return cardContainer;
    }
}
