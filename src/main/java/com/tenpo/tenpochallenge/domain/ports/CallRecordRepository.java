package com.tenpo.tenpochallenge.domain.ports;

import com.tenpo.tenpochallenge.domain.model.CallRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Port for the call record repository
 */
public interface CallRecordRepository {
    /**
     * Saves a call record
     * @param callRecord The record to save
     * @return The saved record with its ID
     */
    CallRecord save(CallRecord callRecord);
    
    /**
     * Gets all call records with pagination
     * @param pageable The pagination configuration
     * @return A page of call records
     */
    Page<CallRecord> findAll(Pageable pageable);
}
