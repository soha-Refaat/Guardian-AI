package com.example.GuardianAIDatabase.Services;

import com.example.GuardianAIDatabase.Entity.Child;
import com.example.GuardianAIDatabase.Entity.MonitoredApp;
import com.example.GuardianAIDatabase.Repository.ChildRepository;
import com.example.GuardianAIDatabase.Repository.MonitoredAppRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MonitoredAppService {

    private final MonitoredAppRepository monitoredAppRepository;
    private final ChildRepository childRepository;

    public List<MonitoredApp> getByChild(String childId) {
        return monitoredAppRepository.findByChildChildId(childId);
    }

    public MonitoredApp addApp(String childId, MonitoredApp incoming) {
        Child child = childRepository.findById(childId)
                .orElseThrow(() -> new RuntimeException("Child not found"));

        MonitoredApp app = new MonitoredApp();
        app.setChild(child);
        app.setAppName(incoming.getAppName());
        app.setPackageName(incoming.getPackageName());
        app.setIsActive(true);
        app.setAddedAt(LocalDateTime.now());

        return monitoredAppRepository.save(app);
    }

    public void removeApp(String appId) {
        monitoredAppRepository.deleteById(appId);
    }
}
