package org.example.bookshop.service;

import java.util.List;
import org.example.bookshop.dto.BookDto;
import org.example.bookshop.dto.BookDtoWithoutCategoryIds;
import org.example.bookshop.dto.CreateBookRequestDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public interface BookService {
    BookDto save(CreateBookRequestDto requestDto);

    BookDto findById(Long id);

    List<BookDto> findAll(Sort sort, Pageable pageable);

    BookDto update(CreateBookRequestDto requestDto, Long id);

    void delete(Long id);

    List<BookDtoWithoutCategoryIds> findAllByCategoryId(
            Sort sort, Pageable pageable, Long id);
}
