package com.codecrafthub.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Valid status values for a course.
 * Jackson uses the display names ("Not Started", etc.) in JSON.
 */
public enum CourseStatus {

    NOT_STARTED("Not Started"),
    IN_PROGRESS("In Progress"),
    COMPLETED("Completed");

    private final String displayName;

    CourseStatus(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Serializes the enum to its JSON string value (e.g. "In Progress").
     */
    @JsonValue
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Deserializes JSON strings back into enum values.
     * Throws IllegalArgumentException for invalid status strings.
     */
    @JsonCreator
    public static CourseStatus fromDisplayName(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Status is required");
        }
        for (CourseStatus status : values()) {
            if (status.displayName.equals(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException(
                "Invalid status: '" + value + "'. Must be one of: Not Started, In Progress, Completed");
    }
}
