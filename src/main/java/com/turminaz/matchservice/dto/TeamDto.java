package com.turminaz.matchservice.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class TeamDto {
    private PlayerDto player1;
    private PlayerDto player2;
}
