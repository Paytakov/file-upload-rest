package com.omnilinx.file_upload_rest_svc.repository;

import com.omnilinx.file_upload_rest_svc.model.Player;
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
        Player player = new Player();
        player.setName("Tor");
        player.setPosition("FWD");
        player.setAge(38);
        player.setTeam("Vitez FC");
        player.setCountry("Test");

        long beforeCount = repository.count();
        Player saved = repository.save(player);

        long countAfter = repository.count();
        assertNotNull(saved.getId());
        assertThat(beforeCount).isLessThan(countAfter);

        Optional<Player> fetched = repository.findById(saved.getId());

        assertTrue(fetched.isPresent());
        assertEquals("Tor", fetched.get().getName());
        assertEquals("Vitez FC", fetched.get().getTeam());
    }

    @Test
    void testSaveMultiple() {
        List<Player> players = List.of(
                new Player(null, "A", "AM", 30, "Test1 FC", "CountryTest1"),
                new Player(null, "B", "DEF", 24, "Test2 FC", "CountryTest2")
        );

        List<Player> saved = repository.saveAll(players);

        assertEquals(2, saved.size());
        assertTrue(saved.get(0).getId() != null && saved.get(1).getId() != null);
    }

}