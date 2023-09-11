package ru.practicum.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.dto.StatRequest;
import ru.practicum.model.Stats;

@UtilityClass
public class StatsMapper {
    public Stats mapToDomain(StatRequest request) {
        return Stats.builder()
                .app(request.getApp())
                .uri(request.getUri())
                .ip(request.getIp())
                .timestamp(request.getTimestamp())
                .build();
    }
}
