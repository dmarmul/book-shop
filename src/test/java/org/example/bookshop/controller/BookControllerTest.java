package org.example.bookshop.controller;

import static org.example.bookshop.util.TestUtil.BOOK_AUTHOR;
import static org.example.bookshop.util.TestUtil.BOOK_ISBN;
import static org.example.bookshop.util.TestUtil.BOOK_PRICE;
import static org.example.bookshop.util.TestUtil.BOOK_TITLE;
import static org.example.bookshop.util.TestUtil.FIRST_ID;
import static org.example.bookshop.util.TestUtil.NEW_PARAM;
import static org.example.bookshop.util.TestUtil.SECOND_ID;
import static org.example.bookshop.util.TestUtil.THIRD_ID;
import static org.example.bookshop.util.TestUtil.UNIQUE_PARAM;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;
import org.example.bookshop.dto.BookDto;
import org.example.bookshop.dto.CategoryDto;
import org.example.bookshop.dto.CreateBookRequestDto;
import org.example.bookshop.util.TestUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = {"classpath:database/categories/delete-all-categories-from-categories-table.sql",
        "classpath:database/books/delete-all-books-from-books-table.sql"},
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(scripts = {"classpath:database/categories/add-three-categories-to-categories-table.sql",
        "classpath:database/books/add-three-books-to-books-table.sql",
        "classpath:database/categories/add-three-categories-to-books_categories-table.sql"},
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = {"classpath:database/categories/delete-all-categories-from-categories-table.sql",
        "classpath:database/books/delete-all-books-from-books-table.sql"},
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class BookControllerTest {
    protected static MockMvc mockMvc;
    private static final BookDto bookDto = new BookDto();
    private static final CategoryDto categoryDto = new CategoryDto();
    private static final CreateBookRequestDto requestDto = new CreateBookRequestDto();
    private static final Set<CategoryDto> categories = new HashSet<>();

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    public static void beforeAll(@Autowired WebApplicationContext applicationContext) {
        mockMvc = TestUtil.buildMockMvc(applicationContext);
    }

    @BeforeEach
    public void beforeEach() {
        TestUtil.setCategoryDtoParam(categoryDto);
        categories.add(categoryDto);

        bookDto.setId(FIRST_ID);
        bookDto.setTitle(BOOK_TITLE);
        bookDto.setAuthor(BOOK_AUTHOR);
        bookDto.setIsbn(BOOK_ISBN);
        bookDto.setPrice(BOOK_PRICE);
        bookDto.setCategoryIds(Set.of(FIRST_ID));

        requestDto.setTitle(BOOK_TITLE);
        requestDto.setAuthor(BOOK_AUTHOR);
        requestDto.setIsbn(BOOK_ISBN);
        requestDto.setPrice(BOOK_PRICE);
        requestDto.setCategories(categories);
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    @DisplayName("Create a new book")
    void createBook_ValidRequestDto_ReturnCreateBookRequestDto() throws Exception {
        // Given
        CreateBookRequestDto saveRequestDto = createNewBookRequestDto();
        String jsonRequest = objectMapper.writeValueAsString(saveRequestDto);
        // When
        MvcResult result = mockMvc.perform(
                post("/books")
                .content(jsonRequest)
                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andReturn();
        // Then
        BookDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), BookDto.class);
        EqualsBuilder.reflectionEquals(saveRequestDto, actual);
    }

    @Test
    @WithMockUser(roles = {"USER"})
    @DisplayName("Get all existing books")
    void getAllBooks_ReturnListBookDto() throws Exception {
        // Given
        List<BookDto> expected = createCategoriesDto();
        // When
        MvcResult result = mockMvc.perform(
                get("/books")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        // Then
        BookDto[] actual = objectMapper.readValue(
                result.getResponse().getContentAsByteArray(), BookDto[].class);
        List<BookDto> actualList = Arrays.stream(actual).toList();
        Assertions.assertEquals(expected.size(), actualList.size());
        Assertions.assertTrue(IntStream.range(0, expected.size())
                .allMatch(i -> EqualsBuilder.reflectionEquals(expected.get(i), actualList.get(i))));
    }

    @Test
    @WithMockUser(roles = {"USER"})
    @DisplayName("Get book by valid id")
    void getBookById_ValidId_ReturnBookDto() throws Exception {
        // When
        MvcResult result = mockMvc.perform(
                        get("/books/{id}", FIRST_ID))
                .andExpect(status().isOk())
                .andReturn();
        // Then
        BookDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), BookDto.class);
        EqualsBuilder.reflectionEquals(bookDto, actual);
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    @DisplayName("Update book by valid id")
    void updateBookById_ValidRequestDtoAndId_ReturnBookDto() throws Exception {
        // Given
        CreateBookRequestDto updatedRequestDto = createNewBookRequestDto();

        bookDto.setTitle(BOOK_TITLE + NEW_PARAM);
        bookDto.setAuthor(BOOK_AUTHOR + NEW_PARAM);
        bookDto.setIsbn(BOOK_ISBN + NEW_PARAM);

        String jsonRequest = objectMapper.writeValueAsString(updatedRequestDto);
        // When
        MvcResult result = mockMvc.perform(
                        put("/books/{id}", FIRST_ID)
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();
        // Then
        BookDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), BookDto.class);

        EqualsBuilder.reflectionEquals(bookDto, actual);
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    @DisplayName("Delete book by valid id")
    void deleteBookById_ValidId_CallDeleteMethodOnce() throws Exception {
        // When
        mockMvc.perform(
                MockMvcRequestBuilders.delete("/books/{id}", FIRST_ID))
                .andExpect(status().isNoContent());
    }

    private CreateBookRequestDto createNewBookRequestDto() {
        CreateBookRequestDto requestDto = new CreateBookRequestDto();
        requestDto.setTitle(BOOK_TITLE + NEW_PARAM);
        requestDto.setAuthor(BOOK_AUTHOR + NEW_PARAM);
        requestDto.setIsbn(BOOK_ISBN + NEW_PARAM);
        requestDto.setPrice(BOOK_PRICE);
        requestDto.setCategories(categories);

        return requestDto;
    }

    private List<BookDto> createCategoriesDto() {
        Set<Long> categoriesId = Set.of(SECOND_ID);
        BookDto secondBookDto = new BookDto();
        secondBookDto.setId(SECOND_ID);
        secondBookDto.setTitle(BOOK_TITLE + UNIQUE_PARAM);
        secondBookDto.setAuthor(BOOK_AUTHOR + UNIQUE_PARAM);
        secondBookDto.setIsbn(BOOK_ISBN + UNIQUE_PARAM);
        secondBookDto.setPrice(BOOK_PRICE);
        secondBookDto.setCategoryIds(categoriesId);

        BookDto thirdBookDto = new BookDto();
        thirdBookDto.setId(THIRD_ID);
        thirdBookDto.setTitle(UNIQUE_PARAM + BOOK_TITLE);
        thirdBookDto.setAuthor(UNIQUE_PARAM + BOOK_AUTHOR);
        thirdBookDto.setIsbn(UNIQUE_PARAM + BOOK_ISBN);
        thirdBookDto.setPrice(BOOK_PRICE);
        thirdBookDto.setCategoryIds(categoriesId);

        return List.of(bookDto, secondBookDto, thirdBookDto);
    }
}
