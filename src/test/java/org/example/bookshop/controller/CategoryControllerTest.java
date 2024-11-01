package org.example.bookshop.controller;

import static org.example.bookshop.util.TestUtil.BOOK_AUTHOR;
import static org.example.bookshop.util.TestUtil.BOOK_ISBN;
import static org.example.bookshop.util.TestUtil.BOOK_PRICE;
import static org.example.bookshop.util.TestUtil.BOOK_TITLE;
import static org.example.bookshop.util.TestUtil.CATEGORY_DESCRIPTION;
import static org.example.bookshop.util.TestUtil.CATEGORY_NAME;
import static org.example.bookshop.util.TestUtil.FIRST_ID;
import static org.example.bookshop.util.TestUtil.SECOND_ID;
import static org.example.bookshop.util.TestUtil.THIRD_ID;
import static org.example.bookshop.util.TestUtil.UNIQUE_PARAM;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import org.example.bookshop.dto.BookDtoWithoutCategoryIds;
import org.example.bookshop.dto.CategoryDto;
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
class CategoryControllerTest {
    protected static MockMvc mockMvc;
    private static final CategoryDto categoryDto = new CategoryDto();

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    public static void beforeAll(@Autowired WebApplicationContext applicationContext) {
        mockMvc = TestUtil.buildMockMvc(applicationContext);
    }

    @BeforeEach
    public void beforeEach() {
        TestUtil.setCategoryDtoParam(categoryDto);
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    @DisplayName("Create a new category")
    void createCategory_ValidRequestDto_ReturnCategoryDto() throws Exception {
        // Given
        CategoryDto newCategoryDto = TestUtil.createNewCategoryDto();
        String jsonRequest = objectMapper.writeValueAsString(newCategoryDto);
        // When
        MvcResult result = mockMvc.perform(
                post("/categories")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andReturn();
        // Then
        CategoryDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), CategoryDto.class);
        EqualsBuilder.reflectionEquals(newCategoryDto, actual);
    }

    @Test
    @WithMockUser(roles = {"USER"})
    @DisplayName("Get all existing categories")
    void getAllCategories_ReturnListCategoryDto() throws Exception {
        // Given
        List<CategoryDto> expected = createCategoriesDto();
        // When
        MvcResult result = mockMvc.perform(
                        get("/categories")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        // Then
        CategoryDto[] actual = objectMapper.readValue(
                result.getResponse().getContentAsByteArray(), CategoryDto[].class);
        List<CategoryDto> actualList = Arrays.stream(actual).toList();
        Assertions.assertEquals(expected.size(), actualList.size());
        Assertions.assertTrue(IntStream.range(0, expected.size())
                .allMatch(i -> EqualsBuilder.reflectionEquals(expected.get(i), actualList.get(i))));
    }

    @Test
    @WithMockUser(roles = {"USER"})
    @DisplayName("Get category by valid id")
    void getCategoryById_ValidId_ReturnCategoryDto() throws Exception {
        // When
        MvcResult result = mockMvc.perform(
                        get("/categories/{id}", FIRST_ID))
                .andExpect(status().isOk())
                .andReturn();
        // Then
        CategoryDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), CategoryDto.class);
        EqualsBuilder.reflectionEquals(categoryDto, actual);
    }

    @Test
    @WithMockUser(roles = {"USER"})
    @DisplayName("Get books by valid category id")
    void getBooksByCategoryId_ValidId_ReturnListBookDtoWithoutCategoryIds() throws Exception {
        // Given
        List<BookDtoWithoutCategoryIds> expected = getBookDtoWithoutCategoryIds();
        // When
        MvcResult result = mockMvc.perform(
                        get("/categories/{id}/books", SECOND_ID)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        // Then
        BookDtoWithoutCategoryIds[] actual = objectMapper.readValue(
                result.getResponse().getContentAsByteArray(), BookDtoWithoutCategoryIds[].class);
        List<BookDtoWithoutCategoryIds> actualList = Arrays.stream(actual).toList();
        Assertions.assertEquals(expected.size(), actualList.size());
        Assertions.assertTrue(IntStream.range(0, expected.size())
                .allMatch(i -> EqualsBuilder.reflectionEquals(expected.get(i), actualList.get(i))));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    @DisplayName("Update category by valid id")
    void updateCategoryById_ValidRequestDtoAndId_ReturnCategoryDto() throws Exception {
        // Given
        CategoryDto updatedCategoryDto = TestUtil.createNewCategoryDto();
        String jsonRequest = objectMapper.writeValueAsString(updatedCategoryDto);
        // When
        MvcResult result = mockMvc.perform(
                        put("/categories/{id}", FIRST_ID)
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();
        // Then
        CategoryDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), CategoryDto.class);
        EqualsBuilder.reflectionEquals(updatedCategoryDto, actual);
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    @DisplayName("Delete category by valid id")
    void deleteCategoryById_ValidId_CallDeleteMethodOnce() throws Exception {
        // When
        mockMvc.perform(
                        MockMvcRequestBuilders.delete("/categories/{id}", FIRST_ID))
                .andExpect(status().isNoContent());
    }

    private List<BookDtoWithoutCategoryIds> getBookDtoWithoutCategoryIds() {
        BookDtoWithoutCategoryIds secondBookDto = new BookDtoWithoutCategoryIds();
        secondBookDto.setId(SECOND_ID);
        secondBookDto.setTitle(BOOK_TITLE + UNIQUE_PARAM);
        secondBookDto.setAuthor(BOOK_AUTHOR + UNIQUE_PARAM);
        secondBookDto.setIsbn(BOOK_ISBN + UNIQUE_PARAM);
        secondBookDto.setPrice(BOOK_PRICE);

        BookDtoWithoutCategoryIds thirdBookDto = new BookDtoWithoutCategoryIds();
        thirdBookDto.setId(THIRD_ID);
        thirdBookDto.setTitle(UNIQUE_PARAM + BOOK_TITLE);
        thirdBookDto.setAuthor(UNIQUE_PARAM + BOOK_AUTHOR);
        thirdBookDto.setIsbn(UNIQUE_PARAM + BOOK_ISBN);
        thirdBookDto.setPrice(BOOK_PRICE);

        return List.of(secondBookDto, thirdBookDto);
    }

    private List<CategoryDto> createCategoriesDto() {
        CategoryDto secondCategoryDto = new CategoryDto();
        secondCategoryDto.setId(SECOND_ID);
        secondCategoryDto.setName(CATEGORY_NAME + UNIQUE_PARAM);
        secondCategoryDto.setDescription(CATEGORY_DESCRIPTION + UNIQUE_PARAM);

        CategoryDto thirdCategoryDto = new CategoryDto();
        thirdCategoryDto.setId(THIRD_ID);
        thirdCategoryDto.setName(UNIQUE_PARAM + CATEGORY_NAME);
        thirdCategoryDto.setDescription(UNIQUE_PARAM + CATEGORY_DESCRIPTION);
        categoryDto.setId(FIRST_ID);

        return List.of(categoryDto, secondCategoryDto, thirdCategoryDto);
    }
}
