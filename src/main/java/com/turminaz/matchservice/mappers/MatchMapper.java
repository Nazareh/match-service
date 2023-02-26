package com.turminaz.matchservice.mappers;

import com.turminaz.matchservice.domain.model.Match;
import com.turminaz.matchservice.dto.MatchDto;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface MatchMapper {

    MatchMapper INSTANCE = Mappers.getMapper(MatchMapper.class);

    Match toEntity(MatchDto dto);

    MatchDto toDto(Match entity);
}
