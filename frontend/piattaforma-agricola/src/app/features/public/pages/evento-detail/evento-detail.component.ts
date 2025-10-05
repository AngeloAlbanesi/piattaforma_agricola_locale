import { Component, OnInit, OnDestroy, ChangeDetectorRef, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDividerModule } from '@angular/material/divider';
import { MatChipsModule } from '@angular/material/chips';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';

import { PublicEventiService } from '../../../../core/services/public-eventi.service';
import { PublicEventoDetailDTO } from '../../../../core/models/public.models';
import { SearchBoxComponent } from '../../shared/components/search/search-box/search-box.component';

@Component({
    selector: 'app-evento-detail',
    standalone: true,
    imports: [
        CommonModule,
        RouterModule,
        MatCardModule,
        MatButtonModule,
        MatIconModule,
        MatDividerModule,
        MatChipsModule,
        MatProgressSpinnerModule,
        MatTooltipModule,
        MatSnackBarModule
    ],
    // schemas: [CUSTOM_ELEMENTS_SCHEMA],
    templateUrl: './evento-detail.component.html',
    styleUrls: ['./evento-detail.component.scss']
})
export class EventoDetailComponent implements OnInit, OnDestroy {
    evento: PublicEventoDetailDTO | null = null;
    isLoading = true;
    error: string | null = null;
    eventoId: number | null = null;

    // Stati calcolati
    isOngoing = false;
    isFuture = false;
    isPast = false;
    isFull = false;
    canRegister = false;
    registrationDaysLeft = 0;
    registrationHoursLeft = 0;

    constructor(
        private route: ActivatedRoute,
        private router: Router,
        private eventiService: PublicEventiService,
        private sanitizer: DomSanitizer,
        private cdr: ChangeDetectorRef,
        private snackBar: MatSnackBar
    ) { }

    ngOnInit(): void {
        this.loadEvento();
    }

    ngOnDestroy(): void {
        // Cleanup if needed
    }

    private loadEvento(): void {
        this.isLoading = true;
        this.error = null;

        // Ottieni ID evento dai parametri del route
        this.route.paramMap.subscribe(params => {
            const id = params.get('id');
            if (id) {
                this.eventoId = +id;
                this.loadEventoData();
            } else {
                this.error = 'ID evento non valido';
                this.isLoading = false;
            }
        });
    }

    private loadEventoData(): void {
        if (!this.eventoId) return;

        this.eventiService.getEventoById(this.eventoId).subscribe({
            next: (evento: PublicEventoDetailDTO) => {
                this.evento = evento;
                this.calculateEventStatus();
                this.calculateRegistrationStatus();
                this.isLoading = false;
                this.cdr.detectChanges();
            },
            error: (err: any) => {
                console.error('Errore nel caricamento dell\'evento:', err);
                this.error = 'Impossibile caricare i dettagli dell\'evento. Riprova più tardi.';
                this.isLoading = false;
                this.cdr.detectChanges();
            }
        });
    }

    private calculateEventStatus(): void {
        if (!this.evento) return;

        const now = new Date();
        const dataInizio = new Date(this.evento.dataOraInizio);
        const dataFine = new Date(this.evento.dataOraFine);

        this.isOngoing = now >= dataInizio && now <= dataFine;
        this.isFuture = now < dataInizio;
        this.isPast = now > dataFine;
    }

