package com.tenpo.tenpochallenge.application.dto;

import com.tenpo.tenpochallenge.domain.model.CallRecord;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CallHistoryResponse {
    private List<CallRecord> content;
    private int pageNumber;
    private int pageSize;
    private long totalElements;
    private int totalPages;
}