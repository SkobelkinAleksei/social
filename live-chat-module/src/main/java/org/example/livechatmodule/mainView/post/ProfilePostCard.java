package org.example.livechatmodule.mainView.post;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import lombok.extern.slf4j.Slf4j;
import org.example.common.dto.comment.CommentDto;
import org.example.common.dto.comment.NewCommentDto;
import org.example.common.dto.post.PostDto;
import org.example.common.dto.user.UserDto;
import org.example.common.dto.user.UserFullDto;
import org.example.livechatmodule.client.CommentClient;
import org.example.livechatmodule.client.LikeClient;
import org.example.livechatmodule.client.PostClient;
import org.example.livechatmodule.client.UserClient;
import org.example.livechatmodule.mainView.comment.DeleteComment;
import org.example.livechatmodule.mainView.comment.EditComment;
import org.example.livechatmodule.mainView.like.LikeListDialog;
import org.example.livechatmodule.mainView.like.ViewListDialog;
import org.example.livechatmodule.utils.CustomDateTimeFormatter;

import java.time.LocalDateTime;
import java.util.List;

@CssImport("./styles/post-modal.css")
@Slf4j
public class ProfilePostCard extends VerticalLayout {

    private final PostDto post;
    private final PostClient postClient;
    private final CommentClient commentClient;
    private final UserClient userClient;
    private final Long currentUserId;
    private Div commentsContainer;
    private TextArea commentInput;
    private Button sendCommentBtn;
    private HorizontalLayout commentForm;
    private Paragraph content;
    private final LikeClient likeClient;
    private Button likeBtn;
    private Span likeCount;
    private Span viewCount;
    private Button viewsBtn;
    private boolean isLiked = false;
    private LocalDateTime lastViewTime = LocalDateTime.now().minusSeconds(10);



    public ProfilePostCard(PostDto post, PostClient postClient, LikeClient likeClient, CommentClient commentClient,
                           UserClient userClient, Long currentUserId) {
        this.post = post;
        this.postClient = postClient;
        this.commentClient = commentClient;
        this.userClient = userClient;
        this.currentUserId = currentUserId;
        this.likeClient = likeClient;
        init();
    }

    private void init() {
        setPadding(true);
        setSpacing(false);
        setMaxWidth("520px");
        addClassNames("profile-post-card", "post-card-relative");

        VerticalLayout contentContainer = new VerticalLayout();
        contentContainer.setPadding(false);
        contentContainer.setSpacing(true);
        contentContainer.setWidthFull();
        contentContainer.addClassName("post-content-container");

        HorizontalLayout postHeader = createPostHeader();
        HorizontalLayout likeLayout = createLikeLayout();

        String formatCommentDate = CustomDateTimeFormatter.formatCommentDate(post.getCreatedAt());
        Paragraph date = new Paragraph(formatCommentDate);
        date.addClassName("profile-date");

        content = new Paragraph(post.getContent() != null ? post.getContent() : "");
        content.addClassName("profile-content-text");

        Button commentsBtn = new Button("💬 Комментарии");
        commentsBtn.addClassName("profile-comments-btn");

        // Создаем форму СНИЗУ
        createCommentForm();
        commentsContainer = new Div();
        commentsContainer.setVisible(false);
        commentsContainer.addClassName("profile-comments-container");

        commentsBtn.addClickListener(e -> toggleComments(commentsBtn));

        // Контент поста
        contentContainer.add(postHeader, date, content, likeLayout, commentsBtn, commentsContainer);

        getElement().addEventListener("mouseenter", e -> registerView());
        getElement().addEventListener("click", e -> registerView());
        // Edit кнопка (если владелец)
        if (isPostOwner()) {
            Button editBtn = createPostEditButton();
            add(contentContainer, commentForm, editBtn);
        } else {
            add(contentContainer, commentForm);
        }

        UI.getCurrent().access(this::loadLikesSync);
    }

    private HorizontalLayout createPostHeader() {
        UserDto author = userClient.getUserById(post.getAuthorId());
        String authorName = author != null
                ? author.getFirstName() + (author.getLastName() != null ? " " + author.getLastName() : "")
                : "Пользователь #" + post.getAuthorId();

        Span authorSpan = new Span(authorName);
        authorSpan.addClassName("post-author-name");
        authorSpan.getElement().setProperty("title", "Перейти к профилю");
        authorSpan.addClickListener(e -> UI.getCurrent().navigate("profile/" + post.getAuthorId()));
        authorSpan.getStyle().set("cursor", "pointer").set("color", "#1976d2")
                .set("text-decoration", "underline").set("font-weight", "500");

        HorizontalLayout header = new HorizontalLayout(authorSpan);
        header.setWidthFull();
        header.setJustifyContentMode(FlexComponent.JustifyContentMode.START);
        header.addClassName("post-header");
        return header;
    }

