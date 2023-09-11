package ru.practicum.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.dto.StatRequest;
import ru.practicum.model.Stats;

@Component
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
