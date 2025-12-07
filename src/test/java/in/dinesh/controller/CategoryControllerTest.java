package in.dinesh.controller;

import in.dinesh.exceptions.AlreadyExistsException;
import in.dinesh.exceptions.ResourceNotFoundException;
import in.dinesh.model.Category;
import in.dinesh.service.category.ICategoryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ICategoryService categoryService;

    @Autowired
    private ObjectMapper objectMapper;

    private Category category;

    @BeforeEach
    void setUp() {
        category = new Category("Electronics");
        category.setId(1L);
    }

    @Test
    void getAllCategories_Success() throws Exception {
        given(categoryService.getAllCategories()).willReturn(Arrays.asList(category));

        mockMvc.perform(get("/api/v1/categories/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Found!"));
    }

    @Test
    void getCategoryById_Success() throws Exception {
        given(categoryService.getCategoryById(1L)).willReturn(category);

        mockMvc.perform(get("/api/v1/categories/category/{id}/category", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Found"));
    }

    @Test
    void getCategoryByName_Success() throws Exception {
        given(categoryService.getCategoryByName("Electronics")).willReturn(category);

        mockMvc.perform(get("/api/v1/categories/category/name/{name}", "Electronics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Found"));
    }

    @Test
    void addCategory_Success() throws Exception {
        given(categoryService.addCategory(any(Category.class))).willReturn(category);

        mockMvc.perform(post("/api/v1/categories/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(category)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success"));
    }

    @Test
    void updateCategory_Success() throws Exception {
        given(categoryService.updateCategory(any(Category.class), anyLong())).willReturn(category);

        mockMvc.perform(put("/api/v1/categories/category/{id}/update", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(category)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Update success!"));
    }

    @Test
    void deleteCategory_Success() throws Exception {
        doNothing().when(categoryService).deleteCategoryById(1L);

        mockMvc.perform(delete("/api/v1/categories/category/{id}/delete", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Found"));
    }

    @Test
    void getAllCategories_Error() throws Exception {
        given(categoryService.getAllCategories()).willThrow(new RuntimeException("Error"));

        mockMvc.perform(get("/api/v1/categories/all"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Error:"));
    }

    @Test
    void addCategory_AlreadyExists() throws Exception {
        given(categoryService.addCategory(any(Category.class)))
                .willThrow(new AlreadyExistsException("Category already exists!"));

        mockMvc.perform(post("/api/v1/categories/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(category)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Category already exists!"));
    }

    @Test
    void getCategoryById_NotFound() throws Exception {
        given(categoryService.getCategoryById(1L)).willThrow(new ResourceNotFoundException("Category not found!"));

        mockMvc.perform(get("/api/v1/categories/category/{id}/category", 1L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Category not found!"));
    }

    @Test
    void getCategoryByName_NotFound() throws Exception {
        given(categoryService.getCategoryByName("Electronics"))
                .willThrow(new ResourceNotFoundException("Category not found!"));

        mockMvc.perform(get("/api/v1/categories/category/name/{name}", "Electronics"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Category not found!"));
    }

    @Test
    void deleteCategory_NotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Category not found!")).when(categoryService).deleteCategoryById(1L);

        mockMvc.perform(delete("/api/v1/categories/category/{id}/delete", 1L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Category not found!"));
    }

    @Test
    void updateCategory_NotFound() throws Exception {
        given(categoryService.updateCategory(any(Category.class), anyLong()))
                .willThrow(new ResourceNotFoundException("Category not found!"));

        mockMvc.perform(put("/api/v1/categories/category/{id}/update", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(category)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Category not found!"));
    }
}
