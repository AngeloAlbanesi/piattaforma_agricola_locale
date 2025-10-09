import { ChangeDetectionStrategy, ChangeDetectorRef, Component, OnInit, ViewChild } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatOptionModule } from '@angular/material/core';
import { MatPaginator, MatPaginatorModule } from '@angular/material/paginator';
import { MatSort, MatSortModule } from '@angular/material/sort';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatChipsModule } from '@angular/material/chips';
import { MatMenuModule } from '@angular/material/menu';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatDividerModule } from '@angular/material/divider';
import { EventoDTO, EventoFilters, StatoEvento, PromoteRequestDTO, AziendaPartecipanteDTO } from '../../../../../core/models/animatore.models';
import { PaginatedResponse } from '../../../../../core/models/common.models';
import { AnimatoreService } from '../../../../../core/services/animatore.service';
import { debounceTime, distinctUntilChanged, switchMap } from 'rxjs/operators';
import { Subject } from 'rxjs';
import { Router } from '@angular/router';
import { PromoteEventDialogComponent } from '../event-dialogs/promote-event-dialog.component';
import { ConfirmActionDialogComponent } from '../event-dialogs/confirm-action-dialog.component';
import { ManageAziendeDialogComponent } from '../event-dialogs/manage-aziende-dialog.component';
import { CreateEventDialogComponent } from '../event-dialogs/create-event-dialog.component';
import { EditEventDialogComponent } from '../event-dialogs/edit-event-dialog.component';

