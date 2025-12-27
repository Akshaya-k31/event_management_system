package com.eventmanagement.model;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "registrations")
public class Registration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER) // instead of default LAZY
    @JoinColumn(name = "event_id", nullable = false)
    @JsonIgnoreProperties("registrations") // prevents infinite recursion
    private Event event;

    @ManyToOne
    @JoinColumn(name = "participant_id", nullable = false)
    @JsonIgnoreProperties({"registrations", "password"})  // prevents User → Registration → User recursion
    private User participant;

    @Column(nullable = false)
    private Boolean attended;

    @Column(name = "registered_at")
    private LocalDateTime registeredAt;

    @PrePersist
    protected void onCreate() {
        registeredAt = LocalDateTime.now();
        if (attended == null) {
            attended = false;
        }
    }

    // Constructors
    public Registration() {}

    public Registration(Event event, User participant) {
        this.event = event;
        this.participant = participant;
        this.attended = false;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public User getParticipant() {
        return participant;
    }

    public void setParticipant(User participant) {
        this.participant = participant;
    }

    public Boolean getAttended() {
        return attended;
    }

    public void setAttended(Boolean attended) {
        this.attended = attended;
    }

    public LocalDateTime getRegisteredAt() {
        return registeredAt;
    }

    public void setRegisteredAt(LocalDateTime registeredAt) {
        this.registeredAt = registeredAt;
    }
}