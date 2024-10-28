package org.example.bookshop.controller;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import javax.sql.DataSource;
import org.example.bookshop.dto.BookDtoWithoutCategoryIds;
import org.example.bookshop.dto.CategoryDto;
import org.example.bookshop.repository.CategoryRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CategoryControllerTest {
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
    private static final String UPDATE_PARAM = "update";
    private static final CategoryDto categoryDto = new CategoryDto();

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private CategoryRepository categoryRepository;

    @BeforeAll
    public static void beforeAll(
            @Autowired DataSource dataSource,
            @Autowired WebApplicationContext applicationContext
    ) throws SQLException {
        categoryDto.setName(CATEGORY_NAME);
        categoryDto.setDescription(CATEGORY_DESCRIPTION);

        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            List<String> scriptPaths = List.of(
                    "database/categories/delete-all-categories-from-categories-table.sql",
                    "database/categories/add-category-to-categories-table.sql"
            );
            for (String scriptPath : scriptPaths) {
                ScriptUtils.executeSqlScript(connection, new ClassPathResource(scriptPath));
            }
        }
    }

    @Test
    @Sql(scripts = "classpath:database/categories/delete-all-categories-from-categories-table.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"classpath:database/categories/delete-all-categories-from-categories-table.sql",
            "classpath:database/categories/add-category-to-categories-table.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @WithMockUser(roles = {"ADMIN"})
    @DisplayName("Create a new category")
    void createCategory_ValidRequestDto_Success() throws Exception {
        String jsonRequest = objectMapper.writeValueAsString(categoryDto);
        MvcResult result = mockMvc.perform(
                post("/categories")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andReturn();
        CategoryDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), CategoryDto.class);
        EqualsBuilder.reflectionEquals(categoryDto, actual);
    }

    @Test
    @Sql(scripts = "classpath:database/categories/add-two-categories-to-categories-table.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/categories/delete-two-categories-from-categories-table.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @WithMockUser(roles = {"USER"})
    @DisplayName("Get all categories")
    void getAllCategories_Success() throws Exception {
        List<CategoryDto> expected = createCategoriesDto();

        MvcResult result = mockMvc.perform(
                        get("/categories")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        CategoryDto[] actual = objectMapper.readValue(
                result.getResponse().getContentAsByteArray(), CategoryDto[].class);
        List<CategoryDto> actualList = Arrays.stream(actual).toList();
        Assertions.assertEquals(expected.size(), actualList.size());
        Assertions.assertTrue(IntStream.range(0, expected.size())
                .allMatch(i -> EqualsBuilder.reflectionEquals(expected.get(i), actualList.get(i))));
    }

    @Test
    @WithMockUser(roles = {"USER"})
    @DisplayName("Get category by id")
    void getCategoryById_ValidId_Success() throws Exception {
        MvcResult result = mockMvc.perform(
                        get("/categories/{id}", FIRST_ID))
                .andExpect(status().isOk())
                .andReturn();
        CategoryDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), CategoryDto.class);

        EqualsBuilder.reflectionEquals(categoryDto, actual);
    }

    @Test
    @Sql(scripts = {"classpath:database/books/delete-all-books-from-books-table.sql",
            "classpath:database/books/add-two-books-to-books-table.sql",
            "classpath:database/categories/add-two-category-to-books_categories-table.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"classpath:database/books/delete-two-books-from-books-table.sql",
            "classpath:database/categories/delete-two-categories-from-categories-table.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @WithMockUser(roles = {"USER"})
    @DisplayName("Get books by category id")
    void getBooksByCategoryId_ValidId_Success() throws Exception {
        List<BookDtoWithoutCategoryIds> expected = getCategoryIds();

        MvcResult result = mockMvc.perform(
                        get("/categories/{id}/books", FIRST_ID)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        BookDtoWithoutCategoryIds[] actual = objectMapper.readValue(
                result.getResponse().getContentAsByteArray(), BookDtoWithoutCategoryIds[].class);
        List<BookDtoWithoutCategoryIds> actualList = Arrays.stream(actual).toList();
        Assertions.assertEquals(expected.size(), actualList.size());
        Assertions.assertTrue(IntStream.range(0, expected.size())
                .allMatch(i -> EqualsBuilder.reflectionEquals(expected.get(i), actualList.get(i))));
    }

    @Test
    @Sql(scripts = {"classpath:database/categories/delete-category-from-categories-table.sql",
            "classpath:database/categories/add-category-to-categories-table.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @WithMockUser(roles = {"ADMIN"})
    @DisplayName("Update category by id")
    void updateCategoryById_ValidRequestDtoAndId_Success() throws Exception {
        CategoryDto updatedCategoryDto = new CategoryDto();
        updatedCategoryDto.setName(CATEGORY_NAME + UPDATE_PARAM);
        updatedCategoryDto.setDescription(CATEGORY_DESCRIPTION + UPDATE_PARAM);

        String jsonRequest = objectMapper.writeValueAsString(updatedCategoryDto);

        MvcResult result = mockMvc.perform(
                        put("/categories/{id}", FIRST_ID)
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();
        CategoryDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), CategoryDto.class);

        EqualsBuilder.reflectionEquals(updatedCategoryDto, actual);
    }

    @Test
    @Sql(scripts = {"classpath:database/categories/delete-all-categories-from-categories-table.sql",
            "classpath:database/categories/add-category-to-categories-table.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @WithMockUser(roles = {"ADMIN"})
    @DisplayName("Delete category by id")
    void deleteCategoryById_ValidId_Success() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.delete("/categories/{id}", FIRST_ID))
                .andExpect(status().isNoContent());
    }

    private List<BookDtoWithoutCategoryIds> getCategoryIds() {
        BookDtoWithoutCategoryIds bookDto = new BookDtoWithoutCategoryIds();
        bookDto.setId(SECOND_ID);
        bookDto.setTitle(BOOK_TITLE + UNIQUE_PARAM);
        bookDto.setAuthor(BOOK_AUTHOR + UNIQUE_PARAM);
        bookDto.setIsbn(BOOK_ISBN + UNIQUE_PARAM);
        bookDto.setPrice(BOOK_PRICE);

        BookDtoWithoutCategoryIds secondBookDto = new BookDtoWithoutCategoryIds();
        secondBookDto.setId(THIRD_ID);
        secondBookDto.setTitle(UNIQUE_PARAM + BOOK_TITLE);
        secondBookDto.setAuthor(UNIQUE_PARAM + BOOK_AUTHOR);
        secondBookDto.setIsbn(UNIQUE_PARAM + BOOK_ISBN);
        secondBookDto.setPrice(BOOK_PRICE);

        return List.of(bookDto, secondBookDto);
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
