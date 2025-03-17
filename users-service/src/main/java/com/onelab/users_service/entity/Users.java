package com.onelab.users_service.entity;

import jakarta.persistence.*;
import lombok.*;
import org.onelab.common.enums.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
@ToString
@EqualsAndHashCode(of = {"id"})
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Users implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    private String passwordHash;

    private String name;

    private String country;

    private Long age;

    @Enumerated(EnumType.STRING)
    private Role role;

    @ElementCollection
    @CollectionTable(name = "user_courses", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "course_id")
    @Builder.Default
    private Set<Long> courseIds = new LinkedHashSet<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getPassword() {
        return passwordHash;
    }

    @Override
    public String getUsername() {
        return email;
    }
}
