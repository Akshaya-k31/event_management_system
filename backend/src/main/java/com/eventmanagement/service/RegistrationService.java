package com.eventmanagement.service;

import com.eventmanagement.model.*;
import com.eventmanagement.repository.RegistrationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.stream.Collectors;
import java.util.List;
import java.util.Optional;

@Service
public class RegistrationService {

    @Autowired
    private RegistrationRepository registrationRepository;

    @Autowired
    private EventService eventService;

    @Autowired
    private UserService userService;

    public Registration registerForEvent(Long eventId, Long participantId) {
        Event event = eventService.getEventById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        User participant = userService.getUserById(participantId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Optional<Registration> existing = registrationRepository
                .findByEventIdAndParticipantId(eventId, participantId);

        if (existing.isPresent()) {
            throw new RuntimeException("Already registered for this event");
        }

        Registration registration = new Registration(event, participant);
        return registrationRepository.save(registration);
    }

    public List<Registration> getEventRegistrations(Long eventId) {
        return registrationRepository.findByEventId(eventId);
    }

    public List<Registration> getUserRegistrations(Long participantId) {
        return registrationRepository.findByParticipantId(participantId);
    }


    public List<Registration> getApprovedRegistrations(Long participantId) {
        return registrationRepository.findByParticipantId(participantId)
                .stream()
                .filter(r -> r.getEvent().getStatus() == EventStatus.APPROVED)
                .collect(Collectors.toList());
    }



    public Registration markAttendance(Long registrationId, Boolean attended) {
        Registration registration = registrationRepository.findById(registrationId)
                .orElseThrow(() -> new RuntimeException("Registration not found"));

        registration.setAttended(attended);
        return registrationRepository.save(registration);
    }
}