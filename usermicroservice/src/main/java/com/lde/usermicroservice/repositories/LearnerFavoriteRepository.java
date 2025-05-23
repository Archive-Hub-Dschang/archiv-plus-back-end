package com.lde.usermicroservice.repositories;

import com.lde.usermicroservice.models.LearnerFavorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LearnerFavoriteRepository extends JpaRepository<LearnerFavorite,Long> {

    List<LearnerFavorite> findByLearnerId(Long LearnerId);
    Optional<LearnerFavorite> findByLearnerIdAndSubjectId(Long learnerId, String subjectId);
}
