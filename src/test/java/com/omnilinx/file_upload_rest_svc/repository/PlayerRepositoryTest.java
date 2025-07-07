package com.omnilinx.file_upload_rest_svc.repository;

import com.omnilinx.file_upload_rest_svc.model.entity.Player;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class PlayerRepositoryTest {

    @Autowired
    private PlayerRepository repository;

    @Test
    void testSaveShouldStoreEntity() {
        Player player = buildPlayer("Tor", 38, "FWD", "Vitez FC", "Test");

        long beforeCount = repository.count();
        Player saved = repository.save(player);

        long countAfter = repository.count();
        assertNotNull(saved.getId());
        assertThat(beforeCount).isLessThan(countAfter);

        Optional<Player> fetched = repository.findById(saved.getId());

        assertTrue(fetched.isPresent());
        assertEquals("Tor", fetched.get().getName());
        assertEquals("Vitez FC", fetched.get().getTeam());
        assertFalse(fetched.get().isSent());
    }

    @Test
    void testSaveMultiple() {
        List<Player> players = List.of(
                buildPlayer("A", 30, "AM", "Test1 FC", "CountryTest1"),
                buildPlayer("B", 24, "DEF", "Test2 FC", "CountryTest2")
        );

        List<Player> saved = repository.saveAll(players);

        assertEquals(2, saved.size());
        assertTrue(saved.get(0).getId() != null && saved.get(1).getId() != null);
    }

    @Test
    void testMarkPlayersAsSentShouldUpdateSentFieldToTrue() {
        List<Player> players = List.of(
                buildPlayer("A", 30, "AM", "Test1 FC", "CountryTest1"),
                buildPlayer("B", 24, "DEF", "Test2 FC", "CountryTest2")
        );
        repository.saveAll(players);

        List<Player> unsentPlayers = repository.findByIsSentFalse();
        assertEquals(2, unsentPlayers.size());
        assertFalse(unsentPlayers.stream().allMatch(Player::isSent));

        List<Long> idsToUpdate = unsentPlayers.stream().map(Player::getId).toList();
        repository.markPlayersAsSent(idsToUpdate);

        List<Player> unsentAfter = repository.findByIsSentFalse();
        assertEquals(0, unsentAfter.size());

        List<Player> updated = repository.findAll();
        assertTrue(updated.stream().allMatch(Player::isSent));
    }

    private Player buildPlayer(String name, int age, String position, String team, String country) {
        return Player.builder()
                .name(name)
                .age(age)
                .position(position)
                .team(team)
                .country(country)
                .build();
    }

}