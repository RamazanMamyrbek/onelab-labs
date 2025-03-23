package com.onelab.users_service.entity;

import jakarta.persistence.*;
import lombok.*;
import org.onelab.common.enums.Role;

@Entity
@Table(name = "pending_users")
@Getter
@Setter
@ToString
@EqualsAndHashCode(of = {"id"})
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PendingUser {
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

    private String code;
}
