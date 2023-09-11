package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.StatRequest;
import ru.practicum.dto.StatResponse;
import ru.practicum.exception.MyApplicationException;
import ru.practicum.util.DateUtil;
import ru.practicum.service.StatsService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class StatsController {

    private final StatsService statsService;
    private final DateUtil dateUtil;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void addHit(@RequestBody @Valid StatRequest request) {
        log.info("Received POST request to add stat hit: {}", request);
        statsService.saveStat(request);
    }

    @GetMapping("/stats")
    public List<StatResponse> getStatistics(@RequestParam("start") String start,
                                            @RequestParam("end") String end,
                                            @RequestParam(value = "uris", required = false) List<String> uris,
                                            @RequestParam(value = "unique", defaultValue = "false") boolean unique) {
        log.info("Received request to GET statistics for period start: {}, end: {}", start, end);
        LocalDateTime startDate = dateUtil.decodeFromString(start);
        LocalDateTime endDate = dateUtil.decodeFromString(end);
        checkDatesCorrect(startDate, endDate);
        List<StatResponse> statistics = statsService.getStatistics(startDate, endDate, uris, unique);
        log.info("Retrieved statistics: {}", statistics);
        return statistics;
    }

    private void checkDatesCorrect(LocalDateTime start, LocalDateTime end) {
        if (start.isAfter(LocalDateTime.now()) || start.isAfter(end)) {
            String msg = String.format("Invalid date parameters: %s, %s",
                    start.format(DateUtil.FORMATTER),
                    end.format(DateUtil.FORMATTER));
            throw new MyApplicationException(msg, HttpStatus.BAD_REQUEST);
        }
    }
}