package ru.practicum.compilation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.mapper.CompilationMapper;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.compilation.repository.CompilationRepository;
import ru.practicum.enums.RequestStatus;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.request.dto.UpdateCompilationRequest;
import ru.practicum.request.model.Request;
import ru.practicum.request.repository.RequestRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;

    @Override
    @Transactional(readOnly = true)
    public Collection<CompilationDto> getAllCompilations(Pageable pageable, Boolean pinned) {
        List<Compilation> result = pinned != null
                ? compilationRepository.findByPinned(pinned, pageable)
                : compilationRepository.findAll(pageable).getContent();

        return result.stream()
                .map(this::toCompilationDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CompilationDto getCompilationById(Long compId) {
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(() ->
                new NotFoundException(String.format("Compilation %s not found", compId), HttpStatus.NOT_FOUND));

        return toCompilationDto(compilation);
    }

    @Override
    public CompilationDto saveCompilation(NewCompilationDto newCompilationDto) {
        Compilation compilation = CompilationMapper.toCompilation(newCompilationDto);

        Set<Long> eventsId = newCompilationDto.getEvents();
        if (eventsId != null) {
            Set<Event> events = new HashSet<>(eventRepository.findAllByIdIn(eventsId));
            compilation.setEvents(events);
        }
        if (compilation.getPinned() == null) {
            compilation.setPinned(false);
        }

        Compilation savedCompilation = compilationRepository.save(compilation);

        return toCompilationDto(savedCompilation);
    }

    @Override
    public void deleteCompilationById(Long compId) {
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(() ->
                new NotFoundException(String.format("Compilation %s not found", compId), HttpStatus.NOT_FOUND));

        compilationRepository.delete(compilation);
    }

    @Override
    public CompilationDto changeCompilation(Long compId, UpdateCompilationRequest dto) {
        Compilation toUpdate = compilationRepository.findById(compId).orElseThrow(() ->
                new NotFoundException(String.format("Compilation %s not found", compId), HttpStatus.NOT_FOUND));

        if (dto.getTitle() != null && !dto.getTitle().isBlank()) {
            toUpdate.setTitle(dto.getTitle());
        }
        if (dto.getPinned() != null) {
            toUpdate.setPinned(dto.getPinned());
        }

        if (dto.getEvents() != null && !dto.getEvents().isEmpty()) {
            Set<Long> eventsId = dto.getEvents();
            Collection<Event> events = eventRepository.findAllByIdIn(eventsId);
            toUpdate.setEvents(new HashSet<>(events));
        }

        return toCompilationDto(toUpdate);
    }

    private CompilationDto toCompilationDto(Compilation compilation) {
        CompilationDto dto = CompilationMapper.toCompilationDto(compilation);
        setConfirmedRequestsToEvent(dto);

        return dto;
    }

    private void setConfirmedRequestsToEvent(CompilationDto dto) {
        Set<EventShortDto> compilationEvents = dto.getEvents();
        if (compilationEvents != null) {
            List<Long> eventIds = new ArrayList<>();

            compilationEvents.forEach(el -> eventIds.add(el.getId()));

            List<Request> confirmedRequests = requestRepository.findAllByStatusAndEventIdIn(
                    RequestStatus.CONFIRMED, eventIds);

            Map<Long, Long> requests = confirmedRequests.stream()
                    .collect(Collectors.groupingBy(request -> request.getEvent().getId()))
                    .entrySet()
                    .stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, e -> (long) e.getValue().size()));

            compilationEvents.forEach(eventShortDto ->
                    eventShortDto.setConfirmedRequests(requests.getOrDefault(eventShortDto.getId(), 0L)));
        }
    }
}