import {computed, Injectable, signal} from '@angular/core';

export type MeasurementSystem = 'metric' | 'imperial';

@Injectable({providedIn: 'root'})
export class MeasurementService {

    readonly system = signal<MeasurementSystem>('metric');

    readonly isMetric = computed(() => this.system() === 'metric');
    readonly dimUnit = computed(() => this.system() === 'metric' ? 'cm' : 'in');
    readonly weightUnit = computed(() => this.system() === 'metric' ? 'kg' : 'lb');
    readonly volUnit = computed(() => this.system() === 'metric' ? 'cm³' : 'in³');

    toggle(): void {
        this.system.set(this.system() === 'metric' ? 'imperial' : 'metric');
    }

    // Display: metric to chosen system
    dim(valueCm: number): number {
        return this.isMetric() ? valueCm : round(valueCm / 2.54, 2);
    }

    weight(valueKg: number): number {
        return this.isMetric() ? valueKg : round(valueKg * 2.20462, 2);
    }

    vol(valueCm3: number): number {
        return this.isMetric() ? valueCm3 : round(valueCm3 / 16.3871, 2);
    }

    // Input: chosen system to metric (for API)
    toMetricDim(value: number): number {
        return this.isMetric() ? value : round(value * 2.54, 4);
    }

    toMetricWeight(value: number): number {
        return this.isMetric() ? value : round(value / 2.20462, 4);
    }
}

function round(v: number, dp: number): number {
    const f = Math.pow(10, dp);
    return Math.round(v * f) / f;
}