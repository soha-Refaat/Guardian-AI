package com.example.GuardianAIDatabase.Repository;

import com.example.GuardianAIDatabase.Entity.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
@Repository
public interface DeviceRepository extends JpaRepository<Device, String> {
    List<Device> findByChildChildId(String childId);
}
