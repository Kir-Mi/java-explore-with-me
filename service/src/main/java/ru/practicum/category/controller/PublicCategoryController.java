package ru.practicum.category.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.model.Category;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.service.CategoryService;
import ru.practicum.utils.OffsetBasedPageRequest;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collection;
import java.util.stream.Collectors;

import static ru.practicum.utils.Constant.PAGE_DEFAULT_FROM;
import static ru.practicum.utils.Constant.PAGE_DEFAULT_SIZE;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
@Validated
public class PublicCategoryController {
    private final CategoryService categoryService;

    @GetMapping
    public Collection<CategoryDto> getAllCategories(@RequestParam(defaultValue = PAGE_DEFAULT_FROM) @PositiveOrZero Integer from,
                                                    @RequestParam(defaultValue = PAGE_DEFAULT_SIZE) @Positive Integer size) {
        Pageable page = new OffsetBasedPageRequest(from, size);
        return categoryService.getAllCategories(page)
                .stream()
                .map(CategoryMapper::toCategoryDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{catId}")
    public CategoryDto getCategoryById(@Positive @PathVariable Long catId) {
        Category category = categoryService.getCategoryById(catId);
        return CategoryMapper.toCategoryDto(category);
    }
}