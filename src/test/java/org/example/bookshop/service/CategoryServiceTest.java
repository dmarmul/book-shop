package org.example.bookshop.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.example.bookshop.util.TestUtil.CATEGORY_DESCRIPTION;
import static org.example.bookshop.util.TestUtil.CATEGORY_NAME;
import static org.example.bookshop.util.TestUtil.FIRST_ID;
import static org.example.bookshop.util.TestUtil.THIRD_ID;
import static org.example.bookshop.util.TestUtil.UNIQUE_PARAM;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.example.bookshop.dto.CategoryDto;
import org.example.bookshop.exception.EntityNotFoundException;
import org.example.bookshop.mapper.CategoryMapper;
import org.example.bookshop.model.Category;
import org.example.bookshop.repository.CategoryRepository;
import org.example.bookshop.service.impl.CategoryServiceImpl;
import org.example.bookshop.util.TestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {
    private static final CategoryDto categoryDto = new CategoryDto();
    private static final Category category = new Category();
    private static final String EXCEPTION_MESSAGE = "Can't find category by id: ";

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @BeforeEach
    void beforeEach() {
        TestUtil.setCategoryDtoParam(categoryDto);
    }

    @Test
    @DisplayName("Save valid category to DB")
    void saveCategory_ValidRequestDto_ReturnCategoryDto() {
        // When
        when(categoryMapper.toModel(categoryDto)).thenReturn(category);
        when(categoryRepository.save(category)).thenReturn(category);
        when(categoryMapper.toDto(category)).thenReturn(categoryDto);
        CategoryDto actualCategory = categoryService.save(categoryDto);
        // Then
        assertThat(actualCategory).isEqualTo(categoryDto);
        verify(categoryRepository, times(1)).save(category);
        verifyNoMoreInteractions(categoryRepository, categoryMapper);
    }

    @Test
    @DisplayName("Find all existing categories in DB")
    void findAll_ReturnListCategoryDto() {
        // Given
        Optional<Category> optionalCategory = Optional.of(category);
        // When
        when(categoryRepository.findById(FIRST_ID)).thenReturn(optionalCategory);
        when(categoryMapper.toDto(optionalCategory.get())).thenReturn(categoryDto);
        CategoryDto actualCategory = categoryService.findById(FIRST_ID);
        // Then
        assertThat(actualCategory).isEqualTo(categoryDto);
        verify(categoryRepository, times(1)).findById(FIRST_ID);
        verifyNoMoreInteractions(categoryRepository, categoryMapper);
    }

    @Test
    @DisplayName("Find category by valid id")
    void findById_ValidCategoryId_ReturnCategoryDto() {
        // Given
        Optional<Category> optionalCategory = Optional.of(category);
        // When
        when(categoryRepository.findById(FIRST_ID)).thenReturn(optionalCategory);
        when(categoryMapper.toDto(optionalCategory.get())).thenReturn(categoryDto);
        CategoryDto actualCategory = categoryService.findById(FIRST_ID);
        // Then
        assertThat(actualCategory).isEqualTo(categoryDto);
        verify(categoryRepository, times(1)).findById(FIRST_ID);
        verifyNoMoreInteractions(categoryRepository, categoryMapper);
    }

    @Test
    @DisplayName("Throw an exception because of non existing category id")
    void findById_NonExistingCategoryId_ThrowException() {
        // Given
        String expected = EXCEPTION_MESSAGE + THIRD_ID;
        // When
        when(categoryRepository.findById(THIRD_ID)).thenReturn(Optional.empty());
        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> categoryService.findById(THIRD_ID)
        );
        // Then
        assertEquals(expected, exception.getMessage());
    }

    @Test
    @DisplayName("Update category by valid id")
    void updateById_ValidCategoryId_ReturnCategoryDto() {
        // Given
        CategoryDto updateCategoryDto = new CategoryDto();
        updateCategoryDto.setName(CATEGORY_NAME + UNIQUE_PARAM);
        updateCategoryDto.setDescription(CATEGORY_DESCRIPTION + UNIQUE_PARAM);
        Category updateCategory = new Category();
        // When
        when(categoryRepository.findById(FIRST_ID)).thenReturn(Optional.of(updateCategory));
        doAnswer(invocation -> {
            updateCategory.setName(CATEGORY_NAME + UNIQUE_PARAM);
            updateCategory.setDescription(CATEGORY_DESCRIPTION + UNIQUE_PARAM);
            return null;
        }).when(categoryMapper).updateCategoryFromDto(updateCategoryDto, updateCategory);
        when(categoryRepository.save(updateCategory)).thenReturn(updateCategory);
        when(categoryMapper.toDto(updateCategory)).thenReturn(updateCategoryDto);

        CategoryDto actualCategory = categoryService.update(updateCategoryDto, FIRST_ID);

        assertThat(actualCategory).isEqualTo(updateCategoryDto);
        verify(categoryRepository, times(1)).findById(FIRST_ID);
        verify(categoryRepository, times(1)).save(updateCategory);
        verify(categoryMapper, times(1)).updateCategoryFromDto(updateCategoryDto, updateCategory);
        verify(categoryMapper, times(1)).toDto(updateCategory);
        verifyNoMoreInteractions(categoryRepository, categoryMapper);
    }

    @Test
    @DisplayName("Throw an exception because of non existing category id")
    void updateById_NonExistingCategoryId_ThrowException() {
        // Given
        String expected = EXCEPTION_MESSAGE + THIRD_ID;
        // When
        when(categoryRepository.findById(THIRD_ID)).thenReturn(Optional.empty());
        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> categoryService.update(categoryDto, THIRD_ID)
        );
        // Then
        assertEquals(expected, exception.getMessage());
    }
}
