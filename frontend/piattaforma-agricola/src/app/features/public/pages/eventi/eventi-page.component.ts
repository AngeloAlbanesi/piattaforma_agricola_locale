import { Component, OnInit, OnDestroy, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { FormBuilder, ReactiveFormsModule, FormGroup } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatSelectModule } from '@angular/material/select';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { MatChipsModule } from '@angular/material/chips';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';

import { PublicEventiService } from '../../../../core/services/public-eventi.service';
import { AuthService } from '../../../../core/services/auth.service';
import { PublicEventoSummaryDTO } from '../../../../core/models/public.models';
import { PublicEventoFilters } from '../../../../core/models/public.models';
import { PaginatedResponse } from '../../../../core/models/common.models';
import { EventoRegistrazioneRequestDTO } from '../../../../core/models/acquirente.models';
import { SearchBoxComponent } from '../../shared/components/search/search-box/search-box.component';
import { EventCardComponent } from '../../../dashboard/acquirente/components/event-card/event-card.component';
import { EventRegistrationDialogComponent, EventRegistrationDialogData } from '../../shared/components/dialogs/event-registration-dialog.component';
import { FiltersPanelComponent } from '../../shared/components/filters/filters-panel/filters-panel.component';

@Component({
    selector: 'app-eventi-page',
    standalone: true,
    imports: [
        CommonModule,
        RouterModule,
        ReactiveFormsModule,
        MatButtonModule,
        MatCardModule,
        MatIconModule,
        MatProgressSpinnerModule,
        MatSnackBarModule,
        MatPaginatorModule,
        MatSelectModule,
        MatSlideToggleModule,
        MatChipsModule,
        MatDatepickerModule,
        MatNativeDateModule,
        MatFormFieldModule,
        MatInputModule,
        MatDialogModule,
        SearchBoxComponent,
        EventCardComponent
    ],
    templateUrl: './eventi-page.component.html',
    styleUrls: ['./eventi-page.component.scss']
})
export class EventiPageComponent implements OnInit, OnDestroy {
    eventi: PublicEventoSummaryDTO[] = [];
    loading = false;
    error: string | null = null;
    totalCount = 0;
    currentPage = 0;
    pageSize = 12;

    // Traccia registrazioni utente
    registeredEvents = new Map<number, boolean>();
    private readonly REGISTERED_EVENTS_KEY = 'registeredEvents';

    // Filtri
    filters: PublicEventoFilters = {};
    showFilters = false;

    // Form per filtri data
    dateRangeForm: FormGroup;

    // Opzioni per i filtri
    categorieOptions: string[] = [];
    statiOptions: string[] = [];
    ordinamentoOptions = [
        { value: 'data_asc', label: 'Data: dal più vicino' },
        { value: 'data_desc', label: 'Data: dal più lontano' },
        { value: 'nome_asc', label: 'Nome A-Z' },
        { value: 'nome_desc', label: 'Nome Z-A' },
        { value: 'luogo', label: 'Luogo' }
    ];

    private subscriptions = new Map<string, any>();

    constructor(
        private eventiService: PublicEventiService,
        private authService: AuthService,
        private router: Router,
        private fb: FormBuilder,
        private snackBar: MatSnackBar,
        private dialog: MatDialog,
        private cdr: ChangeDetectorRef
    ) {
        this.dateRangeForm = fb.group({
            dataInizio: [''],
            dataFine: ['']
        });
    }

    ngOnInit(): void {
        this.loadRegisteredEventsFromStorage();
        this.loadEventi();
        this.loadCategorie();
        this.loadStati();
    }

    ngOnDestroy(): void {
        this.subscriptions.forEach(subscription => {
            if (subscription && subscription.unsubscribe) {
                subscription.unsubscribe();
            }
        });
        this.subscriptions.clear();
    }

