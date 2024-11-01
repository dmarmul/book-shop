package org.example.bookshop.util;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

import java.math.BigDecimal;
import org.example.bookshop.dto.CategoryDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

public class TestUtil {
    public static final BigDecimal BOOK_PRICE = BigDecimal.valueOf(200);
    public static final Long FIRST_ID = 1L;
    public static final Long SECOND_ID = 2L;
    public static final Long THIRD_ID = 3L;
    public static final String CATEGORY_NAME = "Category 1";
    public static final String CATEGORY_DESCRIPTION = "Description 1";
    public static final String BOOK_TITLE = "Book 1";
    public static final String BOOK_AUTHOR = "Author 1";
    public static final String BOOK_ISBN = "ISBN 1";
    public static final String UNIQUE_PARAM = "unique";
    public static final String NEW_PARAM = "new param";

    public static MockMvc buildMockMvc(@Autowired WebApplicationContext applicationContext) {
        return MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
    }

    public static void setCategoryDtoParam(CategoryDto categoryDto) {
        categoryDto.setId(FIRST_ID);
        categoryDto.setName(CATEGORY_NAME);
        categoryDto.setDescription(CATEGORY_DESCRIPTION);
    }

    public static CategoryDto createNewCategoryDto() {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setName(CATEGORY_NAME + NEW_PARAM);
        categoryDto.setDescription(CATEGORY_DESCRIPTION + NEW_PARAM);

        return categoryDto;
    }
}
