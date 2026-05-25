package com.binpacking.controller;

import com.binpacking.data.PackingRequest;
import com.binpacking.data.PackingResponse;
import com.binpacking.service.PackingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Rest controller for FFD Packing.
 */
@Slf4j
@RestController
@RequestMapping("/api/packing")
@RequiredArgsConstructor
public class PackingController {
    private final PackingService packingService;

    /**
     * Optimize packing of the provided SKUs into the given box dimensions.
     *
     * @param request Request containing the SKUs to pack.
     * @return Packing result with box assignments and utilization metrics
     */
    @PostMapping("/optimize")
    public ResponseEntity<PackingResponse> optimize(@Valid @RequestBody PackingRequest request) {
        log.info("POST /api/packing/optimize - {} items", request.getItems().size());
        final var response = packingService.optimizePacking(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Health test endpoint.
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("BinPack API is running.");
    }
}
