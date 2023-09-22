package ru.practicum.event.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.model.Category;
import ru.practicum.enums.EventStatus;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.model.Event;
import ru.practicum.location.mapper.LocationMapper;
import ru.practicum.location.model.Location;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static ru.practicum.enums.EventStatus.PENDING;

@UtilityClass
public class EventMapper {

    public Event toEvent(NewEventDto newEventDto, Location location, Category category,
                         EventStatus state, User initiator) {
        return Event.builder()
                .category(category)
                .title(newEventDto.getTitle())
                .description(newEventDto.getDescription())
                .eventDate(newEventDto.getEventDate())
                .initiator(initiator)
                .paid(newEventDto.isPaid())
                .location(location)
                .annotation(newEventDto.getAnnotation())
                .state(PENDING)
                .participantLimit(newEventDto.getParticipantLimit())
                .createdOn(LocalDateTime.now())
                .requestModeration(newEventDto.isRequestModeration())
                .build();
    }

    public EventShortDto toEventShortDto(Event event) {
        return EventShortDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(CategoryMapper.toCategoryDto(event.getCategory()))
                .eventDate(event.getEventDate())
                .initiator(UserMapper.userShortDto(event.getInitiator()))
                .paid(event.isPaid())
                .title(event.getTitle())
                .build();
    }

    public EventFullDto toEventFullDto(Event event, Category category, User initiator) {
        return EventFullDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(CategoryMapper.toCategoryDto(category))
                .createdOn(event.getCreatedOn())
                .description(event.getDescription())
                .eventDate(event.getEventDate())
                .initiator(UserMapper.userShortDto(initiator))
                .location(LocationMapper.toLocationDtoCoordinates(event.getLocation()))
                .paid(event.isPaid())
                .participantLimit(event.getParticipantLimit())
                .publishedOn(event.getPublishedOn())
                .requestModeration(event.getRequestModeration())
                .title(event.getTitle())
                .state(event.getState())
                .build();
    }

    public EventFullDto toEventFullDto(Event event) {
        return EventFullDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(CategoryMapper.toCategoryDto(event.getCategory()))
                .createdOn(event.getCreatedOn())
                .description(event.getDescription())
                .eventDate(event.getEventDate())
                .initiator(UserMapper.userShortDto(event.getInitiator()))
                .location(LocationMapper.toLocationDtoCoordinates(event.getLocation()))
                .paid(event.isPaid())
                .participantLimit(event.getParticipantLimit())
                .publishedOn(event.getPublishedOn())
                .requestModeration(event.getRequestModeration())
                .title(event.getTitle())
                .state(event.getState())
                .build();
    }

    public Set<EventShortDto> toEventShortDtoSet(Iterable<Event> events) {
        Set<EventShortDto> result = new HashSet<>();
        if (events == null) {
            return result;
        }
        for (Event event : events) {
            result.add(toEventShortDto(event));
        }
        return result;
    }
}