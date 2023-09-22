package ru.practicum.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.enums.EventStatus;
import ru.practicum.enums.RequestStatus;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exceptions.NotAvailableException;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.exceptions.ValidationException;
import ru.practicum.request.model.Request;
import ru.practicum.request.repository.RequestRepository;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;

@Service
@RequiredArgsConstructor
@Transactional
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional(readOnly = true)
    public Collection<Request> getRequestsToParticipateInOtherEvents(Long userId) {
        userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("User %s not found", userId), HttpStatus.NOT_FOUND));

        return requestRepository.findByRequesterId(userId);
    }

    @Override
    public Request saveUserRequest(Long userId, Long eventId) {
        User requester = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("User %s not found", userId), HttpStatus.NOT_FOUND));

        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException(String.format("Event %s not found", eventId), HttpStatus.NOT_FOUND));

        if (event.getInitiator().getId().equals(requester.getId())) {
            throw new NotAvailableException("Event initiator cannot add a request to participate in their event", HttpStatus.CONFLICT);
        }
        if (!event.getState().equals(EventStatus.PUBLISHED)) {
            throw new NotAvailableException("Cannot participate in an unpublished event", HttpStatus.CONFLICT);
        }

        Long confirmedRequests = requestRepository.countAllByEventIdAndStatus(eventId,
                RequestStatus.CONFIRMED);

        if (event.getParticipantLimit() <= confirmedRequests && event.getParticipantLimit() != 0) {
            throw new NotAvailableException("Limit of requests for participation has been exceeded", HttpStatus.CONFLICT);
        }

        Request request = Request.builder()
                .created(LocalDateTime.now())
                .event(event)
                .requester(requester)
                .status(!event.getRequestModeration() || event.getParticipantLimit() == 0
                        ? RequestStatus.CONFIRMED
                        : RequestStatus.PENDING)
                .build();

        return requestRepository.save(request);
    }

    @Override
    public Request cancelOwnEvent(Long userId, Long requestId) {
        userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("User %s not found", userId), HttpStatus.NOT_FOUND));

        Request request = requestRepository.findById(requestId).orElseThrow(() ->
                new NotFoundException(String.format("Request %s not found", requestId), HttpStatus.NOT_FOUND));

        if (!request.getRequester().getId().equals(userId)) {
            throw new ValidationException(
                    String.format("User %s didn't apply for participation %s", userId, requestId), HttpStatus.BAD_REQUEST);
        }
        request.setStatus(RequestStatus.CANCELED);

        return requestRepository.save(request);
    }
}