    loadEventi(): void {
        this.loading = true;
        this.error = null;
        this.cdr.markForCheck();

        const eventiSub = this.eventiService.getEventi({
            page: this.currentPage,
            size: this.pageSize,
            ...this.filters
        }).subscribe({
            next: (response: PaginatedResponse<PublicEventoSummaryDTO>) => {
                this.eventi = response.content || [];
                this.totalCount = response.totalElements || 0;
                this.loading = false;

                // Forza il rilevamento delle modifiche per aggiornare la vista
                this.cdr.detectChanges();

                // Note: La verifica dello stato di registrazione viene fatta solo quando necessario
                // (es. al click del pulsante o nella pagina di dettaglio) per evitare troppe chiamate API
            },
            error: (error: any) => {
                console.error('Errore nel caricamento eventi:', error);
                this.error = 'Impossibile caricare gli eventi. Riprova più tardi.';
                this.loading = false;
                this.cdr.detectChanges();
                this.snackBar.open(this.error, 'Chiudi', {
                    duration: 5000,
                    panelClass: ['error-snackbar']
                });
            }
        });

        this.subscriptions.set('eventi', eventiSub);
    }

    loadCategorie(): void {
        // Carica categorie statiche o da un endpoint dedicato se disponibile
        this.categorieOptions = [
            'Degustazione',
            'Mercato',
            'Workshop',
            'Visita Guidata',
            'Festa di Campagna',
            'Corso di Cucina',
            'Conferenza',
            'Mostra',
            'Festival',
            'Sagra',
            'Altro'
        ];
    }

    loadStati(): void {
        // Carica stati statici o da un endpoint dedicato se disponibile
        this.statiOptions = [
            'PROGRAMMATO',
            'IN_CORSO',
            'COMPLETATO',
            'ANNULLATO'
        ];
    }

    onPageChange(event: PageEvent): void {
        this.currentPage = event.pageIndex;
        this.pageSize = event.pageSize;
        this.loadEventi();
    }

    onSearch(query: string): void {
        this.filters = { ...this.filters, query: query || undefined };
        this.currentPage = 0;
        this.loadEventi();
    }

    onFiltersChange(newFilters: any): void {
        this.filters = { ...this.filters, ...newFilters };
        this.currentPage = 0;
        this.loadEventi();
    }

    onFiltersReset(): void {
        this.filters = {};
        this.dateRangeForm.reset();
        this.currentPage = 0;
        this.loadEventi();
    }

    toggleFilters(): void {
        this.showFilters = !this.showFilters;
    }

    navigateToEventoDetail(eventoId: number): void {
        this.router.navigate(['/eventi', eventoId]);
    }

    retryLoad(): void {
        this.loadEventi();
    }

    // Metodi helper per il template
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

    formatDateTime(dataString: string): string {
        return new Date(dataString).toLocaleString('it-IT', {
            day: '2-digit',
            month: '2-digit',
            year: 'numeric',
            hour: '2-digit',
            minute: '2-digit'
        });
    }

    isEventoInCorso(dataInizio: string, dataFine: string): boolean {
        const now = new Date();
        const inizio = new Date(dataInizio);
        const fine = new Date(dataFine);
        return now >= inizio && now <= fine;
    }

    isEventoFuturo(dataInizio: string): boolean {
        const now = new Date();
        const inizio = new Date(dataInizio);
        return now < inizio;
    }

