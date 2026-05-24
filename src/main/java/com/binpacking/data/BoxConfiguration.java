package com.binpacking.data;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Configuration for a 3D box.
 */
@Data
public class BoxConfiguration {
    /**
     * Length of the box.
     */
    @NotNull(message = "Box length is required")
    @DecimalMin(value = "0.01", message = "Box length must be greater than 0")
    private Double length;

    /**
     * Width of the box.
     */
    @NotNull(message = "Box width is required")
    @DecimalMin(value = "0.01", message = "Box width must be greater than 0")
    private Double width;

    /**
     * Height of the box.
     */
    @NotNull(message = "Box height is required")
    @DecimalMin(value = "0.01", message = "Box height must be greater than 0")
    private Double height;

    /**
     * Maximum weight that can be carried by the box.
     */
    @NotNull(message = "Max weight is required")
    @DecimalMin(value = "0.01", message = "Max weight must be greater than 0")
    private Double maxWeight;

    /**
     * Volume of the box.
     *
     * @return volume of the box
     */
    public double volume() {
        return length * width * height;
    }
}
