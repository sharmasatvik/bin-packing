export interface BoxConfig {
    length: number;
    width: number;
    height: number;
    maxWeight: number;
}

export interface SkuItem {
    id: string;
    sku: string;
    length: number;
    width: number;
    height: number;
    weight: number;
}

export interface PackingRequest {
    box: BoxConfig;
    items: SkuItem[];
}

export interface PackedBox {
    boxNumber: number;
    items: PackedItem[];
    totalWeight: number;
    utilizationPercent: number;
    remainingVolume: number;
}

export interface PackedItem {
    sku: string;
    length: number;
    width: number;
    height: number;
    weight: number;
    volume: number;
}

export interface PackingResponse {
    totalBoxes: number;
    packedBoxes: PackedBox[];
    unpackedItems: string[];
    totalItems: number;
}
