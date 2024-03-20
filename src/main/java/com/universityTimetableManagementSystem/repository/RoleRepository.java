package com.universityTimetableManagementSystem.repository;

import com.universityTimetableManagementSystem.model.ERole;
import com.universityTimetableManagementSystem.model.data.Role;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RoleRepository extends MongoRepository<Role, String> {
  Optional<Role> findByName(ERole name);
}