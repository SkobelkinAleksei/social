package org.example.livechatmodule.mainView.profile;

import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.common.dto.post.PostDto;
import org.example.common.dto.user.UserFullDto;
import org.example.livechatmodule.client.CommentClient;
import org.example.livechatmodule.client.LikeClient;
import org.example.livechatmodule.client.PostClient;
import org.example.livechatmodule.client.UserClient;
import org.example.livechatmodule.mainView.MainLayout;
import org.example.livechatmodule.mainView.post.ProfilePostsBlock;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Route(value = "friends-posts/:userId", layout = MainLayout.class)
public class FriendsPostsView extends VerticalLayout implements BeforeEnterObserver {

    private final PostClient postClient;
    private final UserClient userClient;
    private final LikeClient likeClient;
    private final CommentClient commentClient;

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        removeAll();
        try {
            String userIdParam = event.getRouteParameters().get("userId")
                    .orElseThrow(() -> new EntityNotFoundException("userId required"));

            Long currentUserId = Long.valueOf(userIdParam);

            List<PostDto> friendsPosts = postClient.getFriendsPosts(currentUserId);

            UserFullDto currentUser = userClient.getMyProfile();
            boolean isMyProfile = currentUser != null && currentUser.getId().equals(currentUserId);

            add(buildFriendsPostsContent(friendsPosts, currentUserId, isMyProfile));
            log.info("FriendsPostsView: загружено {} постов друзей для userId={}",
                    friendsPosts.size(), currentUserId);

        } catch (Exception e) {
            log.error("FriendsPostsView ошибка: {}", e.getMessage(), e);
            add(new H3("Ошибка загрузки постов друзей"));
        }
    }

    private VerticalLayout buildFriendsPostsContent(List<PostDto> posts, Long currentUserId, boolean isMyProfile) {
        VerticalLayout content = new VerticalLayout();
        content.setSizeFull();
        content.setPadding(true);
        content.setSpacing(true);
        content.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.CENTER);

        H3 title = new H3("Посты друзей");
        title.getStyle().set("color", "#2c3e50");

        if (posts.isEmpty()) {
            Paragraph empty = new Paragraph("У ваших друзей пока нет постов 😌");
            empty.getStyle().set("color", "#6b7b8a").set("font-size", "18px");
            content.add(title, empty);
            return content;
        }

        ProfilePostsBlock postsBlock = new ProfilePostsBlock(
                posts, postClient, likeClient, commentClient, userClient, false
        );
        content.add(title, postsBlock);

        return content;
    }
}
