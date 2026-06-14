package com.example.GuardianAIDatabase.Repository;

import com.example.GuardianAIDatabase.Entity.PairingCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PairingCodeRepository extends JpaRepository<PairingCode,String> {

    Optional<PairingCode> findByCodeAndUsedFalse(String code);
}
