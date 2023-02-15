package com.turminaz.matchservice.mappers;

import com.turminaz.matchservice.dto.MatchDto;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class MatchMapperTest {

    @Test
    void toEntity() {
        var dto = new EasyRandom().nextObject(MatchDto.class);

        var entity = MatchMapper.INSTANCE.toEntity(dto);

        assertThat(entity)
                .usingRecursiveComparison().ignoringFields("id", "createdOn")
                .isEqualTo(dto);

        assertThat(entity.getId()).isNull();
    }
}