package com.codecrafthub.service;

import com.codecrafthub.exception.CourseNotFoundException;
import com.codecrafthub.exception.CourseStorageException;
import com.codecrafthub.model.Course;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Business logic layer for courses.
 * Reads and writes all course data to a JSON file using Jackson.
 */
@Service
public class CourseService {

    private final ObjectMapper objectMapper;
    private final Path filePath;

    public CourseService(@Value("${codecrafthub.courses.file}") String filePath) {
        this.filePath = Paths.get(filePath).toAbsolutePath().normalize();

        // Configure Jackson to handle Java 8 date/time types (LocalDate, LocalDateTime)
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    /**
     * Runs once at startup — creates courses.json with an empty array if it does not exist.
     */
    @PostConstruct
    public void initializeStorageFile() {
        try {
            if (!Files.exists(filePath)) {
                Files.createDirectories(filePath.getParent() != null ? filePath.getParent() : Paths.get("."));
                objectMapper.writerWithDefaultPrettyPrinter().writeValue(filePath.toFile(), new ArrayList<Course>());
            }
        } catch (IOException ex) {
            throw new CourseStorageException("Failed to initialize courses.json at " + filePath, ex);
        }
    }

    /** Returns every course stored in the JSON file. */
    public List<Course> getAllCourses() {
        return readCoursesFromFile();
    }

    /** Finds a single course by ID, or throws CourseNotFoundException. */
    public Course getCourseById(Long id) {
        return findCourseIndex(id)
                .map(index -> readCoursesFromFile().get(index))
                .orElseThrow(() -> new CourseNotFoundException(id));
    }

    /**
     * Creates a new course with an auto-generated id and created_at timestamp.
     * Appends it to the JSON file.
     */
    public Course createCourse(Course course) {
        List<Course> courses = readCoursesFromFile();

        course.setId(generateNextId(courses));
        course.setCreatedAt(LocalDateTime.now());

        courses.add(course);
        writeCoursesToFile(courses);

        return course;
    }

    /**
     * Updates an existing course while preserving its original id and created_at.
     */
    public Course updateCourse(Long id, Course updatedCourse) {
        List<Course> courses = readCoursesFromFile();

        Optional<Integer> indexOpt = findCourseIndex(id, courses);
        if (indexOpt.isEmpty()) {
            throw new CourseNotFoundException(id);
        }

        int index = indexOpt.get();
        Course existing = courses.get(index);

        existing.setName(updatedCourse.getName());
        existing.setDescription(updatedCourse.getDescription());
        existing.setTargetDate(updatedCourse.getTargetDate());
        existing.setStatus(updatedCourse.getStatus());
        // id and createdAt stay unchanged

        writeCoursesToFile(courses);
        return existing;
    }

    /** Removes a course from the JSON file by ID. */
    public void deleteCourse(Long id) {
        List<Course> courses = readCoursesFromFile();

        Optional<Integer> indexOpt = findCourseIndex(id, courses);
        if (indexOpt.isEmpty()) {
            throw new CourseNotFoundException(id);
        }

        courses.remove((int) indexOpt.get());
        writeCoursesToFile(courses);
    }

    // --- Private helper methods for file I/O ---

    /** Reads the full course list from courses.json. */
    private List<Course> readCoursesFromFile() {
        try {
            if (!Files.exists(filePath)) {
                return new ArrayList<>();
            }
            return objectMapper.readValue(filePath.toFile(), new TypeReference<List<Course>>() {});
        } catch (IOException ex) {
            throw new CourseStorageException("Failed to read courses from " + filePath, ex);
        }
    }

    /** Writes the full course list back to courses.json (overwrites the file). */
    private void writeCoursesToFile(List<Course> courses) {
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(filePath.toFile(), courses);
        } catch (IOException ex) {
            throw new CourseStorageException("Failed to write courses to " + filePath, ex);
        }
    }

    /** Generates the next ID as max(existing ids) + 1, starting at 1. */
    private Long generateNextId(List<Course> courses) {
        return courses.stream()
                .mapToLong(Course::getId)
                .max()
                .orElse(0L) + 1L;
    }

    private Optional<Integer> findCourseIndex(Long id) {
        return findCourseIndex(id, readCoursesFromFile());
    }

    private Optional<Integer> findCourseIndex(Long id, List<Course> courses) {
        for (int i = 0; i < courses.size(); i++) {
            if (courses.get(i).getId().equals(id)) {
                return Optional.of(i);
            }
        }
        return Optional.empty();
    }
}
