package ru.practicum.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.enums.RequestStatus;
import ru.practicum.request.model.Request;

import java.util.Collection;
import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {
    List<Request> findByRequesterId(Long userId);

    Long countAllByEventIdAndStatus(Long eventId, RequestStatus status);

    List<Request> findAllByEventIdAndEventInitiatorId(Long eventId, Long userId);

    List<Request> findAllByStatusAndEventIdIn(RequestStatus status, Collection<Long> eventIds);

    List<Request> findAllByEventIdAndEventInitiatorIdAndIdIn(Long eventId, Long userId,
                                                             Collection<Long> requestsId);
}