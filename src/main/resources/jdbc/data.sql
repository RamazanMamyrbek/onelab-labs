INSERT INTO teacher (name) VALUES ('John Smith');
INSERT INTO teacher (name) VALUES ('Emily Johnson');

INSERT INTO course (course_name, teacher_id) VALUES ('Mathematics', 1);
INSERT INTO course (course_name, teacher_id) VALUES ('Physics', 1);
INSERT INTO course (course_name, teacher_id) VALUES ('Chemistry', 2);

INSERT INTO student (name) VALUES ('Alice Brown');
INSERT INTO student (name) VALUES ('Bob Green');
INSERT INTO student (name) VALUES ('Charlie White');

INSERT INTO student_course (student_id, course_id) VALUES (1, 1);
INSERT INTO student_course (student_id, course_id) VALUES (1, 2);
INSERT INTO student_course (student_id, course_id) VALUES (2, 1);
INSERT INTO student_course (student_id, course_id) VALUES (3, 2);
INSERT INTO student_course (student_id, course_id) VALUES (3, 3);