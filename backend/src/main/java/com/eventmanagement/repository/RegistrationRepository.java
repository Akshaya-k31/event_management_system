package com.eventmanagement.repository;
import com.eventmanagement.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
@Repository
public interface RegistrationRepository extends JpaRepository<Registration, Long> {
    List<Registration> findByEventId(Long eventId);
    Optional<Registration> findByEventIdAndParticipantId(Long eventId, Long participantId);
    List<Registration> findByParticipantId(Long participantId);
}