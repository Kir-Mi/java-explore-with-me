package ru.practicum.compilation.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.service.CompilationService;
import ru.practicum.request.dto.UpdateCompilationRequest;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@RestController
@RequestMapping("/admin/compilations")
@RequiredArgsConstructor
@Validated
public class AdminCompilationController {
    private final CompilationService compilationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto saveCompilation(@Valid @RequestBody NewCompilationDto dto) {
        return compilationService.saveCompilation(dto);
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilationById(@Positive @PathVariable Long compId) {
        compilationService.deleteCompilationById(compId);
    }

    @PatchMapping("/{compId}")
    public CompilationDto changeCompilation(@Positive @PathVariable Long compId,
                                            @Valid @RequestBody UpdateCompilationRequest dto) {
        return compilationService.changeCompilation(compId, dto);
    }
}