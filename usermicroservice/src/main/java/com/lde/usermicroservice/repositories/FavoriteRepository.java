package com.lde.usermicroservice.repositories;

import com.lde.usermicroservice.models.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite,Long> {

    Favorite findByUserIdAndExamId(Long userId, String examId);
    List<Favorite> findByUserId(Long userId);

}
