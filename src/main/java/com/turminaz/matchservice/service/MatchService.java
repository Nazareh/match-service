package com.turminaz.matchservice.service;

import com.turminaz.matchservice.domain.model.Match;
import com.turminaz.matchservice.dto.MatchDto;
import com.turminaz.matchservice.mappers.MatchMapper;
import com.turminaz.matchservice.repository.MatchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class MatchService {

    private final MatchRepository matchRepository;
    private final MatchMapper matchMapper;

    public void addMatch(MatchDto matchDto) {
        verifyFourDistinctPlayers(matchDto);

        var newMatch = matchMapper.toEntity(matchDto);

        matchRepository.findByCourtAndStart(matchDto.getCourt(), matchDto.getStart())
                .ifPresentOrElse(
                        entity -> verifyAttributes(entity,newMatch),
                        () -> matchRepository.save(newMatch)
                );
    }

    private boolean verifyFourDistinctPlayers(MatchDto matchDto) {
        return Set.of(matchDto.getTeam1().getPlayer1().getId(),
                matchDto.getTeam1().getPlayer2().getId(),
                matchDto.getTeam2().getPlayer1().getId(),
                matchDto.getTeam2().getPlayer2().getId()).size() == 4;

    }

    private void verifyAttributes(Match o1, Match o2) {
        if (!o1.equals(o2)){
            throw new RuntimeException("A match for the same start time and court already exists");
        }

    }
}
