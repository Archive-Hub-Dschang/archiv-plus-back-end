package com.lde.usermicroservice.repositories;

import com.lde.usermicroservice.models.UserFavorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserFavoriteRepository extends JpaRepository<UserFavorite,String> {

    UserFavorite findByUserIdAndDocumentId(String userId, String documentId);
    List<UserFavorite> findByUserId(String userId);
}
