package com.turminaz.matchservice.service;

import com.turminaz.matchservice.domain.model.Match;
import com.turminaz.matchservice.dto.MatchDto;
import com.turminaz.matchservice.dto.MatchResultDto;
import com.turminaz.matchservice.dto.PlayerDto;
import com.turminaz.matchservice.dto.TeamDto;
import com.turminaz.matchservice.mappers.MatchMapper;
import com.turminaz.matchservice.mappers.MatchMapperImpl;
import com.turminaz.matchservice.repository.MatchRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MatchServiceTest {

    @Mock
    MatchRepository matchRepository;

    @Mock
    EmailService emailService;

    MatchMapper matchMapper = new MatchMapperImpl();

    MatchService sut;

    MatchDto matchDto;

    @Captor
    ArgumentCaptor<Match> matchCaptor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        sut = new MatchService(matchRepository, MatchMapper.INSTANCE,emailService);
        matchDto = buildcopyMatchDto();

        given(matchRepository.save(any()))
                .willAnswer( invocation -> invocation.getArgument(0));
    }

    @AfterEach
    void tearDown() {
        Mockito.reset(matchRepository, emailService);
    }

    @Test
    void addMatch_shouldSaveToDbAndSendEmail() {
        sut.addMatch(matchDto);

        verify(matchRepository).save(matchCaptor.capture());
        verify(matchRepository).findByCourtAndStart(matchDto.getCourt(), matchDto.getStart());
        verifyNoMoreInteractions(matchRepository);
        verify(emailService).sendSimpleMessage(any(),anyString(), anyString());
        verifyNoMoreInteractions(emailService);
        assertThat(matchCaptor.getValue())
                .usingRecursiveComparison().ignoringFields("id","createdOn")
                .isEqualTo(matchDto);
    }

    @Test
    void addMatch_shouldHandleDuplicates() {
        //given
        given(matchRepository.findByCourtAndStart(anyInt(), any(Instant.class)))
                .willReturn(empty(), ofNullable(matchMapper.toEntity(matchDto)));


        //when
        sut.addMatch(matchDto);
        sut.addMatch(matchDto);

        //then
        verify(matchRepository).save(matchCaptor.capture());
        verify(matchRepository, times(2)).findByCourtAndStart(matchDto.getCourt(), matchDto.getStart());
        verifyNoMoreInteractions(matchRepository);
        verify(emailService).sendSimpleMessage(any(), anyString(), anyString());
        verifyNoMoreInteractions(emailService);
        assertThat(matchCaptor.getValue())
                .usingRecursiveComparison().ignoringFields("id","createdOn")
                .isEqualTo(matchDto);
    }

    @Test
    void addMatch_shouldRaiseInconsistency() {
        //given
        var winInconsistency = buildcopyMatchDto();
        winInconsistency.getMatchResult().setWins(matchDto.getMatchResult().getWins() + 1);

        var team1playerInconsistency = buildcopyMatchDto();
        team1playerInconsistency.getTeam1().getPlayer1().setId(matchDto.getTeam1().getPlayer1().getId() + "abc");

        var team2playerInconsistency = buildcopyMatchDto();
        team2playerInconsistency.getTeam2().getPlayer2().setId(matchDto.getTeam2().getPlayer2().getId() + "abc");

        var inconsistentObjects = List.of(
                winInconsistency,
                buildcopyMatchDto().setEnd(matchDto.getEnd().plus(Duration.ofHours(1))),
                buildcopyMatchDto().setRated(!matchDto.isRated()),
                team1playerInconsistency,
                team2playerInconsistency
        );

        given(matchRepository.findByCourtAndStart(anyInt(), any(Instant.class)))
                .willReturn(ofNullable(matchMapper.toEntity(matchDto)));

        //when
        inconsistentObjects.forEach((inconsistentMatch) -> {
            System.out.println(inconsistentMatch);
            assertThatThrownBy(() -> sut.addMatch(inconsistentMatch)).isInstanceOf(RuntimeException.class)
            .hasMessageContaining("A match for the same start time and court already exists");
        });

        //then
        verify(matchRepository, times(inconsistentObjects.size()))
                .findByCourtAndStart(matchDto.getCourt(), matchDto.getStart());
        verifyNoMoreInteractions(matchRepository);
        verify(emailService, times(5)).sendSimpleMessage(any(),anyString(), anyString());
        verifyNoMoreInteractions(emailService);
    }

    @Test
    void addMatch_shouldRaiseErrorOnDuplicatePlayers() {
        matchDto.getTeam1().setPlayer2(matchDto.getTeam1().getPlayer1());

        assertThatThrownBy(() -> sut.addMatch(matchDto)).isInstanceOf(RuntimeException.class)
                .hasMessageContaining("The match doesnt have four distinct players");

        verifyNoMoreInteractions(matchRepository);
        verify(emailService).sendSimpleMessage(any(),anyString(), anyString());
        verifyNoMoreInteractions(emailService);
    }

    private MatchDto buildcopyMatchDto() {
        return new MatchDto().setCourt(3)
                .setStart(Instant.parse("2023-02-01T20:30:00Z"))
                .setEnd(Instant.parse("2023-02-01T22:00:00Z"))
                .setTeam1(new TeamDto()
                        .setPlayer1(new PlayerDto("Naz"))
                        .setPlayer2(new PlayerDto("Rachel")))
                .setTeam2(new TeamDto()
                        .setPlayer1(new PlayerDto("Tom"))
                        .setPlayer2(new PlayerDto("Megan")))
                .setMatchResult(new MatchResultDto(10,9))
                .setRated(true);
    }

}
