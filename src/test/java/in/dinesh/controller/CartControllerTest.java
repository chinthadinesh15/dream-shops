package in.dinesh.controller;

import in.dinesh.exceptions.ResourceNotFoundException;
import in.dinesh.model.Cart;
import in.dinesh.service.cart.ICartService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ICartService cartService;

    private Cart cart;

    @BeforeEach
    void setUp() {
        cart = new Cart();
        cart.setId(1L);
    }

    @Test
    void getCart_Success() throws Exception {
        given(cartService.getCart(1L)).willReturn(cart);

        mockMvc.perform(get("/api/v1/carts/{cartId}/my-cart", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success"));
    }

    @Test
    void getCart_NotFound() throws Exception {
        given(cartService.getCart(1L)).willThrow(new ResourceNotFoundException("Cart not found!"));

        mockMvc.perform(get("/api/v1/carts/{cartId}/my-cart", 1L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Cart not found!"));
    }

    @Test
    void clearCart_Success() throws Exception {
        doNothing().when(cartService).clearCart(1L);

        mockMvc.perform(delete("/api/v1/carts/{cartId}/clear", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Clear Cart Success!"));
    }

    @Test
    void clearCart_NotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Cart not found!")).when(cartService).clearCart(1L);

        mockMvc.perform(delete("/api/v1/carts/{cartId}/clear", 1L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Cart not found!"));
    }

    @Test
    void getTotalAmount_Success() throws Exception {
        given(cartService.getTotalPrice(1L)).willReturn(BigDecimal.TEN);

        mockMvc.perform(get("/api/v1/carts/{cartId}/cart/total-price", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Total Price"))
                .andExpect(jsonPath("$.data").value(10));
    }

    @Test
    void getTotalAmount_NotFound() throws Exception {
        given(cartService.getTotalPrice(1L)).willThrow(new ResourceNotFoundException("Cart not found!"));

        mockMvc.perform(get("/api/v1/carts/{cartId}/cart/total-price", 1L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Cart not found!"));
    }
}
