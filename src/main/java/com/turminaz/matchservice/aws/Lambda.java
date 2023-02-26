package com.turminaz.matchservice.aws;

import com.turminaz.matchservice.dto.MatchDto;
import com.turminaz.matchservice.service.MatchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
@RequiredArgsConstructor
@Log4j2
public class Lambda {
    private final MatchService matchService;


    @Bean
    public Function<MatchDto, MatchDto> lambdaFunction() {
        return matchService::addMatch;

    }
}
