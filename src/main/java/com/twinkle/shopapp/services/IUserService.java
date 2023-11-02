package com.twinkle.shopapp.services;

import com.twinkle.shopapp.dtos.UserDTO;
import com.twinkle.shopapp.exceptions.DataNotFoundException;
import com.twinkle.shopapp.models.User;
import com.twinkle.shopapp.responses.LoginResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.io.IOException;
import java.util.List;

public interface IUserService {

    User createUser(UserDTO userDTO) throws Exception;

    LoginResponse login(String phoneNumber, String password, Long roleId) throws Exception;

    LoginResponse getUserByPhoneNumber(String phoneNumber) throws DataNotFoundException;

    LoginResponse updateUserByPhoneNumber(UserDTO userDTO) throws DataNotFoundException, IOException;

    User changePassword(String phoneNumber, String password, String newPassword) throws DataNotFoundException;

    Page<User> getAllUsersByAdmin(String keyword, String phoneNumber, Long roleId, PageRequest pageRequest);

    void deleteUsers(Long[] ids);
}
