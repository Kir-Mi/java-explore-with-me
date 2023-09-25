package ru.practicum.compilation.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.request.dto.UpdateCompilationRequest;

import java.util.Collection;

public interface CompilationService {
    Collection<CompilationDto> getAllCompilations(Pageable pageable, Boolean pinned);

    CompilationDto getCompilationById(Long compId);

    CompilationDto saveCompilation(NewCompilationDto newCompilationDto);

    void deleteCompilationById(Long compId);

    CompilationDto changeCompilation(Long compId, UpdateCompilationRequest updateCompilationRequest);
}