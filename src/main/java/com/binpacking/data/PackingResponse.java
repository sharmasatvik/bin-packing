package com.binpacking.data;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * Response for the packing request.
 */
@Data
@Builder
public class PackingResponse {
    /**
     * Total number of boxes used to pack the items.
     */
    private int totalBoxes;

    /**
     * Packed boxes.
     */
    private List<PackedBox> packedBoxes;

    /**
     * Items that could not be packed.
     */
    private List<String> unpackedItems;

    /**
     * Total number of items packed.
     */
    private int totalItems;
}
