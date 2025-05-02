package com.tenpo.tenpochallenge.infrastructure.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.tenpo.tenpochallenge.application.dto.CalculationRequest;
import com.tenpo.tenpochallenge.application.dto.CalculationResponse;
import com.tenpo.tenpochallenge.application.dto.CallHistoryResponse;
import com.tenpo.tenpochallenge.domain.model.CalculationResult;
import com.tenpo.tenpochallenge.domain.model.CallRecord;
import com.tenpo.tenpochallenge.domain.ports.CalculationService;
import com.tenpo.tenpochallenge.domain.ports.CallRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for calculation operations
 * 
 * @author Wilmar Garcia (wilmar.garciava@gmail.com)
 * @see <a href="https://github.com/WilDevp">GitHub Profile</a>
 */
@RestController
@RequestMapping("/v1")
@Tag(name = "Calculation API", description = "API for performing calculations and querying history")
public class CalculationController {

    private static final Logger logger = LoggerFactory.getLogger(CalculationController.class);
    private final CalculationService calculationService;
    private final CallRecordService callRecordService;
    private final ObjectMapper objectMapper;
    private final ObjectMapper prettyPrintMapper;

    public CalculationController(CalculationService calculationService, CallRecordService callRecordService, ObjectMapper objectMapper) {
        this.calculationService = calculationService;
        this.callRecordService = callRecordService;
        this.objectMapper = objectMapper;
        
        // Create a separate mapper for pretty-printing JSON
        this.prettyPrintMapper = objectMapper.copy();
        this.prettyPrintMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    @PostMapping("/calculate")
    @Operation(summary = "Perform calculation", description = "Adds two numbers and applies a dynamic percentage")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful calculation",
            content = @Content(schema = @Schema(implementation = CalculationResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<CalculationResponse> calculate(@Valid @RequestBody CalculationRequest request) {
        CalculationResponse response = null;
        try {
            // Perform calculation
            CalculationResult result = calculationService.calculate(request.getNum1(), request.getNum2());
            
            // Build response
            response = new CalculationResponse();
            response.setNum1(request.getNum1());
            response.setNum2(request.getNum2());
            response.setSum(result.getOriginalSum());
            response.setPercentage(result.getPercentage());
            response.setResult(result.getFinalResult());
            
            // Convert request and response to pretty-printed JSON for recording
            String requestJson = prettyPrintMapper.writeValueAsString(request);
            String responseJson = prettyPrintMapper.writeValueAsString(response);
            
            // Record the call asynchronously (success)
            callRecordService.recordCall(
                "/v1/calculate",
                requestJson,
                responseJson,
                false
            );
            
            return ResponseEntity.ok(response);
        } catch (JsonProcessingException jsonEx) {
            // Handle JSON serialization error (shouldn't happen with simple objects)
            logger.error("Error serializing objects for recording", jsonEx);
            
            // Still try to record error without JSON
            callRecordService.recordCall(
                "/v1/calculate",
                request.toString(),
                jsonEx.getMessage(),
                true
            );
            
            // If we created a response, return it despite logging error
            if (response != null) {
                return ResponseEntity.ok(response);
            }
            
            // Otherwise propagate error
            throw new RuntimeException("Error processing request", jsonEx);
        } catch (Exception e) {
            logger.error("Error in calculation endpoint", e);
            
            // Format request and error for recording
            try {
                String requestJson = prettyPrintMapper.writeValueAsString(request);
                
                // Record the call asynchronously (error)
                callRecordService.recordCall(
                    "/v1/calculate",
                    requestJson,
                    e.getMessage(),
                    true
                );
            } catch (Exception ex) {
                logger.error("Error serializing request for error recording", ex);
                // Fallback to toString
                callRecordService.recordCall(
                    "/v1/calculate",
                    request.toString(),
                    e.getMessage(),
                    true
                );
            }
            
            throw e;
        }
    }

    @GetMapping("/history")
    @Operation(summary = "Get history", description = "Retrieves paginated history of API calls")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful query",
            content = @Content(schema = @Schema(implementation = CallHistoryResponse.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<CallHistoryResponse> getHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Page<CallRecord> history = callRecordService.getCallHistory(page, size);
        
        CallHistoryResponse response = new CallHistoryResponse();
        response.setContent(history.getContent());
        response.setPageNumber(history.getNumber());
        response.setPageSize(history.getSize());
        response.setTotalElements(history.getTotalElements());
        response.setTotalPages(history.getTotalPages());
            
        return ResponseEntity.ok(response);
    }
}