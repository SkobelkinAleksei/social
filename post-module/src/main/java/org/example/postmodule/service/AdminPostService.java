package org.example.postmodule.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.postmodule.dto.PostDto;
import org.example.postmodule.dto.PostFullDto;
import org.example.postmodule.entity.ModerationStatusPost;
import org.example.postmodule.entity.PostEntity;
import org.example.postmodule.mapper.PostMapper;
import org.example.postmodule.repository.PostRepository;
import org.example.postmodule.util.PostLookupService;
import org.example.postmodule.util.PostStatusSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class AdminPostService {
    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final PostLookupService postLookupService;

    @Transactional(readOnly = true)
    public List<PostDto> findUserPostsByStatus(
            Long authorId,
            List<ModerationStatusPost> moderationStatus,
            int page,
            int size
    ) {
        log.info("[INFO] Админ запрашивает посты автора id: {} по статусам: {}, страница: {}, размер: {}",
                authorId, moderationStatus, page, size);

        Pageable pageable = PageRequest.of(page, size, Sort.by("publishAt").descending());
        Specification<PostEntity> spec = PostStatusSpecification.filter(moderationStatus)
                .and(PostStatusSpecification.byAuthor(authorId));
        Page<PostEntity> postEntities = postRepository.findAll(spec, pageable);

        List<PostDto> postDtoList = postEntities.map(postMapper::toDto).toList();
        log.info("[INFO] Найдено постов автора id: {} с указанными статусами: {}", authorId, postDtoList.size());
        return postDtoList;
    }

    @Transactional(readOnly = true)
    public PostFullDto getPostById(Long postId) {
        log.info("[INFO] Админ запрашивает полный пост по id: {}", postId);

        PostEntity postEntity = postLookupService.getById(postId);

        log.info("[INFO] Пост с id: {} успешно найден", postId);
        return postMapper.toFullDto(postEntity);
    }

    @Transactional
    public PostDto updateStatusPost(Long postId, ModerationStatusPost moderationStatus) {
        log.info("[INFO] Админ изменяет статус поста id: {} на {}", postId, moderationStatus);

        PostEntity postEntity = postLookupService.getById(postId);

        if (postEntity.getStatusPost().equals(moderationStatus)) {
            log.info("[INFO] Новый статус поста id: {} совпадает с текущим: {}", postId, moderationStatus);
            return postMapper.toDto(postEntity);
        }

        postEntity.setStatusPost(moderationStatus);

        log.info("[INFO] Статус поста id: {} успешно изменён на {}", postId, moderationStatus);
        return postMapper.toDto(postEntity);
    }

    @Transactional(readOnly = true)
    public List<PostDto> getAllPendingPost(int page, int size) {
        log.info("[INFO] Админ запрашивает все посты в статусе PENDING, страница: {}, размер: {}", page, size);
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<PostEntity> postEntityPage = postRepository.findAllByStatusPost(ModerationStatusPost.PENDING, pageable);

        List<PostDto> postDtoList = postEntityPage.map(postMapper::toDto).toList();
        log.info("[INFO] Найдено постов в статусе PENDING: {}", postDtoList.size());
        return postDtoList;
    }

    @Transactional
    public void deletePostId(Long postId) {
        log.info("[INFO] Админ удаляет пост с id: {}", postId);
        postRepository.deleteById(postId);
        log.info("[INFO] Пост с id: {} успешно удалён", postId);
    }
}
