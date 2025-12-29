package org.example.livechatmodule.mainView;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import org.example.common.dto.PostDto;
import org.example.common.dto.RequestData;
import org.example.common.dto.UserFullDto;
import org.example.httpcore.httpCore.SecuredHttpCore;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import org.example.livechatmodule.client.PostClient;
import org.springframework.http.ResponseEntity;

import java.util.Comparator;
import java.util.List;

@Route("profile/:id")
public class ProfileView extends HorizontalLayout implements BeforeEnterObserver {

    private final SecuredHttpCore http;
    private final PostClient postClient;

    private Long userId;

    public ProfileView(SecuredHttpCore http, PostClient postClient) {
        this.http = http;
        this.postClient = postClient;

        setSizeFull();
        getStyle().set("background-color", "#e5ebf1");
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        String idParam = event.getRouteParameters().get("id").orElse(null);
        if (idParam == null) {
            removeAll();
            add(new Paragraph("Не указан id пользователя"));
            return;
        }

        userId = Long.valueOf(idParam);

        // профиль
        RequestData request = new RequestData(
                "http://localhost:8080/api/v1/social/users/%d".formatted(userId),
                null
        );
        ResponseEntity<UserFullDto> resp = http.get(request, UserFullDto.class);
        UserFullDto user = resp.getBody();

        // посты
        List<PostDto> posts = postClient.getUserPosts(userId);

        removeAll();

        if (user == null) {
            add(new Paragraph("Пользователь не найден"));
            return;
        }

        Component sideMenu = buildSideMenu();
        Component content = buildContent(user, posts);

        add(sideMenu, content);
        setFlexGrow(0, sideMenu);
        setFlexGrow(1, content);
    }

    /* ---------- Левое меню ---------- */

    private VerticalLayout buildSideMenu() {
        VerticalLayout menu = new VerticalLayout();
        menu.setWidth("220px");
        menu.setPadding(true);
        menu.setSpacing(false);
        menu.setAlignItems(FlexComponent.Alignment.STRETCH);
        menu.getStyle()
                .set("background-color", "#ffffff")
                .set("box-shadow", "2px 0 4px rgba(0,0,0,0.05)");

        H3 title = new H3("Меню");
        title.getStyle()
                .set("margin", "0 0 12px 0")
                .set("color", "#2c3e50");

        Button profileBtn = new Button("Моя страница",
                e -> UI.getCurrent().navigate("profile/" + userId));
        Button postsBtn = new Button("Мои посты",
                e -> UI.getCurrent().navigate("profile/" + userId));   // пока тот же view
        Button newPostBtn = new Button("Создать пост",
                e -> UI.getCurrent().navigate("new-post/" + userId));  // реализуешь позже
        Button settingsBtn = new Button("Настройки",
                e -> UI.getCurrent().navigate("settings/" + userId));  // реализуешь позже
        Button friendsBtn = new Button("👥 Друзья",
                e -> UI.getCurrent().navigate("friends/" + userId));

        List<Button> buttons = List.of(profileBtn, postsBtn, newPostBtn, settingsBtn, friendsBtn);
        for (Button b : buttons) {
            b.setWidthFull();
            b.getStyle()
                    .set("justify-content", "flex-start")
                    .set("background-color", "transparent")
                    .set("color", "#2c3e50");
        }

        menu.add(title);
        buttons.forEach(menu::add);
        return menu;
    }

    /* ---------- Правая часть: профиль + посты ---------- */

    private VerticalLayout buildContent(UserFullDto user, List<PostDto> posts) {
        VerticalLayout content = new VerticalLayout();
        content.setSizeFull();
        content.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.CENTER);
        content.setPadding(false);
        content.setSpacing(false);
        content.getStyle().set("background-color", "#e5ebf1");

