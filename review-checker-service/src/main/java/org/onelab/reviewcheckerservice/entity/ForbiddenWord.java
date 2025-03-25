package org.onelab.reviewcheckerservice.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "forbidden_words")
@Getter
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class ForbiddenWord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String word;
}
