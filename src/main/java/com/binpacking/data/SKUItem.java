package com.binpacking.data;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Represents an item with a unique SKU code.
 */
@Data
public class SKUItem {
    /**
     * Unique SKU code for the item.
     */
    @NotBlank(message = "SKU code is required")
    private String sku;

    /**
     * Length of the item.
     */
    @NotNull(message = "Item length is required")
    @DecimalMin(value = "0.01", message = "Item length must be greater than 0")
    private Double length;

    /**
     * Width of the item.
     */
    @NotNull(message = "Item width is required")
    @DecimalMin(value = "0.01", message = "Item width must be greater than 0")
    private Double width;

    /**
     * Height of the item.
     */
    @NotNull(message = "Item height is required")
    @DecimalMin(value = "0.01", message = "Item height must be greater than 0")
    private Double height;

    /**
     * Weight of the item.
     */
    @NotNull(message = "Item weight is required")
    @DecimalMin(value = "0.001", message = "Item weight must be greater than 0")
    private Double weight;

    /**
     * Volume of the item.
     *
     * @return volume of the item
     */
    public double volume() {
        return length * width * height;
    }

    /**
     * Checks if the item fits in the given box.
     *
     * @param box The box to check against.
     * @return True if the item fits, false otherwise.
     */
    public boolean fitsInBox(final BoxConfiguration box) {
        return length <= box.getLength()
                && width <= box.getWidth()
                && height <= box.getHeight()
                && weight <= box.getMaxWeight();
    }
}
