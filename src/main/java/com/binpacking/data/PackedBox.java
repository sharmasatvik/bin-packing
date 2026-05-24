package com.binpacking.data;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * Represents a packed 3D box.
 */
@Data
@Builder
public class PackedBox {
    /**
     * The box number.
     */
    private int boxNumber;

    /**
     * The items packed into the box.
     */
    private List<PackedItem> items;

    /**
     * Total weight of the box.
     */
    private double totalWeight;

    /**
     * Utilization percentage of the box.
     */
    private double utilizationPercent;

    /**
     * Remaining volume of the box.
     */
    private double remainingVolume;
}
