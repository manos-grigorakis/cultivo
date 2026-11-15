package com.mgrigorakis.cultivo.seeds.service;

import com.mgrigorakis.cultivo.common.dto.PageFilterRequest;
import com.mgrigorakis.cultivo.common.dto.PageSortRequest;
import com.mgrigorakis.cultivo.seeds.dto.SeedPacketCreateRequest;
import com.mgrigorakis.cultivo.seeds.dto.SeedPacketResponse;
import com.mgrigorakis.cultivo.seeds.dto.SeedPacketUpdateRequest;
import com.mgrigorakis.cultivo.seeds.model.SeedPacket;
import org.springframework.data.domain.Page;

public interface SeedPacketService {
    Page<SeedPacketResponse> getAllSeedPackets(PageFilterRequest filterRequest, PageSortRequest sortRequest);

    SeedPacketResponse getSeedPacketById(Long id);

    SeedPacket findSeedPacketById(Long id);

    SeedPacketResponse createSeedPacket(SeedPacketCreateRequest request);

    SeedPacketResponse updateSeedPacketById(Long id, SeedPacketUpdateRequest request);

    void deleteSeedPacketById(Long id);
}
