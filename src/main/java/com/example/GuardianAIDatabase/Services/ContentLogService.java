package com.example.GuardianAIDatabase.Services;

import com.example.GuardianAIDatabase.Entity.ContentLog;
import com.example.GuardianAIDatabase.Entity.Device;
import com.example.GuardianAIDatabase.Repository.ContentLogRepository;
import com.example.GuardianAIDatabase.Repository.DeviceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ContentLogService {
    private final ContentLogRepository contentLogRepository;
    private final DeviceRepository deviceRepository;
    public List<ContentLog>getByDevice(String deviceId){
        return contentLogRepository.findByDeviceDeviceId(deviceId);

    }
    public ContentLog getById(String id){
        return contentLogRepository.findById(id).orElseThrow(()->new RuntimeException("Content Log not found"));
    }
    public ContentLog create(String deviceId, ContentLog contentLog){
        Device device = deviceRepository.findById(deviceId).orElseThrow(()->new RuntimeException("Device not found"));
        contentLog.setDevice(device);
        contentLog.setTimestream(LocalDateTime.now());
        return contentLogRepository.save(contentLog);
    }
    public void delete(String id){
        contentLogRepository.deleteById(id);
    }

}
