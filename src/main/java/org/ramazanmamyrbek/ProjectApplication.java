package org.ramazanmamyrbek;

import org.ramazanmamyrbek.configuration.AppConfig;
import org.ramazanmamyrbek.services.SchoolService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class ProjectApplication {
    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        SchoolService schoolService = context.getBean(SchoolService.class);

        System.out.println("All Students: " + schoolService.getAllStudents());
        System.out.println("All Courses: " + schoolService.getAllCourses());
        System.out.println("All Teachers: " + schoolService.getAllTeachers());
        System.out.println("Students in course 1: " + schoolService.getStudentNamesByCourseId(1L));
        System.out.println("Courses by teacher 1: " + schoolService.getCoursesByTeacherId(1L));
    }
}