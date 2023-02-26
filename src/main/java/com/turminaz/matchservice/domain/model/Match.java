package com.turminaz.matchservice.domain.model;


import com.turminaz.matchservice.domain.MatchResult;
import com.turminaz.matchservice.domain.Team;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
@Document
public class Match {

    @Id
    @EqualsAndHashCode.Exclude
    private ObjectId id;
    private Integer court;
    private Instant dateTime;
    private Team team1;

    private Team team2;

    private MatchResult matchResult;

    private boolean rated;

    @CreatedDate
    @EqualsAndHashCode.Exclude
    private Instant createdOn;
}
