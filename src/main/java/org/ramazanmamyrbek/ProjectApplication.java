package org.ramazanmamyrbek;

import org.ramazanmamyrbek.entity.Course;
import org.ramazanmamyrbek.entity.Student;
import org.ramazanmamyrbek.entity.Teacher;
import org.ramazanmamyrbek.services.CourseService;
import org.ramazanmamyrbek.services.SchoolService;
import org.ramazanmamyrbek.services.StudentService;
import org.ramazanmamyrbek.services.TeacherService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class ProjectApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(ProjectApplication.class, args);

        SchoolService schoolService = context.getBean(SchoolService.class);
        TeacherService teacherService = context.getBean(TeacherService.class);
        StudentService studentService = context.getBean(StudentService.class);
        CourseService courseService = context.getBean(CourseService.class);


        System.out.println("--------------------------");
        System.out.println("--------------------------");
        System.out.println("--НАЧИНАЕМ БИЗНЕС ЛОГИКУ--");
        System.out.println("--------------------------");
        System.out.println("--------------------------");


        // СОЗДАНИЕ УЧИТЕЛЕЙ
        Teacher teacher1 = teacherService.createTeacher("Иван Петров");
        Teacher teacher2 = teacherService.createTeacher("Мария Сидорова");

        // СОЗДАНИЕ СТУДЕНТОВ
        Student student1 = studentService.createStudent("Алексей Иванов");
        Student student2 = studentService.createStudent("Ольга Смирнова");
        Student student3 = studentService.createStudent("Дмитрий Соколов");

        // СОЗДАНИЕ КУРСОВ
        Course course1 = courseService.createCourse("Математика", teacher1.getId());
        Course course2 = courseService.createCourse("Информатика", teacher2.getId());

        // НАЗНАЧЕНИЕ УЧИТЕЛЕЙ НА КУРСЫ
        teacherService.assignCourseToTeacher(teacher1.getId(), course1.getId());
        teacherService.assignCourseToTeacher(teacher2.getId(), course2.getId());

        // ЗАПИСЬ СТУДЕНТОВ НА КУРСЫ
        schoolService.enrollStudentToCourse(student1.getId(), course1.getId());
        schoolService.enrollStudentToCourse(student2.getId(), course1.getId());
        schoolService.enrollStudentToCourse(student3.getId(), course2.getId());


        // ФОРМИРОВАНИЕ ОТЧЕТА О СОСТОЯНИИ ШКОЛЫ
        schoolService.generateReport();

        // ОЧИСТКА ДАННЫХ
        schoolService.removeAllData();

        schoolService.generateReport();

        context.close();
    }
}