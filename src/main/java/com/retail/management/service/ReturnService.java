package com.retail.management.service;

import com.retail.management.dto.request.returnDto.CreateReturnRequest;
import com.retail.management.dto.response.returnDto.ReturnResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReturnService {
    ReturnResponse createReturn(CreateReturnRequest request);
    ReturnResponse getReturnById(Long id);
    Page<ReturnResponse> getAllReturns(Pageable pageable);
    ReturnResponse approveReturn(Long id);
    ReturnResponse rejectReturn(Long id, String reason);
}