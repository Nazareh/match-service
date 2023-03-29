package com.turminaz.matchservice.dto;


import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class MatchDto {

    private Integer court;

    private String dateTime;
    private TeamDto team1;

    private TeamDto team2;

    private MatchResultDto matchResult;

    private boolean rated;

}