        content.add(buildProfileCard(user), buildPostsBlock(posts));
        return content;
    }

    /* ---------- Профиль ---------- */

    private VerticalLayout buildProfileCard(UserFullDto user) {
        VerticalLayout card = new VerticalLayout();
        card.setWidth("520px");
        card.setPadding(true);
        card.setSpacing(true);
        card.setAlignItems(FlexComponent.Alignment.STRETCH);
        card.getStyle()
                .set("background-color", "white")
                .set("border-radius", "8px")
                .set("box-shadow", "0 2px 8px rgba(0,0,0,0.08)")
                .set("margin-top", "5vh");

        Avatar avatar = new Avatar(user.getUsername() + " " + user.getLastName());
        avatar.setColorIndex(3);
        avatar.setWidth("72px");
        avatar.setHeight("72px");

        H3 name = new H3(user.getUsername() + " " + user.getLastName());
        name.getStyle()
                .set("margin", "0 0 4px 0")
                .set("color", "#2c3e50");

        Paragraph email = new Paragraph(user.getEmail());
        email.getStyle()
                .set("margin", "0")
                .set("color", "#6b7b8a");

        VerticalLayout nameBlock = new VerticalLayout(name, email);
        nameBlock.setPadding(false);
        nameBlock.setSpacing(false);

        HorizontalLayout header = new HorizontalLayout(avatar, nameBlock);
        header.setWidthFull();
        header.setSpacing(true);
        header.setAlignItems(FlexComponent.Alignment.CENTER);

        VerticalLayout info = new VerticalLayout();
        info.setPadding(false);
        info.setSpacing(false);
        info.getStyle().set("margin-top", "12px");

        info.add(
                line("Телефон", user.getNumberPhone()),
                line("Дата рождения", user.getBirthday() != null ? user.getBirthday().toString() : "-"),
                line("Роль", user.getRole() != null ? user.getRole().name() : "USER"),
                line("Создан", user.getTimeStamp() != null ? user.getTimeStamp().toString() : "-")
        );

        card.add(header, info);
        return card;
    }

    private HorizontalLayout line(String label, String value) {
        Paragraph l = new Paragraph(label + ":");
        l.getStyle()
                .set("margin", "0")
                .set("font-weight", "500")
                .set("color", "#6b7b8a");

        Paragraph v = new Paragraph(value);
        v.getStyle()
                .set("margin", "0")
                .set("color", "#2c3e50");

        HorizontalLayout row = new HorizontalLayout(l, v);
        row.setWidthFull();
        row.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        return row;
    }

    /* ---------- Посты пользователя ---------- */

    private VerticalLayout buildPostsBlock(List<PostDto> posts) {
        VerticalLayout block = new VerticalLayout();
        block.setWidth("520px");
        block.setPadding(true);
        block.setSpacing(true);
        block.getStyle()
                .set("background-color", "white")
                .set("border-radius", "8px")
                .set("box-shadow", "0 2px 8px rgba(0,0,0,0.08)")
                .set("margin-top", "16px");

        H3 title = new H3("Посты");
        title.getStyle()
                .set("margin", "0 0 8px 0")
                .set("color", "#2c3e50");

        block.add(title);

        if (posts == null || posts.isEmpty()) {
            Paragraph empty = new Paragraph("У этого пользователя пока нет постов.");
            empty.getStyle().set("color", "#6b7b8a");
            block.add(empty);
            return block;
        }

        posts.stream()
                .sorted(Comparator.comparing(PostDto::getCreatedAt).reversed())
                .forEach(p -> block.add(postCard(p)));

        return block;
    }

    private Component postCard(PostDto post) {
        VerticalLayout card = new VerticalLayout();
        card.setPadding(true);
        card.setSpacing(false);
        card.getStyle()
                .set("border", "1px solid #e1e5eb")
                .set("border-radius", "6px")
                .set("background-color", "#fafbfc");

        Paragraph date = new Paragraph(
                post.getCreatedAt() != null ? post.getCreatedAt().toString() : ""
        );
        date.getStyle()
                .set("margin", "0 0 4px 0")
                .set("font-size", "12px")
                .set("color", "#8a96a3");

        Paragraph content = new Paragraph(post.getContent());
        content.getStyle()
                .set("margin", "0 0 4px 0")
                .set("white-space", "pre-wrap");

        card.add(date, content);
        return card;
    }
}
