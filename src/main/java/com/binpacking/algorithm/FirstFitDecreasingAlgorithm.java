package com.binpacking.algorithm;

import com.binpacking.data.BoxConfiguration;
import com.binpacking.data.PackedBox;
import com.binpacking.data.PackedItem;
import com.binpacking.data.SKUItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * First Fit Decreasing (FFD) 3D Bin Packing Algorithm.
 * <p>
 * Steps:
 * <ol>
 *      <li>Sort items by volume descending (largest first).</li>
 *      <li>For each item, try to place it into the first bin where it fits (both volume
 *      and weight constraints satisfied).</li>
 *      <li>If no existing bin fits, open a new bin.</li>
 *      <li>Items that exceed the box dimensions individually go to unpackedSkus.</li>
 *  </ol>
 *  </p>
 */
@Slf4j
@Component
public class FirstFitDecreasingAlgorithm {
    /**
     * Pack the given items into the given box.
     *
     * @param box   3D box configuration.
     * @param items List of SKU items to pack.
     * @return Packing result containing packed boxes and unpacked items.
     */
    public PackingResult pack(final BoxConfiguration box, final List<SKUItem> items) {
        log.debug("FFD: packing {} items into box {}x{}x{} maxWt={}",
                items.size(), box.getLength(), box.getWidth(), box.getHeight(), box.getMaxWeight());

        final double boxVolume = box.volume();

        // Separate items that can never fit (oversized in any dimension or overweight)
        final List<String> unpackable = new ArrayList<>();
        final List<SKUItem> packable = new ArrayList<>();

        for (final var item : items) {
            if (item.fitsInBox(box)) {
                packable.add(item);
            } else {
                log.warn("FFD: item '{}' ({}x{}x{} {}kg) exceeds box limits - skipping",
                        item.getSku()
                        , item.getLength()
                        , item.getWidth()
                        , item.getHeight()
                        , item.getWeight());
                unpackable.add(item.getSku());
            }
        }

        // Sort packable items by volume descending.
        packable.sort(Comparator.comparingDouble(SKUItem::volume).reversed());

        // List of open bins
        final List<BinState> bins = new ArrayList<>();

        // Start packing items into bins.
        for (final var item : packable) {
            boolean placed = false;

            for (final var bin : bins) {
                if (bin.canFit(item, box)) {
                    bin.add(item);
                    placed = true;
                    log.debug("FFD: placed '{}' in bin {}", item.getSku(), bin.boxNumber);
                    break;
                }
            }

            if (!placed) {
                // Open a new bin
                final var newBin = new BinState(bins.size() + 1);
                newBin.add(item);
                bins.add(newBin);
                log.debug("FFD: opened new bin {} for '{}'", newBin.boxNumber, item.getSku());
            }
        }

        // Build result DTOs
        final var packedBoxes = bins.stream()
                .map(bin -> PackedBox.builder()
                        .boxNumber(bin.boxNumber)
                        .items(bin.packedItems)
                        .totalWeight(round(bin.usedWeight))
                        .utilizationPercent(round(bin.usedVolume / boxVolume * 100.0))
                        .remainingVolume(round(boxVolume - bin.usedVolume))
                        .build())
                .toList();

        log.info("FFD complete: {} bins used, {} items packed, {} unpacked",
                bins.size(), packable.size(), unpackable.size());

        return new PackingResult(packedBoxes, unpackable, packable.size());
    }

    /**
     * The current state of a bin during packing operations.
     */
    private static class BinState {
        /**
         * The box number associated with this bin state.
         */
        final int boxNumber;

        /**
         * The items packed into this bin.
         */
        final List<PackedItem> packedItems = new ArrayList<>();

        /**
         * The total volume used by items in this bin.
         */
        double usedVolume = 0;

        /**
         * The total weight used by items in this bin.
         */
        double usedWeight = 0;

        /**
         * Constructor.
         *
         * @param boxNumber The box number associated with this bin state.
         */
        BinState(final int boxNumber) {
            this.boxNumber = boxNumber;
        }

        /**
         * Check if the given item can fit in this bin.
         *
         * @param item The item to check.
         * @param box  The box to check against.
         * @return True if the item can fit, false otherwise.
         */
        boolean canFit(final SKUItem item, final BoxConfiguration box) {
            return (usedWeight + item.getWeight()) <= box.getMaxWeight()
                    && (usedVolume + item.volume()) <= box.volume();
        }

        /**
         * Add the given item to this bin.
         *
         * @param item The item to add.
         */
        void add(SKUItem item) {
            usedVolume += item.volume();
            usedWeight += item.getWeight();
            packedItems.add(PackedItem.builder()
                    .sku(item.getSku())
                    .length(item.getLength())
                    .width(item.getWidth())
                    .height(item.getHeight())
                    .weight(item.getWeight())
                    .volume(round(item.volume()))
                    .build());
        }
    }

    /**
     * Round to 2 decimal places.
     *
     * @param value The value to round.
     * @return The rounded value.
     */
    private static double round(final double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    /**
     * Packing result.
     *
     * @param packedBoxes      List of packed boxes.
     * @param unpackedSkus     List of SKUs that could not be packed.
     * @param totalPackedItems Total number of packed items.
     */
    public record PackingResult(List<PackedBox> packedBoxes
            , List<String> unpackedSkus
            , int totalPackedItems) {
    }
}
