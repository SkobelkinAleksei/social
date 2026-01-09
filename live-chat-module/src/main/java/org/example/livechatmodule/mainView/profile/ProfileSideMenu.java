package org.example.livechatmodule.mainView.profile;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.example.livechatmodule.client.PostClient;
import org.example.livechatmodule.client.SettingsClient;
import org.example.livechatmodule.client.UserClient;
import org.example.livechatmodule.mainView.post.PostModalDialog;

import java.util.List;

public class ProfileSideMenu extends VerticalLayout {

    public ProfileSideMenu(Long userId,
                           UserClient userClient,
                           SettingsClient settingsClient,
                           boolean isMyProfile,
                           PostClient postClient) {

        setWidth("220px");
        setPadding(true);
        setSpacing(false);
        setAlignItems(FlexComponent.Alignment.STRETCH);
        addClassName("profile-side-menu");

        H3 title = new H3(isMyProfile ? "Моё меню" : "Меню");
        title.addClassName("profile-title-h3");

        Button backBtn = new Button("На главную", e -> UI.getCurrent().navigate(""));
        Button profileBtn = new Button(isMyProfile ? "Моя страница" : "Главная",
                e -> UI.getCurrent().navigate("profile"));

        Button searchPeopleBtn = new Button("🔍 Поиск людей",
                e -> UI.getCurrent().navigate("search-people"));
        searchPeopleBtn.setWidthFull();

        searchPeopleBtn.addClassName("profile-menu-btn");
        // === КНОПКА «Друзья» как заголовок группы ===
        Button friendsToggleBtn = new Button("Друзья");
        friendsToggleBtn.addClassName("profile-menu-btn");
        friendsToggleBtn.setWidthFull();

        // === ВЛОЖЕННОЕ МЕНЮ ДЛЯ ДРУЗЕЙ ===
        VerticalLayout friendsSubMenu = new VerticalLayout();
        friendsSubMenu.setPadding(false);
        friendsSubMenu.setSpacing(false);
        friendsSubMenu.setMargin(false);
        friendsSubMenu.addClassName("friends-sub-menu");

        Button myFriendsBtn = new Button("Мои друзья", e -> {
            UI.getCurrent().navigate("friends/" + userId);
        });
        Button outgoingReqBtn = new Button("Отправленные заявки",
                e -> UI.getCurrent().navigate("friend-requests/outgoing"));
        Button incomingReqBtn = new Button("Заявки в друзья",
                e -> UI.getCurrent().navigate("friend-requests/incoming"));

        List<Button> friendButtons = List.of(myFriendsBtn, outgoingReqBtn, incomingReqBtn);
        friendButtons.forEach(b -> {
            b.setWidthFull();
            b.addClassName("profile-menu-btn");
            b.addClassName("profile-menu-btn-sub");
        });

        friendsSubMenu.add(myFriendsBtn, outgoingReqBtn, incomingReqBtn);
        friendsSubMenu.setVisible(false); // по умолчанию свернуто

        friendsToggleBtn.addClickListener(e ->
                friendsSubMenu.setVisible(!friendsSubMenu.isVisible())
        );

        Button newPostBtn = new Button("✍️ Новый пост", e -> {
            new PostModalDialog(postClient).open();
        });
        Button settingsBtn = new Button("⚙️ Изменить свои данные", e -> {
            new SettingsDialog(settingsClient, userClient, userId).open();
        });
        Button friendsPostsBtn = new Button("📱 Посты друзей",
                e -> UI.getCurrent().navigate("friends-posts/" + userId));
        friendsPostsBtn.setWidthFull();

        List<Button> commonButtons = List.of(backBtn, profileBtn, newPostBtn, settingsBtn);
        commonButtons.forEach(b -> {
            b.setWidthFull();
            b.addClassName("profile-menu-btn");
        });

        add(title, backBtn, profileBtn,
                friendsToggleBtn, friendsSubMenu, searchPeopleBtn, friendsPostsBtn,
                newPostBtn, settingsBtn);
    }
}