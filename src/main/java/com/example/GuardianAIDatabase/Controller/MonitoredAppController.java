package com.example.GuardianAIDatabase.Controller;

import com.example.GuardianAIDatabase.Entity.MonitoredApp;
import com.example.GuardianAIDatabase.Services.MonitoredAppService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MonitoredAppController {

    private final MonitoredAppService monitoredAppService;

    @GetMapping("/children/{childId}/monitored-apps")
    public ResponseEntity<List<MonitoredApp>> getByChild(@PathVariable String childId) {
        return ResponseEntity.ok(monitoredAppService.getByChild(childId));
    }

    @PostMapping("/children/{childId}/monitored-apps")
    public ResponseEntity<MonitoredApp> addApp(@PathVariable String childId,
                                               @RequestBody MonitoredApp app) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(monitoredAppService.addApp(childId, app));
    }

    @DeleteMapping("/monitored-apps/{appId}")
    public ResponseEntity<Void> removeApp(@PathVariable String appId) {
        monitoredAppService.removeApp(appId);
        return ResponseEntity.noContent().build();
    }
}
