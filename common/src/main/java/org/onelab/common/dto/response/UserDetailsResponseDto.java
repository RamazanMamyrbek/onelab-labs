package org.onelab.common.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class UserDetailsResponseDto {
    private String username;
    private String password;
    private boolean enabled;
    private List<String> authorities;
}
