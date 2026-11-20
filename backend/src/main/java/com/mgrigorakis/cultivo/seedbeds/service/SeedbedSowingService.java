package com.mgrigorakis.cultivo.seedbeds.service;

import com.mgrigorakis.cultivo.common.dto.PageFilterRequest;
import com.mgrigorakis.cultivo.common.dto.PageSortRequest;
import com.mgrigorakis.cultivo.seedbeds.dto.SeedbedSowingRequest;
import com.mgrigorakis.cultivo.seedbeds.dto.SeedbedSowingResponse;
import com.mgrigorakis.cultivo.seedbeds.dto.SeedbedSowingSearchCriteria;
import com.mgrigorakis.cultivo.seedbeds.dto.SeedbedSowingStatusRequest;
import com.mgrigorakis.cultivo.seedbeds.model.SeedbedSowing;
import org.springframework.data.domain.Page;

public interface SeedbedSowingService {
    Page<SeedbedSowingResponse> getAllSeedbedSowings(PageFilterRequest filterRequest, PageSortRequest sortRequest,
                                                     SeedbedSowingSearchCriteria criteria);

    SeedbedSowingResponse getSeedbedSowingByIdAndSeedbedIdAndSlotId(Long id, Long seedbedId, Long slotId);

    SeedbedSowingResponse getSeedbedSowingById(Long id);

    SeedbedSowing findSeedbedSowingById(Long id);

    SeedbedSowingResponse createSeedbedSowing(Long seedbedId, Long slotId, SeedbedSowingRequest request);

    SeedbedSowingResponse updateSeedbedSowingById(Long id, Long seedbedId, Long slotId, SeedbedSowingRequest request);

    void updateSeedbedSowingStatusById(Long id, Long seedbedId, Long slotId, SeedbedSowingStatusRequest request);
}
