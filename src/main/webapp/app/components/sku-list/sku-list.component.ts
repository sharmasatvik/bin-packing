import {Component, EventEmitter, Output} from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {SkuItem} from '../../models/packing.model';
import {MeasurementService} from '../../services/measurement.service';

@Component({
    selector: 'app-sku-list',
    standalone: true,
    imports: [CommonModule, FormsModule],
    templateUrl: './sku-list.component.html',
    styleUrl: './sku-list.component.css'
})
export class SkuListComponent {
    @Output() itemsChanged = new EventEmitter<SkuItem[]>();

    items: SkuItem[] = [];
    showForm = false;
    editingId: string | null = null;

    // Input values in the currently selected display unit
    newItem: Partial<SkuItem> = this.emptyItem();
    errors: Partial<Record<keyof SkuItem, string>> = {};

    constructor(public ms: MeasurementService) {
    }

    /** Volume in the current display unit */
    get displayVolume(): number {
        const {length = 0, width = 0, height = 0} = this.newItem;
        const volCm3 = this.ms.toMetricDim(length || 0)
            * this.ms.toMetricDim(width || 0)
            * this.ms.toMetricDim(height || 0);
        return this.ms.vol(volCm3);
    }

    emptyItem(): Partial<SkuItem> {
        return {sku: '', length: undefined, width: undefined, height: undefined, weight: undefined};
    }

    /** Display volume for a saved item */
    itemDisplayVolume(item: SkuItem): number {
        return this.ms.vol(item.length * item.width * item.height);
    }

    /** Display a saved item's dim (stored as metric) */
    itemDim(v: number): number {
        return this.ms.dim(v);
    }

    itemWeight(v: number): number {
        return this.ms.weight(v);
    }

    openForm(): void {
        this.newItem = this.emptyItem();
        this.errors = {};
        this.editingId = null;
        this.showForm = true;
    }

    editItem(item: SkuItem): void {
        // Convert stored metric → display unit for editing
        this.newItem = {
            sku: item.sku,
            length: this.ms.dim(item.length),
            width: this.ms.dim(item.width),
            height: this.ms.dim(item.height),
            weight: this.ms.weight(item.weight)
        };
        this.editingId = item.id;
        this.errors = {};
        this.showForm = true;
    }

    cancelForm(): void {
        this.showForm = false;
        this.editingId = null;
        this.errors = {};
    }

    validate(): boolean {
        this.errors = {};
        if (!this.newItem.sku?.trim()) this.errors['sku'] = 'Required';
        if (!this.newItem.length || this.newItem.length <= 0) this.errors['length'] = 'Must be > 0';
        if (!this.newItem.width || this.newItem.width <= 0) this.errors['width'] = 'Must be > 0';
        if (!this.newItem.height || this.newItem.height <= 0) this.errors['height'] = 'Must be > 0';
        if (!this.newItem.weight || this.newItem.weight <= 0) this.errors['weight'] = 'Must be > 0';
        if (!this.errors['sku']) {
            const dupe = this.items.find(i => i.sku === this.newItem.sku?.trim() && i.id !== this.editingId);
            if (dupe) this.errors['sku'] = 'SKU already exists';
        }
        return Object.keys(this.errors).length === 0;
    }

    saveItem(): void {
        if (!this.validate()) return;
        // Always store in metric internally
        const metricItem = {
            sku: this.newItem.sku!.trim().toUpperCase(),
            length: this.ms.toMetricDim(this.newItem.length!),
            width: this.ms.toMetricDim(this.newItem.width!),
            height: this.ms.toMetricDim(this.newItem.height!),
            weight: this.ms.toMetricWeight(this.newItem.weight!)
        };
        if (this.editingId) {
            this.items = this.items.map(i =>
                i.id === this.editingId ? {...metricItem, id: this.editingId} : i
            );
        } else {
            this.items = [...this.items, {...metricItem, id: crypto.randomUUID()}];
        }
        this.itemsChanged.emit(this.items);
        this.showForm = false;
        this.editingId = null;
    }

    removeItem(id: string): void {
        this.items = this.items.filter(i => i.id !== id);
        this.itemsChanged.emit(this.items);
    }
}