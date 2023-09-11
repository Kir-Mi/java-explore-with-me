package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.dto.StatRequest;
import ru.practicum.dto.StatResponse;
import ru.practicum.mapper.StatsMapper;
import ru.practicum.storage.StatsRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {

    private final StatsRepository statsRepository;
    private final StatsMapper statsMapper;

    @Override
    public void saveStat(StatRequest request) {
        statsRepository.save(statsMapper.mapToDomain(request));
    }

    @Override
    public List<StatResponse> getStatistics(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        if (unique) {
            return statsRepository.findAllStatsUnique(start, end, uris);
        } else {
            return statsRepository.findAllStats(start, end, uris);
        }
    }
}
