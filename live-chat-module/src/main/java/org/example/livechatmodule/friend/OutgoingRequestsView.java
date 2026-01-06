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
import org.example.common.dto.user.UserDto;
import org.example.livechatmodule.client.FriendRequestClient;
import org.example.livechatmodule.client.UserClient;
import org.example.livechatmodule.mainView.MainLayout;

import java.util.List;

@Route(value = "friend-requests/outgoing", layout = MainLayout.class)
@RequiredArgsConstructor
@CssImport("./styles/friends-view-styles.css")
public class OutgoingRequestsView extends VerticalLayout implements BeforeEnterObserver {

    private final FriendRequestClient friendRequestClient;
    private final UserClient userClient;

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        // ✅ ОЧИСТИТЬ предыдущий контент!
        removeAll();

        setSizeFull();
        setPadding(true);
        setSpacing(true);
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);

        List<FriendRequestDto> requests = friendRequestClient.getOutgoing(null, 0, 50);

        H3 title = new H3("Отправленные заявки (" + requests.size() + ")");
        title.getStyle().set("color", "#2c3e50");

        if (requests.isEmpty()) {
            Paragraph empty = new Paragraph("У вас пока нет отправленных заявок 😔");
            empty.getStyle().set("color", "#6b7b8a").set("font-size", "16px");
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
            UserDto user = userClient.getUserById(targetId);
            grid.add(buildUserCard(user, targetId));
        }

        add(title, grid);
    }

    private Component buildUserCard(UserDto user, Long userId) {
        Div cardContainer = new Div();
        cardContainer.addClassName("friend-card-container");

        String fullName = user != null && user.getFirstName() != null
                ? user.getFirstName() + (user.getLastName() != null ? " " + user.getLastName() : "")
                : "Пользователь #" + userId;

        String email = user != null && user.getEmail() != null ? user.getEmail() : "—";

        VerticalLayout cardContent = new VerticalLayout();
        cardContent.setPadding(true);
        cardContent.setSpacing(false);
        cardContent.setAlignItems(FlexComponent.Alignment.CENTER);

        Avatar avatar = new Avatar(fullName);
        avatar.setColorIndex((int) (userId % 10));
        avatar.setWidth("60px");
        avatar.setHeight("60px");

        H3 name = new H3(fullName);
        name.getStyle().set("margin", "8px 0 4px 0").set("font-size", "17px");

        Paragraph emailText = new Paragraph(email);
        emailText.getStyle().set("color", "#6b7b8a").set("font-size", "14px").set("margin", "0 0 8px 0");

        Button cancelBtn = new Button("❌ Отменить", e -> {
            friendRequestClient.cancelRequest(userId)  // ← addresseeId
                    .thenRun(() -> {
                        Notification.show("✅ Заявка отменена", 2000, Notification.Position.TOP_CENTER);
                        cardContainer.getElement().removeFromParent();
                    })
                    .exceptionally(t -> {
                        Notification.show("❌ Ошибка: " + t.getMessage(), 4000, Notification.Position.MIDDLE);
                        return null;
                    });
        });
        cancelBtn.addClassNames("vk-button", "cancel-btn");

        HorizontalLayout buttonLayout = new HorizontalLayout(cancelBtn);
        buttonLayout.setJustifyContentMode(JustifyContentMode.CENTER);

        cardContent.add(avatar, name, emailText, buttonLayout);

        cardContainer.add(cardContent);

        return cardContainer;
    }
}
