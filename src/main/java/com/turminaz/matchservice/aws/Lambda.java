package com.turminaz.matchservice.aws;

import com.turminaz.matchservice.dto.MatchDto;
import com.turminaz.matchservice.service.MatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class Lambda {
    private final MatchService matchService;

    @Bean
    public Function<MatchDto, String> lambdaFunction() {
        return matchDto -> {
            matchService.addMatch(matchDto);
            return "Success";
        };
    }
}
