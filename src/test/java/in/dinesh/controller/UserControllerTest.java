package in.dinesh.controller;

import in.dinesh.dto.UserDto;
import in.dinesh.model.User;
import in.dinesh.request.CreateUserRequest;
import in.dinesh.request.UserUpdateRequest;
import in.dinesh.service.user.IUserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import in.dinesh.exceptions.AlreadyExistsException;
import in.dinesh.exceptions.ResourceNotFoundException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IUserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private User user;
    private UserDto userDto;
    private CreateUserRequest createUserRequest;
    private UserUpdateRequest userUpdateRequest;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("test@test.com");

        userDto = new UserDto();
        userDto.setId(1L);
        userDto.setEmail("test@test.com");

        createUserRequest = new CreateUserRequest();
        createUserRequest.setEmail("test@test.com");

        userUpdateRequest = new UserUpdateRequest();
        userUpdateRequest.setFirstName("John");
    }

    @Test
    void getUserById_Success() throws Exception {
        given(userService.getUserById(1L)).willReturn(user);
        given(userService.convertUserToDto(any(User.class))).willReturn(userDto);

        mockMvc.perform(get("/api/v1/users/{userId}/user", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success"));
    }

    @Test
    void getUserById_NotFound() throws Exception {
        given(userService.getUserById(1L)).willThrow(new ResourceNotFoundException("User not found!"));

        mockMvc.perform(get("/api/v1/users/{userId}/user", 1L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User not found!"));
    }

    @Test
    void createUser_Success() throws Exception {
        given(userService.createUser(any(CreateUserRequest.class))).willReturn(user);
        given(userService.convertUserToDto(any(User.class))).willReturn(userDto);

        mockMvc.perform(post("/api/v1/users/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createUserRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Create User Success!"));
    }

    @Test
    void createUser_AlreadyExists() throws Exception {
        given(userService.createUser(any(CreateUserRequest.class)))
                .willThrow(new AlreadyExistsException("User already exists!"));

        mockMvc.perform(post("/api/v1/users/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createUserRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("User already exists!"));
    }

    @Test
    void updateUser_Success() throws Exception {
        given(userService.updateUser(any(UserUpdateRequest.class), anyLong())).willReturn(user);
        given(userService.convertUserToDto(any(User.class))).willReturn(userDto);

        mockMvc.perform(put("/api/v1/users/{userId}/update", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userUpdateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Update User Success!"));
    }

    @Test
    void updateUser_NotFound() throws Exception {
        given(userService.updateUser(any(UserUpdateRequest.class), anyLong()))
                .willThrow(new ResourceNotFoundException("User not found!"));

        mockMvc.perform(put("/api/v1/users/{userId}/update", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userUpdateRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User not found!"));
    }

    @Test
    void deleteUser_Success() throws Exception {
        doNothing().when(userService).deleteUser(1L);

        mockMvc.perform(delete("/api/v1/users/{userId}/delete", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Delete User Success!"));
    }

    @Test
    void deleteUser_NotFound() throws Exception {
        doThrow(new ResourceNotFoundException("User not found!")).when(userService).deleteUser(1L);

        mockMvc.perform(delete("/api/v1/users/{userId}/delete", 1L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User not found!"));
    }
}
