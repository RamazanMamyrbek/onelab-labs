package com.onelab.courses_service.consumer;

import com.onelab.courses_service.entity.Lesson;
import com.onelab.courses_service.service.CourseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.onelab.common.dto.request.*;
import org.onelab.common.dto.response.*;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class CourseConsumer {
    private final CourseService courseService;

    @KafkaListener(topics = "${kafka.topics.course.request.getAllCourses}", id = "consumeGetAllCourses")
    @SendTo
    public FindAllCoursesWrapper consumeGetAllCourses() {
        log.info("Received request to get all courses");
        return new FindAllCoursesWrapper(courseService.getAllCourses());
    }

    @KafkaListener(topics = "${kafka.topics.course.request.getCoursesByTeacher}", id = "consumeGetAllCoursesByTeacher")
    @SendTo
    public FindAllCoursesWrapper consumeGetAllCoursesByTeacher(@Payload Long teacherId) {
        log.info("Received request to get all courses");
        return new FindAllCoursesWrapper(courseService.getAllCoursesByTeacher(teacherId));
    }

    @KafkaListener(topics = "${kafka.topics.course.request.getLessonsForCourse}", id = "consumeGetLessonsForCourse")
    @SendTo
    public Object consumeGetLessonsForCourse(@Payload Long courseId) {
        try {
            log.info("Received request to get lessons for course: {}", courseId);
            return new FindAllLessonsWrapper(courseService.getLessonsForCourse(courseId));
        } catch (Exception e) {
            log.error("Error getting lessons for course {}: {}", courseId, e.getMessage(), e);
            return new ErrorResponse("Failed to get lessons: " + e.getMessage());
        }
    }

    @KafkaListener(topics = "${kafka.topics.course.request.createCourse}", id = "consumeCreateCourse")
    @SendTo
    public Object consumeCreateCourse(@Payload CourseRequestDto requestDto) {
        try {
            log.info("Received create course request: {}", requestDto);
            return courseService.createCourse(requestDto);
        } catch (Exception e) {
            log.error("Error while creating a course {}: {}", e.getMessage(), e);
            return new ErrorResponse("Error while creating a course: " + e.getMessage());
        }
    }

    @KafkaListener(topics = "${kafka.topics.course.request.updateCourse}", id = "consumeUpdateCourse")
    @SendTo
    public Object consumeUpdateCourse(@Payload CourseUpdateRequestDto requestDto) {
        try {
            log.info("Received update course request: {}", requestDto);
            return courseService.updateCourse(requestDto);
        } catch (Exception e) {
            log.error("Error while updating course {}: {}", e.getMessage(), e);
            return new ErrorResponse("Error while updating course: " + e.getMessage());
        }
    }

    @KafkaListener(topics = "${kafka.topics.course.request.deleteCourse}", id = "consumeDeleteCourse")
    @SendTo
    public Object consumeDeleteCourse(@Payload CourseDeleteRequestDto requestDto) {
        try {
            log.info("Received delete course request: {}", requestDto);
            courseService.deleteCourse(requestDto);
            return "Course was deleted successfully";
        } catch (Exception e) {
            log.error("Error while deleting a course {}: {}", e.getMessage(), e);
            return new ErrorResponse("Failed to delete a course: " + e.getMessage());
        }
    }

    @KafkaListener(topics = "${kafka.topics.course.request.addLessonToCourse}", id = "consumeAddLesson")
    @SendTo
    public Object consumeAddLesson(@Payload LessonRequestDto requestDto) {
        try {
            log.info("Received add lesson request: {}", requestDto);
            return courseService.addLessonToCourse(requestDto);
        } catch (Exception e) {
            log.error("Error while creating a lesson {}: {}", e.getMessage(), e);
            return new ErrorResponse("Failed to create a lesson: " + e.getMessage());
        }
    }

    @KafkaListener(topics = "${kafka.topics.course.request.updateLesson}", id = "consumeUpdateLesson")
    @SendTo
    public Object consumeUpdateLesson(@Payload LessonUpdateRequestDto requestDto) {
        try {
            log.info("Received update lesson request: {}", requestDto);
            return courseService.updateLesson(requestDto);
        } catch (Exception e) {
            log.error("Error while updating a lesson {}: {}", e.getMessage(), e);
            return new ErrorResponse("Failed to update a lesson: " + e.getMessage());
        }
    }

    @KafkaListener(topics = "${kafka.topics.course.request.deleteLesson}", id = "consumeDeleteLesson")
    @SendTo
    public Object consumeDeleteLesson(@Payload LessonDeleteRequestDto requestDto) {
        try {
            log.info("Received delete lesson request: {}", requestDto);
            courseService.deleteLesson(requestDto);
            return "Lesson was deleted";
        } catch (Exception e) {
            log.error("Error while deleting a lesson {}: {}", e.getMessage(), e);
            return new ErrorResponse("Failed to delete a lesson: " + e.getMessage());
        }
    }

    @KafkaListener(topics = "${kafka.topics.course.request.setTeacher}", id = "consumeSetTeacher")
    @SendTo
    public Object consumeSetTeacher(@Payload AssignCourseDto requestDto) {
        try {
            log.info("Received set teacher request: {}", requestDto);
            return courseService.setTeacher(requestDto);
        } catch (Exception e) {
            log.error("Error while setting a teacher to a course {}: {}", e.getMessage(), e);
            return new ErrorResponse("Error while setting a teacher to a course: " + e.getMessage());
        }
    }

    @KafkaListener(topics = "${kafka.topics.course.request.getCourse}", id = "consumeGetCourse")
    @SendTo
    public Object consumeGetCourse(@Payload Long courseId) {
        try {
            log.info("Received get course request: {}", courseId);
            return courseService.getCourse(courseId);
        } catch (Exception e) {
            log.error("Error while getting a course {}: {}", e.getMessage(), e);
            return new ErrorResponse("Error while getting a course: " + e.getMessage());
        }
    }

    @KafkaListener(topics = "${kafka.topics.course.request.findAllById}", id = "consumeGetAllCoursesById")
    @SendTo
    public Object consumeFindAllById(@Payload FindAllCoursesSetWrapper request) {
        try {
            return new FindAllCoursesWrapper(courseService.findAllById(request.set()));
        } catch (Exception e) {
            log.error("Error while getting a courses {}: {}", e.getMessage(), e);
            return new ErrorResponse("Error while getting a courses: " + e.getMessage());
        }
    }
}

