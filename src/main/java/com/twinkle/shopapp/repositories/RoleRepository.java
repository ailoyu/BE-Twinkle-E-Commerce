package com.twinkle.shopapp.repositories;

import com.twinkle.shopapp.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
}
