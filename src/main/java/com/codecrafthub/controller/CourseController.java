package com.codecrafthub.controller;

import com.codecrafthub.model.Course;
import com.codecrafthub.service.CourseService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller — maps HTTP requests to service methods.
 *
 * Base path: /api/courses
 *
 * Example requests (use Postman, curl, or Thunder Client):
 *   GET    /api/courses
 *   GET    /api/courses/1
 *   POST   /api/courses
 *   PUT    /api/courses/1
 *   DELETE /api/courses/1
 */
@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    /** GET /api/courses — retrieve all courses */
    @GetMapping
    public List<Course> getAllCourses() {
        return courseService.getAllCourses();
    }

    /** GET /api/courses/{id} — retrieve one course by ID */
    @GetMapping("/{id}")
    public Course getCourseById(@PathVariable Long id) {
        return courseService.getCourseById(id);
    }

    /**
     * POST /api/courses — create a new course
     *
     * Request body example:
     * {
     *   "name": "Spring Boot Basics",
     *   "description": "Learn REST APIs",
     *   "target_date": "2026-08-15",
     *   "status": "Not Started"
     * }
     */
    @PostMapping
    public ResponseEntity<Course> createCourse(@Valid @RequestBody Course course) {
        Course created = courseService.createCourse(course);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * PUT /api/courses/{id} — update an existing course
     *
     * Send the full course fields (except id and created_at, which are preserved).
     */
    @PutMapping("/{id}")
    public Course updateCourse(@PathVariable Long id, @Valid @RequestBody Course course) {
        return courseService.updateCourse(id, course);
    }

    /** DELETE /api/courses/{id} — remove a course */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
        return ResponseEntity.noContent().build();
    }
}
