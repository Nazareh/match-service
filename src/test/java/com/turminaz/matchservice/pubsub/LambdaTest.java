//package com.turminaz.matchservice.pubsub;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.turminaz.matchservice.aws.Lambda;
//import com.turminaz.matchservice.dto.MatchDto;
//import com.turminaz.matchservice.service.MatchService;
////import org.jeasy.random.EasyRandom;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.ArgumentCaptor;
//import org.mockito.Captor;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.util.function.Function;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.mockito.Mockito.verify;
//
//@ExtendWith(MockitoExtension.class)
//class LambdaTest {
//
//    @Mock
//    MatchService matchService;
//
//    @InjectMocks
//    private Lambda sut;
//
//    @Captor
//    private ArgumentCaptor<MatchDto> captor;
//
//    @Test
//    void pubSubFunction_shouldDecodeAndDeserialize() throws JsonProcessingException {
//        //given
//        MatchDto matchDto = new EasyRandom().nextObject(MatchDto.class);
//
//        //when
//        Function<MatchDto, String> consumer = sut.lambdaFunction();
//        consumer.apply(matchDto);
//
//       //then
//        verify(matchService).addMatch(captor.capture());
//        assertThat(captor.getValue()).usingRecursiveComparison().isEqualTo(matchDto);
//
//    }
//}