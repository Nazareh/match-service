package com.turminaz.matchservice.mappers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.turminaz.matchservice.domain.model.Match;
import com.turminaz.matchservice.dto.MatchDto;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MatchMapperTest {

    @Test
    void toEntity() {
        MatchDto dto = new EasyRandom().nextObject(MatchDto.class);

        Match entity = MatchMapper.INSTANCE.toEntity(dto);

        assertThat(entity)
                .usingRecursiveComparison().ignoringFields("id", "createdOn", "dateTime")
                .isEqualTo(dto);

        assertThat(entity.getId()).isNull();
    }

    @Test
    void toDto() throws JsonProcessingException {
        Match entity = new EasyRandom().nextObject(Match.class);

        MatchDto dto = MatchMapper.INSTANCE.toDto(entity);

        ObjectMapper a = new ObjectMapper();
        a.registerModule(new JavaTimeModule());

        System.out.println(a.writeValueAsString(dto));

        assertThat(dto)
                .usingRecursiveComparison()
                .ignoringFields("dateTime")
                .isEqualTo(entity);

    }
}