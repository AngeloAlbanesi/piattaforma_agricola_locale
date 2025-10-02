import { ChangeDetectionStrategy, Component, Input, OnChanges, SimpleChanges } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressBarModule } from '@angular/material/progress-bar';

@Component({
    selector: 'app-produttore-stats-overview',
    templateUrl: './produttore-stats-overview.component.html',
    styleUrls: ['./produttore-stats-overview.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: true,
    imports: [CommonModule, MatCardModule, MatIconModule, MatProgressBarModule]
})
export class ProduttoreStatsOverviewComponent implements OnChanges {
    @Input() totalProducts: number = 0;
    @Input() approvedProducts: number = 0;
    @Input() pendingProducts: number = 0;
    @Input() rejectedProducts: number = 0;
    @Input() totalOrders: number = 0;
    @Input() completedOrders: number = 0;
    @Input() totalRevenue: number = 0;
    @Input() productsSold: number = 0;
    @Input() totalViews: number = 0;
    @Input() totalCertifications: number = 0;
    @Input() approvalRate: number = 0;
    @Input() completionRate: number = 0;

    // Formatted values for display
    formattedTotalRevenue: string = '€0';
    formattedTotalProducts: string = '0';
    formattedTotalOrders: string = '0';
    formattedProductsSold: string = '0';
    formattedTotalViews: string = '0';

    ngOnChanges(changes: SimpleChanges): void {
        this.updateFormattedValues();
    }

    private updateFormattedValues(): void {
        this.formattedTotalRevenue = this.formatCurrency(this.totalRevenue);
        this.formattedTotalProducts = this.formatNumber(this.totalProducts);
        this.formattedTotalOrders = this.formatNumber(this.totalOrders);
        this.formattedProductsSold = this.formatNumber(this.productsSold);
        this.formattedTotalViews = this.formatNumber(this.totalViews);
    }

    private formatCurrency(value: number): string {
        return new Intl.NumberFormat('it-IT', {
            style: 'currency',
            currency: 'EUR',
            minimumFractionDigits: 0,
            maximumFractionDigits: 2
        }).format(value);
    }

    private formatNumber(value: number): string {
        if (value >= 1000000) {
            return (value / 1000000).toFixed(1) + 'M';
        } else if (value >= 1000) {
            return (value / 1000).toFixed(0) + 'K';
        }
        return value.toString();
    }

    // Getters per template
    get hasProducts(): boolean {
        return this.totalProducts > 0;
    }

    get hasOrders(): boolean {
        return this.totalOrders > 0;
    }

    get hasRevenue(): boolean {
        return this.totalRevenue > 0;
    }

    get hasViews(): boolean {
        return this.totalViews > 0;
    }

    get hasCertifications(): boolean {
        return this.totalCertifications > 0;
    }

    get approvalProgress(): number {
        return this.totalProducts > 0 ? (this.approvedProducts / this.totalProducts) * 100 : 0;
    }

    get completionProgress(): number {
        return this.totalOrders > 0 ? (this.completedOrders / this.totalOrders) * 100 : 0;
    }

    get productStatusDistribution(): Array<{ label: string; value: number; color: string }> {
        return [
            { label: 'Approvati', value: this.approvedProducts, color: '#27ae60' },
            { label: 'In Attesa', value: this.pendingProducts, color: '#f39c12' },
            { label: 'Respinti', value: this.rejectedProducts, color: '#e74c3c' }
        ];
    }
}