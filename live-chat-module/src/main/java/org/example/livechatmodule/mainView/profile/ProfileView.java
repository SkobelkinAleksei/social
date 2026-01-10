package org.example.livechatmodule.mainView.profile;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
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
import org.example.common.dto.user.UserFullDto;
import org.example.livechatmodule.client.*;
import org.example.livechatmodule.mainView.MainLayout;
import org.example.livechatmodule.mainView.post.ProfilePostsBlock;

import java.util.List;

@Slf4j
@Route(value = "profile/:userId?", layout = MainLayout.class)
@CssImport("./styles/profile-styles.css")
@PageTitle("Профиль пользователя")
@RequiredArgsConstructor
public class ProfileView extends HorizontalLayout implements BeforeEnterObserver {

    private final PostClient postClient;
    private final UserClient userClient;
    private final CommentClient commentClient;
    private final LikeClient likeClient;
    private final FriendClient friendClient;
    private final FriendRequestClient friendRequestClient;

    private Long viewedUserId;
    private boolean isOwnProfile = false;

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        log.info("🔥 beforeEnter ProfileView вызван");

        String userIdParam = event.getRouteParameters().get("userId").orElse(null);

        if (userIdParam != null) {
            try {
                viewedUserId = Long.valueOf(userIdParam);
                isOwnProfile = false;
                log.info("Профиль пользователя ID: {}", viewedUserId);
            } catch (NumberFormatException e) {
                log.error("Неверный userId: {}", userIdParam);
                showError("Неверный ID пользователя");
                return;
            }
        } else {
            isOwnProfile = true;
            log.info("Собственный профиль");
        }

        UI ui = event.getUI();
        if (ui != null) {
            ui.access(this::loadProfile);
        } else {
            log.error("UI недоступен в beforeEnter");
        }
    }

    private void loadProfile() {
        log.info("🚀 Загрузка профиля в UI потоке");

        removeAll(); // Очищаем сразу

        try {
            UserDto userDto = loadUserData();
            List<PostDto> posts = postClient.getUserPosts(viewedUserId);
            log.info("Загружено {} постов", posts.size());

            add(buildMainContent(userDto, posts));

        } catch (Exception e) {
            log.error("Ошибка загрузки профиля", e);
            showError("Ошибка загрузки профиля: " + e.getMessage());
        }
    }

    private UserDto loadUserData() {
        if (isOwnProfile) {
            UserFullDto userFullDto = userClient.getMyProfile();
            if (userFullDto == null) {
                throw new RuntimeException("Не удалось загрузить профиль");
            }
            viewedUserId = userFullDto.getId();
            return mapToDto(userFullDto);
        } else {
            UserDto userDto = userClient.getUserById(viewedUserId);
            if (userDto == null) {
                throw new RuntimeException("Пользователь не найден");
            }
            return userDto;
        }
    }

    private Component buildMainContent(UserDto user, List<PostDto> posts) {
        Long currentUserId = userClient.getCurrentUserId();

        VerticalLayout mainContent = new VerticalLayout();
        mainContent.setWidthFull();
        mainContent.setPadding(false);
        mainContent.setSpacing(true);
        mainContent.setAlignItems(FlexComponent.Alignment.CENTER);
        mainContent.addClassName("profile-main-content");

        ProfileInfoCard infoCard = new ProfileInfoCard(
                user, friendClient, friendRequestClient, currentUserId
        );
        ProfilePostsBlock postsBlock = new ProfilePostsBlock(
                posts, postClient, likeClient, commentClient, userClient, isOwnProfile
        );

        mainContent.add(infoCard, postsBlock);
        return mainContent;
    }

    private void showError(String message) {
        log.error("Показ ошибки: {}", message);
        removeAll();
        Notification.show(message, 5000, Notification.Position.TOP_CENTER);

        VerticalLayout errorLayout = new VerticalLayout();
        errorLayout.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.CENTER);
        errorLayout.setPadding(true);
        errorLayout.add(new H3(message));
        add(errorLayout);
    }

    private UserDto mapToDto(UserFullDto dto) {
        UserDto userDto = new UserDto();
        userDto.setUserId(dto.getId());
        userDto.setFirstName(dto.getFirstName());
        userDto.setLastName(dto.getLastName());
        userDto.setEmail(dto.getEmail());
        userDto.setBirthday(dto.getBirthday());
        userDto.setNumberPhone(dto.getNumberPhone());
        return userDto;
    }
}
