import { ChangeDetectionStrategy, Component, Input, OnChanges, SimpleChanges } from '@angular/core';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { CommonModule } from '@angular/common';

@Component({
    selector: 'app-curatore-stats-overview',
    templateUrl: './curatore-stats-overview.component.html',
    styleUrls: ['./curatore-stats-overview.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: true,
    imports: [CommonModule, MatCardModule, MatIconModule, MatProgressBarModule]
})
export class CuratoreStatsOverviewComponent implements OnChanges {
    @Input() productsToApprove: number = 0;
    @Input() companiesToApprove: number = 0;
    @Input() contentToModerate: number = 0;
    @Input() productsApproved: number = 0;
    @Input() productsRejected: number = 0;
    @Input() companiesApproved: number = 0;
    @Input() companiesRejected: number = 0;
    @Input() contentModerated: number = 0;
    @Input() approvalTrend: number = 0;

    // Statistiche calcolate
    totalPending: number = 0;
    totalApproved: number = 0;
    totalRejected: number = 0;
    approvalRate: number = 0;

    ngOnChanges(changes: SimpleChanges): void {
        this.calculateDerivedStats();
    }

    private calculateDerivedStats(): void {
        // Calcola totali
        this.totalPending = this.productsToApprove + this.companiesToApprove + this.contentToModerate;
        this.totalApproved = this.productsApproved + this.companiesApproved + this.contentModerated;
        this.totalRejected = this.productsRejected + this.companiesRejected;

        // Calcola tasso di approvazione
        const totalProcessed = this.totalApproved + this.totalRejected;
        if (totalProcessed > 0) {
            this.approvalRate = (this.totalApproved / totalProcessed) * 100;
        }
    }

    formatCurrency(value: number): string {
        return new Intl.NumberFormat('it-IT', {
            style: 'currency',
            currency: 'EUR'
        }).format(value);
    }

    formatPercentage(value: number): string {
        return `${value.toFixed(1)}%`;
    }

    getProgressColor(value: number): string {
        if (value >= 80) return '#27ae60'; // Verde
        if (value >= 50) return '#f39c12'; // Arancione
        return '#e74c3c'; // Rosso
    }

    getTrendIcon(): string {
        if (this.approvalTrend > 0) return 'trending_up';
        if (this.approvalTrend < 0) return 'trending_down';
        return 'trending_flat';
    }

    getTrendColor(): string {
        if (this.approvalTrend > 0) return '#27ae60'; // Verde
        if (this.approvalTrend < 0) return '#e74c3c'; // Rosso
        return '#95a5a6'; // Grigio
    }
}