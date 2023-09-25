package ru.practicum.request.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.enums.RequestStatus;

import java.time.LocalDateTime;

import static ru.practicum.utils.Constant.TIME_PATTERN;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class ParticipationRequestDto {
    private Long id;

    @JsonFormat(pattern = TIME_PATTERN)
    private LocalDateTime created;

    private Long event;

    private Long requester;

    private RequestStatus status;
}