package org.onelab.common.exception;

public class BadRequestException extends RuntimeException{
    public BadRequestException(String message) {
        super(message);
    }


    public static BadRequestException teacherAlreadyHaveCourseException(Long userId, Long courseId) {
        return new BadRequestException("Teacher with id %s already have course with id %s".formatted(userId, courseId));
    }

    public static BadRequestException errorWhileSettingTeacherToCourse(String message) {
        return new BadRequestException(message);
    }

    public static BadRequestException errorWhileGettingCourse(String message) {
        return new BadRequestException(message);
    }

    public static BadRequestException errorWhileGettingCourses(String message) {
        return new BadRequestException(message);
    }
}
