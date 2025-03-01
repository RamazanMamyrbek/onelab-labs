CREATE TABLE teacher (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         name VARCHAR(255)
);

CREATE TABLE course (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        course_name VARCHAR(255),
                        teacher_id BIGINT,
                        FOREIGN KEY (teacher_id) REFERENCES teacher(id)
);

CREATE TABLE student (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         name VARCHAR(255)
);

CREATE TABLE student_course (
                                student_id BIGINT,
                                course_id BIGINT,
                                FOREIGN KEY (student_id) REFERENCES student(id),
                                FOREIGN KEY (course_id) REFERENCES course(id)
);