package com.binpacking.data;

import lombok.Builder;
import lombok.Data;

/**
 * Represents a packed item.
 */
@Data
@Builder
public class PackedItem {
    /**
     * The SKU of the item.
     */
    private String sku;

    /**
     * Length of the item.
     */
    private double length;

    /**
     * Width of the item.
     */
    private double width;

    /**
     * Height of the item.
     */
    private double height;

    /**
     * Weight of the item.
     */
    private double weight;

    /**
     * Volume of the item.
     */
    private double volume;
}
