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
import org.junit.jupiter.api.BeforeAll;
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
    private static final String UPDATE_TITLE = "new title";
    private static final String UPDATE_AUTHOR = "new author";
    private static final String UPDATE_ISBN = "new isbn";

    @InjectMocks
    private BookServiceImpl bookService;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookMapper bookMapper;

    @BeforeAll
    static void beforeAll() {
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
    public void saveBook_ValidRequestDto_ShouldReturnValidBookDto() {
        when(bookMapper.toModel(requestDto)).thenReturn(book);
        when(bookRepository.save(book)).thenReturn(book);
        when(bookMapper.toDto(book)).thenReturn(bookDto);

        BookDto actualBook = bookService.save(requestDto);

        assertThat(actualBook).isEqualTo(bookDto);
        verify(bookRepository, times(1)).save(book);
        verifyNoMoreInteractions(bookRepository, bookMapper);
    }

    @Test
    void findById_WithValidBookId_ShouldReturnValidBookDto() {
        Optional<Book> optionalBook = Optional.of(book);

        when(bookRepository.findById(BOOK_ID)).thenReturn(optionalBook);
        when(bookMapper.toDto(optionalBook.get())).thenReturn(bookDto);

        BookDto actualBook = bookService.findById(BOOK_ID);

        assertThat(actualBook).isEqualTo(bookDto);
        verify(bookRepository, times(1)).findById(BOOK_ID);
        verifyNoMoreInteractions(bookRepository, bookMapper);
    }

    @Test
    void findById_WithNonExistingBookId_ShouldThrowException() {
        when(bookRepository.findById(NON_EXISTING_BOOK_ID)).thenReturn(Optional.empty());

        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> bookService.findById(NON_EXISTING_BOOK_ID)
        );

        String expected = "Can't find book by id: " + NON_EXISTING_BOOK_ID;
        String actual = exception.getMessage();

        assertEquals(expected, actual);
    }

    @Test
    void findAll_ShouldReturnValidListBookDto() {
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

        when(bookRepository.findAll(pageable)).thenReturn(bookPage);
        when(bookMapper.toDto(book)).thenReturn(bookDto);
        when(bookMapper.toDto(secondBook)).thenReturn(secondBookDto);

        List<BookDto> actualList = bookService.findAll(sort, pageable);

        assertThat(actualList).isEqualTo(List.of(bookDto, secondBookDto));
        verify(bookRepository, times(1)).findAll(pageable);
        verifyNoMoreInteractions(bookRepository, bookMapper);
    }

    @Test
    void updateByIdWithValidBookId_ShouldReturnValidBookDto() {
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

        assertThat(actualBook).isEqualTo(updatedBookDto);
        verify(bookRepository, times(1)).findById(BOOK_ID);
        verify(bookRepository, times(1)).save(updateBook);
        verify(bookMapper, times(1)).updateBookFromDto(updateRequestDto, updateBook);
        verify(bookMapper, times(1)).toDto(updateBook);
        verifyNoMoreInteractions(bookRepository, bookMapper);
    }

    @Test
    void updateByIdWithNonExistingBookId_ShouldThrowException() {
        when(bookRepository.findById(NON_EXISTING_BOOK_ID)).thenReturn(Optional.empty());

        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> bookService.update(requestDto, NON_EXISTING_BOOK_ID)
        );

        String expected = "Can't find book by id: " + NON_EXISTING_BOOK_ID;
        String actual = exception.getMessage();

        assertEquals(expected, actual);
    }

    @Test
    void deleteByIdWithValidBookId_ShouldCallDeleteMethodOnce() {
        bookService.delete(BOOK_ID);

        verify(bookRepository, times(1)).deleteById(BOOK_ID);
    }

    @Test
    void findAllByCategoryId_ShouldReturnValidListBookDtoWithoutCategoryIds() {
        Book secondBook = new Book();
        secondBook.setTitle(TITLE);
        secondBook.setAuthor(AUTHOR);
        secondBook.setIsbn(ISBN);

        BookDto secondBookDto = new BookDto();
        secondBookDto.setId(BOOK_SECOND_ID);
        secondBookDto.setTitle(secondBook.getTitle());
        secondBookDto.setAuthor(secondBook.getAuthor());
        secondBookDto.setIsbn(secondBook.getIsbn());

        BookDtoWithoutCategoryIds firstBookDtoWithoutCategory = new BookDtoWithoutCategoryIds();
        firstBookDtoWithoutCategory.setId(bookDto.getId());
        firstBookDtoWithoutCategory.setTitle(bookDto.getTitle());
        firstBookDtoWithoutCategory.setAuthor(bookDto.getAuthor());
        firstBookDtoWithoutCategory.setIsbn(bookDto.getIsbn());

        BookDtoWithoutCategoryIds secondBookDtoWithoutCategory = new BookDtoWithoutCategoryIds();
        secondBookDtoWithoutCategory.setId(secondBookDto.getId());
        secondBookDtoWithoutCategory.setTitle(secondBookDto.getTitle());
        secondBookDtoWithoutCategory.setAuthor(secondBookDto.getAuthor());
        secondBookDtoWithoutCategory.setIsbn(secondBookDto.getIsbn());

        when(bookRepository.findAllByCategoriesId(CATEGORY_ID))
                .thenReturn(List.of(book, secondBook));
        when(bookMapper.toDtoWithoutCategories(book)).thenReturn(firstBookDtoWithoutCategory);
        when(bookMapper.toDtoWithoutCategories(secondBook))
                .thenReturn(secondBookDtoWithoutCategory);

        List<BookDtoWithoutCategoryIds> actualList = bookService
                .findAllByCategoryId(Sort.by("id"),
                        PageRequest.of(0, 2), CATEGORY_ID);

        assertThat(actualList).isEqualTo(
                List.of(firstBookDtoWithoutCategory, secondBookDtoWithoutCategory));
        verify(bookRepository, times(1)).findAllByCategoriesId(CATEGORY_ID);
        verifyNoMoreInteractions(bookRepository, bookMapper);
    }
}
