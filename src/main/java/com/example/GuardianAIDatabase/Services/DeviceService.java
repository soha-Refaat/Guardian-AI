package com.example.GuardianAIDatabase.Services;

import com.example.GuardianAIDatabase.Entity.Child;
import com.example.GuardianAIDatabase.Entity.Device;
import com.example.GuardianAIDatabase.Repository.ChildRepository;
import com.example.GuardianAIDatabase.Repository.DeviceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeviceService {
    private final DeviceRepository deviceRepository;
    private final ChildRepository childRepository;
    public List<Device>getByChild(String childId){
        return deviceRepository.findByChildChildId(childId);
    }
    public Device getById(String id){
        return deviceRepository.findById(id).orElseThrow(()->new RuntimeException("Device not found"));
    }
    public Device create(String childId, Device device){
        Child child = childRepository.findById(childId).orElseThrow(()->new RuntimeException("Child not found"));
        device.setChild(child);
        device.setLastSeen(LocalDateTime.now());
        return deviceRepository.save(device);
    }
    public Device update(String id, Device device){
        Device existing = getById(id);
        existing.setDeviceName(device.getDeviceName());
        existing.setDeviceVersion(device.getDeviceVersion());
        existing.setActive(device.isActive());
        existing.setLastSeen(LocalDateTime.now());
        return deviceRepository.save(existing);
    }
    public void delete(String id){
        deviceRepository.deleteById(id);
    }

}
