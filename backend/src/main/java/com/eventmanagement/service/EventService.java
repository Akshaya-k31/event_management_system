package com.eventmanagement.service;

import com.eventmanagement.dto.EventRequest;
import com.eventmanagement.model.*;
import com.eventmanagement.model.Event;
import com.eventmanagement.repository.EventRepository;
import com.eventmanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private UserRepository userRepository;

    public Event createEvent(EventRequest eventRequest) {
        User organizer = userRepository.findById(eventRequest.getOrganizerId())
                .orElseThrow(() -> new RuntimeException("Organizer not found"));
        Event event = new Event();
        event.setTitle(eventRequest.getName());           // matches Event entity field
        event.setDescription(eventRequest.getDescription());
        event.setEventDate(eventRequest.getEventDate());
        event.setLocation(eventRequest.getLocation());
        event.setOrganizer(organizer);
        event.setStatus(EventStatus.PENDING);
        return eventRepository.save(event);
    }

    public Optional<Event> getEventById(Long id) {
        return eventRepository.findById(id);
    }

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    public List<Event> getApprovedEvents() {
        return eventRepository.findByStatus(EventStatus.APPROVED);
    }

    public List<Event> getPendingEvents() {
        return eventRepository.findByStatus(EventStatus.PENDING);
    }

    public List<Event> getEventsByOrganizer(Long organizerId) {
        return eventRepository.findByOrganizerId(organizerId);
    }

    public Event updateEvent(Long id, EventRequest eventRequest) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        event.setTitle(eventRequest.getName());
        event.setDescription(eventRequest.getDescription());
        event.setEventDate(eventRequest.getEventDate());
        event.setLocation(eventRequest.getLocation());

        return eventRepository.save(event);
    }

    public Event approveEvent(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        event.setStatus(EventStatus.APPROVED);
        return eventRepository.save(event);
    }

    public Event rejectEvent(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        event.setStatus(EventStatus.REJECTED);
        return eventRepository.save(event);
    }

    public void deleteEvent(Long id) {
        eventRepository.deleteById(id);
    }
}