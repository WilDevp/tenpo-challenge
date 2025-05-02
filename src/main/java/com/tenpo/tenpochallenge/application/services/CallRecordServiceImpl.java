package com.tenpo.tenpochallenge.application.services;

import com.tenpo.tenpochallenge.domain.model.CallRecord;
import com.tenpo.tenpochallenge.domain.ports.CallRecordRepository;
import com.tenpo.tenpochallenge.domain.ports.CallRecordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class CallRecordServiceImpl implements CallRecordService {
    
    private static final Logger logger = LoggerFactory.getLogger(CallRecordServiceImpl.class);
    
    private final CallRecordRepository callRecordRepository;
    
    public CallRecordServiceImpl(CallRecordRepository callRecordRepository) {
        this.callRecordRepository = callRecordRepository;
    }
    
    @Override
    @Async
    @Transactional
    public void recordCall(String endpoint, String parameters, String response, boolean isError) {
        try {
            CallRecord callRecord = CallRecord.builder()
                .endpoint(endpoint)
                .requestParams(parameters)
                .response(response)
                .errorMessage(isError ? response : null)
                .statusCode(isError ? 500 : 200)
                .isError(isError)
                .timestamp(LocalDateTime.now())
                .build();
            
            callRecordRepository.save(callRecord);
            logger.debug("Call successfully recorded: {}", callRecord);
        } catch (Exception e) {
            logger.error("Error recording call: endpoint={}, parameters={}", endpoint, parameters, e);
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<CallRecord> getCallHistory(int page, int size) {
        return callRecordRepository.findAll(PageRequest.of(page, size));
    }
}