@Component({
    selector: 'app-eventi-management',
    standalone: true,
    imports: [
        CommonModule,
        FormsModule,
        ReactiveFormsModule,
        MatCardModule,
        MatIconModule,
        MatButtonModule,
        MatFormFieldModule,
        MatInputModule,
        MatSelectModule,
        MatOptionModule,
        MatPaginatorModule,
        MatSortModule,
        MatTableModule,
        MatSnackBarModule,
        MatProgressSpinnerModule,
        MatChipsModule,
        MatMenuModule,
        MatTooltipModule,
        MatDividerModule,
        DatePipe
    ],
    templateUrl: './eventi-management.component.html',
    styleUrl: './eventi-management.component.scss',
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class EventiManagementComponent implements OnInit {
    displayedColumns: string[] = ['id', 'titolo', 'stato', 'dataInizio', 'partecipanti', 'costo', 'actions'];
    dataSource = new MatTableDataSource<EventoDTO>([]);
    isLoading = true;
    totalElements = 0;
    pageSize = 10;
    currentPage = 0;

    @ViewChild('paginator') paginator!: MatPaginator;
    @ViewChild('sort') sort!: MatSort;

    filters: EventoFilters = {
        pagina: 0,
        elementiPerPagina: 10,
        stato: 'TUTTI'
    };

    statiEvento = [
        { value: 'TUTTI', viewValue: 'Tutti' },
        { value: StatoEvento.IN_PROGRAMMA, viewValue: 'In Programma' },
        { value: StatoEvento.IN_CORSO, viewValue: 'In Corso' },
        { value: StatoEvento.CONCLUSO, viewValue: 'Concluso' },
        { value: StatoEvento.ANNULLATO, viewValue: 'Annullato' }
    ];

    private searchTerms = new Subject<string>();

    constructor(
        private animatoreService: AnimatoreService,
        private dialog: MatDialog,
        private snackBar: MatSnackBar,
        private router: Router,
        private cdr: ChangeDetectorRef
    ) { }

    ngOnInit(): void {
        this.loadEventi();

        this.searchTerms.pipe(
            debounceTime(300),
            distinctUntilChanged(),
            switchMap((term: string) => {
                this.filters.search = term;
                this.filters.pagina = 0;
                return this.animatoreService.getMyEvents(this.filters);
            })
        ).subscribe({
            next: (data: PaginatedResponse<EventoDTO>) => {
                this.dataSource.data = data.content || [];
                this.totalElements = data.totalElements || 0;
                this.isLoading = false;
                this.cdr.markForCheck();
            },
            error: (error: any) => {
                this.snackBar.open('Errore durante il caricamento degli eventi.', 'Chiudi', { duration: 3000 });
                this.isLoading = false;
                this.cdr.markForCheck();
            }
        });
    }

    ngAfterViewInit(): void {
        if (this.paginator) {
            this.dataSource.paginator = this.paginator;
            
            this.paginator.page.subscribe(() => {
                this.filters.pagina = this.paginator.pageIndex;
                this.filters.elementiPerPagina = this.paginator.pageSize;
                this.loadEventi();
            });
        }

        if (this.sort) {
            this.dataSource.sort = this.sort;
            
            this.sort.sortChange.subscribe(() => {
                this.filters.pagina = 0;
                // Implementare logica di ordinamento se l'API lo supporta
                this.loadEventi();
            });
        }
    }

    loadEventi(): void {
        this.isLoading = true;
        this.cdr.markForCheck();

        this.animatoreService.getMyEvents(this.filters).subscribe({
            next: (data: PaginatedResponse<EventoDTO>) => {
                console.log('📊 Eventi caricati:', data);
                this.dataSource.data = data.content || [];
                this.totalElements = data.totalElements || 0;
                this.isLoading = false;
                this.cdr.markForCheck();
            },
            error: (error: any) => {
                console.error('❌ Errore caricamento eventi:', error);
                this.snackBar.open('Errore durante il caricamento degli eventi.', 'Chiudi', { duration: 3000 });
                this.isLoading = false;
                this.dataSource.data = [];
                this.cdr.markForCheck();
            }
        });
    }

    applyFilter(event: Event): void {
        const filterValue = (event.target as HTMLInputElement).value;
        this.searchTerms.next(filterValue.trim().toLowerCase());
    }

    onStatusChange(status: StatoEvento | 'TUTTI'): void {
        this.filters.stato = status;
        this.filters.pagina = 0;
        this.loadEventi();
    }

    viewEventDetails(evento: EventoDTO): void {
        const eventoId = (evento as any).idEvento || evento.id;
        this.router.navigate(['/dashboard/animatore/eventi', eventoId]);
    }

    editEvent(evento: EventoDTO): void {
        const dialogRef = this.dialog.open(EditEventDialogComponent, {
            width: '700px',
            data: { evento }
        });

        dialogRef.afterClosed().subscribe(result => {
            if (result) {
                this.animatoreService.updateEvento(evento.id, result).subscribe({
                    next: () => {
                        this.snackBar.open('Evento aggiornato con successo', 'Chiudi', { duration: 3000 });
                        this.loadEventi();
                    },
                    error: (err) => {
                        this.snackBar.open('Errore durante l\'aggiornamento dell\'evento: ' + (err.error?.message || 'Errore sconosciuto'), 'Chiudi', { duration: 5000 });
                    }
                });
            }
        });
    }

    deleteEvent(evento: EventoDTO): void {
        const dialogRef = this.dialog.open(ConfirmActionDialogComponent, {
            width: '500px',
            data: {
                title: 'Elimina Evento',
                message: 'Sei sicuro di voler eliminare questo evento? Questa azione è irreversibile.',
                evento,
                actionType: 'cancel',
                confirmButtonText: 'Elimina',
                confirmButtonColor: 'warn'
            }
        });

        dialogRef.afterClosed().subscribe(result => {
            if (result) {
                const eventoId = (evento as any).idEvento || evento.id;
                this.animatoreService.deleteEvento(eventoId).subscribe({
                    next: () => {
                        this.snackBar.open('Evento eliminato con successo', 'Chiudi', { duration: 3000 });
                        this.loadEventi();
                    },
                    error: () => {
                        this.snackBar.open('Errore durante l\'eliminazione dell\'evento', 'Chiudi', { duration: 3000 });
                    }
                });
            }
        });
    }

    // === NUOVE FUNZIONALITÀ PER GESTIONE STATO EVENTI ===

    startEvent(evento: EventoDTO): void {
        const dialogRef = this.dialog.open(ConfirmActionDialogComponent, {
            width: '500px',
            data: {
                title: 'Avvia Evento',
                message: 'Stai per avviare questo evento. Lo stato cambierà da "In Programma" a "In Corso".',
                evento,
                actionType: 'start',
                confirmButtonText: 'Avvia Evento',
                confirmButtonColor: 'primary'
            }
        });

        dialogRef.afterClosed().subscribe(result => {
            if (result) {
                const eventoId = (evento as any).idEvento || evento.id;
                this.animatoreService.iniziaEvento(eventoId).subscribe({
                    next: () => {
                        this.snackBar.open('Evento avviato con successo', 'Chiudi', { duration: 3000 });
                        this.loadEventi();
                    },
                    error: (err) => {
                        this.snackBar.open('Errore durante l\'avvio dell\'evento: ' + (err.error?.message || 'Errore sconosciuto'), 'Chiudi', { duration: 5000 });
                    }
                });
            }
        });
    }

    endEvent(evento: EventoDTO): void {
        const dialogRef = this.dialog.open(ConfirmActionDialogComponent, {
            width: '500px',
            data: {
                title: 'Termina Evento',
                message: 'Stai per terminare questo evento. Lo stato cambierà da "In Corso" a "Concluso".',
                evento,
                actionType: 'end',
                confirmButtonText: 'Termina Evento',
                confirmButtonColor: 'primary'
            }
        });

        dialogRef.afterClosed().subscribe(result => {
            if (result) {
                const eventoId = (evento as any).idEvento || evento.id;
                this.animatoreService.terminaEvento(eventoId).subscribe({
                    next: () => {
                        this.snackBar.open('Evento terminato con successo', 'Chiudi', { duration: 3000 });
                        this.loadEventi();
                    },
                    error: (err) => {
                        this.snackBar.open('Errore durante la chiusura dell\'evento: ' + (err.error?.message || 'Errore sconosciuto'), 'Chiudi', { duration: 5000 });
                    }
                });
            }
        });
    }

    cancelEvent(evento: EventoDTO): void {
        const dialogRef = this.dialog.open(ConfirmActionDialogComponent, {
            width: '500px',
            data: {
                title: 'Annulla Evento',
                message: 'Stai per annullare questo evento. Questa azione non può essere annullata.',
                evento,
                actionType: 'cancel',
                confirmButtonText: 'Annulla Evento',
                confirmButtonColor: 'warn'
            }
        });

        dialogRef.afterClosed().subscribe(result => {
            if (result) {
                const eventoId = (evento as any).idEvento || evento.id;
                this.animatoreService.annullaEvento(eventoId).subscribe({
                    next: () => {
                        this.snackBar.open('Evento annullato con successo', 'Chiudi', { duration: 3000 });
                        this.loadEventi();
                    },
                    error: (err) => {
                        this.snackBar.open('Errore durante l\'annullamento dell\'evento: ' + (err.error?.message || 'Errore sconosciuto'), 'Chiudi', { duration: 5000 });
                    }
                });
            }
        });
    }

    promoteEvent(evento: EventoDTO): void {
        const dialogRef = this.dialog.open(PromoteEventDialogComponent, {
            width: '600px',
            data: { evento }
        });

        dialogRef.afterClosed().subscribe((result: PromoteRequestDTO | undefined) => {
            if (result) {
                const eventoId = (evento as any).idEvento || evento.id;
                this.animatoreService.promoteEvento(eventoId, result).subscribe({
                    next: (response) => {
                        this.snackBar.open(`Evento promosso con successo su ${response.canaliPromossi.length} canali`, 'Chiudi', { duration: 3000 });
                    },
                    error: (err) => {
                        this.snackBar.open('Errore durante la promozione dell\'evento: ' + (err.error?.message || 'Errore sconosciuto'), 'Chiudi', { duration: 5000 });
                    }
                });
            }
        });
    }

    manageAziende(evento: EventoDTO): void {
        const eventoId = (evento as any).idEvento || evento.id;

        // Carica le aziende partecipanti
        this.animatoreService.getAziendePartecipanti(eventoId).subscribe({
            next: (aziendePartecipanti) => {
                // TODO: Implementare chiamata per ottenere tutte le aziende disponibili
                // Per ora usiamo un array vuoto
                const aziendeDisponibili: AziendaPartecipanteDTO[] = [];

                const dialogRef = this.dialog.open(ManageAziendeDialogComponent, {
                    width: '800px',
                    data: {
                        eventoId: eventoId,
                        eventoNome: this.animatoreService.getEventoNome(evento),
                        aziendePartecipanti,
                        aziendeDisponibili
                    }
                });

                dialogRef.afterClosed().subscribe(result => {
                    if (result) {
                        if (result.action === 'add') {
                            this.animatoreService.addAziendaPartecipante(eventoId, result.azienda.id).subscribe({
                                next: () => {
                                    this.snackBar.open('Azienda aggiunta con successo', 'Chiudi', { duration: 3000 });
                                    // Ricarica il dialog
                                    this.manageAziende(evento);
                                },
                                error: (err) => {
                                    this.snackBar.open('Errore durante l\'aggiunta dell\'azienda: ' + (err.error?.message || 'Errore sconosciuto'), 'Chiudi', { duration: 5000 });
                                }
                            });
                        } else if (result.action === 'remove') {
                            this.animatoreService.removeAziendaPartecipante(eventoId, result.azienda.id).subscribe({
                                next: () => {
                                    this.snackBar.open('Azienda rimossa con successo', 'Chiudi', { duration: 3000 });
                                    // Ricarica il dialog
                                    this.manageAziende(evento);
                                },
                                error: (err) => {
                                    this.snackBar.open('Errore durante la rimozione dell\'azienda: ' + (err.error?.message || 'Errore sconosciuto'), 'Chiudi', { duration: 5000 });
                                }
                            });
                        }
                    }
                });
            },
            error: (err) => {
                this.snackBar.open('Errore durante il caricamento delle aziende: ' + (err.error?.message || 'Errore sconosciuto'), 'Chiudi', { duration: 5000 });
            }
        });
    }

    viewParticipants(evento: EventoDTO): void {
        const eventoId = (evento as any).idEvento || evento.id;
        // Naviga alla pagina dei partecipanti
        this.router.navigate(['/dashboard/animatore/eventi', eventoId, 'partecipanti']);
    }

    // === UTILITY METHODS ===

    canStartEvent(evento: EventoDTO): boolean {
        return this.animatoreService.canStartEvent(evento);
    }

    canEndEvent(evento: EventoDTO): boolean {
        return this.animatoreService.canEndEvent(evento);
    }

    canCancelEvent(evento: EventoDTO): boolean {
        return this.animatoreService.canCancelEvent(evento);
    }

    createNewEvent(): void {
        const dialogRef = this.dialog.open(CreateEventDialogComponent, {
            width: '700px'
        });

        dialogRef.afterClosed().subscribe(result => {
            if (result) {
                this.animatoreService.createEvento(result).subscribe({
                    next: () => {
                        this.snackBar.open('Evento creato con successo', 'Chiudi', { duration: 3000 });
                        this.loadEventi();
                    },
                    error: (err) => {
                        this.snackBar.open('Errore durante la creazione dell\'evento: ' + (err.error?.message || 'Errore sconosciuto'), 'Chiudi', { duration: 5000 });
                    }
                });
            }
        });
    }

    getStatoClass(stato: string): string {
        switch (stato) {
            case StatoEvento.IN_PROGRAMMA:
                return 'status-pending';
            case StatoEvento.IN_CORSO:
                return 'status-active';
            case StatoEvento.CONCLUSO:
                return 'status-completed';
            case StatoEvento.ANNULLATO:
                return 'status-cancelled';
            default:
                return '';
        }
    }
}