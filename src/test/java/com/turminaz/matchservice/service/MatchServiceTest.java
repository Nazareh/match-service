package com.turminaz.matchservice.service;

import com.turminaz.matchservice.domain.model.Match;
import com.turminaz.matchservice.dto.MatchDto;
import com.turminaz.matchservice.dto.MatchResultDto;
import com.turminaz.matchservice.dto.PlayerDto;
import com.turminaz.matchservice.dto.TeamDto;
import com.turminaz.matchservice.mappers.MatchMapper;
import com.turminaz.matchservice.repository.MatchRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Arrays;
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

    MatchService sut;

    MatchDto matchDto;

    @Captor
    ArgumentCaptor<Match> matchCaptor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        sut = new MatchService(matchRepository, MatchMapper.INSTANCE);
        matchDto = buildcopyMatchDto();

        given(matchRepository.save(any()))
                .willAnswer( invocation -> invocation.getArgument(0));
    }

    @AfterEach
    void tearDown() {
        Mockito.reset(matchRepository);
    }

    @Test
    void addMatch_shouldSaveToDbAndSendEmail() {
       var savedMatch = sut.addMatch(matchDto);

        verify(matchRepository).save(matchCaptor.capture());
        verify(matchRepository).findByCourtAndDateTime(matchDto.getCourt(), Instant.parse(matchDto.getDateTime()));
        verifyNoMoreInteractions(matchRepository);

        assertThat(matchCaptor.getValue())
                .usingRecursiveComparison().ignoringFields("id","createdOn","dateTime")
                .isEqualTo(matchDto);

        assertThat(savedMatch.getDateTime()).isEqualTo(matchCaptor.getValue().getDateTime().toString());
    }

    @Test
    void addMatch_shouldHandleDuplicates() {
        //given
        given(matchRepository.findByCourtAndDateTime(anyInt(), any(Instant.class)))
                .willReturn(empty(), ofNullable(MatchMapper.INSTANCE.toEntity(matchDto)));


        //when
        sut.addMatch(matchDto);
        assertThatThrownBy(() -> sut.addMatch(matchDto));

        //then
        verify(matchRepository).save(matchCaptor.capture());
        verify(matchRepository, times(2)).findByCourtAndDateTime(matchDto.getCourt(), Instant.parse(matchDto.getDateTime()));
        verifyNoMoreInteractions(matchRepository);
        assertThat(matchCaptor.getValue())
                .usingRecursiveComparison().ignoringFields("id","createdOn", "dateTime")
                .isEqualTo(matchDto);
    }

    @Test
    void addMatch_shouldRaiseInconsistency() {
        //given
        MatchDto winInconsistency = buildcopyMatchDto();
        winInconsistency.getMatchResult().setWins(matchDto.getMatchResult().getWins() + 1);

        MatchDto team1playerInconsistency = buildcopyMatchDto();
        team1playerInconsistency.getTeam1().getPlayer1().setId(matchDto.getTeam1().getPlayer1().getId() + "abc");

        MatchDto team2playerInconsistency = buildcopyMatchDto();
        team2playerInconsistency.getTeam2().getPlayer2().setId(matchDto.getTeam2().getPlayer2().getId() + "abc");

        List<MatchDto> inconsistentObjects = Arrays.asList(
                winInconsistency,
                buildcopyMatchDto().setRated(!matchDto.isRated()),
                team1playerInconsistency,
                team2playerInconsistency
        );

        given(matchRepository.findByCourtAndDateTime(anyInt(), any(Instant.class)))
                .willReturn(ofNullable(MatchMapper.INSTANCE.toEntity(matchDto)));

        //when
        inconsistentObjects.forEach((inconsistentMatch) -> {
            System.out.println(inconsistentMatch);
            assertThatThrownBy(() -> sut.addMatch(inconsistentMatch)).isInstanceOf(RuntimeException.class)
            .hasMessageContaining("A match for the same start time and court already exists");
        });

        //then
        verify(matchRepository, times(inconsistentObjects.size()))
                .findByCourtAndDateTime(matchDto.getCourt(),Instant.parse(matchDto.getDateTime()));
        verifyNoMoreInteractions(matchRepository);
    }

    @Test
    void addMatch_shouldRaiseErrorOnDuplicatePlayers() {
        matchDto.getTeam1().setPlayer2(matchDto.getTeam1().getPlayer1());

        assertThatThrownBy(() -> sut.addMatch(matchDto)).isInstanceOf(RuntimeException.class)
                .hasMessageContaining("The match doesnt have four distinct players");

        verifyNoMoreInteractions(matchRepository);
    }

    private MatchDto buildcopyMatchDto() {
        return new MatchDto().setCourt(3)
                .setDateTime("2023-02-01T20:30:00Z")
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
