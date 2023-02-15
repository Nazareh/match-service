package com.turminaz.matchservice.service;

import com.turminaz.matchservice.domain.model.Match;
import com.turminaz.matchservice.dto.MatchDto;
import com.turminaz.matchservice.mappers.MatchMapper;
import com.turminaz.matchservice.repository.MatchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Log4j2
public class MatchService {

    private final MatchRepository matchRepository;
    private final MatchMapper matchMapper;

    private final EmailService emailService;

    @Value("${app.notifications.email.send-to}")
    private String emailTo;


    public void addMatch(MatchDto matchDto) {

        verifyFourDistinctPlayers(matchDto);

        var newMatch = matchMapper.toEntity(matchDto);

        matchRepository.findByCourtAndStart(matchDto.getCourt(), matchDto.getStart())
                .ifPresentOrElse(
                        entity -> {
                            checkInconsistencies(entity, newMatch);
                            log.info("Match duplicated, ignoring message.");
                        },
                        () -> {
                            var createdMatch = matchRepository.save(newMatch);
                            emailService.sendSimpleMessage(emailTo, "Match Results",
                                    buildEmailTemplate(createdMatch)
                            );
                        }
                );
    }

    private void verifyFourDistinctPlayers(MatchDto matchDto) {

        var players = new HashSet<String>();
        players.add(matchDto.getTeam1().getPlayer1().getId());
        players.add(matchDto.getTeam1().getPlayer2().getId());
        players.add(matchDto.getTeam2().getPlayer1().getId());
        players.add(matchDto.getTeam2().getPlayer2().getId());

        if (players.size() != 4){
            sendInconsistencyEmail(matchMapper.toEntity(matchDto));
            throw new RuntimeException("The match doesnt have four distinct players");
        }

    }

    private void checkInconsistencies(Match o1, Match o2) {
        if (!o1.equals(o2)) {
            sendInconsistencyEmail(o1, o2);
            throw new RuntimeException("A match for the same start time and court already exists");
        }
    }

    private void sendInconsistencyEmail(Match existingMatch, Match newMatch) {
        var o1AsStr = buildEmailTemplate(existingMatch);
        var o2AsStr = buildEmailTemplate(newMatch);

        log.error("Match inconsistency found");
        emailService.sendSimpleMessage(emailTo, "Match Inconsistency", String.format(
                """
                A match for the same start time and court already exists
                                                 
                EXISTING MATCH
                -------------
                    %s
                                                
                NEW MATCH
                -------------
                    %s
                """, o1AsStr, o2AsStr));
    }

    private void sendInconsistencyEmail(Match match) {
        var matchAsStr = buildEmailTemplate(match);
        log.error("Match inconsistency found");
        emailService.sendSimpleMessage(emailTo, "Match Inconsistency", String.format(
                """
                A inconsistency for the following match was found:
                    %s
                """, matchAsStr));
    }

    private static String buildEmailTemplate(Match createdMatch) {
        return String.format(
                """
                Court: %d
                Start: %s
                Finish: %s
                Players: %s - %s X %s - %s
                Result: %d - %d
                Rated: %s
                """, createdMatch.getCourt(), createdMatch.getStart(), createdMatch.getEnd(),
                createdMatch.getTeam1().getPlayer1().getId(), createdMatch.getTeam1().getPlayer2().getId(),
                createdMatch.getTeam2().getPlayer1().getId(), createdMatch.getTeam2().getPlayer2().getId(),
                createdMatch.getMatchResult().getWins(), createdMatch.getMatchResult().getLosses(),
                createdMatch.isRated()
        );
    }
}
