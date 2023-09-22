package ru.practicum.category.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;
import ru.practicum.category.model.Category;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.service.CategoryService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@RestController
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
@Validated
public class AdminCategoryController {
    private final CategoryService categoryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto saveCategory(@Valid @RequestBody NewCategoryDto dto) {
        Category category = categoryService.saveCategory(CategoryMapper.newToCategory(dto));
        return CategoryMapper.toCategoryDto(category);
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategoryById(@Positive @PathVariable Long catId) {
        categoryService.deleteCategoryById(catId);
    }

    @PatchMapping("/{catId}")
    public CategoryDto changeCategory(@Positive @PathVariable Long catId,
                                      @Valid @RequestBody NewCategoryDto dto) {
        Category category = categoryService
                .changeCategory(catId, CategoryMapper.newToCategory(dto));
        return CategoryMapper.toCategoryDto(category);
    }
}