import { ChangeDetectionStrategy, Component, Input, Output, EventEmitter } from '@angular/core';
import { ApprovazionePendingDTO } from '../../../../../core/models/curatore.models';

@Component({
  selector: 'app-approvazione-card',
  templateUrl: './approvazione-card.component.html',
  styleUrls: ['./approvazione-card.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ApprovazioneCardComponent {
  @Input() approvazione: ApprovazionePendingDTO | null = null;
  @Output() approve = new EventEmitter<number>();
  @Output() reject = new EventEmitter<{ id: number, motivo: string }>();
  @Output() viewDetails = new EventEmitter<number>();

  // Stato per il dialogo di rifiuto
  showRejectDialog = false;
  rejectReason = '';

  onApprove(): void {
    if (this.approvazione) {
      this.approve.emit(this.approvazione.elementoId);
    }
  }

  onReject(): void {
    this.showRejectDialog = true;
  }

  confirmReject(): void {
    if (this.approvazione && this.rejectReason.trim()) {
      this.reject.emit({ 
        id: this.approvazione.elementoId, 
        motivo: this.rejectReason.trim() 
      });
      this.showRejectDialog = false;
      this.rejectReason = '';
    }
  }

  cancelReject(): void {
    this.showRejectDialog = false;
    this.rejectReason = '';
  }

  onViewDetails(): void {
    if (this.approvazione) {
      this.viewDetails.emit(this.approvazione.elementoId);
    }
  }

  formatDate(date: string): string {
    return new Date(date).toLocaleDateString('it-IT', {
      year: 'numeric',
      month: 'short',
      day: 'numeric'
    });
  }

  formatDateTime(date: string): string {
    return new Date(date).toLocaleString('it-IT', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  }

  getStatoColor(stato: string): string {
    switch (stato) {
      case 'IN_ATTESA':
        return '#f39c12';
      case 'APPROVATO':
        return '#27ae60';
      case 'RIFIUTATO':
        return '#e74c3c';
      default:
        return '#95a5a6';
    }
  }

  getStatoLabel(stato: string): string {
    switch (stato) {
      case 'IN_ATTESA':
        return 'In Attesa';
      case 'APPROVATO':
        return 'Approvato';
      case 'RIFIUTATO':
        return 'Rifiutato';
      default:
        return stato;
    }
  }

  getTipoLabel(tipo: string): string {
    switch (tipo) {
      case 'PRODOTTO':
        return 'Prodotto';
      case 'AZIENDA':
        return 'Azienda';
      case 'CONTENUTO':
        return 'Contenuto';
      default:
        return tipo;
    }
  }

  getTipoIcon(tipo: string): string {
    switch (tipo) {
      case 'PRODOTTO':
        return 'inventory_2';
      case 'AZIENDA':
        return 'business';
      case 'CONTENUTO':
        return 'article';
      default:
        return 'category';
    }
  }
}