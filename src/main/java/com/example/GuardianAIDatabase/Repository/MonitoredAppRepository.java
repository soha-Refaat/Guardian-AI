package com.example.GuardianAIDatabase.Repository;

import com.example.GuardianAIDatabase.Entity.MonitoredApp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MonitoredAppRepository extends JpaRepository<MonitoredApp, String> {
    List<MonitoredApp> findByChildChildId(String childId);
}
