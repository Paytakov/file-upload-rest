package com.omnilinx.file_upload_rest_svc.repository;

import com.omnilinx.file_upload_rest_svc.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {
}
