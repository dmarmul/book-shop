package org.example.bookshop.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
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
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {
    private static final CategoryDto categoryDto = new CategoryDto();
    private static final Category category = new Category();
    private static final Long CATEGORY_ID = 1L;
    private static final Long NON_EXISTING_CATEGORY_ID = 100L;
    private static final String CATEGORY_NAME = "Category 1";
    private static final String CATEGORY_DESCRIPTION = "Description 1";
    private static final String UPDATE_NAME = "new name";
    private static final String UPDATE_DESCRIPTION = "new description";

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @BeforeAll
    static void beforeAll() {
        categoryDto.setName(CATEGORY_NAME);
        categoryDto.setDescription(CATEGORY_DESCRIPTION);
    }

    @Test
    void save() {
        when(categoryMapper.toModel(categoryDto)).thenReturn(category);
        when(categoryRepository.save(category)).thenReturn(category);
        when(categoryMapper.toDto(category)).thenReturn(categoryDto);

        CategoryDto actualCategory = categoryService.save(categoryDto);

        assertThat(actualCategory).isEqualTo(categoryDto);
        verify(categoryRepository, times(1)).save(category);
        verifyNoMoreInteractions(categoryRepository, categoryMapper);
    }

    @Test
    void findAll_ShouldReturnValidListCategoryDto() {
        Optional<Category> optionalCategory = Optional.of(category);

        when(categoryRepository.findById(CATEGORY_ID)).thenReturn(optionalCategory);
        when(categoryMapper.toDto(optionalCategory.get())).thenReturn(categoryDto);

        CategoryDto actualCategory = categoryService.findById(CATEGORY_ID);

        assertThat(actualCategory).isEqualTo(categoryDto);
        verify(categoryRepository, times(1)).findById(CATEGORY_ID);
        verifyNoMoreInteractions(categoryRepository, categoryMapper);
    }

    @Test
    void findById_WithValidCategoryId_ShouldReturnValidCategoryDto() {
        Optional<Category> optionalCategory = Optional.of(category);

        when(categoryRepository.findById(CATEGORY_ID)).thenReturn(optionalCategory);
        when(categoryMapper.toDto(optionalCategory.get())).thenReturn(categoryDto);

        CategoryDto actualCategory = categoryService.findById(CATEGORY_ID);

        assertThat(actualCategory).isEqualTo(categoryDto);
        verify(categoryRepository, times(1)).findById(CATEGORY_ID);
        verifyNoMoreInteractions(categoryRepository, categoryMapper);
    }

    @Test
    void findById_WithNonExistingCategoryId_ShouldThrowException() {
        when(categoryRepository.findById(NON_EXISTING_CATEGORY_ID)).thenReturn(Optional.empty());

        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> categoryService.findById(NON_EXISTING_CATEGORY_ID)
        );

        String expected = "Can't find category by id: " + NON_EXISTING_CATEGORY_ID;
        String actual = exception.getMessage();

        assertEquals(expected, actual);
    }

    @Test
    void updateByIdWithValidCategoryId_ShouldReturnValidCategoryDto() {
        CategoryDto updateCategoryDto = new CategoryDto();
        updateCategoryDto.setName(UPDATE_NAME);
        updateCategoryDto.setDescription(UPDATE_DESCRIPTION);

        Category updateCategory = new Category();

        when(categoryRepository.findById(CATEGORY_ID)).thenReturn(Optional.of(updateCategory));
        doAnswer(invocation -> {
            updateCategory.setName(UPDATE_NAME);
            updateCategory.setDescription(UPDATE_DESCRIPTION);
            return null;
        }).when(categoryMapper).updateCategoryFromDto(updateCategoryDto, updateCategory);
        when(categoryRepository.save(updateCategory)).thenReturn(updateCategory);
        when(categoryMapper.toDto(updateCategory)).thenReturn(updateCategoryDto);

        CategoryDto actualCategory = categoryService.update(updateCategoryDto, CATEGORY_ID);

        assertThat(actualCategory).isEqualTo(updateCategoryDto);
        verify(categoryRepository, times(1)).findById(CATEGORY_ID);
        verify(categoryRepository, times(1)).save(updateCategory);
        verify(categoryMapper, times(1)).updateCategoryFromDto(updateCategoryDto, updateCategory);
        verify(categoryMapper, times(1)).toDto(updateCategory);
        verifyNoMoreInteractions(categoryRepository, categoryMapper);
    }

    @Test
    void updateByIdWithNonExistingCategoryId_ShouldThrowException() {
        when(categoryRepository.findById(NON_EXISTING_CATEGORY_ID)).thenReturn(Optional.empty());

        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> categoryService.update(categoryDto, NON_EXISTING_CATEGORY_ID)
        );

        String expected = "Can't find category by id: " + NON_EXISTING_CATEGORY_ID;
        String actual = exception.getMessage();

        assertEquals(expected, actual);
    }
}
