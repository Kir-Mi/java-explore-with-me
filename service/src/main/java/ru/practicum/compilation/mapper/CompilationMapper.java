package ru.practicum.compilation.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;

import java.util.Collection;
import java.util.Set;

@UtilityClass
public class CompilationMapper {

    public Compilation toCompilation(NewCompilationDto newCompilationDto, Collection<Event> events) {
        return Compilation.builder()
                .events((Set<Event>) events)
                .pinned(newCompilationDto.getPinned())
                .title(newCompilationDto.getTitle())
                .build();
    }

    public Compilation toCompilation(NewCompilationDto newCompilationDto) {
        return Compilation.builder()
                .pinned(newCompilationDto.getPinned())
                .title(newCompilationDto.getTitle())
                .build();
    }

    public CompilationDto toCompilationDto(Compilation compilation) {
        return CompilationDto.builder()
                .events(EventMapper.toEventShortDtoSet(compilation.getEvents()))
                .id(compilation.getId())
                .pinned(compilation.getPinned())
                .title(compilation.getTitle())
                .build();
    }
}
