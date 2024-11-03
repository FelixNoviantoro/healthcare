package com.felix.healthcare.api_core.repository;

import com.felix.healthcare.api_core.entity.Roles;
import com.felix.healthcare.api_core.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Roles, Long> {
    Roles findByName(String name);
}
