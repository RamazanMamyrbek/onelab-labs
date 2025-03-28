package org.onelab.common.exception;

public class ResourceNotFoundException extends RuntimeException{

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public static ResourceNotFoundException userNotFound(Long userId) {
        return new ResourceNotFoundException("User with id %s was not found".formatted(userId));
    }

    public static ResourceNotFoundException courseNotFound(Long courseId) {
        return new ResourceNotFoundException("Course with id %s was not found".formatted(courseId));
    }

    public static ResourceNotFoundException lessonNotFound(Long lessonId) {
        return new ResourceNotFoundException("Lesson with id %s was not found".formatted(lessonId));
    }

    public static ResourceNotFoundException studentNotFound(Long id) {
        return new ResourceNotFoundException("Student with id %s was not found".formatted(id));
    }

    public static ResourceNotFoundException teacherNotFound(Long id) {
        return new ResourceNotFoundException("Teacher with id %s was not found".formatted(id));
    }

    public static ResourceNotFoundException chatNotFound(String chatId) {
        return new ResourceNotFoundException("Chat with id %s was not found".formatted(chatId));
    }

    public static ResourceNotFoundException resourceNotFoundById(Long resourceId) {
        return new ResourceNotFoundException("Resource with id %s was not found".formatted(resourceId));
    }

    public static ResourceNotFoundException reviewNotFound(Long reviewId) {
        return new ResourceNotFoundException("Review with id %s was not found".formatted(reviewId));
    }

    public static ResourceNotFoundException pendingUserNotFound(String email) {
        return new ResourceNotFoundException("Pending user with email %s was not found".formatted(email));
    }

    public static ResourceNotFoundException exchangeRateNotFound(String currencyPair) {
        return new ResourceNotFoundException("Currency pair " + currencyPair + " is not found. Try to update exchanges by invoking /api/exchange-rates");
    }
}
