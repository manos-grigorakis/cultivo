package com.mgrigorakis.cultivo.seedbeds.service;

import com.mgrigorakis.cultivo.common.dto.PageFilterRequest;
import com.mgrigorakis.cultivo.common.dto.PageSortRequest;
import com.mgrigorakis.cultivo.seedbeds.dto.SeedbedCreateRequest;
import com.mgrigorakis.cultivo.seedbeds.dto.SeedbedDetailResponse;
import com.mgrigorakis.cultivo.seedbeds.dto.SeedbedResponse;
import com.mgrigorakis.cultivo.seedbeds.dto.SeedbedUpdateRequest;
import com.mgrigorakis.cultivo.seedbeds.model.Seedbed;
import org.springframework.data.domain.Page;

public interface SeedbedService {
    Page<SeedbedResponse> getAllSeedbeds(PageFilterRequest filterRequest, PageSortRequest sortRequest);

    SeedbedDetailResponse getSeedbedById(Long id);

    Seedbed findSeedbedById(Long id);

    SeedbedDetailResponse createSeedbed(SeedbedCreateRequest request);

    SeedbedResponse updateSeedbedById(Long id, SeedbedUpdateRequest request);

    void deleteSeedbedById(Long id);
}
