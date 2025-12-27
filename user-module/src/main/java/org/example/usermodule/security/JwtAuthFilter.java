package org.example.usermodule.security;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.usermodule.entity.enums.UserEntity;
import org.example.usermodule.repository.UserRepository;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@RequiredArgsConstructor
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final UserRepository userRepository;
    private final AuthUtil authUtil;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        final String authorization = request.getHeader("Authorization");

        if (isNull(authorization) || !authorization.startsWith("Bearer ")) {
            filterChain.doFilter(
                    request,
                    response
            );
            return;
        }

        String token = authorization.split("Bearer ")[1];
        String usernameFromToken = authUtil.getUsernameFromToken(token);

        if (nonNull(usernameFromToken)
                && isNull(SecurityContextHolder.getContext().getAuthentication())
        ) {
            UserEntity userEntity = userRepository.findByEmail(usernameFromToken)
                    .orElseThrow(() -> new EntityNotFoundException(""));

            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(userEntity, null, userEntity.getAuthorities());

            authenticationToken.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request)
            );

            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }

        filterChain.doFilter(request, response);
    }
}
