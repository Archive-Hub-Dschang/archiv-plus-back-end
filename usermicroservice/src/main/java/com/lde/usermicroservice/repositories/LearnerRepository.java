package com.lde.usermicroservice.repositories;

import com.lde.usermicroservice.models.Learner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LearnerRepository extends JpaRepository<Learner, Long> {
}
