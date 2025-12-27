package org.example.likepostmodule.util;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.common.dto.PostDto;
import org.example.common.dto.RequestData;
import org.example.common.dto.UserDto;
import org.example.httpcore.httpCore.IHttpCore;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import static java.util.Objects.isNull;

@Service
@RequiredArgsConstructor
public class LikePostLookupService {

    private final IHttpCore iHttpCore;

    public Long getUserFromApi(Long authorId) {
        RequestData requestData = new RequestData(
                "http://localhost:8080/api/v1/social/users/post/%s"
                        .formatted(authorId),
                null
        );

        ResponseEntity<UserDto> userDtoResponseEntity =
                iHttpCore.get(requestData, null, UserDto.class);

        if (isNull(userDtoResponseEntity.getBody())) {
            throw new EntityNotFoundException("Пользователь по GET запросу не найден!.");
        }
        return userDtoResponseEntity.getBody().getUserId();
    }

    public Long getPostFromApi(Long postId) {
        RequestData requestData = new RequestData(
                "http://localhost:8082/api/v1/social/posts/id/%s".formatted(postId),
                null
        );

        ResponseEntity<PostDto> postDtoResponseEntity = iHttpCore.get(requestData, null, PostDto.class);

        if (isNull(postDtoResponseEntity.getBody())) {
            throw new EntityNotFoundException("Пост по GET запросу не найден!.");
        }

        PostDto body = postDtoResponseEntity.getBody();
        Long postIdFromApi = body.getPostId();

        if (postIdFromApi == null) {
            throw new IllegalStateException("postIdFromApi == null");
        }

        return postIdFromApi;
    }
}
