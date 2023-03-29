package com.turminaz.matchservice.service;

import com.turminaz.matchservice.dto.MatchDto;
import com.turminaz.matchservice.mappers.MatchMapper;
import com.turminaz.matchservice.repository.MatchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Log4j2
public class MatchService {
    private final MatchRepository matchRepository;
    private final MatchMapper matchMapper;
    private final EmailService emailService;

    @Value("${app.email.receiver}")
    private String to;


    public MatchDto addMatch(MatchDto matchDto) {
        verifyFourDistinctPlayers(matchDto);

        var newMatch = matchMapper
                .toEntity(matchDto)
                .setDateTime(Instant.parse(matchDto.getDateTime()));

        var match = matchRepository.save(newMatch);
        var dto = matchMapper.toDto(match);

        emailService
                .sendSimpleMessage(to, "Match Uploaded", matchMapper.toEmailBody(match));

        return dto;

    }

    private void verifyFourDistinctPlayers(MatchDto matchDto) {
        Set<String> players = new HashSet<>();
        players.add(matchDto.getTeam1().getPlayer1().getId());
        players.add(matchDto.getTeam1().getPlayer2().getId());
        players.add(matchDto.getTeam2().getPlayer1().getId());
        players.add(matchDto.getTeam2().getPlayer2().getId());

        if (players.size() != 4) {
            emailService
                    .sendSimpleMessage(to,
                            "Invalid Match Uploaded",
                            String.format("Match doesnt have four distinct players.\n %s",
                                    matchMapper.toEmailBody(matchDto)

                            ));
            throw new RuntimeException("The match doesnt have four distinct players");
        }
    }
}
