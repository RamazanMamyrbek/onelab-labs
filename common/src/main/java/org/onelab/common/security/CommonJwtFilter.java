package org.onelab.common.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.onelab.common.dto.response.ErrorResponseDto;
import org.onelab.common.dto.response.UserDetailsResponseDto;
import org.onelab.common.feign.JwtFeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommonJwtFilter extends OncePerRequestFilter {
    final ObjectMapper objectMapper;
    final JwtFeignClient jwtFeignClient;
    //
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer")) {
            token = token.substring(7);
        }
        try {
            if (token != null && jwtFeignClient.validateToken(token)) {
                if (SecurityContextHolder.getContext().getAuthentication() == null) {
                    String email = jwtFeignClient.getEmail(token);
                    UserDetailsResponseDto userDetailsResponseDto = jwtFeignClient.loadUserByUsername(email);
                    UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                            userDetailsResponseDto.getUsername(),
                            "",
                            userDetailsResponseDto.isEnabled(),
                            true, true, true,
                            userDetailsResponseDto.getAuthorities().stream()
                                    .map(element -> new SimpleGrantedAuthority(element))
                                    .toList()
                    );
                    SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    ));
                }
            }
        } catch (FeignException ex) {
            ErrorResponseDto errorResponseDto = new ErrorResponseDto(
                    request.getPathInfo(),
                    HttpStatus.BAD_REQUEST,
                    "Invalid JWT",
                    LocalDateTime.now()
            );
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write(objectMapper.writeValueAsString(errorResponseDto));
            return;
        }
        filterChain.doFilter(request, response);
    }
}
