package org.example.livechatmodule.client;

import lombok.RequiredArgsConstructor;
import org.example.common.dto.*;
import org.example.common.dto.auth.LoginResponse;
import org.example.common.dto.auth.LoginUserDto;
import org.example.common.dto.auth.RegistrationUserDto;
import org.example.common.dto.user.UserDto;
import org.example.common.security.JwtHolder;
import org.example.httpcore.httpCore.IHttpCore;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthClient {

    private final IHttpCore httpCore;
    private final JwtHolder jwtHolder;

    private static final String BASE_URL = "http://localhost:8080/social/public/v1/auth";

    public UserDto signUp(RegistrationUserDto dto) {
        RequestData request = new RequestData(BASE_URL + "/signUp", dto);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<UserDto> response =
                httpCore.post(request, headers, UserDto.class);

        return response.getBody();
    }

    public LoginResponse login(LoginUserDto dto) {
        RequestData request = new RequestData(BASE_URL + "/login", dto);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<LoginResponse> response =
                httpCore.post(request, headers, LoginResponse.class);

        LoginResponse body = response.getBody();
        jwtHolder.setToken(body.token());
        return body;
    }
}
