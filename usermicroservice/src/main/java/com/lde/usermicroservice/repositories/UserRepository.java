package com.lde.usermicroservice.repositories;

import com.lde.usermicroservice.models.RoleName;
import com.lde.usermicroservice.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    User findByEmail(String email);
    List<User> findAllByRole(RoleName role);

}
