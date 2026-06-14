package com.example.GuardianAIDatabase.Controller;

import com.example.GuardianAIDatabase.DTOs.GeneratePairingRequest;
import com.example.GuardianAIDatabase.DTOs.GeneratePairingResponse;
import com.example.GuardianAIDatabase.DTOs.VerifyPairingRequest;
import com.example.GuardianAIDatabase.DTOs.VerifyPairingResponse;
import com.example.GuardianAIDatabase.Services.PairingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/pairing")
@RequiredArgsConstructor
public class PairingController {
    private final PairingService pairingService;

    //parent App
    @PostMapping("/generate")
    public ResponseEntity<GeneratePairingResponse>generateCode(@RequestBody GeneratePairingRequest request){
        return ResponseEntity.ok(pairingService.generateCode(request.getChildId()));
    }

    //Child App
    @PostMapping("/verify")
    public ResponseEntity<VerifyPairingResponse>verifyCode(@RequestBody VerifyPairingRequest request){
        return ResponseEntity.ok(pairingService.verifyCode(request));
    }


}
