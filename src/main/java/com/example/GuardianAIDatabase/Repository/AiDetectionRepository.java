package com.example.GuardianAIDatabase.Repository;

import com.example.GuardianAIDatabase.Entity.AiDetection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Repository
public interface AiDetectionRepository extends JpaRepository<AiDetection, String> {

    Optional<AiDetection> findByContentLogContentId(String contentId);

    // جلب كل الـ detections بتاعة parent معين
    @Query("""
        SELECT d FROM AiDetection d
        WHERE d.contentLog.device.child.parent.parentId = :parentId
        ORDER BY d.contentLog.timestream DESC
    """)
    List<AiDetection> findAllByParentId(@Param("parentId") String parentId);

    // جلب الـ detections بتاعة child معين
    @Query("""
        SELECT d FROM AiDetection d
        WHERE d.contentLog.device.child.childId = :childId
        ORDER BY d.contentLog.timestream DESC
    """)
    List<AiDetection> findAllByChildId(@Param("childId") String childId);
}
