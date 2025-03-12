package org.onelab.common.feign;

import org.onelab.common.dto.response.UserDetailsResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "users-service", contextId = "jwtClient")
public interface JwtFeignClient {
    @GetMapping(path = "/jwt/validate")
    public boolean validateToken(@RequestParam(name = "token") String token);

    @GetMapping(path = "/jwt/load-user-by-email")
    public UserDetailsResponseDto loadUserByUsername(@RequestParam(name = "email") String email);

    @GetMapping(path = "/jwt/extract-email")
    public String getEmail(@RequestParam(name = "token") String token);
}
