import { Component, Input, Output, EventEmitter, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatChipsModule } from '@angular/material/chips';
import { MatTooltipModule } from '@angular/material/tooltip';
import { PublicEventoSummaryDTO } from '../../../../../core/models/public.models';

@Component({
    selector: 'app-event-card',
    standalone: true,
    imports: [
        CommonModule,
        MatCardModule,
        MatButtonModule,
        MatIconModule,
        MatChipsModule,
        MatTooltipModule
    ],
    templateUrl: './event-card.component.html',
    styleUrls: ['./event-card.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class EventCardComponent {
    @Input() evento!: PublicEventoSummaryDTO;
    @Input() isAuthenticated = false;
    @Input() isRegistered = false;
    @Input() showRegistrationStatus = true;

    @Output() registerClick = new EventEmitter<number>();
    @Output() cancelRegistrationClick = new EventEmitter<number>();
    @Output() viewDetails = new EventEmitter<number>();

    // Metodi helper per calcolare stato evento
    isEventoInCorso(): boolean {
        if (!this.evento) return false;
        const now = new Date();
        const inizio = new Date(this.evento.dataOraInizio);
        const fine = new Date(this.evento.dataOraFine);
        return now >= inizio && now <= fine;
    }

    isEventoFuturo(): boolean {
        if (!this.evento) return false;
        const now = new Date();
        const inizio = new Date(this.evento.dataOraInizio);
        return now < inizio;
    }

    isEventoPassato(): boolean {
        if (!this.evento) return false;
        const now = new Date();
        const fine = new Date(this.evento.dataOraFine);
        return now > fine;
    }

    isEventoPieno(): boolean {
        if (!this.evento) return false;
        const numeroMassimo = this.evento.numeroMassimoPartecipanti || this.evento.capienzaMassima;
        const numeroAttuale = this.evento.numeroPartecipanti;
        const postiDisp = this.evento.postiDisponibili;

        // Se c'è postiDisponibili, usa quello
        if (postiDisp !== null && postiDisp !== undefined) {
            return postiDisp === 0;
        }

        // Altrimenti usa il calcolo classico
        return numeroMassimo !== null && numeroMassimo !== undefined &&
            numeroAttuale !== null && numeroAttuale !== undefined &&
            numeroAttuale >= numeroMassimo;
    }

    canRegister(): boolean {
        // Non permettere iscrizione a eventi annullati o conclusi
        const statoEvento = this.evento?.statoEvento || this.evento?.stato;
        if (statoEvento === 'ANNULLATO' || statoEvento === 'CONCLUSO') {
            return false;
        }

        return this.isAuthenticated &&
            !this.isRegistered &&
            this.isEventoFuturo() &&
            !this.isEventoPieno();
    }

    // Metodi helper per formattazione date
    formatDate(dataString: string): string {
        return new Date(dataString).toLocaleDateString('it-IT', {
            day: '2-digit',
            month: '2-digit',
            year: 'numeric'
        });
    }

    formatTime(dataString: string): string {
        return new Date(dataString).toLocaleTimeString('it-IT', {
            hour: '2-digit',
            minute: '2-digit'
        });
    }

    getStatoEvento(stato: string): string {
        switch (stato) {
            case 'PROGRAMMATO': return 'Programmato';
            case 'IN_CORSO': return 'In corso';
            case 'COMPLETATO': return 'Completato';
            case 'ANNULLATO': return 'Annullato';
            default: return stato;
        }
    }

    getStatoColor(stato: string): string {
        switch (stato) {
            case 'PROGRAMMATO': return 'primary';
            case 'IN_CORSO': return 'accent';
            case 'COMPLETATO': return 'primary';
            case 'ANNULLATO': return 'warn';
            default: return 'primary';
        }
    }

    getStatoBadgeClass(stato: string): string {
        switch (stato) {
            case 'PROGRAMMATO': return 'status-programmato';
            case 'IN_CORSO': return 'status-in-corso';
            case 'COMPLETATO': return 'status-completato';
            case 'ANNULLATO': return 'status-annullato';
            default: return 'status-default';
        }
    }

    getPostiDisponibili(): number {
        if (!this.evento) return 0;

        // Priorità a postiDisponibili se presente
        if (this.evento.postiDisponibili !== null && this.evento.postiDisponibili !== undefined) {
            return this.evento.postiDisponibili;
        }

        // Altrimenti calcola da capienzaMassima o numeroMassimoPartecipanti
        const numeroMassimo = this.evento.numeroMassimoPartecipanti || this.evento.capienzaMassima;
        const numeroAttuale = this.evento.numeroPartecipanti;
        if (numeroMassimo === null || numeroMassimo === undefined ||
            numeroAttuale === null || numeroAttuale === undefined) {
            return 0;
        }
        return numeroMassimo - numeroAttuale;
    }

    getPostiDisponibiliText(): string {
        const postiDisponibili = this.getPostiDisponibili();
        const numeroMassimo = this.evento?.numeroMassimoPartecipanti || this.evento?.capienzaMassima;
        if (numeroMassimo === null || numeroMassimo === undefined) {
            return 'Posti illimitati';
        }
        return `${postiDisponibili} posti disponibili`;
    }

    onCardClick(): void {
        this.viewDetails.emit(this.evento.idEvento || this.evento.id!);
    }

    onRegisterClick(event: Event): void {
        event.stopPropagation();
        this.registerClick.emit(this.evento.idEvento || this.evento.id!);
    }

    onCancelRegistrationClick(event: Event): void {
        event.stopPropagation();
        this.cancelRegistrationClick.emit(this.evento.idEvento || this.evento.id!);
    }

    onViewDetailsClick(event: Event): void {
        event.stopPropagation();
        this.viewDetails.emit(this.evento.idEvento || this.evento.id!);
    }

    getImageUrl(): string {
        return this.evento.immagineUrl || '/assets/images/placeholder-event.jpg';
    }
}
