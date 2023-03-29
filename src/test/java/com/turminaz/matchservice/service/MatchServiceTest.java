package com.turminaz.matchservice.service;

import com.turminaz.matchservice.domain.model.Match;
import com.turminaz.matchservice.dto.MatchDto;
import com.turminaz.matchservice.dto.MatchResultDto;
import com.turminaz.matchservice.dto.PlayerDto;
import com.turminaz.matchservice.dto.TeamDto;
import com.turminaz.matchservice.mappers.MatchMapper;
import com.turminaz.matchservice.repository.MatchRepository;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
public class MatchServiceTest {

    @Mock
    MatchRepository matchRepository;

    @Mock
    EmailService emailService;

    MatchService sut;

    MatchDto matchDto;

    @Captor
    ArgumentCaptor<Match> matchCaptor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        sut = new MatchService(matchRepository, MatchMapper.INSTANCE, emailService);
        matchDto = buildMatchDto();

        given(matchRepository.save(any()))
                .willAnswer( invocation -> invocation.getArgument(0));
    }

    @AfterEach
    void tearDown() {
        Mockito.reset(matchRepository, emailService);
    }

    @Test
    void addMatch_shouldSaveToDbAndSendEmail() {
       var savedMatch = sut.addMatch(matchDto);

        verify(matchRepository).save(matchCaptor.capture());
        verifyNoMoreInteractions(matchRepository);

        assertThat(matchCaptor.getValue())
                .usingRecursiveComparison().ignoringFields("id","createdOn","dateTime")
                .isEqualTo(matchDto);

        assertThat(savedMatch.getDateTime()).isEqualTo(matchCaptor.getValue().getDateTime().toString());
    }

    @Test
    void addMatch_shouldRaiseErrorOnDuplicatePlayers() {
        matchDto.getTeam1().setPlayer2(matchDto.getTeam1().getPlayer1());

        assertThatThrownBy(() -> sut.addMatch(matchDto)).isInstanceOf(RuntimeException.class)
                .hasMessageContaining("The match doesnt have four distinct players");

        verifyNoMoreInteractions(matchRepository);
    }

    private MatchDto buildMatchDto() {
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
