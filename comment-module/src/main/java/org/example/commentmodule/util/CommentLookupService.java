package org.example.commentmodule.util;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.common.dto.post.PostDto;
import org.example.common.dto.RequestData;
import org.example.common.dto.user.UserDto;
import org.example.common.dto.user.UserFullDto;
import org.example.httpcore.httpCore.SecuredHttpCore;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import static java.util.Objects.isNull;

@Service
@RequiredArgsConstructor
public class CommentLookupService {

    private final SecuredHttpCore iHttpCore;

    public Long getUserIdFromApi(Long authorId) {
        RequestData requestData = new RequestData(
                "http://localhost:8080/api/v1/social/users/post/%s"
                        .formatted(authorId),
                null
        );

        ResponseEntity<UserDto> userDtoResponseEntity =
                iHttpCore.get(requestData, UserDto.class);

        if (isNull(userDtoResponseEntity.getBody())) {
            throw new EntityNotFoundException("Пользователь по GET запросу не найден!.");
        }
        return userDtoResponseEntity.getBody().getUserId();
    }

    public UserFullDto getUserDtoFromApi(Long adminId) {
        RequestData requestData = new RequestData(
                "http://localhost:8080/social/v1/admin/users/%s/profile-user"
                        .formatted(adminId),
                null
        );

        ResponseEntity<UserFullDto> userDtoResponseEntity =
                iHttpCore.get(requestData, UserFullDto.class);

        if (isNull(userDtoResponseEntity.getBody())) {
            throw new EntityNotFoundException("Пользователь по GET запросу не найден!.");
        }
        return userDtoResponseEntity.getBody();
    }

    public Long getPostIdFromApi(Long postId) {
        RequestData requestData = new RequestData(
                "http://localhost:8082/api/v1/social/posts/id/%s".formatted(postId),
                null
        );

        ResponseEntity<PostDto> postDtoResponseEntity = iHttpCore.get(requestData, PostDto.class);

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

    public PostDto getPostDtoFromApi(Long postId) {
        RequestData requestData = new RequestData(
                "http://localhost:8082/api/v1/social/posts/id/%s".formatted(postId),
                null
        );

        ResponseEntity<PostDto> postDtoResponseEntity = iHttpCore.get(requestData, PostDto.class);

        if (isNull(postDtoResponseEntity.getBody())) {
            throw new EntityNotFoundException("Пост по GET запросу не найден!.");
        }

        return postDtoResponseEntity.getBody();
    }
}
