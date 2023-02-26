package com.turminaz.matchservice.dto;


import lombok.Data;
import lombok.experimental.Accessors;

import java.time.Instant;

@Data
@Accessors(chain = true)
public class MatchDto {

    private Integer court;
    private Instant dateTime;
    private TeamDto team1;

    private TeamDto team2;

    private MatchResultDto matchResult;

    private boolean rated;

}
