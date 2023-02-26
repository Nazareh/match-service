package com.turminaz.matchservice.service;

import com.turminaz.matchservice.domain.model.Match;
import com.turminaz.matchservice.dto.MatchDto;
import com.turminaz.matchservice.mappers.MatchMapper;
import com.turminaz.matchservice.repository.MatchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Log4j2
public class MatchService {
    private final MatchRepository matchRepository;
    private final MatchMapper matchMapper;

    public MatchDto addMatch(MatchDto matchDto) {

        verifyFourDistinctPlayers(matchDto);

        Match newMatch = matchMapper.toEntity(matchDto);

        Optional<Match> existingMatch = matchRepository
                .findByCourtAndDateTime(matchDto.getCourt(), matchDto.getDateTime());
        existingMatch
                .ifPresent(previousMatch -> checkInconsistencies(previousMatch, newMatch));

        return matchMapper.toDto(existingMatch.orElseGet(() -> matchRepository.save(newMatch)));
    }

    private void verifyFourDistinctPlayers(MatchDto matchDto) {

        Set<String> players = new HashSet<>();
        players.add(matchDto.getTeam1().getPlayer1().getId());
        players.add(matchDto.getTeam1().getPlayer2().getId());
        players.add(matchDto.getTeam2().getPlayer1().getId());
        players.add(matchDto.getTeam2().getPlayer2().getId());

        if (players.size() != 4){
            throw new RuntimeException("The match doesnt have four distinct players");
        }

    }

    private void checkInconsistencies(Match o1, Match o2) {
        if (!o1.equals(o2)) {
            throw new RuntimeException("A match for the same start time and court already exists");
        }
        log.info("Match duplicated, ignoring message.");
    }
}
