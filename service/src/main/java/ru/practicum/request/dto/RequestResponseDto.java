package ru.practicum.request.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.enums.EventStatus;
import ru.practicum.event.dto.EventFullDto;

import java.time.LocalDateTime;

import static ru.practicum.utils.Constant.TIME_PATTERN;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class RequestResponseDto {

    @JsonFormat(pattern = TIME_PATTERN)
    private LocalDateTime created;

    private EventFullDto eventResponseDto;

    private Long id;

    private Long requester;

    private EventStatus status;
}