package ru.practicum.event.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.StatisticsClient;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.dto.StatResponse;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exceptions.MyApplicationExceptions;
import ru.practicum.exceptions.NotAvailableException;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.exceptions.ValidationException;
import ru.practicum.location.mapper.LocationMapper;
import ru.practicum.location.model.Location;
import ru.practicum.location.repository.LocationRepository;
import ru.practicum.request.dto.*;
import ru.practicum.enums.*;
import ru.practicum.request.mapper.RequestMapper;
import ru.practicum.request.model.Request;
import ru.practicum.request.repository.RequestRepository;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;
    private final RequestRepository requestRepository;
    private final StatisticsClient statsClient;
    private final ObjectMapper objectMapper;

    private String app = "service";

    @Override
    public EventFullDto saveEvent(Long userId, NewEventDto dto) {
        User initiator = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("User %s not found", userId), HttpStatus.NOT_FOUND));

        Category category = categoryRepository.findById(dto.getCategory()).orElseThrow(() ->
                new NotFoundException(String.format("Category %s not found", dto.getCategory()), HttpStatus.NOT_FOUND));

        Location savedLocation = locationRepository
                .save(LocationMapper.toLocation(dto.getLocation()));

        Event event = EventMapper.toEvent(dto, savedLocation, category, EventStatus.PENDING, initiator);
        event.setCreatedOn(LocalDateTime.now());

        Event savedEvent = eventRepository.save(event);

        return EventMapper.toEventFullDto(savedEvent, category, initiator);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getEventsAddedByCurrentUser(Long userId, Pageable page) {
        List<Event> events = eventRepository.findAllByInitiator_Id(userId, page);

        return mapToEventShortDto(events);
    }

    @Override
    @Transactional(readOnly = true)
    public EventFullDto getEventAddedCurrentUser(Long userId, Long eventId) {
        Event event = eventRepository.findEventByInitiatorIdAndEventId(userId, eventId)
                .orElseThrow(() -> new NotFoundException(String.format(
                        "Event with user id %s and eventId %s not found", userId, eventId), HttpStatus.NOT_FOUND));

        return mapToEventFullDto(List.of(event)).get(0);
    }

    @Override
    public EventFullDto changeEventAddedCurrentUser(Long userId, Long eventId, UpdateEventUserRequestDto dto) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Event %s not found", eventId), HttpStatus.NOT_FOUND));

        if (event.getState().equals(EventStatus.PUBLISHED)) {
            throw new NotAvailableException("Only canceled events or events pending moderation can be changed", HttpStatus.CONFLICT);
        }

        userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("User %s not found", userId), HttpStatus.NOT_FOUND));

        patchUpdateEvent(dto, event);

        if (dto.getStateAction() != null) {
            if (dto.getStateAction().equals(UpdateEventUserRequestDto.StateAction.SEND_TO_REVIEW)) {
                event.setState(EventStatus.PENDING);
            } else {
                event.setState(EventStatus.CANCELED);
            }
        }

        return mapToEventFullDto(List.of(event)).get(0);
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<ParticipationRequestDto> getRequestsByCurrentUser(Long userId, Long eventId) {
        userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("User %s not found", userId), HttpStatus.NOT_FOUND));

        eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Event %s not found", eventId), HttpStatus.NOT_FOUND));

        return requestRepository.findAllByEventIdAndEventInitiatorId(eventId, userId)
                .stream()
                .map(RequestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public EventRequestStatusUpdateResultDto changeStatusOfRequestsByCurrentUser(Long userId, Long eventId,
                                                                                 EventRequestStatusUpdateRequestDto dto) {
        userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("User %s not found", userId), HttpStatus.NOT_FOUND));

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Event %s not found", eventId), HttpStatus.NOT_FOUND));

        Long confirmedRequests = requestRepository.countAllByEventIdAndStatus(eventId,
                RequestStatus.CONFIRMED);

        long freePlaces = event.getParticipantLimit() - confirmedRequests;

        RequestStatus status = RequestStatus.valueOf(String.valueOf(dto.getStatus()));

        if (status.equals(RequestStatus.CONFIRMED) && freePlaces <= 0) {
            throw new NotAvailableException("The limit of requests to participate in the event has been reached", HttpStatus.CONFLICT);
        }

        List<Request> requests = requestRepository.findAllByEventIdAndEventInitiatorIdAndIdIn(eventId,
                userId, dto.getRequestIds());

        setStatus(requests, status, freePlaces);

        List<ParticipationRequestDto> requestsDto = requests
                .stream()
                .map(RequestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());

        List<ParticipationRequestDto> confirmedRequestsDto = new ArrayList<>();
        List<ParticipationRequestDto> rejectedRequestsDto = new ArrayList<>();

        requestsDto.forEach(el -> {
            if (status.equals(RequestStatus.CONFIRMED)) {
                confirmedRequestsDto.add(el);
            } else {
                rejectedRequestsDto.add(el);
            }
        });

        return EventRequestStatusUpdateResultDto.builder()
                .confirmedRequests(confirmedRequestsDto)
                .rejectedRequests(rejectedRequestsDto)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<EventFullDto> getEventsByAdmin(Set<Long> userIds, Set<Long> categoryIds,
                                                     Collection<EventStatus> states,
                                                     LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                                     Pageable pageable) {
        List<Event> events = eventRepository.findByAdmin(userIds, states, categoryIds, rangeStart, rangeEnd, pageable);

        return mapToEventFullDto(events);
    }

    @Override
    public EventFullDto updateEventByAdmin(Long eventId, UpdateEventAdminRequestDto dto) {
        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException(String.format("Event %s not found", eventId), HttpStatus.NOT_FOUND));

        if (dto.getStateAction() != null) {
            if (dto.getStateAction().equals(UpdateEventAdminRequestDto.StateAction.PUBLISH_EVENT)) {
                if (!event.getState().equals(EventStatus.PENDING)) {
                    throw new NotAvailableException(String.format("Event %s has already been published", eventId), HttpStatus.CONFLICT);
                }
                event.setState(EventStatus.PUBLISHED);
                event.setPublishedOn(LocalDateTime.now());
            } else {
                if (!event.getState().equals(EventStatus.PENDING)) {
                    throw new NotAvailableException("Event must be in PENDING status", HttpStatus.CONFLICT);
                }
                event.setState(EventStatus.CANCELED);
            }
        }
        if (event.getPublishedOn() != null && event.getEventDate().isBefore(event.getPublishedOn().plusHours(1))) {
            throw new NotAvailableException("The start date of the modified event must be" +
                    " no earlier than one hour from the publication date", HttpStatus.CONFLICT);
        }
        patchUpdateEvent(dto, event);
        locationRepository.save(event.getLocation());

        return mapToEventFullDto(List.of(event)).get(0);
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<EventShortDto> getEventsPublic(String text, Set<Long> categoriesIds, Boolean paid,
                                                     LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                                     boolean onlyAvailable, EventSort sort, Pageable pageable,
                                                     HttpServletRequest httpServletRequest) {
        if (rangeStart == null) {
            rangeStart = LocalDateTime.now();
        }
        if (rangeEnd != null && rangeEnd.isBefore(rangeStart)) {
            throw new ValidationException("RangeStart cannot be later than rangeEnd", HttpStatus.BAD_REQUEST);
        }

        List<Event> events = eventRepository.findAllPublic(text, categoriesIds, paid,
                rangeStart, rangeEnd, onlyAvailable, pageable);

        sendStats(httpServletRequest.getRequestURI(), httpServletRequest.getRemoteAddr());

        return mapToEventShortDto(events);
    }

    @Override
    @Transactional(readOnly = true)
    public EventFullDto getEventByIdPublic(Long eventId, String uri, String ip) {
        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException(String.format("Event %s not found", eventId), HttpStatus.NOT_FOUND));

        if (!event.getState().equals(EventStatus.PUBLISHED)) {
            throw new NotFoundException(String.format("Event %s not published", eventId), HttpStatus.NOT_FOUND);
        }

        sendStats(uri, ip);

        return mapToEventFullDto(List.of(event)).get(0);
    }

    private List<EventFullDto> mapToEventFullDto(Collection<Event> events) {
        List<Long> eventIds = events.stream()
                .map(Event::getId)
                .collect(Collectors.toList());

        List<EventFullDto> dtos = events.stream()
                .map(EventMapper::toEventFullDto)
                .collect(Collectors.toList());

        Map<Long, Long> eventsViews = getViews(eventIds);
        Map<Long, Long> confirmedRequests = getConfirmedRequests(eventIds);

        dtos.forEach(el -> {
            el.setViews(eventsViews.getOrDefault(el.getId(), 0L));
            el.setConfirmedRequests(confirmedRequests.getOrDefault(el.getId(), 0L));
        });

        return dtos;
    }

    private List<EventShortDto> mapToEventShortDto(Collection<Event> events) {
        List<Long> eventIds = events.stream()
                .map(Event::getId)
                .collect(Collectors.toList());

        List<EventShortDto> dtos = events.stream()
                .map(EventMapper::toEventShortDto)
                .collect(Collectors.toList());

        Map<Long, Long> eventsViews = getViews(eventIds);
        Map<Long, Long> confirmedRequests = getConfirmedRequests(eventIds);

        dtos.forEach(el -> {
            el.setViews(eventsViews.getOrDefault(el.getId(), 0L));
            el.setConfirmedRequests(confirmedRequests.getOrDefault(el.getId(), 0L));
        });

        return dtos;
    }

    private Map<Long, Long> getConfirmedRequests(Collection<Long> eventsId) {
        List<Request> confirmedRequests = requestRepository
                .findAllByStatusAndEventIdIn(RequestStatus.CONFIRMED, eventsId);

        return confirmedRequests.stream()
                .collect(Collectors.groupingBy(request -> request.getEvent().getId()))
                .entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> (long) e.getValue().size()));
    }

    private void sendStats(String uri, String ip) {
        statsClient.saveStatistics(app, uri, ip, LocalDateTime.now());
    }

    private Map<Long, Long> getViews(Collection<Long> eventsId) {
        List<String> uris = eventsId
                .stream()
                .map(id -> "/events/" + id)
                .collect(Collectors.toList());

        Optional<LocalDateTime> start = eventRepository.getStart(eventsId);

        Map<Long, Long> views = new HashMap<>();

        if (start.isPresent()) {
            try {
                ResponseEntity<Object> response = statsClient.getStatistics(start.get(), LocalDateTime.now(), uris, true);

                if (response != null && response.getBody() != null) {
                    Object body = response.getBody();
                    String bodyStr = objectMapper.writeValueAsString(body);
                    List<StatResponse> responseList = objectMapper.readValue(bodyStr, new TypeReference<>() {
                    });

                    responseList.forEach(dto -> {
                        String uri = dto.getUri();
                        String[] split = uri.split("/");
                        String id = split[2];
                        Long eventId = Long.parseLong(id);
                        views.put(eventId, dto.getHits());
                    });
                }
            } catch (JsonProcessingException e) {
                String msg = "Failed to parse response from Statistics Client: " + e.getMessage();
                throw new MyApplicationExceptions(msg, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            eventsId.forEach(el -> views.put(el, 0L));
        }

        return views;
    }

    private void patchUpdateEvent(UpdateEventRequest dto, Event event) {
        if (dto.getAnnotation() != null && !dto.getAnnotation().isBlank()) {
            event.setAnnotation(dto.getAnnotation());
        }
        if (dto.getCategory() != null) {
            Category category = categoryRepository.findById(dto.getCategory()).orElseThrow(() ->
                    new NotFoundException(String.format("Category %s not found", dto.getCategory()), HttpStatus.NOT_FOUND));
            event.setCategory(category);
        }
        if (dto.getDescription() != null && !dto.getDescription().isBlank()) {
            event.setDescription(dto.getDescription());
        }
        if (dto.getEventDate() != null) {
            event.setEventDate(dto.getEventDate());
        }
        if (dto.getLocation() != null) {
            event.setLocation(LocationMapper.toLocation(dto.getLocation()));
        }
        if (dto.getPaid() != null) {
            event.setPaid(dto.getPaid());
        }
        if (dto.getParticipantLimit() != null) {
            event.setParticipantLimit(dto.getParticipantLimit());
        }
        if (dto.getRequestModeration() != null) {
            event.setRequestModeration(dto.getRequestModeration());
        }
        if (dto.getTitle() != null && !dto.getTitle().isBlank()) {
            event.setTitle(dto.getTitle());
        }
    }

    private void setStatus(Collection<Request> requests, RequestStatus status, long freePlaces) {
        if (status.equals(RequestStatus.CONFIRMED)) {
            for (Request request : requests) {
                if (!request.getStatus().equals(RequestStatus.PENDING)) {
                    throw new NotAvailableException("Request's status has to be PENDING", HttpStatus.CONFLICT);
                }
                if (freePlaces > 0) {
                    request.setStatus(RequestStatus.CONFIRMED);
                    freePlaces--;
                } else {
                    request.setStatus(RequestStatus.REJECTED);
                }
            }
        } else if (status.equals(RequestStatus.REJECTED)) {
            requests.forEach(request -> {
                if (!request.getStatus().equals(RequestStatus.PENDING)) {
                    throw new NotAvailableException("Request's status has to be PENDING", HttpStatus.CONFLICT);
                }
                request.setStatus(RequestStatus.REJECTED);
            });
        } else {
            throw new NotAvailableException("You must either approve - CONFIRMED" +
                    " or reject - REJECTED the application", HttpStatus.CONFLICT);
        }
    }
}