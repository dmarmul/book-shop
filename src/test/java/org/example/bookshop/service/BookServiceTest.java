package org.example.bookshop.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.example.bookshop.dto.BookDto;
import org.example.bookshop.dto.BookDtoWithoutCategoryIds;
import org.example.bookshop.dto.CreateBookRequestDto;
import org.example.bookshop.exception.EntityNotFoundException;
import org.example.bookshop.mapper.BookMapper;
import org.example.bookshop.model.Book;
import org.example.bookshop.repository.BookRepository;
import org.example.bookshop.service.impl.BookServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {
    private static final BookDto bookDto = new BookDto();
    private static final Book book = new Book();
    private static final CreateBookRequestDto requestDto = new CreateBookRequestDto();
    private static final Long BOOK_ID = 1L;
    private static final Long BOOK_SECOND_ID = 2L;
    private static final Long CATEGORY_ID = 2L;
    private static final Long NON_EXISTING_BOOK_ID = 100L;
    private static final String TITLE = "title";
    private static final String AUTHOR = "author";
    private static final String ISBN = "isbn";
    private static final String EXCEPTION_MESSAGE = "Can't find book by id: ";
    private static final String UPDATE_TITLE = "new title";
    private static final String UPDATE_AUTHOR = "new author";
    private static final String UPDATE_ISBN = "new isbn";

    @InjectMocks
    private BookServiceImpl bookService;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookMapper bookMapper;

    @BeforeEach
    void beforeEach() {
        requestDto.setTitle(TITLE);
        requestDto.setAuthor(AUTHOR);
        requestDto.setIsbn(ISBN);

        book.setTitle(requestDto.getTitle());
        book.setAuthor(requestDto.getAuthor());
        book.setIsbn(requestDto.getIsbn());

        bookDto.setId(BOOK_ID);
        bookDto.setTitle(book.getTitle());
        bookDto.setAuthor(book.getAuthor());
        bookDto.setIsbn(book.getIsbn());
    }

    @Test
    @DisplayName("verify save() method works")
    public void saveBook_ValidRequestDto_ReturnBookDto() {
        // When
        when(bookMapper.toModel(requestDto)).thenReturn(book);
        when(bookRepository.save(book)).thenReturn(book);
        when(bookMapper.toDto(book)).thenReturn(bookDto);
        BookDto actualBook = bookService.save(requestDto);
        // Then
        assertThat(actualBook).isEqualTo(bookDto);
        verify(bookRepository, times(1)).save(book);
        verifyNoMoreInteractions(bookRepository, bookMapper);
    }

    @Test
    void findById_ValidBookId_ReturnBookDto() {
        // Given
        Optional<Book> optionalBook = Optional.of(book);
        // When
        when(bookRepository.findById(BOOK_ID)).thenReturn(optionalBook);
        when(bookMapper.toDto(optionalBook.get())).thenReturn(bookDto);
        BookDto actualBook = bookService.findById(BOOK_ID);
        // Then
        assertThat(actualBook).isEqualTo(bookDto);
        verify(bookRepository, times(1)).findById(BOOK_ID);
        verifyNoMoreInteractions(bookRepository, bookMapper);
    }

    @Test
    void findById_NonExistingBookId_ThrowException() {
        // Given
        String expected = EXCEPTION_MESSAGE + NON_EXISTING_BOOK_ID;
        // When
        when(bookRepository.findById(NON_EXISTING_BOOK_ID)).thenReturn(Optional.empty());
        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> bookService.findById(NON_EXISTING_BOOK_ID)
        );
        // Then
        assertEquals(expected, exception.getMessage());
    }

    @Test
    void findAll_ReturnListBookDto() {
        // Given
        Book secondBook = new Book();
        secondBook.setTitle(TITLE);
        secondBook.setAuthor(AUTHOR);
        secondBook.setIsbn(ISBN);

        BookDto secondBookDto = new BookDto();
        secondBookDto.setId(BOOK_SECOND_ID);
        secondBookDto.setTitle(secondBook.getTitle());
        secondBookDto.setAuthor(secondBook.getAuthor());
        secondBookDto.setIsbn(secondBook.getIsbn());

        Pageable pageable = PageRequest.of(0, 2);
        Sort sort = Sort.by("id");
        Page<Book> bookPage = new PageImpl<>(List.of(book, secondBook), pageable, 2);
        // When
        when(bookRepository.findAll(pageable)).thenReturn(bookPage);
        when(bookMapper.toDto(book)).thenReturn(bookDto);
        when(bookMapper.toDto(secondBook)).thenReturn(secondBookDto);
        List<BookDto> actualList = bookService.findAll(sort, pageable);
        // Then
        assertThat(actualList).isEqualTo(List.of(bookDto, secondBookDto));
        verify(bookRepository, times(1)).findAll(pageable);
        verifyNoMoreInteractions(bookRepository, bookMapper);
    }

    @Test
    void updateById_ValidBookId_ReturnBookDto() {
        // Given
        CreateBookRequestDto updateRequestDto = new CreateBookRequestDto();
        updateRequestDto.setTitle(UPDATE_TITLE);
        updateRequestDto.setAuthor(UPDATE_AUTHOR);
        updateRequestDto.setIsbn(UPDATE_ISBN);

        BookDto updatedBookDto = new BookDto();
        updatedBookDto.setId(BOOK_SECOND_ID);
        updatedBookDto.setTitle(updateRequestDto.getTitle());
        updatedBookDto.setAuthor(updateRequestDto.getAuthor());
        updatedBookDto.setIsbn(updateRequestDto.getIsbn());
        Book updateBook = new Book();
        // When
        when(bookRepository.findById(BOOK_ID)).thenReturn(Optional.of(updateBook));
        doAnswer(invocation -> {
            updateBook.setTitle(updateRequestDto.getTitle());
            updateBook.setAuthor(updateRequestDto.getAuthor());
            updateBook.setIsbn(updateRequestDto.getIsbn());
            return null;
        }).when(bookMapper).updateBookFromDto(updateRequestDto, updateBook);
        when(bookRepository.save(updateBook)).thenReturn(updateBook);
        when(bookMapper.toDto(updateBook)).thenReturn(updatedBookDto);
        BookDto actualBook = bookService.update(updateRequestDto, BOOK_ID);
        // Then
        assertThat(actualBook).isEqualTo(updatedBookDto);
        verify(bookRepository, times(1)).findById(BOOK_ID);
        verify(bookRepository, times(1)).save(updateBook);
        verify(bookMapper, times(1)).updateBookFromDto(updateRequestDto, updateBook);
        verify(bookMapper, times(1)).toDto(updateBook);
        verifyNoMoreInteractions(bookRepository, bookMapper);
    }

    @Test
    void updateByI_NonExistingBookId_ThrowException() {
        // Given
        String expected = EXCEPTION_MESSAGE + NON_EXISTING_BOOK_ID;
        // When
        when(bookRepository.findById(NON_EXISTING_BOOK_ID)).thenReturn(Optional.empty());
        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> bookService.update(requestDto, NON_EXISTING_BOOK_ID)
        );
        // Then
        assertEquals(expected, exception.getMessage());
    }

    @Test
    void deleteById_ValidBookId_CallDeleteMethodOnce() {
        // When
        bookService.delete(BOOK_ID);
        // Then
        verify(bookRepository, times(1)).deleteById(BOOK_ID);
    }

    @Test
    void findAllByCategoryId_ReturnListBookDtoWithoutCategoryIds() {
        // Given
        Book secondBook = new Book();
        secondBook.setTitle(TITLE);
        secondBook.setAuthor(AUTHOR);
        secondBook.setIsbn(ISBN);
        List<BookDtoWithoutCategoryIds> booksDtoWithoutCategoryIds =
                createBookDtoWithoutCategoryIdsList();
        // When
        when(bookRepository.findAllByCategoriesId(CATEGORY_ID))
                .thenReturn(List.of(book, secondBook));
        when(bookMapper.toDtoWithoutCategories(book))
                .thenReturn(booksDtoWithoutCategoryIds.get(0));
        when(bookMapper.toDtoWithoutCategories(secondBook))
                .thenReturn(booksDtoWithoutCategoryIds.get(1));

        List<BookDtoWithoutCategoryIds> actualList = bookService
                .findAllByCategoryId(Sort.by("id"),
                        PageRequest.of(0, 2), CATEGORY_ID);
        // Then
        assertThat(actualList).isEqualTo(booksDtoWithoutCategoryIds);
        verify(bookRepository, times(1)).findAllByCategoriesId(CATEGORY_ID);
        verifyNoMoreInteractions(bookRepository, bookMapper);
    }

    private List<BookDtoWithoutCategoryIds> createBookDtoWithoutCategoryIdsList() {
        BookDtoWithoutCategoryIds firstBookDtoWithoutCategory = new BookDtoWithoutCategoryIds();
        firstBookDtoWithoutCategory.setId(bookDto.getId());
        firstBookDtoWithoutCategory.setTitle(bookDto.getTitle());
        firstBookDtoWithoutCategory.setAuthor(bookDto.getAuthor());
        firstBookDtoWithoutCategory.setIsbn(bookDto.getIsbn());

        BookDtoWithoutCategoryIds secondBookDtoWithoutCategory = new BookDtoWithoutCategoryIds();
        secondBookDtoWithoutCategory.setId(BOOK_SECOND_ID);
        secondBookDtoWithoutCategory.setTitle(TITLE);
        secondBookDtoWithoutCategory.setAuthor(AUTHOR);
        secondBookDtoWithoutCategory.setIsbn(ISBN);
        return List.of(firstBookDtoWithoutCategory, secondBookDtoWithoutCategory);
    }
}
