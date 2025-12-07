package in.dinesh.controller;

import in.dinesh.exceptions.ResourceNotFoundException;
import in.dinesh.model.Cart;
import in.dinesh.model.User;
import in.dinesh.service.cart.ICartItemService;
import in.dinesh.service.cart.ICartService;
import in.dinesh.service.user.IUserService;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class CartItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ICartItemService cartItemService;

    @MockBean
    private ICartService cartService;

    @MockBean
    private IUserService userService;

    private User user;
    private Cart cart;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        cart = new Cart();
        cart.setId(1L);
    }

    @Test
    void addItemToCart_Success() throws Exception {
        given(userService.getAuthenticatedUser()).willReturn(user);
        given(cartService.initializeNewCart(any(User.class))).willReturn(cart);
        doNothing().when(cartItemService).addItemToCart(anyLong(), anyLong(), anyInt());

        mockMvc.perform(post("/api/v1/cartItems/item/add")
                .param("productId", "1")
                .param("quantity", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Add Item Success"));
    }

    @Test
    void addItemToCart_NotFound() throws Exception {
        given(userService.getAuthenticatedUser()).willReturn(user);
        given(cartService.initializeNewCart(any(User.class))).willReturn(cart);
        doThrow(new ResourceNotFoundException("Product not found")).when(cartItemService).addItemToCart(anyLong(),
                anyLong(), anyInt());

        mockMvc.perform(post("/api/v1/cartItems/item/add")
                .param("productId", "1")
                .param("quantity", "1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Product not found"));
    }

    @Test
    void addItemToCart_Unauthorized() throws Exception {
        given(userService.getAuthenticatedUser()).willThrow(new JwtException("Unauthorized"));

        mockMvc.perform(post("/api/v1/cartItems/item/add")
                .param("productId", "1")
                .param("quantity", "1"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Unauthorized"));
    }

    @Test
    void removeItemFromCart_Success() throws Exception {
        doNothing().when(cartItemService).removeItemFromCart(1L, 1L);

        mockMvc.perform(delete("/api/v1/cartItems/cart/{cartId}/item/{itemId}/remove", 1L, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Remove Item Success"));
    }

    @Test
    void removeItemFromCart_NotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Item not found!")).when(cartItemService).removeItemFromCart(1L, 1L);

        mockMvc.perform(delete("/api/v1/cartItems/cart/{cartId}/item/{itemId}/remove", 1L, 1L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Item not found!"));
    }

    @Test
    void updateItemQuantity_Success() throws Exception {
        doNothing().when(cartItemService).updateItemQuantity(1L, 1L, 2);

        mockMvc.perform(put("/api/v1/cartItems/cart/{cartId}/item/{itemId}/update", 1L, 1L)
                .param("quantity", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Update Item Success"));
    }

    @Test
    void updateItemQuantity_NotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Item not found!")).when(cartItemService).updateItemQuantity(1L, 1L, 2);

        mockMvc.perform(put("/api/v1/cartItems/cart/{cartId}/item/{itemId}/update", 1L, 1L)
                .param("quantity", "2"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Item not found!"));
    }
}
