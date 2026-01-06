package org.example.livechatmodule.mainView.profile;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.H3;
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
@Route(value = "profile", layout = MainLayout.class)
@CssImport("./styles/profile-styles.css")
@PageTitle("Мой профиль")
@RequiredArgsConstructor
public class ProfileView extends HorizontalLayout implements BeforeEnterObserver {

    private final PostClient postClient;
    private final UserClient userClient;
    private final CommentClient commentClient;
    private final LikeClient likeClient;

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        try {
            initLayout();
        } catch (Exception e) {
            log.error("❌ Ошибка загрузки профиля: {}", e.getMessage(), e);
            add(new H3("Ошибка загрузки профиля"));
        }
    }

    private void initLayout() {
        removeAll();

        UserFullDto userFullDto = userClient.getMyProfile();
        if (userFullDto == null) {
            add(new H3("Ошибка загрузки профиля"));
            return;
        }

        UserDto userDto = mapToDto(userFullDto);
        List<PostDto> posts = postClient.getUserPosts(userDto.getUserId());

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
        VerticalLayout content = new VerticalLayout();
        content.setWidthFull();
        content.setPadding(false);
        content.setSpacing(true);
        content.setAlignItems(FlexComponent.Alignment.CENTER);
        content.addClassName("profile-main-content");

        ProfileInfoCard infoCard = new ProfileInfoCard(user);
        ProfilePostsBlock postsBlock =
                new ProfilePostsBlock(posts, postClient, likeClient, commentClient, userClient, true);
        content.add(infoCard, postsBlock);
        return content;
    }
}
