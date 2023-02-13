package com.turminaz.matchservice.dto;


import lombok.Data;
import lombok.experimental.Accessors;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Accessors(chain = true)
public class MatchDto {

    private Integer court;
    private Instant start;
    private Instant end;
    private TeamDto team1;

    private TeamDto team2;

    private MatchResultDto matchResult;

    private boolean rated;

}
