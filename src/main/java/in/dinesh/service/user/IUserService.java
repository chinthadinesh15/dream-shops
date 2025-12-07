package in.dinesh.service.user;

import in.dinesh.dto.UserDto;
import in.dinesh.model.User;
import in.dinesh.request.CreateUserRequest;
import in.dinesh.request.UserUpdateRequest;

public interface IUserService {

    User getUserById(Long userId);
    User createUser(CreateUserRequest request);
    User updateUser(UserUpdateRequest request, Long userId);
    void deleteUser(Long userId);

    UserDto convertUserToDto(User user);

    User getAuthenticatedUser();
}
