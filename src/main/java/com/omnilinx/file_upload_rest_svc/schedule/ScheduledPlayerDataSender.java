package com.omnilinx.file_upload_rest_svc.schedule;

import com.omnilinx.file_upload_rest_svc.helper.RestClient;
import com.omnilinx.file_upload_rest_svc.model.dto.PlayerDto;
import com.omnilinx.file_upload_rest_svc.model.entity.Player;
import com.omnilinx.file_upload_rest_svc.repository.PlayerRepository;
import com.omnilinx.file_upload_rest_svc.util.ModelFactoryUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class ScheduledPlayerDataSender {

    private final PlayerRepository playerRepository;
    private final RestClient client;

    public ScheduledPlayerDataSender(PlayerRepository playerRepository, RestClient client) {
        this.playerRepository = playerRepository;
        this.client = client;
    }

    @Scheduled(fixedRateString = "${app.scheduling.interval-ms}")
    public void sendPlayers() {
        List<Player> playersToSend = playerRepository.findByIsSentFalse();

        if (!playersToSend.isEmpty()) {
            List<PlayerDto> playerDtosToSend = playersToSend.stream()
                    .map(p -> ModelFactoryUtil.buildPlayerDto(
                            p.getName(), p.getPosition(), p.getAge(), p.getTeam(), p.getCountry()))
                    .toList();

            log.info("Scheduled sending of players data to remote server");
            client.send(playerDtosToSend);
            List<Long> playersToUpdateIds = playersToSend.stream().map(Player::getId).toList();
            playerRepository.markPlayersAsSent(playersToUpdateIds);
        }
    }
}

