package com.binpacking.data;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * Request received for packing items into a 3D box.
 */
@Data
public class PackingRequest {

    /**
     * Configuration for the box.
     */
    @NotNull(message = "Box configuration is required")
    @Valid
    private BoxConfiguration box;

    /**
     * List of SKU items to be packed.
     */
    @NotEmpty(message = "At least one SKU item is required")
    @Valid
    private List<SKUItem> items;
}
