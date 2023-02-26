package com.turminaz.matchservice.repository;


import com.turminaz.matchservice.domain.model.Match;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.Instant;
import java.util.Optional;

public interface MatchRepository extends MongoRepository<Match, ObjectId> {

    Optional<Match> findByCourtAndDateTime(Integer court, Instant start);
}
