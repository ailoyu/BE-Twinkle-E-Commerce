package com.twinkle.shopapp.repositories;

import com.twinkle.shopapp.models.Product;
import com.twinkle.shopapp.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByPhoneNumber(String phoneNumber);
    Optional<User> findByPhoneNumber(String phoneNumber);
    //SELECT * FROM users WHERE phoneNumber=?

    @Query(value = "SELECT * FROM users u " +
            "WHERE " +
            "(:roleId IS NULL OR :roleId = 0 OR u.role_id = :roleId) " +
            "AND " +
            "(:keyword IS NULL OR :keyword = '' OR u.fullname LIKE %:keyword%) " +
            "AND " +
            "(:phoneNumber IS NULL OR :phoneNumber = '' OR u.phone_number LIKE %:phoneNumber%)", nativeQuery = true)
    Page<User> searchUsers(@Param("roleId") Long roleId, @Param("keyword") String keyword, @Param("phoneNumber") String phoneNumber, Pageable pageable);


}
