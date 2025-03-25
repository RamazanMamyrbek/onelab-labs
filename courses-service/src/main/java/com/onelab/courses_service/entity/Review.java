package com.onelab.courses_service.entity;

import jakarta.persistence.*;
import lombok.*;
import org.onelab.common.enums.ReviewStatus;

import java.time.LocalDateTime;

@Entity
@Table(name = "reviews", uniqueConstraints = @UniqueConstraint(columnNames = {"course_id", "user_id"}))
@Getter
@Setter
@ToString
@EqualsAndHashCode(of = {"id"})
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long rating;

    @Column(columnDefinition = "TEXT")
    private String text;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    @Column(name = "user_id")
    private Long userId;

    @Enumerated(EnumType.STRING)
    private ReviewStatus reviewStatus;
}
