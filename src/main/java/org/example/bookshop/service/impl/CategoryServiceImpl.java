package org.example.bookshop.service.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.bookshop.dto.CategoryDto;
import org.example.bookshop.exception.EntityNotFoundException;
import org.example.bookshop.mapper.CategoryMapper;
import org.example.bookshop.model.Category;
import org.example.bookshop.repository.CategoryRepository;
import org.example.bookshop.service.CategoryService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public List<CategoryDto> findAll(Sort sort, Pageable pageable) {
        return categoryRepository.findAll().stream()
                .map(categoryMapper::toDto)
                .toList();
    }

    @Override
    public CategoryDto findById(Long id) {
        Category category = categoryRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can't find category by id: " + id));
        return categoryMapper.toDto(category);
    }

    @Override
    public CategoryDto save(CategoryDto categoryDto) {
        Category category = categoryMapper.toModel(categoryDto);
        return categoryMapper.toDto(categoryRepository.save(category));
    }

    @Override
    public CategoryDto update(CategoryDto categoryDto, Long id) {
        Category category = categoryMapper.toModel(categoryDto);
        category.setName(categoryDto.getName());
        category.setDescription(categoryDto.getDescription());

        return categoryMapper.toDto(categoryRepository.save(category));
    }

    @Override
    public void delete(Long id) {
        categoryRepository.deleteById(id);
    }
}
