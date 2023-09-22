package ru.practicum.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.user.dto.UserShortDto;

import java.time.LocalDateTime;

import static ru.practicum.utils.Constant.TIME_PATTERN;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventShortDto {
    private String annotation;

    private CategoryDto category;

    private Long confirmedRequests;

    @JsonFormat(pattern = TIME_PATTERN)
    private LocalDateTime eventDate;

    private Long id;

    private UserShortDto initiator;

    private Boolean paid;

    private String title;

    private Long views;

    private Long comments;
}