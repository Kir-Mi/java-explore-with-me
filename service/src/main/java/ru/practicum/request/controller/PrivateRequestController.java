package ru.practicum.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.mapper.RequestMapper;
import ru.practicum.request.model.Request;
import ru.practicum.request.service.RequestService;

import javax.validation.constraints.Positive;
import java.util.Collection;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users/{userId}/requests")
@RequiredArgsConstructor
@Validated
public class PrivateRequestController {
    private final RequestService requestService;

    @GetMapping
    public Collection<ParticipationRequestDto> getRequestsToParticipateInOtherEvents(@Positive @PathVariable Long userId) {
        return requestService.getRequestsToParticipateInOtherEvents(userId)
                .stream()
                .map(RequestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto saveUserRequest(@Positive @PathVariable Long userId,
                                                   @Positive @RequestParam Long eventId) {
        Request request = requestService.saveUserRequest(userId, eventId);
        return RequestMapper.toParticipationRequestDto(request);
    }

    @PatchMapping("/{requestId}/cancel")
    public ParticipationRequestDto cancelOwnEvent(@Positive @PathVariable Long userId,
                                                  @Positive @PathVariable Long requestId) {
        Request request = requestService.cancelOwnEvent(userId, requestId);
        return RequestMapper.toParticipationRequestDto(request);
    }
}