    isEventoPassato(dataFine: string): boolean {
        const now = new Date();
        const fine = new Date(dataFine);
        return now > fine;
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

    // Metodi per i filtri specifici degli eventi
    onCategoriaChange(categoria: string): void {
        // this.filters = { ...this.filters, categoria: categoria || undefined };
        this.currentPage = 0;
        this.loadEventi();
    }

    onStatoChange(stato: string): void {
        // this.filters = { ...this.filters, stato: stato || undefined };
        this.currentPage = 0;
        this.loadEventi();
    }

    onOrdinamentoChange(ordinamento: string): void {
        this.filters = { ...this.filters, sortBy: ordinamento as any || undefined };
        this.currentPage = 0;
        this.loadEventi();
    }

    onLuogoChange(luogo: string): void {
        this.filters = { ...this.filters, luogo: luogo || undefined };
        this.currentPage = 0;
        this.loadEventi();
    }

    onDataRangeChange(): void {
        const { dataInizio, dataFine } = this.dateRangeForm.value;
        this.filters = {
            ...this.filters,
            dataInizio: dataInizio ? this.formatDateForAPI(dataInizio) : undefined,
            dataFine: dataFine ? this.formatDateForAPI(dataFine) : undefined
        };
        this.currentPage = 0;
        this.loadEventi();
    }

    onGratuitoChange(gratuito: boolean): void {
        this.filters = { ...this.filters, gratuito: gratuito || undefined };
        this.currentPage = 0;
        this.loadEventi();
    }

    onDisponibilitaChange(disponibilita: boolean): void {
        this.filters = { ...this.filters, disponibilita: disponibilita || undefined };
        this.currentPage = 0;
        this.loadEventi();
    }

    // Metodo per convertire le date per l'API
    formatDateForAPI(date: Date): string {
        return date.toISOString().split('T')[0]; // Formatta come YYYY-MM-DD
    }

    // Metodo per ottenere il numero di filtri attivi
    getActiveFiltersCount(): number {
        let count = 0;
        if (this.filters.query) count++;
        // if (this.filters.categoria) count++;
        // if (this.filters.stato) count++;
        if (this.filters.sortBy) count++;
        if (this.filters.luogo) count++;
        if (this.filters.dataInizio) count++;
        if (this.filters.dataFine) count++;
        if (this.filters.gratuito !== undefined) count++;
        if (this.filters.disponibilita !== undefined) count++;
        return count;
    }

    // Metodo per resettare tutti i filtri
    resetAllFilters(): void {
        this.filters = {};
        this.dateRangeForm.reset();
        this.currentPage = 0;
        this.loadEventi();
    }

    // === UI helper: chips filtri attivi ===
    getActiveFilterChips(): Array<{ key: keyof PublicEventoFilters | 'query'; label: string; value: string }> {
        const chips: Array<{ key: keyof PublicEventoFilters | 'query'; label: string; value: string }> = [];
        if (this.filters.query) chips.push({ key: 'query', label: 'Ricerca', value: this.filters.query });
        if (this.filters.luogo) chips.push({ key: 'luogo', label: 'Luogo', value: this.filters.luogo });
        if (this.filters.dataInizio) chips.push({ key: 'dataInizio', label: 'Dal', value: this.filters.dataInizio });
        if (this.filters.dataFine) chips.push({ key: 'dataFine', label: 'Al', value: this.filters.dataFine });
        if (this.filters.gratuito !== undefined) chips.push({ key: 'gratuito', label: 'Gratuito', value: this.filters.gratuito ? 'Sì' : 'No' });
        if (this.filters.disponibilita !== undefined) chips.push({ key: 'disponibilita', label: 'Disponibilità', value: this.filters.disponibilita ? 'Disponibili' : 'Tutti' });
        if (this.filters.sortBy) chips.push({ key: 'sortBy', label: 'Ordina', value: this.getSortLabel(this.filters.sortBy) });
        return chips;
    }

    clearFilter(key: keyof PublicEventoFilters | 'query'): void {
        const newFilters = { ...this.filters } as any;
        delete newFilters[key as string];
        // page reset
        this.filters = newFilters;
        this.currentPage = 0;
        this.loadEventi();
    }

    private getSortLabel(value: string): string {
        const found = this.ordinamentoOptions.find(o => o.value === value);
        return found?.label || 'Rilevanza';
    }

    // Metodo per convertire le categorie in FilterOption
    getCategorieOptions(): any[] {
        return this.categorieOptions.map(cat => ({ value: cat, label: cat }));
    }

    // Metodo per convertire gli stati in FilterOption
    getStatiOptions(): any[] {
        return this.statiOptions.map(stato => ({ value: stato, label: this.getStatoEvento(stato) }));
    }

    // Metodo per scrollare in cima alla pagina
    scrollToTop(): void {
        window.scrollTo({ top: 0, behavior: 'smooth' });
    }

    // === METODI PER AUTENTICAZIONE E REGISTRAZIONE ===

    /**
     * Verifica se l'utente è autenticato
     */
    isAuthenticated(): boolean {
        return this.authService.isAuthenticated();
    }

    /**
     * Verifica se l'utente è registrato a un evento
     */
    isRegisteredToEvent(eventoId: number): boolean {
        return this.registeredEvents.get(eventoId) || false;
    }

    /**
     * Carica le registrazioni salvate da localStorage
     */
    private loadRegisteredEventsFromStorage(): void {
        try {
            const stored = localStorage.getItem(this.REGISTERED_EVENTS_KEY);
            if (stored) {
                const eventIds: number[] = JSON.parse(stored);
                eventIds.forEach(id => this.registeredEvents.set(id, true));
            }
        } catch (error) {
            console.error('Error loading registered events from storage:', error);
        }
    }

    /**
     * Salva le registrazioni in localStorage
     */
    private saveRegisteredEventsToStorage(): void {
        try {
            const eventIds = Array.from(this.registeredEvents.keys());
            localStorage.setItem(this.REGISTERED_EVENTS_KEY, JSON.stringify(eventIds));
        } catch (error) {
            console.error('Error saving registered events to storage:', error);
        }
    }

    /**
     * Gestisce il click sul pulsante "Iscriviti"
     */
    onRegisterClick(evento: PublicEventoSummaryDTO): void {
        if (!this.isAuthenticated()) {
            this.snackBar.open('Devi effettuare il login per iscriverti', 'Accedi', {
                duration: 5000
            }).onAction().subscribe(() => {
                this.router.navigate(['/auth/login']);
            });
            return;
        }

        const postiDisponibili = this.eventiService.getPostiRimanenti(evento);

        if (postiDisponibili <= 0) {
            this.snackBar.open('Evento completo, nessun posto disponibile', 'Chiudi', {
                duration: 3000,
                panelClass: ['error-snackbar']
            });
            return;
        }

        const dialogData: EventRegistrationDialogData = {
            evento,
            postiDisponibili
        };

        const dialogRef = this.dialog.open(EventRegistrationDialogComponent, {
            width: '500px',
            maxWidth: '90vw',
            data: dialogData,
            disableClose: false
        });

        dialogRef.afterClosed().subscribe((result: EventoRegistrazioneRequestDTO | null) => {
            if (result) {
                this.registerToEvent(evento.idEvento || evento.id!, result);
            }
        });
    }

    /**
     * Registra l'utente all'evento
     */
    private registerToEvent(eventoId: number, request: EventoRegistrazioneRequestDTO): void {
        this.eventiService.registerForEvent(eventoId, request).subscribe({
            next: () => {
                this.registeredEvents.set(eventoId, true);
                this.saveRegisteredEventsToStorage();
                this.snackBar.open('Iscrizione completata con successo!', 'Chiudi', {
                    duration: 3000,
                    panelClass: ['success-snackbar']
                });
                // Ricarica gli eventi per aggiornare il conteggio partecipanti
                this.loadEventi();
                // Forza il change detection dopo il reload
                this.cdr.detectChanges();
            },
            error: (error: any) => {
                console.error('Errore durante la registrazione:', error);
                const message = error.error?.message || 'Impossibile completare l\'iscrizione. Riprova più tardi.';
                this.snackBar.open(message, 'Chiudi', {
                    duration: 5000,
                    panelClass: ['error-snackbar']
                });
            }
        });
    }

    /**
     * Gestisce l'annullamento della registrazione
     */
    onCancelRegistration(eventoId: number): void {
        const confirmSnackBar = this.snackBar.open(
            'Sei sicuro di voler annullare l\'iscrizione?',
            'Conferma',
            {
                duration: 5000,
                panelClass: ['warning-snackbar']
            }
        );

        confirmSnackBar.onAction().subscribe(() => {
            this.eventiService.cancelEventRegistration(eventoId).subscribe({
                next: () => {
                    this.registeredEvents.delete(eventoId);
                    this.saveRegisteredEventsToStorage();
                    this.snackBar.open('Iscrizione annullata con successo', 'Chiudi', {
                        duration: 3000,
                        panelClass: ['success-snackbar']
                    });
                    // Ricarica gli eventi per aggiornare il conteggio partecipanti
                    this.loadEventi();
                    this.cdr.detectChanges();
                },
                error: (error: any) => {
                    console.error('Errore durante l\'annullamento:', error);
                    const message = error.error?.message || 'Impossibile annullare l\'iscrizione. Riprova più tardi.';
                    this.snackBar.open(message, 'Chiudi', {
                        duration: 5000,
                        panelClass: ['error-snackbar']
                    });
                }
            });
        });
    }

    /**
     * Naviga ai dettagli dell'evento
     */
    onViewEventDetails(eventoId: number): void {
        this.navigateToEventoDetail(eventoId);
    }
}