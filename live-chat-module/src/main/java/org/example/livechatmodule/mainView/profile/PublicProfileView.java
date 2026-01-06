package org.example.livechatmodule.mainView.profile;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.common.dto.post.PostDto;
import org.example.common.dto.user.UserDto;
import org.example.livechatmodule.client.*;
import org.example.livechatmodule.mainView.MainLayout;
import org.example.livechatmodule.mainView.post.ProfilePostsBlock;

import java.util.List;

@Route(value = "profile/:userId", layout = MainLayout.class)
@PageTitle("Профиль пользователя")
@RequiredArgsConstructor
@Slf4j
public class PublicProfileView extends HorizontalLayout implements BeforeEnterObserver {

    private final UserClient userClient;
    private final PostClient postClient;
    private final LikeClient likeClient;
    private final CommentClient commentClient;

    private Long viewedUserId;

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        try {
            String userIdParam = event.getRouteParameters()
                    .get("userId")
                    .orElseThrow(() -> new IllegalArgumentException("userId is required"));

            viewedUserId = Long.valueOf(userIdParam);
            initLayout();
        } catch (Exception e) {
            log.error("Ошибка загрузки публичного профиля: {}", e.getMessage(), e);
            showError("Ошибка загрузки профиля пользователя");
        }
    }

    private void initLayout() {
        removeAll();

        UserDto userDto = userClient.getUserById(viewedUserId);
        if (userDto == null) {
            add(new H3("Пользователь не найден"));
            return;
        }

        List<PostDto> posts = postClient.getUserPosts(viewedUserId);
        add(buildMainContent(userDto, posts));
    }

    private Component buildMainContent(UserDto user, List<PostDto> posts) {
        VerticalLayout content = new VerticalLayout();
        content.setWidthFull();
        content.setPadding(false);
        content.setSpacing(true);
        content.setAlignItems(Alignment.CENTER);
        content.addClassName("profile-main-content");

        ProfileInfoCard infoCard = new ProfileInfoCard(user);
        ProfilePostsBlock postsBlock =
                new ProfilePostsBlock(posts, postClient, likeClient, commentClient, userClient, false);

        content.add(infoCard, postsBlock);
        return content;
    }

    private void showError(String message) {
        removeAll();
        Notification.show(message, 5000, Notification.Position.TOP_CENTER);
        VerticalLayout errorLayout = new VerticalLayout();
        errorLayout.setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        errorLayout.setPadding(true);
        errorLayout.add(message);
        add(errorLayout);
    }
}
