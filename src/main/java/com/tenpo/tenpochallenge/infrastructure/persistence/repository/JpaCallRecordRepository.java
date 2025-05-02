package com.tenpo.tenpochallenge.infrastructure.persistence.repository;

import com.tenpo.tenpochallenge.infrastructure.persistence.entity.CallRecordEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * JPA Repository for call records
 */
@Repository
public interface JpaCallRecordRepository extends JpaRepository<CallRecordEntity, Long> {
    Page<CallRecordEntity> findAllByOrderByTimestampDesc(Pageable pageable);
}
