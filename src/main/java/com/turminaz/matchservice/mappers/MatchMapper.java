package com.turminaz.matchservice.mappers;

import com.turminaz.matchservice.domain.model.Match;
import com.turminaz.matchservice.dto.MatchDto;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.time.Instant;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface MatchMapper {

    MatchMapper INSTANCE = Mappers.getMapper(MatchMapper.class);


    @Mapping(target = "dateTime", ignore = true) Match toEntity(MatchDto dto);

    @Mapping(target = "dateTime", ignore = true)
    MatchDto toDto(Match entity);

}
