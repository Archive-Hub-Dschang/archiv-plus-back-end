package com.lde.usermicroservice.repositories;

import com.lde.usermicroservice.models.Contributor;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

@Repository
public interface ContributorRepository extends JpaRepository<Contributor, Long> {
}