    private calculateRegistrationStatus(): void {
        if (!this.evento) return;

        // Verifica se l'evento è pieno
        this.isFull = this.evento.numeroMassimoPartecipanti !== null &&
            this.evento.numeroPartecipanti !== null &&
            this.evento.numeroPartecipanti! >= this.evento.numeroMassimoPartecipanti!;

        // Verifica se ci si può ancora registrare
        const now = new Date();
        const dataLimiteRegistrazione = new Date(this.evento.dataOraInizio);

        this.canRegister = !this.isPast &&
            !this.isFull &&
            now < dataLimiteRegistrazione;

        // Calcola tempo rimanente per la registrazione
        if (this.canRegister) {
            const diffMs = dataLimiteRegistrazione.getTime() - now.getTime();
            this.registrationDaysLeft = Math.floor(diffMs / (1000 * 60 * 60 * 24));
            this.registrationHoursLeft = Math.floor((diffMs % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
        }
    }

    getFormattedDate(dateString: string): string {
        const date = new Date(dateString);
        const options: Intl.DateTimeFormatOptions = {
            weekday: 'long',
            year: 'numeric',
            month: 'long',
            day: 'numeric'
        };
        return date.toLocaleDateString('it-IT', options);
    }

    getFormattedTime(dateString: string): string {
        const date = new Date(dateString);
        return date.toLocaleTimeString('it-IT', {
            hour: '2-digit',
            minute: '2-digit'
        });
    }

    getFormattedDuration(): string {
        if (!this.evento) return '';

        const start = new Date(this.evento.dataOraInizio);
        const end = new Date(this.evento.dataOraFine);
        const diffMs = end.getTime() - start.getTime();
        const diffHours = Math.floor(diffMs / (1000 * 60 * 60));
        const diffDays = Math.floor(diffHours / 24);

        if (diffDays > 0) {
            return `${diffDays} giorno${diffDays > 1 ? 'i' : ''}`;
        } else if (diffHours > 0) {
            return `${diffHours} ora${diffHours > 1 ? 'e' : ''}`;
        } else {
            return 'Meno di un\'ora';
        }
    }

    getRegistrationTimeLeft(): string {
        if (!this.canRegister) return '';

        if (this.registrationDaysLeft > 0) {
            return `${this.registrationDaysLeft} giorno${this.registrationDaysLeft > 1 ? 'i' : ''} rimanenti`;
        } else if (this.registrationHoursLeft > 0) {
            return `${this.registrationHoursLeft} ora${this.registrationHoursLeft > 1 ? 'e' : ''} rimanenti`;
        } else {
            return 'Meno di un\'ora rimanente';
        }
    }

    getAvailabilityStatus(): { text: string; color: string } {
        if (!this.evento) return { text: 'N/D', color: 'gray' };

        if (this.evento.numeroMassimoPartecipanti === null) {
            return { text: 'Posti illimitati', color: 'green' };
        }

        if (this.evento.numeroPartecipanti === null) {
            return { text: 'Posti non specificati', color: 'gray' };
        }

        const postiDisponibili = this.evento.numeroMassimoPartecipanti! - this.evento.numeroPartecipanti!;

        if (postiDisponibili <= 0) {
            return { text: 'Esaurito', color: 'red' };
        }

        const percentage = (postiDisponibili / this.evento.numeroMassimoPartecipanti!) * 100;

        if (percentage > 50) {
            return { text: `${postiDisponibili} posti disponibili`, color: 'green' };
        } else if (percentage > 20) {
            return { text: `${postiDisponibili} posti disponibili`, color: 'orange' };
        } else {
            return { text: `Solo ${postiDisponibili} posti disponibili`, color: 'red' };
        }
    }

    getAvailabilityPercentage(): number {
        if (!this.evento || this.evento.numeroMassimoPartecipanti === null || this.evento.numeroPartecipanti === null) {
            return 100;
        }
        const postiDisponibili = this.evento.numeroMassimoPartecipanti! - this.evento.numeroPartecipanti!;
        return (postiDisponibili / this.evento.numeroMassimoPartecipanti!) * 100;
    }

    getAvailabilityColor(): string {
        const percentage = this.getAvailabilityPercentage();

        if (percentage > 50) {
            return '#4caf50'; // verde
        } else if (percentage > 20) {
            return '#ff9800'; // arancione
        } else {
            return '#f44336'; // rosso
        }
    }

    getEventStatus(): { text: string; color: string; icon: string } {
        if (this.isOngoing) {
            return { text: 'In corso', color: '#4caf50', icon: 'play_circle' };
        } else if (this.isFuture) {
            return { text: 'Prossimo', color: '#2196f3', icon: 'schedule' };
        } else {
            return { text: 'Concluso', color: '#9e9e9e', icon: 'check_circle' };
        }
    }

    getEventTypeColor(): string {
        if (!this.evento) return '#9e9e9e';

        switch (this.evento.modalitaIscrizione.toLowerCase()) {
            case 'festa':
            case 'sagra':
                return '#ff5722';
            case 'mercato':
            case 'vendita':
                return '#4caf50';
            case 'degustazione':
            case 'laboratorio':
                return '#ff9800';
            case 'conferenza':
            case 'seminario':
                return '#2196f3';
            case 'visita':
            case 'tour':
                return '#9c27b0';
            default:
                return '#607d8b';
        }
    }

    sanitizeHtml(html: string): SafeHtml {
        return this.sanitizer.bypassSecurityTrustHtml(html);
    }

    hasDescription(): boolean {
        return !!this.evento?.descrizione && this.evento.descrizione.trim().length > 0;
    }

    hasLocation(): boolean {
        return !!this.evento?.luogo && this.evento.luogo.trim().length > 0;
    }

    hasOrganizer(): boolean {
        return !!this.evento?.organizzatore?.nomeAzienda && this.evento.organizzatore.nomeAzienda.trim().length > 0;
    }

    hasContactInfo(): boolean {
        return !!(this.evento?.contatti && this.evento.contatti.length > 0);
    }

    hasRegistrationInfo(): boolean {
        return this.canRegister || this.isFull || this.isPast;
    }

    retry(): void {
        this.loadEvento();
    }

    goBack(): void {
        this.router.navigate(['/eventi']);
    }

    shareEvent(): void {
        if (!this.evento) return;

        if (navigator.share) {
            navigator.share({
                title: this.evento.nome,
                text: this.evento.descrizione?.substring(0, 200) + '...',
                url: window.location.href
            }).catch(err => console.log('Errore nella condivisione:', err));
        } else {
            // Fallback: copia negli appunti
            this.copyToClipboard(window.location.href);
        }
    }

    private copyToClipboard(text: string): void {
        navigator.clipboard.writeText(text).then(() => {
            this.snackBar.open('Link copiato negli appunti!', 'Chiudi', {
                duration: 3000,
                horizontalPosition: 'center',
                verticalPosition: 'bottom'
            });
        }).catch(err => {
            console.error('Errore nella copia negli appunti:', err);
        });
    }

    openGoogleMaps(): void {
        if (!this.evento?.luogo) return;

        const encodedLocation = encodeURIComponent(this.evento.luogo);
        window.open(`https://www.google.com/maps/search/?api=1&query=${encodedLocation}`, '_blank');
    }

    openExternalLink(url: string): void {
        if (!url) return;

        // Assicura che l'URL abbia il protocollo
        const fullUrl = url.startsWith('http') ? url : `https://${url}`;
        window.open(fullUrl, '_blank');
    }

    sendEmail(): void {
        const emailContatto = this.evento?.contatti?.find(c => c.tipo === 'email')?.valore;
        if (!emailContatto) return;

        const subject = encodeURIComponent(`Informazioni sull'evento: ${this.evento!.nome}`);
        const body = encodeURIComponent(`Buongiorno,\n\nVorrei ricevere maggiori informazioni sull'evento "${this.evento!.nome}" che si terrà il ${this.getFormattedDate(this.evento!.dataOraInizio)}.\n\nGrazie.`);

        window.open(`mailto:${emailContatto}?subject=${subject}&body=${body}`);
    }

    callPhone(): void {
        const telefonoContatto = this.evento?.contatti?.find(c => c.tipo === 'telefono')?.valore;
        if (!telefonoContatto) return;
        window.open(`tel:${telefonoContatto}`);
    }

    // Metodo per la registrazione all'evento (da implementare)
    registerToEvent(): void {
        if (!this.evento || !this.canRegister) return;

        // Qui implementeremo la logica di registrazione
        this.snackBar.open('Funzionalità di registrazione in arrivo!', 'Chiudi', {
            duration: 3000,
            horizontalPosition: 'center',
            verticalPosition: 'bottom'
        });
    }
}