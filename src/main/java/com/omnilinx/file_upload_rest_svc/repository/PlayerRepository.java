package com.omnilinx.file_upload_rest_svc.repository;

import com.omnilinx.file_upload_rest_svc.model.entity.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {

    List<Player> findByIsSentFalse();

    @Transactional
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Player p SET p.isSent = true WHERE p.id IN :ids")
    void markPlayersAsSent(@Param("ids") List<Long> ids);
}
