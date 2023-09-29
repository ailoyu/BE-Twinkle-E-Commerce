package com.twinkle.shopapp.controllers;

import com.twinkle.shopapp.dtos.UserDTO;
import com.twinkle.shopapp.dtos.UserLoginDTO;
import com.twinkle.shopapp.models.User;
import com.twinkle.shopapp.responses.LoginResponse;
import com.twinkle.shopapp.responses.RegisterResponse;
import com.twinkle.shopapp.services.IUserService;
import com.twinkle.shopapp.component.LocalizationUtils;
import com.twinkle.shopapp.utils.MessageKeys;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/users")
@RequiredArgsConstructor
public class UserController {

    private final IUserService userService;

    private final LocalizationUtils localizationUtils;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> createUser(
            @Valid @RequestBody UserDTO userDTO,
            BindingResult result){
        try {
            if(result.hasErrors()){
                List<String> errorMessages = result.getFieldErrors()
                        .stream()
                        .map(fieldError -> fieldError.getDefaultMessage())
                        .toList();
                return ResponseEntity.badRequest().body(RegisterResponse.builder()
                                .message(errorMessages.toString())
                        .build());
            }
            if(!userDTO.getPassword().equals(userDTO.getRetypePassword())){
                return ResponseEntity.badRequest().body(RegisterResponse.builder()
                                .message(localizationUtils.getLocalizedMessage(MessageKeys.PASSWORD_NOT_MATCH))
                        .build());
            }
            User user = userService.createUser(userDTO);
            return ResponseEntity.ok(RegisterResponse.builder()
                            .message(localizationUtils.getLocalizedMessage(MessageKeys.REGISTER_SUCCESSFULLY))
                            .user(user)
                    .build());
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    RegisterResponse.builder()
                            .message(e.getMessage())
                            .build()
            );
        }
    }

    @GetMapping("/{phoneNumber}")
    public ResponseEntity<LoginResponse> getUserByPhoneNumber(
            @PathVariable(name = "phoneNumber") String phoneNumber
    ){
        try{
            LoginResponse userByPhoneNumber = userService.getUserByPhoneNumber(phoneNumber);
            userByPhoneNumber.setMessage(localizationUtils.getLocalizedMessage(MessageKeys.LOGIN_SUCCESSFULLY));
            return ResponseEntity.ok().body(userByPhoneNumber);
        } catch (Exception e){
            return ResponseEntity.badRequest().body(
                    LoginResponse.builder()
                            .message(localizationUtils.getLocalizedMessage(MessageKeys.LOGIN_FAILED, e.getMessage()))
                            .build());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody UserLoginDTO userLoginDTO){

        // Kiểm tra thông tin đăng nhập và sinh token
        // Sau khi đăng nhap, Trả ra token cho Client
        try {

            LoginResponse user = userService.login(
                    userLoginDTO.getPhoneNumber(),
                    userLoginDTO.getPassword(),
                    userLoginDTO.getRoleId() == null ? 1 : userLoginDTO.getRoleId());
            user.setMessage(localizationUtils.getLocalizedMessage(MessageKeys.LOGIN_SUCCESSFULLY));

            return ResponseEntity.ok(user);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(
                    LoginResponse.builder()
                            .message(localizationUtils.getLocalizedMessage(MessageKeys.LOGIN_FAILED, e.getMessage()))
                            .build()
            );
        }
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateUser(
            @RequestBody UserDTO userDTO){
        try {
            LoginResponse user = userService.updateUserByPhoneNumber(userDTO);
            return ResponseEntity.ok().body(user);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/change-password")
    public ResponseEntity<RegisterResponse> changePassword(
            @RequestBody UserLoginDTO userDTO){
        try{
            if(!userDTO.getNewPassword().equals(userDTO.getConfirmNewPassword())){
                return ResponseEntity.badRequest().body(RegisterResponse.builder()
                        .message(localizationUtils.getLocalizedMessage(MessageKeys.PASSWORD_NOT_MATCH))
                        .build());
            }
            User user = userService.changePassword(userDTO.getPhoneNumber(),
                    userDTO.getPassword(), userDTO.getNewPassword());
            return ResponseEntity.ok().body(RegisterResponse.builder()
                            .message("Thay đổi mật khẩu thành công!")
                            .user(user)
                    .build());
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    RegisterResponse.builder()
                            .message(e.getMessage())
                            .build());
        }
    }



}
