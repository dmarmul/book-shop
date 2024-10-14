package org.example.bookshop.service;

import java.util.List;
import org.example.bookshop.dto.CategoryDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public interface CategoryService {
    List<CategoryDto> findAll(Sort sort, Pageable pageable);

    CategoryDto findById(Long id);

    CategoryDto save(CategoryDto categoryDto);

    CategoryDto update(CategoryDto categoryDto, Long id);

    void delete(Long id);
}
