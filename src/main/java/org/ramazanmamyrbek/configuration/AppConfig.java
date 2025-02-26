package org.ramazanmamyrbek.configuration;

import org.ramazanmamyrbek.repository.CourseRepository;
import org.ramazanmamyrbek.repository.StudentRepository;
import org.ramazanmamyrbek.repository.TeacherRepository;
import org.ramazanmamyrbek.repository.impl.CourseRepositoryImpl;
import org.ramazanmamyrbek.repository.impl.StudentRepositoryImpl;
import org.ramazanmamyrbek.repository.impl.TeacherRepositoryImpl;
import org.ramazanmamyrbek.services.SchoolService;
import org.ramazanmamyrbek.services.impl.SchoolServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public StudentRepository studentRepository() {
        return new StudentRepositoryImpl();
    }

    @Bean
    public CourseRepository courseRepository() {
        return new CourseRepositoryImpl();
    }

    @Bean
    public TeacherRepository teacherRepository() {
        return new TeacherRepositoryImpl();
    }

    @Bean
    public SchoolService schoolService(StudentRepository studentRepository,
                                                CourseRepository courseRepository,
                                                TeacherRepository teacherRepository) {
        return new SchoolServiceImpl(studentRepository, courseRepository, teacherRepository);
    }
}
