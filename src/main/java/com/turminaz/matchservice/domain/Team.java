package com.turminaz.matchservice.domain;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Team {
    private Player player1;
    private Player player2;
}
