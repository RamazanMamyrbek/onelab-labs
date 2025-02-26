package org.ramazanmamyrbek.entity;

import lombok.*;

import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
@ToString(exclude = "courses")
@NoArgsConstructor
@AllArgsConstructor
public class Student {
    private Long id;
    private String name;
    private List<Course> courses;
}