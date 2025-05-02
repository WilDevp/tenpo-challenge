package com.tenpo.tenpochallenge.domain.ports;

import com.tenpo.tenpochallenge.domain.model.CallRecord;
import org.springframework.data.domain.Page;

/**
 * Port for the call record service
 */
public interface CallRecordService {
    /**
     * Records an API call asynchronously
     * @param endpoint The endpoint that was called
     * @param parameters The call parameters
     * @param response The response received
     * @param isError Indicates if the call resulted in an error
     */
    void recordCall(String endpoint, String parameters, String response, boolean isError);
    
    /**
     * Gets the call history with pagination
     * @param page Page number (0-indexed)
     * @param size Page size
     * @return A page of records
     */
    Page<CallRecord> getCallHistory(int page, int size);
}
