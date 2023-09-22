package ru.practicum.event.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.request.dto.UpdateEventAdminRequestDto;
import ru.practicum.enums.EventStatus;
import ru.practicum.event.service.EventService;
import ru.practicum.utils.OffsetBasedPageRequest;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static ru.practicum.utils.Constant.*;

@RestController
@RequestMapping("/admin/events")
@RequiredArgsConstructor
@Validated
public class AdminEventController {
    private final EventService eventService;

    @GetMapping
    public Collection<EventFullDto> getEventsByAdmin(@RequestParam(defaultValue = PAGE_DEFAULT_FROM) @PositiveOrZero Integer from,
                                                     @RequestParam(defaultValue = PAGE_DEFAULT_SIZE) @Positive Integer size,
                                                     @RequestParam(required = false) @DateTimeFormat(pattern = TIME_PATTERN) LocalDateTime rangeStart,
                                                     @RequestParam(required = false) @DateTimeFormat(pattern = TIME_PATTERN) LocalDateTime rangeEnd,
                                                     @RequestParam(required = false) List<EventStatus> states,
                                                     @RequestParam(required = false) Set<Long> users,
                                                     @RequestParam(required = false) Set<Long> categories) {
        Pageable page = new OffsetBasedPageRequest(from, size);
        return eventService.getEventsByAdmin(users, categories, states, rangeStart, rangeEnd, page);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEventByAdmin(@Positive @PathVariable Long eventId,
                                           @Valid @RequestBody UpdateEventAdminRequestDto dto) {
        return eventService.updateEventByAdmin(eventId, dto);
    }
}