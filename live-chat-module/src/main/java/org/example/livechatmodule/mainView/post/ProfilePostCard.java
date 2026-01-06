package org.example.livechatmodule.mainView.post;

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
import org.example.livechatmodule.utils.CustomDateTimeFormatter;

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
    private final LikeClient likeClient;  // ✅ ДОБАВЬ
    private Button likeBtn;
    private Span likeCount;
    private boolean isLiked = false;  // Статус лайка

    public ProfilePostCard(PostDto post, PostClient postClient, LikeClient likeClient, CommentClient commentClient,
                           UserClient userClient, Long currentUserId) {
        this.post = post;
        this.postClient = postClient;
        this.commentClient = commentClient;
        this.userClient = userClient;
        this.currentUserId = currentUserId;
        this.likeClient = likeClient;
        init();
        loadLikeStatus();
    }

    private void init() {
        setPadding(true);
        setSpacing(false);
        setMaxWidth("520px");
        addClassNames("profile-post-card", "post-card-relative");

        // ✅ Контейнер контента
        VerticalLayout contentContainer = new VerticalLayout();
        contentContainer.setPadding(false);
        contentContainer.setSpacing(true);
        contentContainer.setWidthFull();
        contentContainer.addClassName("post-content-container");


        HorizontalLayout likeLayout = createLikeLayout();

        // Дата
        String formatCommentDate = CustomDateTimeFormatter.formatCommentDate(post.getCreatedAt());
        Paragraph date = new Paragraph(formatCommentDate);
        date.addClassName("profile-date");

        content = new Paragraph(post.getContent() != null ? post.getContent() : "");
        content.addClassName("profile-content-text");

        Button commentsBtn = new Button("💬 Комментарии");
        commentsBtn.addClassName("profile-comments-btn");

        createCommentForm();
        commentsContainer = new Div();
        commentsContainer.setVisible(false);
        commentsContainer.addClassName("profile-comments-container");

        commentsBtn.addClickListener(e -> toggleComments(commentsBtn));

        // ✅ Добавляем в контейнер
        contentContainer.add(date, content, likeLayout, commentsBtn, commentsContainer, commentForm);

        // ✅ Шестерёнка поверх (если владелец)
        if (isPostOwner()) {
            Button editBtn = createPostEditButton();
            add(editBtn, contentContainer);  // ✅ Шестерёнка ПЕРВАЯ
        } else {
            add(contentContainer);
        }
    }

    private HorizontalLayout createLikeLayout() {
        // ✅ КНОПКА ЛАЙКА + СЧЁТЧИК
        likeCount = new Span("0");
        likeCount.addClassName("like-count");

        likeBtn = new Button();
        likeBtn.addClassNames("like-btn");
        likeBtn.addClickListener(e -> toggleLike());

        // ✅ КНОПКА ПРОСМОТРА ЛАЙКОВ ПОСТА
        Button showLikesBtn = new Button("👁");
        showLikesBtn.addClassName("show-likes-btn");
        showLikesBtn.addClickListener(e -> showLikes());

        // ✅ Загружаем статус и счётчик
        loadLikeStatus();

        HorizontalLayout layout = new HorizontalLayout(likeBtn, likeCount, showLikesBtn);
        layout.setSpacing(true);
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        layout.setWidthFull();

        return layout;
    }

    private void loadLikeStatus() {
        // 1. Статус лайка (liked/me)
        likeClient.getLikes(post.getPostId())
                .thenAccept(likes -> {
                    isLiked = likes.stream().anyMatch(like -> like.getAuthorId().equals(currentUserId));
                    updateLikeButton();
                });

        // 2. Счётчик лайков (отдельно)
        likeClient.getLikesCount(post.getPostId())
                .thenAccept(count -> likeCount.setText(String.valueOf(count)));
    }

    private void toggleLike() {
        likeClient.toggleLike(post.getPostId())
                .thenAccept(status -> {
                    isLiked = !isLiked;
                    updateLikeButton();

                    // Обновляем счётчик
                    likeClient.getLikesCount(post.getPostId())
                            .thenAccept(count -> {
                                likeCount.setText(String.valueOf(count));
                                Notification.show(isLiked ? "❤️ Лайк поставлен!" : "💔 Лайк убран",
                                        800, Notification.Position.TOP_CENTER);
                            });
                });
    }

    private void updateLikeButton() {
        if (isLiked) {
            likeBtn.setText("❤️");  // Красное заполненное
            likeBtn.addClassName("liked");
        } else {
            likeBtn.setText("🤍");  // Белое пустое
            likeBtn.removeClassName("liked");
        }
    }

    private void showLikes() {
        log.info("👁 Клик по просмотру лайков поста {}", post.getPostId());
        likeClient.getLikes(post.getPostId())
                .thenAccept(likes -> {
                    log.info("✅ Получено {} лайков для показа", likes.size());
                    if (likes.isEmpty()) {
                        Notification.show("Лайков нет", 2000, Notification.Position.TOP_CENTER);
                        return;
                    }

                    // 🔥 КРИТИЧЕСКИ ВАЖНО!
                    getUI().ifPresent(ui -> {
                        ui.access(() -> {
                            LikeListDialog dialog = new LikeListDialog(likes, userClient);
                            dialog.open();
                            log.info("✅ Диалог открыт в UI-потоке!");
                        });
                    });
                });
    }

    private boolean isPostOwner() {
        UserFullDto currentUser = userClient.getMyProfile();
        return currentUser != null && currentUser.getId().equals(post.getAuthorId());
    }

    private Button createPostEditButton() {
        Button editBtn = new Button("⚙️");
        editBtn.addClassName("profile-post-edit-btn");
        editBtn.addClickListener(e -> openEditPostDialog());
        return editBtn;
    }

    private void openEditPostDialog() {
        EditPostDialog dialog = new EditPostDialog(
                postClient,
                post.getPostId(),
                post.getContent(),
                updatedContent -> {
                    // ✅ Обновляем контент ЛОКАЛЬНО (лучше!)
                    post.setContent(updatedContent);
                    content.removeAll();  // Очищаем текущий Paragraph
                    content.add(new Paragraph(updatedContent));  // Добавляем новый
                }
        );
        dialog.open();
    }

    private void createCommentForm() {
        commentInput = new TextArea();
        commentInput.setPlaceholder("Напишите комментарий...");
        commentInput.setMaxHeight("80px");
        commentInput.addClassName("profile-comment-input");
        commentInput.setVisible(false);

        commentInput.addValueChangeListener(e -> {
            sendCommentBtn.setEnabled(!e.getValue().trim().isEmpty());
        });

        sendCommentBtn = new Button("📤 Отправить");
        sendCommentBtn.addClassName("profile-send-comment-btn");
        sendCommentBtn.setVisible(false);
        sendCommentBtn.setEnabled(false);
        sendCommentBtn.addClickListener(e -> sendComment());

        commentForm = new HorizontalLayout(commentInput, sendCommentBtn);
        commentForm.setWidthFull();
        commentForm.setSpacing(true);
        commentForm.setVisible(false);
        commentForm.addClassName("profile-comment-form");
    }

    private void toggleComments(Button commentsBtn) {
        boolean isVisible = commentsContainer.isVisible();
        commentsContainer.setVisible(!isVisible);
        commentForm.setVisible(!isVisible);

        if (!isVisible) {
            loadComments(commentsContainer);
            commentsBtn.setText("▲ Закрыть комментарии");
            commentInput.setVisible(true);
            sendCommentBtn.setVisible(true);
            commentInput.focus();
        } else {
            commentsBtn.setText("💬 Комментарии");
        }
    }

    private void sendComment() {
        String content = commentInput.getValue().trim();
        if (content.isEmpty()) {
            Notification.show("⚠️ Введите текст комментария", 2000, Notification.Position.TOP_CENTER);
            return;
        }

        log.info("📤 Отправляем комментарий для поста {}", post.getPostId());

        try {
            NewCommentDto newComment = new NewCommentDto();
            newComment.setContent(content);

            CommentDto createdComment = commentClient.createComment(post.getPostId(), newComment);
            log.info("✅ Комментарий создан: {}", createdComment.getId());

            commentInput.clear();
            reloadComments();
            Notification.show("✅ Комментарий добавлен!", 2000, Notification.Position.TOP_CENTER);
        } catch (Exception e) {
            log.error("❌ Ошибка создания комментария: {}", e.getMessage(), e);
            Notification.show("❌ Ошибка отправки", 3000, Notification.Position.TOP_CENTER);
        }
    }

    private void reloadComments() {
        if (commentsContainer != null) {
            loadComments(commentsContainer);
        }
    }

    private void loadComments(Div container) {
        container.removeAll();
        log.info("🔄 Загружаем комментарии для поста {}", post.getPostId());

        try {
            List<CommentDto> comments = commentClient.getCommentsByPostId(post.getPostId());
            log.info("✅ Загружено {} комментариев", comments != null ? comments.size() : 0);

            if (comments == null || comments.isEmpty()) {
                container.add(new Paragraph("💭 Комментариев пока нет"));
                return;
            }

            // 👉 УБИРАЕМ userClient.getMyProfile() — используем переданный currentUserId!
            log.info("👤 Текущий пользователь ID: {}", currentUserId);

            comments.forEach(comment ->
                    createCommentLayout(container, comment, currentUserId)
            );

        } catch (Exception e) {
            log.error("❌ Ошибка загрузки: {}", e.getMessage(), e);
            container.add(new Paragraph("❌ Ошибка загрузки комментариев"));
        }
    }

    private void createCommentLayout(Div container, CommentDto comment, Long currentUserId) {
        log.info("📝 Комментарий: authorId={}, id={}, content={}",
                comment.getAuthorId(), comment.getId(), comment.getContent());

        VerticalLayout commentLayout = new VerticalLayout();
        commentLayout.setPadding(false);
        commentLayout.setSpacing(false);
        commentLayout.setWidth("100%");
        commentLayout.addClassName("profile-comment-layout");

        UserDto author = userClient.getUserById(comment.getAuthorId());
        String authorName = author != null ?
                author.getFirstName() + (author.getLastName() != null ? " " + author.getLastName() : "") :
                "Пользователь";

        Span authorSpan = new Span(authorName);
        authorSpan.addClassName("profile-author-text");

        String formattedCreatedAt = CustomDateTimeFormatter.formatCommentDate(comment.getCreatedAt());
        Span dateSpan = new Span(" • " + formattedCreatedAt);
        dateSpan.addClassName("profile-date");

        Button deleteBtn = createDeleteButton(comment, currentUserId);
        Button editBtn = createEditButton(comment, currentUserId);

        deleteBtn.addClassName("profile-delete-btn");
        editBtn.addClassName("profile-edit-btn");

        HorizontalLayout header = new HorizontalLayout(authorSpan, dateSpan, editBtn, deleteBtn);
        header.addClassName("profile-header-row");
        header.setWidthFull();
        header.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        header.setAlignItems(Alignment.CENTER);

        Paragraph commentText = new Paragraph(comment.getContent() != null ? comment.getContent() : "");
        commentText.addClassName("profile-comment-text");

        commentLayout.add(header, commentText);
        container.add(commentLayout);
    }

    private Button createEditButton(CommentDto comment, Long currentUserId) {
        Button editBtn = new Button("⚙️");
        editBtn.addClassName("profile-edit-btn");

        boolean isAuthor = currentUserId != null && comment.getAuthorId() != null &&
                currentUserId.equals(comment.getAuthorId());
        editBtn.setVisible(isAuthor);

        editBtn.addClickListener(e -> openEditDialog(comment)); // ✅ Готово!
        return editBtn;
    }

    private void openEditDialog(CommentDto comment) {
        EditComment dialog = new EditComment(
                commentClient,
                comment.getId(),
                post.getPostId(),
                this::reloadComments
        );
        dialog.open();
    }

    private Button createDeleteButton(CommentDto comment, Long currentUserId) {
        Button deleteBtn = new Button("🗑 Удалить");
        deleteBtn.addClassName("profile-delete-btn");

        boolean isAuthor = currentUserId != null && comment.getAuthorId() != null &&
                currentUserId.equals(comment.getAuthorId());
        log.info("🔍 Комментарий {} является своим? {}", comment.getId(), isAuthor);
        deleteBtn.setVisible(isAuthor);

        deleteBtn.addClickListener(e -> {
            log.info("🗑 КЛИК ПО УДАЛЕНИЮ commentId={}", comment.getId());

            DeleteComment dialog = new DeleteComment(
                    commentClient,
                    comment.getId(),
                    post.getPostId(),
                    ignored -> reloadComments()  // перезагрузка
            );
            dialog.open();
        });

        return deleteBtn;
    }
}
