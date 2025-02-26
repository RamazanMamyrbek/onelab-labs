package org.ramazanmamyrbek;

import org.ramazanmamyrbek.configuration.AppConfig;
import org.ramazanmamyrbek.entity.Course;
import org.ramazanmamyrbek.entity.Student;
import org.ramazanmamyrbek.entity.Teacher;
import org.ramazanmamyrbek.services.SchoolService;
import org.ramazanmamyrbek.services.impl.SchoolServiceImpl;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Arrays;

public class ProjectApplication {
    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        SchoolService schoolService = context.getBean(SchoolServiceImpl.class);


        Teacher teacher1 = new Teacher(1L, "John Smith", null);
        Teacher teacher2 = new Teacher(2L, "Emily Johnson", null);

        Course course1 = new Course(1L, "Mathematics", teacher1, null);
        Course course2 = new Course(2L, "Physics", teacher1, null);
        Course course3 = new Course(3L, "Chemistry", teacher2, null);

        teacher1.setCourses(Arrays.asList(course1, course2));
        teacher2.setCourses(Arrays.asList(course3));

        Student student1 = new Student(1L, "Alice Brown", Arrays.asList(course1, course2));
        Student student2 = new Student(2L, "Bob Green", Arrays.asList(course1));
        Student student3 = new Student(3L, "Charlie White", Arrays.asList(course2, course3));

        course1.setStudents(Arrays.asList(student1, student2));
        course2.setStudents(Arrays.asList(student1, student3));
        course3.setStudents(Arrays.asList(student3));

        schoolService.saveCourse(course1);
        schoolService.saveCourse(course2);
        schoolService.saveCourse(course3);

        schoolService.saveStudent(student1);
        schoolService.saveStudent(student2);
        schoolService.saveStudent(student3);

        schoolService.saveTeacher(teacher1);
        schoolService.saveTeacher(teacher2);

        System.out.println("All Students: " + schoolService.getAllStudents());
        System.out.println("All Courses: " + schoolService.getAllCourses());
        System.out.println("All Teachers: " + schoolService.getAllTeachers());

        System.out.println("Students in course 1: " + schoolService.getStudentNamesByCourseId(1L));
        System.out.println("Students in course 2: " + schoolService.getStudentNamesByCourseId(2L));

        System.out.println("Courses by teacher 1: " + schoolService.getCoursesByTeacherId(1L));
        System.out.println("Courses by teacher 2: " + schoolService.getCoursesByTeacherId(2L));
    }
}