package in.dinesh.controller;

import in.dinesh.dto.OrderDto;
import in.dinesh.exceptions.ResourceNotFoundException;
import in.dinesh.model.Order;
import in.dinesh.service.order.IOrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IOrderService orderService;

    private Order order;
    private OrderDto orderDto;

    @BeforeEach
    void setUp() {
        order = mock(Order.class);
        orderDto = mock(OrderDto.class);
    }

    @Test
    void createOrder_Success() throws Exception {
        given(orderService.placeOrder(anyLong())).willReturn(order);
        given(orderService.convertToDto(any(Order.class))).willReturn(orderDto);

        mockMvc.perform(post("/api/v1/orders/order")
                .param("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Item Order Success!"));
    }

    @Test
    void createOrder_Error() throws Exception {
        given(orderService.placeOrder(anyLong())).willThrow(new RuntimeException("Error"));

        mockMvc.perform(post("/api/v1/orders/order")
                .param("userId", "1"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Error Occured!"));
    }

    @Test
    void getOrderById_Success() throws Exception {
        given(orderService.getOrder(anyLong())).willReturn(orderDto);

        mockMvc.perform(get("/api/v1/orders/{orderId}/order", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Item Order Success!"));
    }

    @Test
    void getOrderById_NotFound() throws Exception {
        given(orderService.getOrder(anyLong())).willThrow(new ResourceNotFoundException("Order not found"));

        mockMvc.perform(get("/api/v1/orders/{orderId}/order", 1L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Oops!"));
    }

    @Test
    void getUserOrders_Success() throws Exception {
        given(orderService.getUserOrders(anyLong())).willReturn(Collections.singletonList(orderDto));

        mockMvc.perform(get("/api/v1/orders/user/{userId}/order", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Item Order Success!"));
    }

    @Test
    void getUserOrders_NotFound() throws Exception {
        given(orderService.getUserOrders(anyLong())).willThrow(new ResourceNotFoundException("No orders found"));

        mockMvc.perform(get("/api/v1/orders/user/{userId}/order", 1L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Oops!"));
    }
}
