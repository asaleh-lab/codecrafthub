package com.codecrafthub.exception;

/**
 * Thrown when reading from or writing to courses.json fails.
 * The global handler converts this to a 500 Internal Server Error response.
 */
public class CourseStorageException extends RuntimeException {

    public CourseStorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
