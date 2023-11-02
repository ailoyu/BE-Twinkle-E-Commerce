package com.twinkle.shopapp.controllers;

import com.twinkle.shopapp.dtos.UserDTO;
import com.twinkle.shopapp.dtos.UserLoginDTO;
import com.twinkle.shopapp.models.User;
import com.twinkle.shopapp.responses.*;
import com.twinkle.shopapp.services.IUserService;
import com.twinkle.shopapp.component.LocalizationUtils;
import com.twinkle.shopapp.utils.MessageKeys;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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

    @GetMapping("/find-by-phone/{phoneNumber}")
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

    @GetMapping("/get-all-users-by-admin")
    public ResponseEntity<UserListResponse> getAllUsers(
            @RequestParam(defaultValue = "") String keyword, // search
            @RequestParam(defaultValue = "", name = "phone_number") String phoneNumber,
            @RequestParam(defaultValue = "1", name = "role_id") Long roleId, // tìm theo thể loại
            @RequestParam int page,
            @RequestParam("limit") int limits
    ) {
        // Lưu ý: page bắt đầu từ 0 (phải lấy page - 1)
        // page: là trang đang đứng htai, limits: tổng số item trong 1 trang
        PageRequest pageRequest = PageRequest.of(
                page - 1, limits,
//                Sort.by("createdAt").descending());
                Sort.by("id").ascending() // sắp xếp theo id tăng dần
        );

        Page<User> productPage = userService
                .getAllUsersByAdmin(keyword, phoneNumber, roleId, pageRequest);

        // lấy tổng số trang
        int totalPages = productPage.getTotalPages();

        // danh sách các products ở tất cả các trang
        List<User> users = productPage.getContent();

        return ResponseEntity.ok(new UserListResponse().builder()
                .users(users)
                .totalPage(totalPages)
                .build());
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

    @DeleteMapping("/")
    public ResponseEntity<?> deleteUsers(@RequestBody Map<String, Long[]> request) {
        try{
            Long[] ids = request.get("ids");
            userService.deleteUsers(ids);
            return ResponseEntity.ok().body(CategoryResponse.builder()
                    .message("Xóa thành công")
                    .build());
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }



}
