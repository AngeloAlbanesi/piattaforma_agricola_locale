import { ChangeDetectionStrategy, Component, Input, OnChanges, SimpleChanges } from '@angular/core';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'app-stats-overview',
  templateUrl: './stats-overview.component.html',
  styleUrls: ['./stats-overview.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  standalone: true,
  imports: [MatCardModule, MatIconModule]
})
export class StatsOverviewComponent implements OnChanges {
  @Input() totalOrders: number = 0;
  @Input() totalSpent: number = 0;
  @Input() productsPurchased: number = 0;
  @Input() eventsAttended: number = 0;

  // Formatted values for display
  formattedTotalSpent: string = '€0';
  formattedTotalOrders: string = '0';
  formattedProductsPurchased: string = '0';
  formattedEventsAttended: string = '0';

  ngOnChanges(changes: SimpleChanges): void {
    this.updateFormattedValues();
  }

  private updateFormattedValues(): void {
    this.formattedTotalSpent = this.formatCurrency(this.totalSpent);
    this.formattedTotalOrders = this.formatNumber(this.totalOrders);
    this.formattedProductsPurchased = this.formatNumber(this.productsPurchased);
    this.formattedEventsAttended = this.formatNumber(this.eventsAttended);
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

  get hasOrders(): boolean {
    return this.totalOrders > 0;
  }

  get hasSpent(): boolean {
    return this.totalSpent > 0;
  }

  get hasProducts(): boolean {
    return this.productsPurchased > 0;
  }

  get hasEvents(): boolean {
    return this.eventsAttended > 0;
  }
}