package org.example.likepostmodule.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.likepostmodule.dto.LikePostDto;
import org.example.likepostmodule.entity.LikePostEntity;
import org.example.likepostmodule.entity.LikeStatus;
import org.example.likepostmodule.mapper.LikePostMapper;
import org.example.likepostmodule.repository.LikePostRepository;
import org.example.likepostmodule.util.LikePostLookupService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class LikePostService {
    private final LikePostRepository likeRepository;
    private final LikePostMapper likeMapper;

    @Transactional(readOnly = true)
    public List<LikePostDto> getLikesByPostId(Long postId) {
        log.info("[INFO] Получение лайков для поста с id: {}", postId);
        List<LikePostEntity> allLikes = likeRepository.findAllActiveByPostId(postId);

        List<LikePostDto> likePostDtos = allLikes.stream()
                .map(likeMapper::toDto)
                .toList();
        log.info("[INFO] Для поста с id: {} найдено активных лайков: {}", postId, likePostDtos.size());
        return likePostDtos;
    }

    @Transactional
    public String toggleLike(Long postId, Long authorId) {
        log.info("[INFO] Лайк-дизлайк для поста id: {} пользователем id: {}", postId, authorId);

        Optional<LikePostEntity> optionalLike = likeRepository.findByPostIdAndAuthorId(postId, authorId);

        if (optionalLike.isPresent()) {
            LikePostEntity likeEntity = optionalLike.get();
            log.info("[INFO] Найден существующий лайк id: {} со статусом: {}",
                    likeEntity.getId(), likeEntity.getLikeStatus());

            if (likeEntity.getLikeStatus() == LikeStatus.ACTIVE) {
                likeEntity.setLikeStatus(LikeStatus.NO_ACTIVE);
                log.info("[INFO] Лайк id: {} переключен в статус NO_ACTIVE", likeEntity.getId());
            } else {
                likeEntity.setLikeStatus(LikeStatus.ACTIVE);
                log.info("[INFO] Лайк id: {} переключен в статус ACTIVE", likeEntity.getId());
            }
            likeRepository.save(likeEntity);
            log.info("[INFO] СОХРАНЕНО likeEntity id: {}", likeEntity.getId());

            return likeEntity.getLikeStatus().toString();
        } else {
            log.info("[INFO] Лайк для поста id: {} и пользователя id: {} не найден. Создаем новый.",
                    postId, authorId);
            LikePostEntity likePostEntity = new LikePostEntity();

            likePostEntity.setPostId(postId);
            likePostEntity.setAuthorId(authorId);
            likePostEntity.setLikeStatus(LikeStatus.ACTIVE);

            LikePostEntity saved = likeRepository.save(likePostEntity);
            log.info("💾 СОХРАНЁН НОВЫЙ like id: {}", saved.getId());

            return likePostEntity.getLikeStatus().toString();
        }
    }

    @Transactional(readOnly = true)
    public Long countActiveLikesByPostId(Long postId) {
        return likeRepository.countActiveLikesByPostId(postId);
    }

    public boolean isLikedByUser(Long postId, Long userId) {
        log.info("🔍 Проверяем лайк postId={} userId={}", postId, userId);
        boolean result = likeRepository.existsByPostIdAndAuthorIdAndLikeStatus(postId, userId, LikeStatus.ACTIVE);
        log.info("✅ isLikedByUser postId={} userId={} → {}", postId, userId, result);
        return result;
    }
}
