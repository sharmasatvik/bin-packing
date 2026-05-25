package com.binpacking.algorithm;

import com.binpacking.data.BoxConfiguration;
import com.binpacking.data.SKUItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit test cases for {@link FirstFitDecreasingAlgorithm}
 */
class FirstFitDecreasingAlgorithmTest {
    private FirstFitDecreasingAlgorithm algorithm;
    private BoxConfiguration box;

    @BeforeEach
    void setUp() {
        algorithm = new FirstFitDecreasingAlgorithm();

        box = new BoxConfiguration();
        box.setLength(100.0);
        box.setWidth(100.0);
        box.setHeight(100.0);
        box.setMaxWeight(50.0);
    }

    @Test
    @DisplayName("All items fit in a single box")
    void allItemsFitInOneBox() {
        final var items = List.of(
                item("SKU-A", 10, 10, 10, 1.0),
                item("SKU-B", 10, 10, 10, 1.0),
                item("SKU-C", 10, 10, 10, 1.0)
        );

        var result = algorithm.pack(box, items);

        assertThat(result.packedBoxes()).hasSize(1);
        assertThat(result.unpackedSkus()).isEmpty();
        assertThat(result.totalPackedItems()).isEqualTo(3);
    }

    @Test
    @DisplayName("Items split across multiple boxes due to volume")
    void itemsSplitAcrossBoxes() {
        // Each item is 60% of box volume, so each needs its own box
        final var items = List.of(
                item("SKU-A", 90, 90, 90, 1.0),
                item("SKU-B", 90, 90, 90, 1.0),
                item("SKU-C", 90, 90, 90, 1.0)
        );

        var result = algorithm.pack(box, items);

        assertThat(result.packedBoxes()).hasSize(3);
        assertThat(result.unpackedSkus()).isEmpty();
    }

    @Test
    @DisplayName("Item too large for any box goes to unpacked")
    void oversizedItemGoesToUnpacked() {
        final var items = List.of(
                item("GIANT", 200, 200, 200, 1.0),  // too big
                item("SMALL", 10, 10, 10, 1.0)
        );

        var result = algorithm.pack(box, items);

        assertThat(result.unpackedSkus()).containsExactly("GIANT");
        assertThat(result.totalPackedItems()).isEqualTo(1);
    }

    @Test
    @DisplayName("Item exceeding max weight goes to unpacked")
    void overweightItemGoesToUnpacked() {
        final var items = List.of(
                item("HEAVY", 10, 10, 10, 999.0)   // over 50kg limit
        );

        var result = algorithm.pack(box, items);

        assertThat(result.unpackedSkus()).containsExactly("HEAVY");
        assertThat(result.packedBoxes()).isEmpty();
    }

    @Test
    @DisplayName("Weight constraint splits items into separate boxes")
    void weightConstraintSplitsBoxes() {
        // box max weight = 50kg; each item = 30kg → each needs separate box
        final var items = List.of(
                item("SKU-A", 10, 10, 10, 30.0),
                item("SKU-B", 10, 10, 10, 30.0)
        );

        var result = algorithm.pack(box, items);

        assertThat(result.packedBoxes()).hasSize(2);
        assertThat(result.unpackedSkus()).isEmpty();
    }

    @Test
    @DisplayName("Empty item list returns empty result")
    void emptyItemListReturnsEmpty() {
        var result = algorithm.pack(box, List.of());

        assertThat(result.packedBoxes()).isEmpty();
        assertThat(result.unpackedSkus()).isEmpty();
        assertThat(result.totalPackedItems()).isZero();
    }

    @Test
    @DisplayName("Utilization percent is calculated correctly")
    void utilizationCalculatedCorrectly() {
        // Box volume = 100*100*100 = 1,000,000 cm³
        // Item volume = 50*100*100 = 500,000 cm³ → 50% utilization
        final var items = List.of(
                item("SKU-HALF", 50, 100, 100, 1.0)
        );

        var result = algorithm.pack(box, items);

        assertThat(result.packedBoxes()).hasSize(1);
        assertThat(result.packedBoxes().getFirst().getUtilizationPercent()).isEqualTo(50.0);
    }

    /**
     * Helps create a new SKU Item.
     *
     * @param sku    The SKU of the item.
     * @param length The length of the item.
     * @param width  The width of the item.
     * @param height The height of the item.
     * @param weight The weight of the item.
     * @return An SKU Item.
     */
    private SKUItem item(final String sku
            , final double length
            , final double width
            , final double height
            , final double weight) {
        SKUItem item = new SKUItem();
        item.setSku(sku);
        item.setLength(length);
        item.setWidth(width);
        item.setHeight(height);
        item.setWeight(weight);
        return item;
    }
}
