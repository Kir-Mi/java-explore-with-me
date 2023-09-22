package ru.practicum.category.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exceptions.MyApplicationExceptions;

import java.util.Collection;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;

    @Override
    public Category saveCategory(Category category) {
        return categoryRepository.save(category);
    }

    @Override
    public void deleteCategoryById(Long catId) {
        Category category = categoryRepository.findById(catId).orElseThrow(() ->
                new MyApplicationExceptions(String.format("Category %s not found", catId), HttpStatus.NOT_FOUND));

        boolean isExist = eventRepository.existsByCategoryId(catId);

        if (isExist) {
            throw new MyApplicationExceptions(String.format("Category %s isn't empty", catId), HttpStatus.CONFLICT);
        } else {
            categoryRepository.delete(category);
        }
    }

    @Override
    public Category changeCategory(Long catId, Category category) {
        Category updated = categoryRepository.findById(catId).orElseThrow(() ->
                new MyApplicationExceptions(String.format("Category %s not found", catId), HttpStatus.NOT_FOUND));

        if (category.getName() != null && !category.getName().isBlank()) {
            updated.setName(category.getName());
        }

        return updated;
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<Category> getAllCategories(Pageable page) {
        return categoryRepository.findAll(page).getContent();
    }

    @Override
    @Transactional(readOnly = true)
    public Category getCategoryById(Long catId) {
        return categoryRepository.findById(catId).orElseThrow(() ->
                new MyApplicationExceptions(String.format("Category %s not found", catId), HttpStatus.NOT_FOUND));
    }
}