package org.example.postmodule.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.common.dto.*;
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
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import static java.util.Objects.isNull;

@Slf4j
@AllArgsConstructor
@Service
public class UserPostService {

    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final SecuredHttpCore iHttpCore;
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
        Long userId = getUserFromApi(authorId);

        PostEntity postEntity = postMapper.toEntity(newPostDto);

        postEntity.setAuthorId(userId);
        postEntity.setCreatedAt(LocalDateTime.now());

        // это удалить нужно будет
        postEntity.setPublishAt(LocalDateTime.now());

        postEntity.setStatusPost(ModerationStatusPost.PENDING);
        postEntity.setViewSet(new HashSet<>());
        postRepository.save(postEntity);

        log.info("[INFO] Пост успешно создан, id: {}, автор id: {}", postEntity.getId(), userId);
        return postEntity.getId();
    }

    @Transactional
    public PostDto updatePost(Long postId, UpdatePostDto updatePostDto, Long userId) {
        log.info("[INFO] Обновление поста с id: {} пользователем с id: {}", postId, userId);
        Long authorId = getUserFromApi(userId);

        PostEntity postEntity = postLookupService.getById(postId);

        postAccessValidator.validateAuthor(postEntity, authorId);

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
        Long authorId = getUserFromApi(userId);

        PostEntity postEntity = postLookupService.getById(postId);

        postAccessValidator.validateAuthor(postEntity, authorId);

        postEntity.setStatusPost(ModerationStatusPost.REMOVED);
        return postMapper.toDto(postEntity)
                .getStatusPost()
                .toString();
    }

    @Transactional
    public PostDto getPostByIdForUser(Long userId, Long postId) {

        log.info("[INFO] Пользователь с id: {} запрашивает пост с id: {}", userId, postId);
        Long userFromApi = getUserFromApi(userId);

        PostEntity postEntity = postLookupService.getById(postId);

        if (postEntity.getStatusPost().equals(ModerationStatusPost.PUBLISHED)
                && !postEntity.getAuthorId().equals(userFromApi)) {

            log.info("[INFO] Пост id: {} просмотрен пользователем id: {}", postEntity.getId(), userFromApi);
            postEntity.getViewSet().add(userFromApi);
            return postMapper.toDto(postEntity);

        } else if (postEntity.getAuthorId().equals(userFromApi)) {
            log.info("[INFO] Автор поста id: {} просматривает свой пост id: {}", userFromApi, postEntity.getId());
            return postMapper.toDto(postEntity);
        } else {
            log.warn("[ERROR] Пост id: {} недоступен пользователю id: {}", postEntity.getId(), userFromApi);
            throw new IllegalArgumentException("Данный пост не доступен!");
        }
    }

    @Transactional(readOnly = true)
    public List<PostDto> getUserPosts(Long userId) {
        log.info("[INFO] Получение списка постов для пользователя с id: {}", userId);
        Long userFromApi = getUserFromApi(userId);

        List<PostEntity> allPostsByAuthorId = postRepository.findAllByAuthorId(userFromApi);

        List<PostDto> postDtoList = allPostsByAuthorId.stream()
                .filter(
                        postEntity -> postEntity.getStatusPost().equals(ModerationStatusPost.PUBLISHED)
                ).map(
                        postMapper::toDto
                ).sorted(
                        Comparator.comparing(PostDto::getPublishAt)
                ).toList();

        log.info("[INFO] Для пользователя с id: {} найдено опубликованных постов: {}",
                userFromApi, postDtoList.size());
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
        Long userFromApi = getUserFromApi(authorId);

        Pageable pageable = PageRequest.of(page, size, Sort.by("publishAt").descending());
        Specification<PostEntity> spec = PostStatusSpecification
                .filter(moderationStatus)
                .and(PostStatusSpecification.byAuthor(userFromApi));

        Page<PostEntity> postEntities = postRepository.findAll(spec, pageable);

        List<PostDto> postDtoList = postEntities.map(postMapper::toDto).toList();
        log.info("[INFO] Поиск постов по статусам завершён. Найдено записей: {}", postDtoList.size());
        return postDtoList;
    }

    protected Long getUserFromApi(Long authorId) {
        log.info("[INFO] Запрос данных пользователя по id: {} во внешний сервис", authorId);
        RequestData requestData = new RequestData(
                "http://localhost:8080/api/v1/social/users/post/%s"
                        .formatted(authorId),
                null
        );

        ResponseEntity<UserDto> userDtoResponseEntity =
                iHttpCore.get(requestData, UserDto.class);

        if (isNull(userDtoResponseEntity.getBody())) {
            log.warn("[INFO] Пользователь с id: {} не найден во внешнем сервисе", authorId);
            throw new EntityNotFoundException("Пользователь по GET запросу не найден!.");
        }

        Long userId = userDtoResponseEntity.getBody().getUserId();
        log.info("[INFO] Пользователь из внешнего сервиса найден, id: {}", userId);

        return userId;
    }
}






































