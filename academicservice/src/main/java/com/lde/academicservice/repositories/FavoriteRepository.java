package com.lde.academicservice.repositories;

import com.lde.academicservice.models.Favorite;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FavoriteRepository extends MongoRepository<Favorite, String> {
}
