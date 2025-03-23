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

    public static BadRequestException userAlreadyExistsException(String email) {
        return new BadRequestException("Email %s is already exists".formatted(email));
    }

    public static BadRequestException invalidTeacherException(Long userId, Long courseId) {
        return new BadRequestException("Teacher with id %s is not owner of course with id %s".formatted(userId, courseId));
    }

    public static BadRequestException accessDeniedForCourse(Long userId, Long courseId) {
        return new BadRequestException("User with id %s has no access to course with id %s".formatted(userId, courseId));
    }

    public static BadRequestException reviewExistsByCourseAndUser(Long courseId, Long userId) {
        return new BadRequestException("User with id %s already left review for course with id %s".formatted(userId, courseId));
    }

    public static BadRequestException accessDeniedForReview(Long userId, Long reviewId) {
        return new BadRequestException("User with id %s is not owner for review with id %s".formatted(userId, reviewId));
    }

    public static BadRequestException invalidConfirmationCode() {
        return new BadRequestException("Invalid confirmation code");
    }
}