    private HorizontalLayout createLikeLayout() {
        likeCount = new Span("⏳");
        likeCount.addClassName("like-count");
        likeCount.getStyle().set("cursor", "pointer").set("color", "#1976d2")
                .set("text-decoration", "underline");
        likeCount.addClickListener(e -> showLikes());

        likeBtn = new Button("🤍");
        likeBtn.addClassNames("like-btn");
        likeBtn.addClickListener(e -> toggleLike());

        viewsBtn = new Button("👁");
        viewsBtn.addClassName("views-btn");
        viewsBtn.addClickListener(e -> showViews());
        viewCount = new Span("0");
        viewCount.addClassName("view-count");
        viewCount.getStyle().set("color", "#888").set("font-size", "14px").set("marginLeft", "4px");

        HorizontalLayout layout = new HorizontalLayout(likeBtn, likeCount, viewsBtn, viewCount);
        layout.setSpacing(true);
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        layout.setWidthFull();
        return layout;
    }

    @SuppressWarnings("unused")
    public void registerView() {
        LocalDateTime now = LocalDateTime.now();
        if (now.minusSeconds(3).isAfter(lastViewTime)) {
            lastViewTime = now;
            log.info("👁 View: {}", post.getPostId());
            postClient.getPostById(post.getPostId())
                    .thenRun(this::updateViewCount);
        }
    }

    private void updateViewCount() {
        postClient.getViewsCount(post.getPostId()).thenAccept(count ->
                getUI().ifPresent(ui -> ui.access(() -> {
                    viewCount.setText(count != null ? count.toString() : "0");
                    log.info("📊 Обновлено: {} просмотров", count);
                })));
    }

    private void showViews() {
        // Дополнительный просмотр при клике на глазик
        postClient.getPostById(post.getPostId());

        postClient.getPostViews(post.getPostId())
                .thenAccept(views -> getUI().ifPresent(ui -> ui.access(() -> {
                    if (views.isEmpty()) {
                        Notification.show("👁 Нет просмотров");
                        return;
                    }
                    new ViewListDialog(views, userClient).open();
                })));
    }

    private void loadLikesSync() {
        log.info("[INFO] Синхронная загрузка лайков поста {}", post.getPostId());

        Long count = likeClient.getLikesCount(post.getPostId()).join();
        likeCount.setText(count != null ? count.toString() : "0");
        log.info("[INFO] Синхронно: количество = {}", count);

        // ✅ Загружаем просмотры
        postClient.getViewsCount(post.getPostId()).thenAccept(c -> {
            getUI().ifPresent(ui -> ui.access(() -> viewCount.setText(c != null ? c.toString() : "0")));
            log.info("[INFO] Просмотров: {}", c);
        });

        Boolean liked = likeClient.isLiked(post.getPostId(), currentUserId).join();
        isLiked = liked != null ? liked : false;
        updateLikeButtonLocal();
        log.info("[INFO] Синхронно: liked = {}", isLiked);
    }

    private void toggleLike() {
        registerView();
        likeClient.toggleLike(post.getPostId()).join();
        loadLikesSync();
    }

    private void updateLikeButtonLocal() {
        if (isLiked) {
            likeBtn.setText("❤️");
            likeBtn.addClassName("liked");
        } else {
            likeBtn.setText("🤍");
            likeBtn.removeClassName("liked");
        }
    }

    private void showLikes() {
        registerView();
        likeClient.getLikes(post.getPostId())
                .thenAccept(likes -> getUI().ifPresent(ui -> ui.access(() -> {
                    if (likes.isEmpty()) {
                        Notification.show("Лайков нет", 2000, Notification.Position.TOP_CENTER);
                        return;
                    }
                    new LikeListDialog(likes, userClient).open();
                })));
    }

    private boolean isPostOwner() {
        try {
            UserFullDto currentUser = userClient.getMyProfile();
            return currentUser != null && currentUser.getId().equals(post.getAuthorId());
        } catch (Exception e) {
            return false;
        }
    }

    private Button createPostEditButton() {
        Button editBtn = new Button("⚙️");
        editBtn.addClassName("profile-post-edit-btn");
        editBtn.addClickListener(e -> openEditPostDialog());
        return editBtn;
    }

    private void openEditPostDialog() {
        new EditPostDialog(postClient, post.getPostId(), post.getContent(), updatedContent -> {
            post.setContent(updatedContent);
            content.removeAll();
            content.add(new Paragraph(updatedContent));
        }).open();
    }

