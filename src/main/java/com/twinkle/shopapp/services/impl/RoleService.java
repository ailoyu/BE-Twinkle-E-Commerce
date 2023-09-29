package com.twinkle.shopapp.services.impl;

import com.twinkle.shopapp.models.Role;
import com.twinkle.shopapp.repositories.RoleRepository;
import com.twinkle.shopapp.services.IRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleService implements IRoleService {

    private final RoleRepository roleRepository;
    @Override
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }
}
