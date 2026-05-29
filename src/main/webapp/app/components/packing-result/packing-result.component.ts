import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {CommonModule} from '@angular/common';
import {BoxConfig, PackedBox, PackingResponse} from '../../models/packing.model';
import {MeasurementService} from '../../services/measurement.service';

@Component({
    selector: 'app-packing-result',
    standalone: true,
    imports: [CommonModule],
    templateUrl: './packing-result.component.html',
    styleUrl: './packing-result.component.css'
})
export class PackingResultComponent implements OnInit, OnDestroy {
    @Input() result!: PackingResponse;
    @Input() boxConfig!: BoxConfig;

    visibleBoxes: PackedBox[] = [];
    private revealInterval: ReturnType<typeof setInterval> | null = null;

    constructor(public ms: MeasurementService) {
    }

    get overallUtilization(): number {
        if (!this.result.packedBoxes.length) return 0;
        const avg = this.result.packedBoxes.reduce((s, b) => s + b.utilizationPercent, 0)
            / this.result.packedBoxes.length;
        return Math.round(avg);
    }

    ngOnInit(): void {
        this.animateBoxReveal();
    }

    animateBoxReveal(): void {
        this.visibleBoxes = [];
        let idx = 0;
        this.revealInterval = setInterval(() => {
            if (idx < this.result.packedBoxes.length) {
                this.visibleBoxes = [...this.visibleBoxes, this.result.packedBoxes[idx++]];
            } else {
                if (this.revealInterval) clearInterval(this.revealInterval);
            }
        }, 300);
    }

    utilizationColor(pct: number): string {
        if (pct >= 75) return 'high';
        if (pct >= 40) return 'mid';
        return 'low';
    }

    ngOnDestroy(): void {
        if (this.revealInterval) clearInterval(this.revealInterval);
    }
}