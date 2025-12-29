package org.example.livechatmodule.client;

import lombok.RequiredArgsConstructor;
import org.example.common.dto.PostDto;
import org.example.common.dto.RequestData;
import org.example.httpcore.httpCore.SecuredHttpCore;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PostClient {

    private final SecuredHttpCore http;  // сюда уже подставляется JWT

    private static final String BASE_URL = "http://localhost:8082/api/v1/social/posts";

    public List<PostDto> getUserPosts(Long userId) {
        RequestData rd = new RequestData(
                BASE_URL + "/user/" + userId,
                null
        );
        ResponseEntity<PostDto[]> resp = http.get(rd, PostDto[].class);
        PostDto[] body = resp.getBody();
        return body == null ? List.of() : Arrays.asList(body);
    }
}
