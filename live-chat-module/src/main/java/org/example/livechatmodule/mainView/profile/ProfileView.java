package org.example.livechatmodule.mainView.profile;

import com.vaadin.flow.component.Component;
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
        try {
            String userIdParam = event.getRouteParameters().get("userId").orElse(null);

            if (userIdParam != null) {
                // Чужой профиль
                viewedUserId = Long.valueOf(userIdParam);
            } else {
                // Мой профиль
                isOwnProfile = true;
            }

            initLayout();
        } catch (Exception e) {
            log.error("[ERROR] Ошибка загрузки профиля: {}", e.getMessage(), e);
            showError("Ошибка загрузки профиля");
        }
    }

    private void initLayout() {
        removeAll();

        UserDto userDto;
        if (isOwnProfile) {
            UserFullDto userFullDto = userClient.getMyProfile();
            if (userFullDto == null) {
                add(new H3("Ошибка загрузки профиля"));
                return;
            }
            userDto = mapToDto(userFullDto);
            viewedUserId = userDto.getUserId();
        } else {
            userDto = userClient.getUserById(viewedUserId);
            if (userDto == null) {
                add(new H3("Пользователь не найден"));
                return;
            }
        }

        List<PostDto> posts = postClient.getUserPosts(viewedUserId);
        add(buildMainContent(userDto, posts));
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
    
    private Component buildMainContent(UserDto user, List<PostDto> posts) {
        Long currentUserId = userClient.getCurrentUserId();

        VerticalLayout content = new VerticalLayout();
        content.setWidthFull();
        content.setPadding(false);
        content.setSpacing(true);
        content.setAlignItems(FlexComponent.Alignment.CENTER);
        content.addClassName("profile-main-content");

         ProfileInfoCard infoCard = new ProfileInfoCard(
            user, friendClient, friendRequestClient, currentUserId
        );
        ProfilePostsBlock postsBlock = new ProfilePostsBlock(
                posts, postClient, likeClient, commentClient, userClient, isOwnProfile
        );

        content.add(infoCard, postsBlock);
        return content;
    }

    private void showError(String message) {
        removeAll();
        Notification.show(message, 5000, Notification.Position.TOP_CENTER);
        VerticalLayout errorLayout = new VerticalLayout();
        errorLayout.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.CENTER);
        errorLayout.setPadding(true);
        errorLayout.add(new H3(message));
        add(errorLayout);
    }
}
