import { ChangeDetectionStrategy, Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule, CurrencyPipe } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatChipsModule } from '@angular/material/chips';
import { MatTooltipModule } from '@angular/material/tooltip';
import { ProduttoreProductSummaryDTO, StatoVerifica } from '../../../../../core/models/produttore.models';

@Component({
  selector: 'app-produttore-product-card',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatIconModule,
    MatButtonModule,
    MatChipsModule,
    MatTooltipModule,
    CurrencyPipe
  ],
  templateUrl: './produttore-product-card.component.html',
  styleUrl: './produttore-product-card.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProduttoreProductCardComponent {
  @Input() prodotto!: ProduttoreProductSummaryDTO;
  @Output() viewDetails = new EventEmitter<ProduttoreProductSummaryDTO>();
  @Output() editProduct = new EventEmitter<ProduttoreProductSummaryDTO>();
  @Output() deleteProduct = new EventEmitter<ProduttoreProductSummaryDTO>();

  onViewDetails(): void {
    this.viewDetails.emit(this.prodotto);
  }

  onEditProduct(): void {
    this.editProduct.emit(this.prodotto);
  }

  onDeleteProduct(): void {
    this.deleteProduct.emit(this.prodotto);
  }

  getStatoClass(stato: string): 'primary' | 'accent' | 'warn' | 'basic' {
    switch (stato) {
      case StatoVerifica.APPROVATO:
        return 'primary';
      case StatoVerifica.IN_ATTESA:
        return 'accent';
      case StatoVerifica.RESPINTO:
        return 'warn';
      default:
        return 'basic';
    }
  }
}