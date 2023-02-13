package com.turminaz.matchservice.pubsub;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.turminaz.matchservice.dto.MatchDto;
import com.turminaz.matchservice.dto.PubSubMessage;
import com.turminaz.matchservice.service.MatchService;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PubSubConsumerTest {

    @Mock
    MatchService matchService;

    @InjectMocks
    private PubSubConsumer sut;

    @Captor
    private ArgumentCaptor<MatchDto> captor;

    @Test
    void pubSubFunction_shouldDecodeAndDeserialize() throws JsonProcessingException {
        //given
        var originalMatchDto = new EasyRandom().nextObject(MatchDto.class);
        var payload = new ObjectMapper().registerModule(new JavaTimeModule())
                .writeValueAsString(originalMatchDto);
        var encodedPayload = new String(Base64.getEncoder().encode(payload.getBytes()));

        //when
        var consumer = sut.pubSubFunction();
        consumer.accept(new PubSubMessage().setData(encodedPayload));

       //then
        verify(matchService).addMatch(captor.capture());
        assertThat(captor.getValue()).usingRecursiveComparison().isEqualTo(originalMatchDto);

    }
}