package in.dinesh.controller;

import in.dinesh.dto.ProductDto;
import in.dinesh.exceptions.ResourceNotFoundException;
import in.dinesh.model.Category;
import in.dinesh.model.Product;
import in.dinesh.request.AddProductRequest;
import in.dinesh.request.ProductUpdateRequest;
import in.dinesh.service.product.IProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;

import in.dinesh.exceptions.AlreadyExistsException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    private Product product;
    private ProductDto productDto;
    private AddProductRequest addProductRequest;
    private ProductUpdateRequest productUpdateRequest;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setId(1L);
        product.setName("Laptop");

        productDto = new ProductDto();
        productDto.setId(1L);
        productDto.setName("Laptop");

        addProductRequest = new AddProductRequest();
        addProductRequest.setName("Laptop");
        addProductRequest.setCategory(new Category("Electronics"));

        productUpdateRequest = new ProductUpdateRequest();
        productUpdateRequest.setName("Updated Laptop");
        productUpdateRequest.setCategory(new Category("Electronics"));
    }

    @Test
    void getAllProducts_Success() throws Exception {
        given(productService.getAllProducts()).willReturn(Arrays.asList(product));
        given(productService.getConvertedProducts(any())).willReturn(Arrays.asList(productDto));

        mockMvc.perform(get("/api/v1/products/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    void getProductById_Success() throws Exception {
        given(productService.getProductById(1L)).willReturn(product);
        given(productService.convertToDto(any(Product.class))).willReturn(productDto);

        mockMvc.perform(get("/api/v1/products/product/{productId}/product", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("success"));
    }

    @Test
    void getProductById_NotFound() throws Exception {
        given(productService.getProductById(1L)).willThrow(new ResourceNotFoundException("Product not found!"));

        mockMvc.perform(get("/api/v1/products/product/{productId}/product", 1L))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void addProduct_Success() throws Exception {
        given(productService.addProduct(any(AddProductRequest.class))).willReturn(product);
        given(productService.convertToDto(any(Product.class))).willReturn(productDto);

        mockMvc.perform(post("/api/v1/products/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addProductRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Add product success!"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void addProduct_AlreadyExists() throws Exception {
        given(productService.addProduct(any(AddProductRequest.class)))
                .willThrow(new AlreadyExistsException("Product already exists!"));

        mockMvc.perform(post("/api/v1/products/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addProductRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Product already exists!"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateProduct_Success() throws Exception {
        given(productService.updateProduct(any(ProductUpdateRequest.class), anyLong())).willReturn(product);
        given(productService.convertToDto(any(Product.class))).willReturn(productDto);

        mockMvc.perform(put("/api/v1/products/product/{productId}/update", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productUpdateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Update product success!"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateProduct_NotFound() throws Exception {
        given(productService.updateProduct(any(ProductUpdateRequest.class), anyLong()))
                .willThrow(new ResourceNotFoundException("Product not found!"));

        mockMvc.perform(put("/api/v1/products/product/{productId}/update", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productUpdateRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Product not found!"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteProduct_Success() throws Exception {
        doNothing().when(productService).deleteProductById(1L);

        mockMvc.perform(delete("/api/v1/products/product/{productId}/delete", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Delete product success!"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteProduct_NotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Product not found!")).when(productService).deleteProductById(1L);

        mockMvc.perform(delete("/api/v1/products/product/{productId}/delete", 1L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Product not found!"));
    }

    @Test
    void searchProducts_Success() throws Exception {
        Page<Product> productPage = new PageImpl<>(Collections.singletonList(product));
        given(productService.getProductsWithFilters(any(), any(), any(), any(Pageable.class))).willReturn(productPage);
        given(productService.convertToDto(any(Product.class))).willReturn(productDto);

        mockMvc.perform(get("/api/v1/products/search")
                .param("search", "Laptop"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("success"));
    }

    @Test
    void searchProducts_SortDesc() throws Exception {
        Page<Product> productPage = new PageImpl<>(Collections.singletonList(product));
        given(productService.getProductsWithFilters(any(), any(), any(), any(Pageable.class))).willReturn(productPage);
        given(productService.convertToDto(any(Product.class))).willReturn(productDto);

        mockMvc.perform(get("/api/v1/products/search")
                .param("sortDir", "desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("success"));
    }

    @Test
    void searchProducts_SortAsc() throws Exception {
        Page<Product> productPage = new PageImpl<>(Collections.singletonList(product));
        given(productService.getProductsWithFilters(any(), any(), any(), any(Pageable.class))).willReturn(productPage);
        given(productService.convertToDto(any(Product.class))).willReturn(productDto);

        mockMvc.perform(get("/api/v1/products/search")
                .param("sortDir", "asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("success"));
    }

    @Test
    void searchProducts_Error() throws Exception {
        given(productService.getProductsWithFilters(any(), any(), any(), any(Pageable.class)))
                .willThrow(new RuntimeException("Error"));

        mockMvc.perform(get("/api/v1/products/search")
                .param("search", "Laptop"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("error"));
    }

    @Test
    void getProductByBrandAndName_Success() throws Exception {
        given(productService.getProductsByBrandAndName("Brand", "Name")).willReturn(Arrays.asList(product));
        given(productService.getConvertedProducts(any())).willReturn(Arrays.asList(productDto));

        mockMvc.perform(get("/api/v1/products/products/by/brand-and-name")
                .param("brandName", "Brand")
                .param("productName", "Name"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("success"));
    }

    @Test
    void getProductByBrandAndName_NotFound() throws Exception {
        given(productService.getProductsByBrandAndName("Brand", "Name")).willReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/products/products/by/brand-and-name")
                .param("brandName", "Brand")
                .param("productName", "Name"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("No products found "));
    }

    @Test
    void getProductByBrandAndName_Error() throws Exception {
        given(productService.getProductsByBrandAndName("Brand", "Name")).willThrow(new RuntimeException("Error"));

        mockMvc.perform(get("/api/v1/products/products/by/brand-and-name")
                .param("brandName", "Brand")
                .param("productName", "Name"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Error"));
    }

    @Test
    void getProductByCategoryAndBrand_Success() throws Exception {
        given(productService.getProductsByCategoryAndBrand("Category", "Brand")).willReturn(Arrays.asList(product));
        given(productService.getConvertedProducts(any())).willReturn(Arrays.asList(productDto));

        mockMvc.perform(get("/api/v1/products/products/by/category-and-brand")
                .param("category", "Category")
                .param("brand", "Brand"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("success"));
    }

    @Test
    void getProductByCategoryAndBrand_NotFound() throws Exception {
        given(productService.getProductsByCategoryAndBrand("Category", "Brand")).willReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/products/products/by/category-and-brand")
                .param("category", "Category")
                .param("brand", "Brand"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("No products found "));
    }

    @Test
    void getProductByCategoryAndBrand_Error() throws Exception {
        given(productService.getProductsByCategoryAndBrand("Category", "Brand"))
                .willThrow(new RuntimeException("Error"));

        mockMvc.perform(get("/api/v1/products/products/by/category-and-brand")
                .param("category", "Category")
                .param("brand", "Brand"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("error"));
    }

    @Test
    void getProductByName_Success() throws Exception {
        given(productService.getProductsByName("Name")).willReturn(Arrays.asList(product));
        given(productService.getConvertedProducts(any())).willReturn(Arrays.asList(productDto));

        mockMvc.perform(get("/api/v1/products/products/{name}/products", "Name"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("success"));
    }

    @Test
    void getProductByName_NotFound() throws Exception {
        given(productService.getProductsByName("Name")).willReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/products/products/{name}/products", "Name"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("No products found "));
    }

    @Test
    void getProductByName_Error() throws Exception {
        given(productService.getProductsByName("Name")).willThrow(new RuntimeException("Error"));

        mockMvc.perform(get("/api/v1/products/products/{name}/products", "Name"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("error"));
    }

    @Test
    void findProductByBrand_Success() throws Exception {
        given(productService.getProductsByBrand("Brand")).willReturn(Arrays.asList(product));
        given(productService.getConvertedProducts(any())).willReturn(Arrays.asList(productDto));

        mockMvc.perform(get("/api/v1/products/product/by-brand")
                .param("brand", "Brand"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("success"));
    }

    @Test
    void findProductByBrand_NotFound() throws Exception {
        given(productService.getProductsByBrand("Brand")).willReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/products/product/by-brand")
                .param("brand", "Brand"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("No products found "));
    }

    @Test
    void findProductByBrand_Error() throws Exception {
        given(productService.getProductsByBrand("Brand")).willThrow(new RuntimeException("Error"));

        mockMvc.perform(get("/api/v1/products/product/by-brand")
                .param("brand", "Brand"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Error"));
    }

    @Test
    void findProductByCategory_Success() throws Exception {
        given(productService.getProductsByCategory("Category")).willReturn(Arrays.asList(product));
        given(productService.getConvertedProducts(any())).willReturn(Arrays.asList(productDto));

        mockMvc.perform(get("/api/v1/products/product/{category}/all/products", "Category"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("success"));
    }

    @Test
    void findProductByCategory_NotFound() throws Exception {
        given(productService.getProductsByCategory("Category")).willReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/products/product/{category}/all/products", "Category"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("No products found "));
    }

    @Test
    void findProductByCategory_Error() throws Exception {
        given(productService.getProductsByCategory("Category")).willThrow(new RuntimeException("Error"));

        mockMvc.perform(get("/api/v1/products/product/{category}/all/products", "Category"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Error"));
    }

    @Test
    void countProductsByBrandAndName_Success() throws Exception {
        given(productService.countProductsByBrandAndName("Brand", "Name")).willReturn(5L);

        mockMvc.perform(get("/api/v1/products/product/count/by-brand/and-name")
                .param("brand", "Brand")
                .param("name", "Name"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Product count!"))
                .andExpect(jsonPath("$.data").value(5));
    }

    @Test
    void countProductsByBrandAndName_Error() throws Exception {
        given(productService.countProductsByBrandAndName("Brand", "Name")).willThrow(new RuntimeException("Error"));

        mockMvc.perform(get("/api/v1/products/product/count/by-brand/and-name")
                .param("brand", "Brand")
                .param("name", "Name"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Error"));
    }
}
