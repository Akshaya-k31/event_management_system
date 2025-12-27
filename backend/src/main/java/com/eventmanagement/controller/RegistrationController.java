package com.eventmanagement.controller;

import com.eventmanagement.model.Registration;
import com.eventmanagement.service.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/registrations")
@CrossOrigin(origins = "*")
public class RegistrationController {

    @Autowired
    private RegistrationService registrationService;

    @PostMapping
    public ResponseEntity<?> registerForEvent(@RequestBody RegistrationRequest request) {
        try {
            Registration registration = registrationService.registerForEvent(
                    request.getEventId(),
                    request.getParticipantId()
            );
            return ResponseEntity.ok(registration);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<Registration>> getEventRegistrations(@PathVariable Long eventId) {
        return ResponseEntity.ok(registrationService.getEventRegistrations(eventId));
    }

    @GetMapping("/participant/{participantId}")
    public ResponseEntity<List<Registration>> getUserRegistrations(@PathVariable Long participantId) {
        return ResponseEntity.ok(registrationService.getUserRegistrations(participantId));
    }
    @GetMapping("/participant/{id}/approved")
    public List<Registration> getApprovedRegistrations(@PathVariable Long id) {
        return registrationService.getApprovedRegistrations(id);
    }


    @PutMapping("/{id}/attendance")
    public ResponseEntity<?> markAttendance(
            @PathVariable Long id,
            @RequestBody AttendanceRequest request
    ) {
        try {
            Registration registration = registrationService.markAttendance(id, request.getAttended());
            return ResponseEntity.ok(registration);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
class RegistrationRequest {
    private Long eventId;
    private Long participantId;

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public Long getParticipantId() {
        return participantId;
    }

    public void setParticipantId(Long participantId) {
        this.participantId = participantId;
    }
}

class AttendanceRequest {
    private Boolean attended;

    public Boolean getAttended() {
        return attended;
    }

    public void setAttended(Boolean attended) {
        this.attended = attended;
    }
}