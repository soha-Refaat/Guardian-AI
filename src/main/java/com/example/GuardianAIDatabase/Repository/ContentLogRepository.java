package com.example.GuardianAIDatabase.Repository;

import com.example.GuardianAIDatabase.Entity.ContentLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ContentLogRepository extends JpaRepository<ContentLog, UUID> {
    List<ContentLog> findByDeviceDeviceId(UUID deviceId);
}
