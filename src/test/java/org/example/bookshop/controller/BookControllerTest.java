package org.example.bookshop.controller;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;
import org.example.bookshop.dto.BookDto;
import org.example.bookshop.dto.CategoryDto;
import org.example.bookshop.dto.CreateBookRequestDto;
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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = {"classpath:database/categories/delete-all-categories-from-categories-table.sql",
        "classpath:database/books/delete-all-books-from-books-table.sql",
        "classpath:database/categories/add-three-categories-to-categories-table.sql",
        "classpath:database/books/add-three-books-to-books-table.sql",
        "classpath:database/categories/add-three-categories-to-books_categories-table.sql"},
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class BookControllerTest {
    protected static MockMvc mockMvc;
    private static final BigDecimal BOOK_PRICE = BigDecimal.valueOf(200);
    private static final Long FIRST_ID = 1L;
    private static final Long SECOND_ID = 2L;
    private static final Long THIRD_ID = 3L;
    private static final String CATEGORY_NAME = "Category 1";
    private static final String CATEGORY_DESCRIPTION = "Description 1";
    private static final String BOOK_TITLE = "Book 1";
    private static final String BOOK_AUTHOR = "Author 1";
    private static final String BOOK_ISBN = "ISBN 1";
    private static final String UNIQUE_PARAM = "unique";
    private static final String NEW_BOOK_PARAM = "new param";
    private static final BookDto bookDto = new BookDto();
    private static final CategoryDto categoryDto = new CategoryDto();
    private static final CreateBookRequestDto requestDto = new CreateBookRequestDto();
    private static final Set<CategoryDto> categories = new HashSet<>();

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    public static void beforeAll(@Autowired WebApplicationContext applicationContext) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
    }

    @BeforeEach
    public void beforeEach() {
        categoryDto.setId(FIRST_ID);
        categoryDto.setName(CATEGORY_NAME);
        categoryDto.setDescription(CATEGORY_DESCRIPTION);
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
        CreateBookRequestDto saveRequestDto = new CreateBookRequestDto();
        saveRequestDto.setTitle(BOOK_TITLE + NEW_BOOK_PARAM);
        saveRequestDto.setAuthor(BOOK_AUTHOR + NEW_BOOK_PARAM);
        saveRequestDto.setIsbn(BOOK_ISBN + NEW_BOOK_PARAM);
        saveRequestDto.setPrice(BOOK_PRICE);
        saveRequestDto.setCategories(categories);
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
        CreateBookRequestDto updatedRequestDto = new CreateBookRequestDto();
        updatedRequestDto.setTitle(BOOK_TITLE + NEW_BOOK_PARAM);
        updatedRequestDto.setAuthor(BOOK_AUTHOR + NEW_BOOK_PARAM);
        updatedRequestDto.setIsbn(BOOK_ISBN + NEW_BOOK_PARAM);
        updatedRequestDto.setPrice(BOOK_PRICE.add(BOOK_PRICE));
        updatedRequestDto.setCategories(categories);

        BookDto updatedBookDto = new BookDto();
        updatedBookDto.setId(FIRST_ID);
        updatedBookDto.setTitle(BOOK_TITLE + NEW_BOOK_PARAM);
        updatedBookDto.setAuthor(BOOK_AUTHOR + NEW_BOOK_PARAM);
        updatedBookDto.setIsbn(BOOK_ISBN + NEW_BOOK_PARAM);
        updatedBookDto.setPrice(BOOK_PRICE.add(BOOK_PRICE));
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

        EqualsBuilder.reflectionEquals(updatedBookDto, actual);
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
