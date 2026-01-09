package org.example.livechatmodule.mainView.post;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.example.common.dto.post.PostDto;
import org.example.common.dto.user.UserFullDto;
import org.example.livechatmodule.client.CommentClient;
import org.example.livechatmodule.client.LikeClient;
import org.example.livechatmodule.client.PostClient;
import org.example.livechatmodule.client.UserClient;

import java.util.Comparator;
import java.util.List;

public class ProfilePostsBlock extends VerticalLayout {

    private final List<PostDto> posts;
    private final PostClient postClient;
    private final LikeClient likeClient;
    private final CommentClient commentClient;
    private final UserClient userClient;
    private final boolean isMyProfile;

    public ProfilePostsBlock(
            List<PostDto> posts,
            PostClient postClient,
            LikeClient likeClient,
            CommentClient commentClient,
            UserClient userClient,
            boolean isMyProfile
    ) {
        this.posts = posts;
        this.postClient = postClient;
        this.likeClient = likeClient;
        this.commentClient = commentClient;
        this.userClient = userClient;
        this.isMyProfile = isMyProfile;

        init();
    }

    private void init() {
        setWidth("520px");
        setPadding(false);
        setSpacing(true);
        addClassName("profile-card");

        if (posts == null || posts.isEmpty()) {
            Paragraph empty = new Paragraph(
                    isMyProfile ? "У вас пока нет постов." : "У пользователя пока нет постов.");
            empty.addClassName("profile-secondary-text");
            add(empty);
            return;
        }

        posts.stream()
                .sorted(Comparator.comparing(PostDto::getCreatedAt).reversed())
                .map(this::createPostCard)
                .forEach(this::add);
    }

    private Component createPostCard(PostDto post) {
        UserFullDto currentUser = userClient.getMyProfile();
        Long currentUserId = currentUser != null ? currentUser.getId() : null;
        return new ProfilePostCard(post, postClient, likeClient, commentClient, userClient, currentUserId);
    }
}
