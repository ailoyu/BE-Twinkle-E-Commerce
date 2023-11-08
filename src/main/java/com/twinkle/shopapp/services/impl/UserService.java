package com.twinkle.shopapp.services.impl;

import com.cloudinary.Cloudinary;
import com.twinkle.shopapp.component.JwtTokenUtils;
import com.twinkle.shopapp.component.LocalizationUtils;
import com.twinkle.shopapp.dtos.UserDTO;
import com.twinkle.shopapp.exceptions.DataNotFoundException;
import com.twinkle.shopapp.exceptions.PermissionDenyException;
import com.twinkle.shopapp.models.*;
import com.twinkle.shopapp.repositories.CustomerRepository;
import com.twinkle.shopapp.repositories.EmployeeRepository;
import com.twinkle.shopapp.repositories.RoleRepository;
import com.twinkle.shopapp.repositories.UserRepository;
import com.twinkle.shopapp.responses.LoginResponse;
import com.twinkle.shopapp.services.IUserService;
import com.twinkle.shopapp.utils.ImageUtils;
import com.twinkle.shopapp.utils.MessageKeys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final CustomerRepository customerRepository;

    private final EmployeeRepository employeeRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtTokenUtils jwtTokenUtils;

    private final AuthenticationManager authenticationManager;

    private final LocalizationUtils localizationUtils;


    @Override
    public User createUser(UserDTO userDTO) throws Exception {
        String phoneNumber = userDTO.getPhoneNumber();
        // kiểm tra sdt đã tồn tại chưa
        if(userRepository.existsByPhoneNumber(phoneNumber)){
            throw new RuntimeException("Số điện thoại đã tồn tại");
        }

        // Set role cho user mới
        Role role =roleRepository.findById(userDTO.getRoleId())
                .orElseThrow(() -> new DataNotFoundException(
                        localizationUtils.getLocalizedMessage(MessageKeys.ROLE_DOES_NOT_EXISTS)));



        // Convert from DTO to model
        User newUser = User
                .builder()
                .fullName(userDTO.getFullName())
                .phoneNumber(userDTO.getPhoneNumber())
                .password(userDTO.getPassword())
                .address(userDTO.getAddress())
                .dateOfBirth(userDTO.getDateOfBirth())
                .facebookAccountId(userDTO.getFacebookAccountId())
                .googleAccountId(userDTO.getGoogleAccountId())
                .avatar("https://img.myloview.com/stickers/default-avatar-profile-icon-vector-social-media-user-photo-700-205577532.jpg")
                .build();


        newUser.setRole(role);
        newUser.setActive(true);

        // kiểm tra nếu ko đăng nhập fb or google, thì yêu cầu cần mật khẩu
        if(userDTO.getFacebookAccountId() == 0 && userDTO.getGoogleAccountId() == 0){
            String password = userDTO.getPassword();
            // mã hóa mật khẩu
            String encodedPassword = passwordEncoder.encode(password);
            newUser.setPassword(encodedPassword);
        }

        User save = userRepository.save(newUser);

        if(role.getName().toUpperCase().equals(Role.ADMIN)) {
            Employee employee = Employee.builder()
                    .user(save)
                    .fullName(newUser.getFullName())
                    .phoneNumber(newUser.getPhoneNumber())
                    .avatar(newUser.getAvatar())
                    .address(newUser.getAddress())
                    .dateOfBirth(newUser.getDateOfBirth())
                    .build();
            employeeRepository.save(employee);
        } else if (role.getName().toUpperCase().equals(Role.USER)){
            Customer customer = Customer.builder()
                    .user(save)
                    .fullName(newUser.getFullName())
                    .phoneNumber(newUser.getPhoneNumber())
                    .avatar(newUser.getAvatar())
                    .address(newUser.getAddress())
                    .dateOfBirth(newUser.getDateOfBirth())
                    .build();
            customerRepository.save(customer);
        }

        return save;
    }

    @Override
    public LoginResponse login(String phoneNumber, String password, Long roleId) throws Exception {

        // Kiểm tra sdt
        Optional<User> optionalUser = userRepository.findByPhoneNumber(phoneNumber);
        if(optionalUser.isEmpty()){
            throw new DataNotFoundException(localizationUtils.getLocalizedMessage(MessageKeys.WRONG_PHONE_PASSWORD));
        }
        User existingUser = optionalUser.get();
        if(!optionalUser.get().isActive()) {
            throw new DataNotFoundException(localizationUtils.getLocalizedMessage(MessageKeys.USER_IS_LOCKED));
        }

        // check password (nếu đăng nhập fb or google khỏi ktra password)
        if(existingUser.getFacebookAccountId() == 0 && existingUser.getGoogleAccountId() == 0) {
            // Kiểm tra mk chưa mã hóa vs mk đã mã hóa trong DB
            if(!passwordEncoder.matches(password, existingUser.getPassword())){
                throw new BadCredentialsException(localizationUtils.getLocalizedMessage(MessageKeys.WRONG_PHONE_PASSWORD));
            }
        }

        // Check role ADMIN or USER
        Optional<Role> optionalRole = roleRepository.findById(roleId);
        if(optionalRole.isEmpty() || !roleId.equals(existingUser.getRole().getId())) {
            throw new DataNotFoundException(localizationUtils.getLocalizedMessage(MessageKeys.ROLE_DOES_NOT_EXISTS));
        }





        // authenticate with Java Spring Security
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                phoneNumber, password,
                existingUser.getAuthorities()
        );
        authenticationManager.authenticate(authenticationToken);

        LoginResponse login = LoginResponse.builder()
                .id(existingUser.getId())
                .fullName(existingUser.getFullName())
                .token(jwtTokenUtils.generateToken(existingUser))  // trả JWT token
                .address(existingUser.getAddress())
                .phoneNumber(existingUser.getPhoneNumber())
                .avatar(existingUser.getAvatar())
                .dateOfBirth(existingUser.getDateOfBirth().toString())
                .roleId(existingUser.getRole().getId())
                .build();


        Employee employee = employeeRepository.findEmployeeByUserId(existingUser.getId());
        if(employee != null){
            login.setEmployeeId(employee.getId());
        }

        return login;
    }

    @Autowired
    private ImageUtils imageUtils;
    @Override
    @Transactional
    public LoginResponse updateUserByPhoneNumber(UserDTO userDTO) throws DataNotFoundException, IOException {
        User existingUser = userRepository.findByPhoneNumber(userDTO.getPhoneNumber())
                .orElseThrow(() -> new DataNotFoundException("Không tìm thấy số user này"));
        // Convert from DTO to model
        existingUser.setPhoneNumber(userDTO.getPhoneNumber());

        // Base64 -> MultipartFile
        if(userDTO.getAvatar() != null){
            String generatedFileName = imageUtils.storeFileWithBase64(userDTO.getAvatar());
            existingUser.setAvatar(generatedFileName);
        }

        existingUser.setAddress(userDTO.getAddress());
//        existingUser.setDateOfBirth(existingUser.getDateOfBirth());
        existingUser.setFullName(userDTO.getFullName());

        User updatedUser = userRepository.save(existingUser);

        return LoginResponse.builder()
                .avatar(updatedUser.getAvatar())
                .phoneNumber(updatedUser.getPhoneNumber())
                .address(updatedUser.getAddress())
                .dateOfBirth(updatedUser.getDateOfBirth().toString())
                .fullName(updatedUser.getFullName())
                .build();
    }

    @Override
    public User changePassword(String phoneNumber, String password, String newPassword) throws DataNotFoundException {
        User existingUser = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new DataNotFoundException("Không tìm thấy người dùng này!"));

        // Kiểm tra mk chưa mã hóa vs mk đã mã hóa trong DB
        if(!passwordEncoder.matches(password, existingUser.getPassword())){
            throw new BadCredentialsException("Mật khẩu hiện tại không chính xác!");
        }

        String encodedPassword = passwordEncoder.encode(newPassword);
        existingUser.setPassword(encodedPassword);

        User userWithNewPassword = userRepository.save(existingUser);

        return userWithNewPassword;
    }

    @Override
    public Page<User> getAllUsersByAdmin(String keyword, String phoneNumber, Long roleId, PageRequest pageRequest) {
        return userRepository.searchUsers(roleId, keyword, phoneNumber, pageRequest);
    }

    @Override
    @Transactional
    public void deleteUsers(Long[] ids) {
        for(long id : ids){
            Optional<User> optionalUser = userRepository.findById(id);
            if(optionalUser.isPresent()){
                if(optionalUser.get().isActive()){
                    optionalUser.get().setActive(false);
                } else {
                    optionalUser.get().setActive(true);
                }
            }
        }
    }






    @Override
    public LoginResponse getUserByPhoneNumber(String phoneNumber) throws DataNotFoundException {
        User existingUser = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new DataNotFoundException("Không tìm thấy số user này"));

        return LoginResponse.builder()
                .id(existingUser.getId())
                .fullName(existingUser.getFullName())
                .address(existingUser.getAddress())
                .phoneNumber(existingUser.getPhoneNumber())
                .roleId(existingUser.getRole().getId())
                .avatar(existingUser.getAvatar())
                .dateOfBirth(existingUser.getDateOfBirth().toString())
                .build();
    }
}
