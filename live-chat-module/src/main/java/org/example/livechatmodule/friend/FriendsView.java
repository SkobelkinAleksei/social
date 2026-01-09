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
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.common.dto.friend.FriendDto;
import org.example.common.dto.user.UserDto;
import org.example.livechatmodule.client.FriendClient;
import org.example.livechatmodule.client.UserClient;
import org.example.livechatmodule.entity.ChatEntity;
import org.example.livechatmodule.mainView.MainLayout;
import org.example.livechatmodule.mainView.profile.ConfirmDialog;
import org.example.livechatmodule.service.ChatService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@CssImport("./styles/friends-view-styles.css")
@Route(value = "friends/:userId", layout = MainLayout.class)
public class FriendsView extends VerticalLayout implements BeforeEnterObserver {

    private final FriendClient friendClient;
    private final UserClient userClient;
    private final ChatService chatService;

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        removeAll();
        try {
            String userIdParam = event.getRouteParameters().get("userId")
                    .orElseThrow(() -> new EntityNotFoundException("userId required"));

            Long currentUserId = Long.valueOf(userIdParam);
            List<FriendDto> friends = friendClient.getFriends(currentUserId);

            add(buildFriendsContent(friends, currentUserId));
            log.info("[INFO] FriendsView: загружено {} друзей для userId={}", friends.size(), currentUserId);
        } catch (Exception e) {
            log.error("[ERROR] FriendsView ошибка: {}", e.getMessage(), e);
            add(new H3("Ошибка загрузки друзей"));
        }
    }

    private Component buildFriendsContent(List<FriendDto> friends, Long currentUserId) {
        VerticalLayout content = new VerticalLayout();
        content.setSizeFull();
        content.setPadding(true);
        content.setSpacing(true);
        content.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.CENTER);

        H3 title = new H3("Мои друзья (" + friends.size() + ")");
        title.getStyle().set("color", "#2c3e50");

        if (friends.isEmpty()) {
            Paragraph empty = new Paragraph("У вас пока нет друзей 😔");
            empty.getStyle().set("color", "#6b7b8a").set("font-size", "16px");
            content.add(title, empty);
            return content;
        }

        Div friendsGrid = new Div();
        friendsGrid.getStyle()
                .set("display", "grid")
                .set("grid-template-columns", "repeat(auto-fill, minmax(280px, 1fr))")
                .set("gap", "16px")
                .set("width", "100%")
                .set("max-width", "900px");

        friends.forEach(friend -> {
            // Определяем ID друга относительно текущего пользователя
            Long friendId = friend.getUserId1().equals(currentUserId)
                    ? friend.getUserId2()
                    : friend.getUserId1();

            UserDto friendUser = userClient.getUserById(friendId);
            friendsGrid.add(friendCard(friendUser, currentUserId, friendId));
        });

        content.add(title, friendsGrid);
        return content;
    }

    private Component friendCard(UserDto friendUser, Long currentUserId, Long friendId) {
        Div cardContainer = new Div();
        cardContainer.addClassName("friend-card-container");

        String fullName = friendUser != null && friendUser.getFirstName() != null
                ? friendUser.getFirstName() + (friendUser.getLastName() != null ? " " + friendUser.getLastName() : "")
                : "Друг #" + friendId;

        String email = friendUser != null && friendUser.getEmail() != null ? friendUser.getEmail() : "—";

        // Контент карточки
        VerticalLayout cardContent = new VerticalLayout();
        cardContent.setPadding(true);
        cardContent.setSpacing(false);
        cardContent.setAlignItems(FlexComponent.Alignment.CENTER);

        Avatar avatar = new Avatar(fullName);
        avatar.setColorIndex((int) (friendId % 10));
        avatar.setWidth("60px");
        avatar.setHeight("60px");

        H3 name = new H3(fullName);
        name.getStyle().set("margin", "8px 0 4px 0").set("font-size", "17px");

        Paragraph emailText = new Paragraph(email);
        emailText.getStyle().set("color", "#6b7b8a").set("font-size", "14px").set("margin", "0 0 8px 0");

        Button startChatBtn = new Button("💬 Начать чат", e -> {
            try {
                ChatEntity chat = chatService.getOrCreatedPrivateChat(currentUserId, friendId);
                if (chat.getId() != null) {
                    UI.getCurrent().navigate("chat/" + chat.getId());
                } else {
                    Notification.show("Ошибка: не удалось создать чат");
                }
            } catch (Exception ex) {
                log.error("Ошибка при открытии чата с userId={}: {}", friendId, ex.getMessage());
                Notification.show("Ошибка открытия чата");
            }
        });
        startChatBtn.addClassNames("vk-button", "chat-btn");

        HorizontalLayout buttonLayout = new HorizontalLayout(startChatBtn);
        buttonLayout.setJustifyContentMode(JustifyContentMode.CENTER);

        cardContent.add(avatar, name, emailText, buttonLayout);

        // Крестик
        Button deleteBtn = new Button("✕");
        deleteBtn.addClassNames("delete-friend-btn");
        deleteBtn.getElement().executeJs(
                "this.addEventListener('click', function(event) { event.stopPropagation(); })"
        );
        deleteBtn.addClickListener(e -> deleteFriend(currentUserId, friendId, cardContainer));

        // Клик по карточке ИГНОРИРУЕТ крестик
        cardContainer.addClickListener(e -> {
            UI.getCurrent().navigate("profile/" + friendId);
        });

        cardContainer.add(cardContent, deleteBtn);
        return cardContainer;
    }


    private void deleteFriend(Long currentUserId, Long friendId, Component card) {
        new ConfirmDialog("Удалить друга?",
                "Друг будет удалён из списка навсегда.",
                () -> {
                    friendClient.deleteFriend(currentUserId, friendId)
                            .thenAccept(v -> {
                                Notification.show("✅ Друг удалён!", 2000, Notification.Position.TOP_CENTER);
                                card.getElement().removeFromParent();
                            })
                            .exceptionally(t -> {
                                Notification.show("❌ " + t.getMessage(), 4000, Notification.Position.MIDDLE);
                                return null;
                            });
                }).open();
    }
}
