package ru.practicum.service;

import ru.practicum.dto.StatRequest;
import ru.practicum.dto.StatResponse;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsService {

    void saveStat(StatRequest request);

    List<StatResponse> getStatistics(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique);

}
