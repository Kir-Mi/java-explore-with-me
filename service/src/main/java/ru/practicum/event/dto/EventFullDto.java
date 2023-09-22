package ru.practicum.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.location.dto.LocationDtoCoordinates;
import ru.practicum.user.dto.UserShortDto;
import ru.practicum.enums.EventStatus;

import java.time.LocalDateTime;

import static ru.practicum.utils.Constant.TIME_PATTERN;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventFullDto {
    private Long id;

    private String annotation;

    private CategoryDto category;

    private Long confirmedRequests;

    @JsonFormat(pattern = TIME_PATTERN)
    private LocalDateTime createdOn;

    private String description;

    @JsonFormat(pattern = TIME_PATTERN)
    private LocalDateTime eventDate;

    private UserShortDto initiator;

    private LocationDtoCoordinates location;

    private Boolean paid;

    private Long participantLimit;

    @JsonFormat(pattern = TIME_PATTERN)
    private LocalDateTime publishedOn;

    private Boolean requestModeration;

    private EventStatus published;

    private String title;

    private Long views;

    private EventStatus state;
}