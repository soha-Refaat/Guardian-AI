package com.example.GuardianAIDatabase.Services;

import com.example.GuardianAIDatabase.DTOs.GeneratePairingResponse;
import com.example.GuardianAIDatabase.DTOs.VerifyPairingRequest;
import com.example.GuardianAIDatabase.DTOs.VerifyPairingResponse;
import com.example.GuardianAIDatabase.Entity.Child;
import com.example.GuardianAIDatabase.Entity.Device;
import com.example.GuardianAIDatabase.Entity.PairingCode;
import com.example.GuardianAIDatabase.Entity.Parent;
import com.example.GuardianAIDatabase.Repository.ChildRepository;
import com.example.GuardianAIDatabase.Repository.DeviceRepository;
import com.example.GuardianAIDatabase.Repository.PairingCodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class PairingService {
    private final PairingCodeRepository pairingCodeRepository;
    private final ChildRepository childRepository;
    private final DeviceRepository deviceRepository;
    private final JwtService jwtService;
    public GeneratePairingResponse generateCode(String childId){
        Child child = childRepository.findById(childId).orElseThrow(
                ()-> new RuntimeException("Child Not Found")
        );
        String code = generateRandomCode();
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(10);
        PairingCode pairingCode = new PairingCode();
        pairingCode.setCode(code);
        pairingCode.setChild(child);
        pairingCode.setExpiresAt(expiresAt);
        pairingCodeRepository.save(pairingCode);
        return new GeneratePairingResponse(code,expiresAt);
    }

    public VerifyPairingResponse verifyCode(VerifyPairingRequest request){
        PairingCode pairingCode = pairingCodeRepository
                .findByCodeAndUsedFalse(request.getCode()).orElseThrow(
                        ()->new RuntimeException("Invalid or used code")
                );
        if(pairingCode.getExpiresAt().isBefore(LocalDateTime.now())){
            throw new RuntimeException("Code has expired");
        }
        Child child = pairingCode.getChild();
        Parent parent = child.getParent();

        Device device = new Device();
        device.setChild(child);
        device.setDeviceName(request.getDeviceName());
        device.setDeviceVersion(request.getAndroidVersion());
        device.setActive(true);
        device.setLastSeen(LocalDateTime.now());
        deviceRepository.save(device);

        pairingCode.setUsed(true);
        pairingCodeRepository.save(pairingCode);

        String authToken = jwtService.generateToken(parent.getEmail());

        return new VerifyPairingResponse(
                child.getChildId(),
                child.getName(),
                device.getDeviceId(),
                authToken,
                parent.getParentId(),
                "Device paired successfully"
        );
    }

    private String generateRandomCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000); // 6 digits
        return String.valueOf(code);
    }

}