    private void createCommentForm() {
        commentInput = new TextArea("Напишите комментарий...");
        commentInput.setMaxHeight("80px");
        commentInput.addClassName("profile-comment-input");

        // АКТИВИРУЕМ кнопку при вводе текста
        commentInput.addValueChangeListener(e -> {
            boolean hasText = !e.getValue().trim().isEmpty();
            sendCommentBtn.setEnabled(hasText);
            log.info("⌨️ Текст: '{}', enabled: {}", e.getValue().trim(), hasText);
        });

        sendCommentBtn = new Button("📤 Отправить");
        sendCommentBtn.addClassName("profile-send-comment-btn");
        sendCommentBtn.setEnabled(false); // Изначально отключена
        sendCommentBtn.addClickListener(e -> sendComment());

        commentForm = new HorizontalLayout(commentInput, sendCommentBtn);
        commentForm.setWidthFull();
        commentForm.setSpacing(true);
        commentForm.addClassName("profile-comment-form");
    }

    private void toggleComments(Button commentsBtn) {
        registerView();
        boolean visible = commentsContainer.isVisible();
        commentsContainer.setVisible(!visible);

        commentsBtn.setText(visible ? "💬 Комментарии" : "▲ Закрыть комментарии");

        if (!visible) {
            loadComments(commentsContainer);
            commentInput.focus();
        }
    }

    private void sendComment() {
        registerView();
        String text = commentInput.getValue().trim();
        if (text.isEmpty()) return;

        commentClient.createComment(post.getPostId(), new NewCommentDto(text));
        commentInput.clear();

        commentsContainer.setVisible(true);
        reloadComments();

        Notification.show("✅ Готово!");
    }

    private void reloadComments() {
        if (commentsContainer != null && commentsContainer.isVisible()) {
            loadComments(commentsContainer);
        }
    }

    private void loadComments(Div container) {
        container.removeAll();
        log.info("[INFO] Загружаем комментарии для поста {}", post.getPostId());

        try {
            List<CommentDto> comments = commentClient.getCommentsByPostId(post.getPostId());
            log.info("[INFO] Загружено {} комментариев", comments != null ? comments.size() : 0);

            if (comments == null || comments.isEmpty()) {
                container.add(new Paragraph("💭 Комментариев пока нет"));
                return;
            }

            comments.forEach(comment -> createCommentLayout(container, comment, currentUserId));
        } catch (Exception e) {
            log.error("[ERROR] Ошибка загрузки комментариев: {}", e.getMessage());
            container.add(new Paragraph("❌ Ошибка загрузки комментариев"));
        }
    }

    private void createCommentLayout(Div container, CommentDto comment, Long currentUserId) {
        VerticalLayout layout = new VerticalLayout();
        layout.addClassName("profile-comment-layout");

        UserDto author = userClient.getUserById(comment.getAuthorId());
        String authorName = author != null ? author.getFirstName() + " " + author.getLastName() : "Пользователь";

        Span authorSpan = new Span(authorName);
        authorSpan.addClassName("profile-author-text");

        Span dateSpan = new Span(" • " + CustomDateTimeFormatter.formatCommentDate(comment.getCreatedAt()));
        dateSpan.addClassName("profile-date");

        boolean isCommentAuthor = currentUserId.equals(comment.getAuthorId());
        boolean isPostOwner = currentUserId.equals(post.getAuthorId());
        boolean canEdit = isCommentAuthor;
        boolean canDelete = isCommentAuthor || isPostOwner;

        Button editBtn = canEdit ? new Button("⚙️") : new Button();
        Button deleteBtn = canDelete ? new Button("🗑") : new Button();

        editBtn.addClassName("profile-edit-btn");
        deleteBtn.addClassName("profile-delete-btn");
        editBtn.setVisible(canEdit);
        deleteBtn.setVisible(canDelete);

        if (canEdit) {
            editBtn.addClickListener(e ->
                    new EditComment(commentClient, comment.getId(), post.getPostId(),
                            () -> reloadComments()).open());
        }
        if (canDelete) {
            deleteBtn.addClickListener(e ->
                    new DeleteComment(commentClient, comment.getId(), post.getPostId(),
                            ignored -> reloadComments()).open());
        }

        HorizontalLayout header = new HorizontalLayout(authorSpan, dateSpan, editBtn, deleteBtn);
        header.addClassName("profile-header-row");
        header.setWidthFull();
        header.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);

        Paragraph text = new Paragraph(comment.getContent());
        text.addClassName("profile-comment-text");

        layout.add(header, text);
        container.add(layout);
    }

}
