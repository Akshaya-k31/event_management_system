package com.eventmanagement.controller;

import com.eventmanagement.model.Event;
import com.eventmanagement.service.EventService;
import com.eventmanagement.dto.EventRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/events")
@CrossOrigin(origins = "*")
public class EventController {

    @Autowired
    private EventService eventService;

    @PostMapping
    public ResponseEntity<?> createEvent(@RequestBody EventRequest eventRequest) {
        try {
            Event savedEvent = eventService.createEvent(eventRequest);
            return ResponseEntity.ok(savedEvent);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<Event>> getAllEvents() {
        return ResponseEntity.ok(eventService.getAllEvents());
    }

    @GetMapping("/approved")
    public ResponseEntity<List<Event>> getApprovedEvents() {
        return ResponseEntity.ok(eventService.getApprovedEvents());
    }

    @GetMapping("/pending")
    public ResponseEntity<List<Event>> getPendingEvents() {
        return ResponseEntity.ok(eventService.getPendingEvents());
    }

    @GetMapping("/organizer/{organizerId}")
    public ResponseEntity<List<Event>> getEventsByOrganizer(@PathVariable Long organizerId) {
        return ResponseEntity.ok(eventService.getEventsByOrganizer(organizerId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getEventById(@PathVariable Long id) {
        return eventService.getEventById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateEvent(@PathVariable Long id, @RequestBody EventRequest eventRequest) {
        try {
            Event updatedEvent = eventService.updateEvent(id, eventRequest);
            return ResponseEntity.ok(updatedEvent);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @PutMapping("/{id}/approve")
    public ResponseEntity<?> approveEvent(@PathVariable Long id) {
        try {
            Event approvedEvent = eventService.approveEvent(id);
            return ResponseEntity.ok(approvedEvent);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<?> rejectEvent(@PathVariable Long id) {
        try {
            Event rejectedEvent = eventService.rejectEvent(id);
            return ResponseEntity.ok(rejectedEvent);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEvent(@PathVariable Long id) {
        try {
            eventService.deleteEvent(id);
            return ResponseEntity.ok("Event deleted successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}