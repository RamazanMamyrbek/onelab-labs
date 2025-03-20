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
}
