package com.binpacking.service;

import com.binpacking.algorithm.FirstFitDecreasingAlgorithm;
import com.binpacking.data.PackingRequest;
import com.binpacking.data.PackingResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service for packing items into a 3D box.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PackingService {
    private final FirstFitDecreasingAlgorithm algorithm;

    /**
     * Optimize packing of the provided SKUs into the given box dimensions.
     *
     * @param request Request containing the SKUs to pack and the box configuration.
     *
     * @return Packing result with box assignments and utilization metrics.
     */
    public PackingResponse optimizePacking(final PackingRequest request) {
        log.info("PackingService: received packing request with {} items", request.getItems().size());

        final var box = request.getBox();
        final var items = request.getItems();

        final var result = algorithm.pack(box, items);

        PackingResponse response = PackingResponse.builder()
                                                  .totalBoxes(result.packedBoxes().size())
                                                  .packedBoxes(result.packedBoxes())
                                                  .unpackedItems(result.unpackedSkus())
                                                  .totalItems(items.size())
                                                  .build();

        log.info("PackingService: returning {} boxes, {} unpacked items",
                 response.getTotalBoxes(), response.getUnpackedItems().size());

        return response;
    }
}
