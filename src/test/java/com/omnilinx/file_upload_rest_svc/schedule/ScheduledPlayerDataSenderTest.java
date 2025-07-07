package com.omnilinx.file_upload_rest_svc.schedule;

import com.omnilinx.file_upload_rest_svc.exception.PlayerSendingException;
import com.omnilinx.file_upload_rest_svc.helper.RestClient;
import com.omnilinx.file_upload_rest_svc.model.entity.Player;
import com.omnilinx.file_upload_rest_svc.repository.PlayerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class ScheduledPlayerDataSenderTest {

    @Mock
    private PlayerRepository playerRepository;
    @Mock
    private RestClient client;
    @InjectMocks
    private ScheduledPlayerDataSender toTest;

    @Test
    void testSendPlayersSuccessWhenUnsentPlayersExist() {
        Player player1 = new Player(1L, "John", "DEF", 25, "AC Milan", "Italy", false);
        Player player2 = new Player(2L, "Mark", "GK", 30, "PSG", "France", false);
        List<Player> unsentPlayers = List.of(player1, player2);

        when(playerRepository.findByIsSentFalse()).thenReturn(unsentPlayers);

        toTest.sendPlayers();

        verify(client).send(argThat(dtos -> dtos.size() == 2));
        verify(playerRepository).markPlayersAsSent(List.of(1L, 2L));
    }

    @Test
    void testSendPlayersShouldNotSendWhenNoUnsentPlayers() {
        when(playerRepository.findByIsSentFalse()).thenReturn(Collections.emptyList());

        toTest.sendPlayers();

        verifyNoInteractions(client);
        verify(playerRepository, never()).markPlayersAsSent(any());
    }

    @Test
    void testSendPlayersShouldNotMarkAsSentWhenRestCallFails() {
        Player p = new Player(1L, "Tosho", "DEF", 25, "Cottbus FC", "Germany", false);
        when(playerRepository.findByIsSentFalse()).thenReturn(List.of(p));
        doThrow(new PlayerSendingException("Server down")).when(client).send(any());

        assertThrows(PlayerSendingException.class, toTest::sendPlayers);

        verify(client).send(any());
        verify(playerRepository, never()).markPlayersAsSent(any());
    }

}