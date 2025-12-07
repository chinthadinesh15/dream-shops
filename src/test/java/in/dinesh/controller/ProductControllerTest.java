package in.dinesh.controller;

import in.dinesh.dto.ProductDto;
import in.dinesh.model.Product;
import in.dinesh.response.ApiResponse;
import in.dinesh.service.product.IProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false) // Disable security filters for this test
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IProductService productService;

    @Test
    public void testSearchProducts() throws Exception {
        // Given
        Product product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        product.setBrand("Test Brand");

        ProductDto productDto = new ProductDto();
        productDto.setId(1L);
        productDto.setName("Test Product");
        productDto.setBrand("Test Brand");

        Page<Product> productPage = new PageImpl<>(Collections.singletonList(product));

        given(productService.getProductsWithFilters(any(), any(), any(), any(Pageable.class)))
                .willReturn(productPage);
        given(productService.convertToDto(any(Product.class))).willReturn(productDto);

        // When & Then
        mockMvc.perform(get("/api/v1/products/search")
                .param("search", "Test")
                .param("category", "Electronics")
                .param("brand", "Dell")
                .param("page", "0")
                .param("size", "10")
                .param("sortBy", "price")
                .param("sortDir", "asc")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data.content[0].name").value("Test Product"));

        // Verify that the service was called with the correct parameters
        // Note: We can captures arguments to verify exact values if needed,
        // but here we just ensure the flow works.
    }
}
