package org.ramazanmamyrbek.entity;

import lombok.*;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
@ToString(exclude = "students")
@NoArgsConstructor
@AllArgsConstructor
public class Course {
    private Long id;
    private String courseName;
    private Teacher teacher;
    private List<Student> students;
}