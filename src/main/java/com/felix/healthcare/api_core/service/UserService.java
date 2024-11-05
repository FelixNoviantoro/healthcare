package com.felix.healthcare.api_core.service;

import com.felix.healthcare.api_core.dto.UsersDto;
import com.felix.healthcare.api_core.entity.Roles;
import com.felix.healthcare.api_core.entity.Users;
import com.felix.healthcare.api_core.repository.RoleRepository;
import com.felix.healthcare.api_core.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public Users save(UsersDto.SaveRequest data) throws Exception {

        Optional<Users> optionalUsers = userRepository.findByUsername(data.getUsername());

        if (optionalUsers.isPresent()){
            // User with this username has been existed
            throw new Exception("User with this " + data.getUsername() + " already exists");
        }

        // Hash Password
        String hashPassword = BCrypt.hashpw(data.getPassword(), BCrypt.gensalt(12));

        Set<Roles> roles = new HashSet<>();
        for (String roleName : data.getRolesName()) {
            Roles role = roleRepository.findByName(roleName);
            if (role != null) {
                roles.add(role);
            }
        }

        Users user = new Users();
        user.setUsername(data.getUsername());
        user.setEmail(data.getEmail());
        user.setPassword(hashPassword);
        user.setRoles(roles);

        return userRepository.save(user);

    }
}
