package org.example.livechatmodule.mainView.profile;

import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import lombok.extern.slf4j.Slf4j;
import org.example.common.dto.user.UserDto;
import org.example.livechatmodule.client.FriendClient;
import org.example.livechatmodule.client.FriendRequestClient;

@Slf4j
public class ProfileInfoCard extends VerticalLayout {

    private final FriendClient friendClient;
    private final FriendRequestClient friendRequestClient;
    private final Long currentUserId;
    private final Long viewedUserId;
    private Button friendButton;
    private String friendshipStatus = "NONE";

    public ProfileInfoCard(UserDto user, FriendClient friendClient, FriendRequestClient friendRequestClient, Long currentUserId) {
        this.friendClient = friendClient;
        this.friendRequestClient = friendRequestClient;
        this.currentUserId = currentUserId;
        this.viewedUserId = user.getUserId();

        setWidth("520px");
        setPadding(false);
        setSpacing(true);
        setAlignItems(FlexComponent.Alignment.STRETCH);
        addClassName("profile-card");

        add(buildHeader(user));
        if (!currentUserId.equals(viewedUserId)) {
            add(buildFriendActions());
            loadFriendshipStatus();
        }
        add(buildInfoSection(user));

    }

    private HorizontalLayout buildHeader(UserDto user) {
        Avatar avatar = new Avatar(user.getFirstName() + " " + user.getLastName());
        avatar.setColorIndex(3);
        avatar.setWidth("72px");
        avatar.setHeight("72px");

        H3 name = new H3(user.getFirstName() + " " + user.getLastName());
        name.addClassName("profile-title-h3-name");

        Paragraph email = new Paragraph(user.getEmail());
        email.addClassName("profile-secondary-text");

        VerticalLayout nameBlock = new VerticalLayout(name, email);
        nameBlock.setPadding(false);
        nameBlock.setSpacing(false);

        HorizontalLayout header = new HorizontalLayout(avatar, nameBlock);
        header.setWidthFull();
        header.setSpacing(true);
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        return header;
    }

    private HorizontalLayout buildFriendActions() {
        HorizontalLayout actions = new HorizontalLayout();
        actions.setWidthFull();
        actions.setSpacing(true);
        actions.setPadding(true);
        actions.getStyle().set("border-top", "1px solid #e0e0e0");
        actions.getStyle().set("padding-top", "16px");

        friendButton = createFriendButton();
        actions.add(friendButton);
        actions.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        return actions;
    }

    private Button createFriendButton() {
        Button button = new Button("Загрузка...");
        button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        button.setWidthFull();
        button.setMaxWidth("220px");
        return button;
    }

    private void loadFriendshipStatus() {
        try {
            String status = friendClient.getFriendshipStatusSync(currentUserId, viewedUserId);
            friendshipStatus = status != null ? status : "NONE";
            updateButton(friendshipStatus);
            log.info("[INFO] Статус дружбы: {}", friendshipStatus);
        } catch (Exception e) {
            friendshipStatus = "NONE";
            updateButton(friendshipStatus);
        }
    }

    private void updateButton(String status) {
        friendButton.removeThemeVariants();
        friendButton.setIcon(null);
        friendButton.setText("");

        switch (status) {
            case "FRIENDS" -> {
                friendButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
                friendButton.setText("Друг ✓");
                friendButton.setEnabled(false);
            }
            case "PENDING_OUT", "PENDING_IN" -> {
                friendButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
                friendButton.setText("Заявка отправлена ⏰");
                friendButton.setEnabled(false);
            }
            case "REJECTED" -> {
                friendButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
                friendButton.setText("Заявка отклонена ❌");
                friendButton.setEnabled(true);
                friendButton.addClickListener(e -> sendFriendRequest());
            }
            default -> {
                friendButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
                friendButton.setText("Добавить в друзья ➕");
                friendButton.setEnabled(true);
                friendButton.addClickListener(e -> sendFriendRequest());
            }
        }
    }


    private void sendFriendRequest() {
        if (friendButton == null) return;

        try {
            friendButton.setText("Отправка...");
            friendButton.setEnabled(false);

            friendRequestClient.addFriend(viewedUserId);
            friendshipStatus = "PENDING_OUT";
            updateButton(friendshipStatus);
            log.info("[INFO] Заявка отправлена!");
        } catch (Exception e) {
            friendButton.setText("Ошибка");
            friendButton.setEnabled(true);
            updateButton("NONE");
            log.error("[ERROR] Ошибка заявки: {}", e.getMessage());
        }
    }

    private VerticalLayout buildInfoSection(UserDto user) {
        VerticalLayout info = new VerticalLayout();
        info.setPadding(false);
        info.setSpacing(false);
        info.getStyle().set("margin-top", "12px");

        info.add(
                line("Телефон", user.getNumberPhone() != null ? user.getNumberPhone() : "-"),
                line("Дата рождения", user.getBirthday() != null ? user.getBirthday().toString() : "-"),
                line("Email", user.getEmail() != null ? user.getEmail() : "-")
        );
        return info;
    }

    private HorizontalLayout line(String label, String value) {
        Paragraph l = new Paragraph(label + ":");
        l.addClassName("profile-label");

        Paragraph v = new Paragraph(value);
        v.addClassName("profile-value");

        HorizontalLayout row = new HorizontalLayout(l, v);
        row.setWidthFull();
        row.setSpacing(true);
        row.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        return row;
    }
}