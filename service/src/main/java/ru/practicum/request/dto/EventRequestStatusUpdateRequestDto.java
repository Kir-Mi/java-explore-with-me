package ru.practicum.request.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventRequestStatusUpdateRequestDto {
    private Set<Long> requestIds;
    private RequestUpdateStatus status;

    public enum RequestUpdateStatus {
        CONFIRMED,
        REJECTED
    }
}