package com.turminaz.matchservice.pubsub;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.turminaz.matchservice.dto.MatchDto;
import com.turminaz.matchservice.dto.PubSubMessage;
import com.turminaz.matchservice.service.MatchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.function.Consumer;

@Component
@RequiredArgsConstructor
@Log4j2
public class PubSubConsumer {

    private final MatchService matchService;

    @Bean
    public Consumer<PubSubMessage> pubSubFunction() {
        return message -> {
            // The PubSubMessage data field arrives as a base-64 encoded string and must be decoded.
            // See: https://cloud.google.com/functions/docs/calling/pubsub#event_structure
            String decodedMessage = new String(Base64.getDecoder().decode(message.getData()), StandardCharsets.UTF_8);
            log.info("Received Pub/Sub message with data: " + decodedMessage);

            ObjectMapper o = new ObjectMapper().registerModule(new JavaTimeModule());

            MatchDto matchDto;
            try {
                matchDto = o.readValue(decodedMessage, MatchDto.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }

            matchService.addMatch(matchDto);

        };
    }
}
