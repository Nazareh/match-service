package com.turminaz.matchservice.mappers;

import com.turminaz.matchservice.domain.model.Match;
import com.turminaz.matchservice.dto.MatchDto;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface MatchMapper {

    MatchMapper INSTANCE = Mappers.getMapper(MatchMapper.class);

    @Mapping(target = "dateTime", ignore = true) Match toEntity(MatchDto dto);

    MatchDto toDto(Match entity);

    default String toEmailBody(Match match) {
        DateTimeFormatter formatter = DateTimeFormatter.RFC_1123_DATE_TIME
                .withZone(ZoneId.systemDefault());

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Court: %d Date: %s \n", match.getCourt(), formatter.format(match.getDateTime())));
        sb.append(String.format("Team 1: %s & %s \n", match.getTeam1().getPlayer1().getId(),
                match.getTeam1().getPlayer2().getId()));
        sb.append(String.format("Team 2: %s & %s \n", match.getTeam2().getPlayer1().getId(),
                match.getTeam2().getPlayer2().getId()));
        sb.append(String.format("Result: %d:%d \n", match.getMatchResult().getWins(), match.getMatchResult().getLosses()));
        return sb.toString();
    }

    default String toEmailBody(MatchDto dto) {
        return toEmailBody(toEntity(dto)
                .setDateTime(Instant.parse(dto.getDateTime()))
        );
    }
}
