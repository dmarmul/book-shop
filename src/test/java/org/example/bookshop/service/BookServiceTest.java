package org.example.bookshop.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.example.bookshop.util.TestUtil.BOOK_AUTHOR;
import static org.example.bookshop.util.TestUtil.BOOK_ISBN;
import static org.example.bookshop.util.TestUtil.BOOK_TITLE;
import static org.example.bookshop.util.TestUtil.FIRST_ID;
import static org.example.bookshop.util.TestUtil.SECOND_ID;
import static org.example.bookshop.util.TestUtil.THIRD_ID;
import static org.example.bookshop.util.TestUtil.UNIQUE_PARAM;
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
    private static final String EXCEPTION_MESSAGE = "Can't find book by id: ";

    @InjectMocks
    private BookServiceImpl bookService;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookMapper bookMapper;

    @BeforeEach
    void beforeEach() {
        requestDto.setTitle(BOOK_TITLE);
        requestDto.setAuthor(BOOK_AUTHOR);
        requestDto.setIsbn(BOOK_ISBN);

        book.setTitle(requestDto.getTitle());
        book.setAuthor(requestDto.getAuthor());
        book.setIsbn(requestDto.getIsbn());

        bookDto.setId(FIRST_ID);
        bookDto.setTitle(book.getTitle());
        bookDto.setAuthor(book.getAuthor());
        bookDto.setIsbn(book.getIsbn());
    }

    @Test
    @DisplayName("Save valid book to DB")
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
    @DisplayName("Find book by valid id")
    void findById_ValidBookId_ReturnBookDto() {
        // Given
        Optional<Book> optionalBook = Optional.of(book);
        // When
        when(bookRepository.findById(FIRST_ID)).thenReturn(optionalBook);
        when(bookMapper.toDto(optionalBook.get())).thenReturn(bookDto);
        BookDto actualBook = bookService.findById(FIRST_ID);
        // Then
        assertThat(actualBook).isEqualTo(bookDto);
        verify(bookRepository, times(1)).findById(FIRST_ID);
        verifyNoMoreInteractions(bookRepository, bookMapper);
    }

    @Test
    @DisplayName("Throw an exception because of non existing book id")
    void findById_NonExistingBookId_ThrowException() {
        // Given
        String expected = EXCEPTION_MESSAGE + THIRD_ID;
        // When
        when(bookRepository.findById(THIRD_ID)).thenReturn(Optional.empty());
        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> bookService.findById(THIRD_ID)
        );
        // Then
        assertEquals(expected, exception.getMessage());
    }

    @Test
    @DisplayName("Find all existing books in DB")
    void findAll_ReturnListBookDto() {
        // Given
        Book secondBook = new Book();
        secondBook.setTitle(BOOK_TITLE);
        secondBook.setAuthor(BOOK_AUTHOR);
        secondBook.setIsbn(BOOK_ISBN);

        BookDto secondBookDto = new BookDto();
        secondBookDto.setId(SECOND_ID);
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
    @DisplayName("Update book by valid id")
    void updateById_ValidBookId_ReturnBookDto() {
        // Given
        requestDto.setTitle(BOOK_TITLE + UNIQUE_PARAM);
        requestDto.setAuthor(BOOK_AUTHOR + UNIQUE_PARAM);
        requestDto.setIsbn(BOOK_ISBN + UNIQUE_PARAM);

        bookDto.setTitle(requestDto.getTitle());
        bookDto.setAuthor(requestDto.getAuthor());
        bookDto.setIsbn(requestDto.getIsbn());
        Book updateBook = new Book();
        // When
        when(bookRepository.findById(FIRST_ID)).thenReturn(Optional.of(updateBook));
        doAnswer(invocation -> {
            updateBook.setTitle(bookDto.getTitle());
            updateBook.setAuthor(bookDto.getAuthor());
            updateBook.setIsbn(bookDto.getIsbn());
            return null;
        }).when(bookMapper).updateBookFromDto(requestDto, updateBook);
        when(bookRepository.save(updateBook)).thenReturn(updateBook);
        when(bookMapper.toDto(updateBook)).thenReturn(bookDto);
        BookDto actualBook = bookService.update(requestDto, FIRST_ID);
        // Then
        assertThat(actualBook).isEqualTo(bookDto);
        verify(bookRepository, times(1)).findById(FIRST_ID);
        verify(bookRepository, times(1)).save(updateBook);
        verify(bookMapper, times(1)).updateBookFromDto(requestDto, updateBook);
        verify(bookMapper, times(1)).toDto(updateBook);
        verifyNoMoreInteractions(bookRepository, bookMapper);
    }

    @Test
    @DisplayName("Throw an exception because of non existing book id")
    void updateByI_NonExistingBookId_ThrowException() {
        // Given
        String expected = EXCEPTION_MESSAGE + THIRD_ID;
        // When
        when(bookRepository.findById(THIRD_ID)).thenReturn(Optional.empty());
        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> bookService.update(requestDto, THIRD_ID)
        );
        // Then
        assertEquals(expected, exception.getMessage());
    }

    @Test
    @DisplayName("Delete book by id")
    void deleteById_ValidBookId_CallDeleteMethodOnce() {
        // When
        bookService.delete(FIRST_ID);
        // Then
        verify(bookRepository, times(1)).deleteById(FIRST_ID);
    }

    @Test
    @DisplayName("Find all books with same category id in DB")
    void findAllByCategoryId_ReturnListBookDtoWithoutCategoryIds() {
        // Given
        Book secondBook = new Book();
        secondBook.setTitle(BOOK_TITLE + UNIQUE_PARAM);
        secondBook.setAuthor(BOOK_AUTHOR + UNIQUE_PARAM);
        secondBook.setIsbn(BOOK_ISBN + UNIQUE_PARAM);
        List<BookDtoWithoutCategoryIds> booksDtoWithoutCategoryIds =
                createBookDtoWithoutCategoryIdsList();
        // When
        when(bookRepository.findAllByCategoriesId(SECOND_ID))
                .thenReturn(List.of(book, secondBook));
        when(bookMapper.toDtoWithoutCategories(book))
                .thenReturn(booksDtoWithoutCategoryIds.get(0));
        when(bookMapper.toDtoWithoutCategories(secondBook))
                .thenReturn(booksDtoWithoutCategoryIds.get(1));

        List<BookDtoWithoutCategoryIds> actualList = bookService
                .findAllByCategoryId(Sort.by("id"),
                        PageRequest.of(0, 2), SECOND_ID);
        // Then
        assertThat(actualList).isEqualTo(booksDtoWithoutCategoryIds);
        verify(bookRepository, times(1)).findAllByCategoriesId(SECOND_ID);
        verifyNoMoreInteractions(bookRepository, bookMapper);
    }

    private List<BookDtoWithoutCategoryIds> createBookDtoWithoutCategoryIdsList() {
        BookDtoWithoutCategoryIds firstBookDtoWithoutCategory = new BookDtoWithoutCategoryIds();
        firstBookDtoWithoutCategory.setId(FIRST_ID);
        firstBookDtoWithoutCategory.setTitle(BOOK_TITLE);
        firstBookDtoWithoutCategory.setAuthor(BOOK_AUTHOR);
        firstBookDtoWithoutCategory.setIsbn(BOOK_ISBN);

        BookDtoWithoutCategoryIds secondBookDtoWithoutCategory = new BookDtoWithoutCategoryIds();
        secondBookDtoWithoutCategory.setId(SECOND_ID);
        secondBookDtoWithoutCategory.setTitle(BOOK_TITLE + UNIQUE_PARAM);
        secondBookDtoWithoutCategory.setAuthor(BOOK_AUTHOR + UNIQUE_PARAM);
        secondBookDtoWithoutCategory.setIsbn(BOOK_ISBN + UNIQUE_PARAM);

        return List.of(firstBookDtoWithoutCategory, secondBookDtoWithoutCategory);
    }
}
