package com.codecrafthub.exception;

/**
 * Thrown when a course with the given ID does not exist.
 * The global handler converts this to a 404 Not Found response.
 */
public class CourseNotFoundException extends RuntimeException {

    public CourseNotFoundException(Long id) {
        super("Course not found with id: " + id);
    }
}
