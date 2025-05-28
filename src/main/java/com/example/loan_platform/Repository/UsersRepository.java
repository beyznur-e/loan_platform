package com.example.loan_platform.Repository;

import com.example.loan_platform.Entity.Enum.UserRole;
import com.example.loan_platform.Entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByName(String name);

    List<Users> findByUserRole(UserRole userRole);

    Optional<Users> findById(Long id);

    Optional<Users> findByEmail(String email);

    List<Users> findAll();
}
