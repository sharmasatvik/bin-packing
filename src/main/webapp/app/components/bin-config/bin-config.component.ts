import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {BoxConfig} from '../../models/packing.model';
import {MeasurementService} from '../../services/measurement.service';

@Component({
    selector: 'app-bin-config',
    standalone: true,
    imports: [CommonModule, FormsModule],
    templateUrl: './bin-config.component.html',
    styleUrl: './bin-config.component.css'
})
export class BinConfigComponent implements OnInit {
    @Output() configSaved = new EventEmitter<BoxConfig>();
    @Input() savedConfig: BoxConfig | null = null;

    // Raw input values in the selected display unit
    input = {length: 0, width: 0, height: 0, maxWeight: 0};
    isSaved = false;
    errors: Partial<Record<keyof BoxConfig, string>> = {};

    constructor(public ms: MeasurementService) {
    }

    get displayVolume(): number {
        return this.ms.vol(
            this.ms.toMetricDim(this.input.length || 0) *
            this.ms.toMetricDim(this.input.width || 0) *
            this.ms.toMetricDim(this.input.height || 0)
        );
    }

    ngOnInit(): void {
        if (this.savedConfig) {
            // Convert stored metric values back to display unit
            this.input = {
                length: this.ms.dim(this.savedConfig.length),
                width: this.ms.dim(this.savedConfig.width),
                height: this.ms.dim(this.savedConfig.height),
                maxWeight: this.ms.weight(this.savedConfig.maxWeight)
            };
            this.isSaved = true;
        }
    }

    validate(): boolean {
        this.errors = {};
        if (!this.input.length || this.input.length <= 0) this.errors['length'] = 'Must be > 0';
        if (!this.input.width || this.input.width <= 0) this.errors['width'] = 'Must be > 0';
        if (!this.input.height || this.input.height <= 0) this.errors['height'] = 'Must be > 0';
        if (!this.input.maxWeight || this.input.maxWeight <= 0) this.errors['maxWeight'] = 'Must be > 0';
        return Object.keys(this.errors).length === 0;
    }

    save(): void {
        if (!this.validate()) return;
        this.isSaved = true;
        // Always emit metric values to the API
        this.configSaved.emit({
            length: this.ms.toMetricDim(this.input.length),
            width: this.ms.toMetricDim(this.input.width),
            height: this.ms.toMetricDim(this.input.height),
            maxWeight: this.ms.toMetricWeight(this.input.maxWeight)
        });
    }

    edit(): void {
        this.isSaved = false;
    }
}