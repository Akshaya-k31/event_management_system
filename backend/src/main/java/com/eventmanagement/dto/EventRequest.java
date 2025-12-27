package com.eventmanagement.dto;

import java.time.LocalDateTime;

public class EventRequest {
    private String name;
    private String description;
    private LocalDateTime eventDate;
    private String location;
    private Long organizerId; // passed from afrontend

    // Getters and setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getEventDate() { return eventDate; }
    public void setEventDate(LocalDateTime eventDate) { this.eventDate = eventDate; }
    public String getLocation() { return location; }      // <-- ADD THIS
    public void setLocation(String location) { this.location = location; }

    public Long getOrganizerId() { return organizerId; }
    public void setOrganizerId(Long organizerId) { this.organizerId = organizerId; }
}
