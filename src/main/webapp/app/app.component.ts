import {Component} from '@angular/core';
import {CommonModule} from '@angular/common';
import {BinConfigComponent} from './components/bin-config/bin-config.component';
import {SkuListComponent} from './components/sku-list/sku-list.component';
import {PackingResultComponent} from './components/packing-result/packing-result.component';
import {PackingService} from './services/packing.service';
import {MeasurementService} from './services/measurement.service';
import {BoxConfig, PackingResponse, SkuItem} from './models/packing.model';

@Component({
    selector: 'app-root',
    standalone: true,
    imports: [CommonModule, BinConfigComponent, SkuListComponent, PackingResultComponent],
    templateUrl: './app.component.html',
    styleUrl: './app.component.css'
})
export class AppComponent {
    boxConfig: BoxConfig | null = null;
    skuItems: SkuItem[] = [];
    packingResult: PackingResponse | null = null;
    isLoading = false;
    error: string | null = null;
    activeStep = 1;

    constructor(
        private packingService: PackingService,
        public measurement: MeasurementService
    ) {
    }

    onBoxConfigured(config: BoxConfig): void {
        this.boxConfig = config;
        this.activeStep = 2;
        this.packingResult = null;
    }

    onSkusUpdated(items: SkuItem[]): void {
        this.skuItems = items;
    }

    onOptimize(): void {
        if (!this.boxConfig || this.skuItems.length === 0) return;
        this.isLoading = true;
        this.error = null;
        this.packingResult = null;

        this.packingService.pack({box: this.boxConfig, items: this.skuItems}).subscribe({
            next: (result) => {
                this.packingResult = result;
                this.isLoading = false;
                this.activeStep = 3;
            },
            error: (err) => {
                this.error = err.status === 0
                    ? 'Cannot connect to server. Make sure the Spring Boot backend is running on port 8080.'
                    : `Server error: ${err.error?.message || err.statusText}`;
                this.isLoading = false;
            }
        });
    }

    canOptimize(): boolean {
        return !!this.boxConfig && this.skuItems.length > 0 && !this.isLoading;
    }

    reset(): void {
        this.boxConfig = null;
        this.skuItems = [];
        this.packingResult = null;
        this.error = null;
        this.activeStep = 1;
    }
}