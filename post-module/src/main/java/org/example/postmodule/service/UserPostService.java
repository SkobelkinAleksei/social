package org.example.postmodule.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.common.dto.*;
import org.example.common.dto.user.UserDto;
import org.example.httpcore.httpCore.SecuredHttpCore;
import org.example.postmodule.entity.ModerationStatusPost;
import org.example.postmodule.dto.NewPostDto;
import org.example.postmodule.dto.PostDto;
import org.example.postmodule.dto.UpdatePostDto;
import org.example.postmodule.entity.PostEntity;
import org.example.postmodule.mapper.PostMapper;
import org.example.postmodule.repository.PostRepository;
import org.example.postmodule.util.PostAccessValidator;
import org.example.postmodule.util.PostLookupService;
import org.example.postmodule.util.PostStatusSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;


@Slf4j
@AllArgsConstructor
@Service
public class UserPostService {

    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final PostLookupService postLookupService;
    private final PostAccessValidator postAccessValidator;

    @Transactional(readOnly = true)
    public PostDto getPostById(Long postId) {
        log.info("[INFO] Получение поста по id: {}", postId);
        PostEntity postEntity = postLookupService.getById(postId);

        log.info("[INFO] Пост с id: {} успешно найден", postEntity.getId());
        return postMapper.toDto(postEntity);
    }

    @Transactional
    public Long submitPost(NewPostDto newPostDto, Long authorId) {
        log.info("[INFO] Создание нового поста от автора с id: {}", authorId);

        PostEntity postEntity = postMapper.toEntity(newPostDto);

        postEntity.setAuthorId(authorId);
        postEntity.setCreatedAt(LocalDateTime.now());

        // это удалить нужно будет
        postEntity.setPublishAt(LocalDateTime.now());

        postEntity.setStatusPost(ModerationStatusPost.PENDING);
        postEntity.setViewSet(new HashSet<>());
        postRepository.save(postEntity);

        log.info("[INFO] Пост успешно создан, id: {}, автор id: {}", postEntity.getId(), authorId);
        return postEntity.getId();
    }

    @Transactional
    public PostDto updatePost(Long postId, UpdatePostDto updatePostDto, Long userId) {
        log.info("[INFO] Обновление поста с id: {} пользователем с id: {}", postId, userId);

        PostEntity postEntity = postLookupService.getById(postId);

        postAccessValidator.validateAuthor(postEntity, userId);

        if (postEntity.getStatusPost().equals(ModerationStatusPost.REMOVED)) {
            log.warn("[ERROR] Попытка изменить удалённый пост id: {}", postEntity.getId());
            throw new IllegalArgumentException("Нельзя изменить удаленный пост.");
        }

        postEntity.setContent(updatePostDto.getContent());
        //Эту дату перенести к админу
        postEntity.setUpdatedAt(LocalDateTime.now());
        postEntity.setStatusPost(ModerationStatusPost.PENDING);

        log.info("[INFO] Пост с id: {} успешно обновлён и отправлен на модерацию", postEntity.getId());
        return postMapper.toDto(postEntity);
    }

    @Transactional
    public String deletePost(Long userId, Long postId) {

        log.info("[INFO] Удаление поста с id: {} пользователем с id: {}", postId, userId);

        PostEntity postEntity = postLookupService.getById(postId);

        postAccessValidator.validateAuthor(postEntity, userId);

        postEntity.setStatusPost(ModerationStatusPost.REMOVED);
        return postMapper.toDto(postEntity)
                .getStatusPost()
                .toString();
    }

    @Transactional
    public PostDto getPostByIdForUser(Long userId, Long postId) {
        log.info("Пользователь {} запрашивает пост {}", userId, postId);

        PostEntity postEntity = postLookupService.getById(postId);

        // ✅ ЛОГИКА просмотров:
        if (postEntity.getStatusPost() == ModerationStatusPost.PUBLISHED) {
            // ✅ ВСЕГДА сохраняем просмотр (даже для автора!)
            if (postEntity.getViewSet() == null) {
                postEntity.setViewSet(new HashSet<>());
            }
            postEntity.getViewSet().add(userId);
            log.info("✅ View сохранен: user={} post={}", userId, postId);
        }

        return postMapper.toDto(postEntity);
    }


    @Transactional(readOnly = true)
    public List<PostDto> getUserPosts(Long userId) {
        log.info("[INFO] Получение списка постов для пользователя с id: {}", userId);

        List<PostEntity> allPostsByAuthorId = postRepository.findAllByAuthorId(userId);

        List<PostDto> postDtoList = allPostsByAuthorId.stream()
                .filter(
                        postEntity -> postEntity.getStatusPost().equals(ModerationStatusPost.PUBLISHED)
                ).map(
                        postMapper::toDto
                ).sorted(
                        Comparator.comparing(PostDto::getPublishAt)
                ).toList();

        log.info("[INFO] Для пользователя с id: {} найдено опубликованных постов: {}",
                userId, postDtoList.size());
        return postDtoList;
    }

    @Transactional(readOnly = true)
    public List<PostDto> findUserPostsByStatus(
            Long authorId,
            List<ModerationStatusPost> moderationStatus,
            int page,
            int size
    ) {
        //Это планируется, что автор постов сможет смотреть свои посты по статусам
        log.info("[INFO] Поиск постов автора id: {} по статусам: {}, страница: {}, размер: {}",
                authorId, moderationStatus, page, size);

        Pageable pageable = PageRequest.of(page, size, Sort.by("publishAt").descending());
        Specification<PostEntity> spec = PostStatusSpecification
                .filter(moderationStatus)
                .and(PostStatusSpecification.byAuthor(authorId));

        Page<PostEntity> postEntities = postRepository.findAll(spec, pageable);

        List<PostDto> postDtoList = postEntities.map(postMapper::toDto).toList();
        log.info("[INFO] Поиск постов по статусам завершён. Найдено записей: {}", postDtoList.size());
        return postDtoList;
    }

    @Transactional(readOnly = true)
    public Long getViewsCount(Long postId) {
        log.info("[INFO] Получение количества просмотров поста: {}", postId);
        PostEntity post = postLookupService.getById(postId);
        Long count = post.getViewSet() != null ? (long) post.getViewSet().size() : 0L;
        log.info("[INFO] Пост {} имеет {} просмотров", postId, count);
        return count;
    }

    @Transactional(readOnly = true)
    public List<Long> getPostViews(Long postId) {
        log.info("[INFO] Получение списка просмотров поста: {}", postId);
        PostEntity post = postLookupService.getById(postId);
        List<Long> views = post.getViewSet() != null ? new ArrayList<>(post.getViewSet()) : List.of();
        log.info("[INFO] Пост {} просмотрели {} пользователей", postId, views.size());
        return views;
    }
}