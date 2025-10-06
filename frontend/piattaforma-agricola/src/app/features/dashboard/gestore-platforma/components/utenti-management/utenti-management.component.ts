import { ChangeDetectionStrategy, ChangeDetectorRef, Component, OnInit, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
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
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { PaginatedResponse } from '../../../../../core/models/common.models';
import { AdminService } from '../../../../../core/services/admin.service';
import { UserPublicDTO, TipoRuolo } from '../../../../../core/models/admin.models';
import { BanUserDialogComponent } from '../ban-user-dialog/ban-user-dialog.component';
import { ActivationDialogComponent } from '../activation-dialog/activation-dialog.component';
import { debounceTime, distinctUntilChanged, switchMap } from 'rxjs/operators';
import { Subject } from 'rxjs';
import {
    mapStatoAccreditamentoToChipColor,
    mapStatoAccreditamentoToDisplayName,
    mapStatoAccreditamentoToIcon,
    mapTipoRuoloToDisplayName,
    mapTipoRuoloToIcon,
    mapTipoRuoloToColor
} from '../../utils/gestore.utils';

@Component({
    selector: 'app-utenti-management',
    standalone: true,
    imports: [
        CommonModule,
        FormsModule,
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
        MatTooltipModule,
        MatSlideToggleModule
    ],
    templateUrl: './utenti-management.component.html',
    styleUrls: ['./utenti-management.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class UtentiManagementComponent implements OnInit {
    displayedColumns: string[] = ['idUtente', 'nome', 'cognome', 'tipoRuolo', 'statoAccreditamento', 'isAttivo', 'actions'];
    dataSource = new MatTableDataSource<UserPublicDTO>([]);
    isLoading = true;
    totalElements = 0;
    pageSize = 10;
    currentPage = 0;
    pageSizeOptions: number[] = [5, 10, 25, 50];

    @ViewChild(MatPaginator) paginator!: MatPaginator;
    @ViewChild(MatSort) sort!: MatSort;

    filters: { search?: string; soloAttivi?: boolean; tipoRuolo?: TipoRuolo | string; pagina: number; elementiPerPagina: number } = {
        pagina: 0,
        elementiPerPagina: 10
    };

    tipiRuolo = Object.values(TipoRuolo);
    selectedTipoRuolo: TipoRuolo | string | null = null;

    private searchTerms = new Subject<string>();

    constructor(
        private adminService: AdminService,
        private dialog: MatDialog,
        private snackBar: MatSnackBar,
        private cdr: ChangeDetectorRef
    ) { }

    ngOnInit(): void {
        this.loadUsers();
        this.searchTerms.pipe(
            debounceTime(300),
            distinctUntilChanged(),
            switchMap((term: string) => {
                this.filters.search = term;
                this.filters.pagina = 0;
                return this.getFilteredUsers();
            })
        ).subscribe((data: PaginatedResponse<UserPublicDTO>) => {
            this.dataSource.data = data.content;
            this.totalElements = data.totalElements;
            this.isLoading = false;
        });
    }

    ngAfterViewInit(): void {
        if (this.paginator) {
            this.dataSource.paginator = this.paginator;
            this.paginator.page.subscribe(() => {
                this.filters.pagina = this.paginator.pageIndex;
                this.filters.elementiPerPagina = this.paginator.pageSize;
                this.loadUsers();
            });
        }

        if (this.sort) {
            this.dataSource.sort = this.sort;
            this.sort.sortChange.subscribe(() => {
                this.filters.pagina = 0;
                // Ordinamento client-side opzionale: non applicato (API non espone sort)
                this.loadUsers();
            });
        }
    }

    loadUsers(): void {
        this.isLoading = true;
        this.getFilteredUsers()
            .subscribe({
                next: (data: PaginatedResponse<UserPublicDTO>) => {
                    this.dataSource.data = data.content;
                    this.totalElements = data.totalElements;
                    this.isLoading = false;

                    // Verifica che tutti gli utenti abbiano lo stato di attivazione corretto
                    this.verifyUserActivationStates(data.content);

                    this.cdr.markForCheck();
                },
                error: (error: any) => {
                    this.snackBar.open('Errore durante il caricamento degli utenti.', 'Chiudi', { duration: 3000 });
                    this.isLoading = false;
                    this.cdr.markForCheck();
                }
            });
    }

    /**
     * Verifica che tutti gli utenti abbiano lo stato di attivazione corretto.
     * Questo metodo assicura che il toggle rifletta lo stato reale dal backend.
     */
    private verifyUserActivationStates(users: UserPublicDTO[]): void {
        // Verifica che tutti gli utenti abbiano il campo isAttivo popolato
        const usersWithUndefinedStatus = users.filter(user => user.isAttivo === undefined);
        if (usersWithUndefinedStatus.length > 0) {
            console.warn('Alcuni utenti hanno isAttivo undefined:', usersWithUndefinedStatus);
        }
    }

    private getFilteredUsers() {
        if (this.filters.tipoRuolo) {
            return this.adminService.getUsersByType(this.filters.tipoRuolo, this.filters.pagina, this.filters.elementiPerPagina);
        } else {
            return this.adminService.listUsers(this.filters.search, this.filters.soloAttivi, this.filters.pagina, this.filters.elementiPerPagina);
        }
    }

    applyFilter(event: Event): void {
        const filterValue = (event.target as HTMLInputElement).value;
        this.searchTerms.next(filterValue.trim().toLowerCase());
    }

    onSoloAttiviToggle(checked: boolean): void {
        this.filters.soloAttivi = checked;
        this.filters.pagina = 0;
        this.loadUsers();
    }

    onTipoRuoloChange(tipoRuolo: TipoRuolo | string | null): void {
        this.filters.tipoRuolo = tipoRuolo || undefined;
        this.filters.pagina = 0;
        this.loadUsers();
    }

    clearFilters(): void {
        this.selectedTipoRuolo = null;
        this.filters.tipoRuolo = undefined;
        this.filters.search = undefined;
        this.filters.soloAttivi = false;
        this.filters.pagina = 0;
        this.loadUsers();
    }

    // Metodi per azioni utente (blocca, sospendi, visualizza dettagli)
    viewUserDetails(user: UserPublicDTO): void {
        // Implementare l'apertura di un dialog o navigazione per i dettagli utente
        this.snackBar.open(`Visualizza dettagli per ${user.nome} ${user.cognome}`, 'Chiudi', { duration: 2000 });
    }

    blockUser(user: UserPublicDTO): void {
        const ref = this.dialog.open(BanUserDialogComponent, {
            data: { displayName: `${user.nome} ${user.cognome}` },
            disableClose: true // Impedisce chiusura cliccando fuori o premendo ESC
        });
        ref.afterClosed().subscribe((motivazione?: string) => {
            if (!motivazione) { return; }
            this.adminService.banUser(user.idUtente, motivazione).subscribe({
                next: (msg) => {
                    this.snackBar.open(msg || 'Utente bannato con successo', 'Chiudi', { duration: 2500 });
                    this.loadUsers();
                },
                error: () => this.snackBar.open('Errore durante il ban utente', 'Chiudi', { duration: 3000 })
            });
        });
    }

    toggleActivation(user: UserPublicDTO, desiredActive?: boolean): void {
        // Gestisce il caso in cui isAttivo è undefined (default: false)
        const currentState = user.isAttivo === true;
        const attivo = typeof desiredActive === 'boolean' ? desiredActive : !currentState;

        // Mappa il tipoRuolo al tipo richiesto dall'API
        const tipo = this.mapTipoRuoloToApiType(user.tipoRuolo);

        if (!tipo) {
            this.snackBar.open('Impossibile attivare/disattivare questo tipo di utente', 'Chiudi', { duration: 3000 });
            this.cdr.markForCheck();
            return;
        }

        // Conferma l'azione con un dialog semplice
        const ref = this.dialog.open(ActivationDialogComponent, {
            data: {
                displayName: `${user.nome} ${user.cognome}`,
                attivo,
                skipTipoSelection: true // Non chiedere il tipo
            },
            disableClose: true // Impedisce chiusura cliccando fuori o premendo ESC
        });

        ref.afterClosed().subscribe((confirmed: boolean) => {
            if (!confirmed) {
                // Se l'utente annulla il dialog, ripristina lo stato precedente del toggle
                this.cdr.markForCheck();
                return;
            }

            this.adminService.setUserActivation(user.idUtente, tipo, attivo).subscribe({
                next: (msg) => {
                    this.snackBar.open(msg || `Utente ${attivo ? 'attivato' : 'disattivato'} con successo`, 'Chiudi', { duration: 2500 });

                    // Aggiorna immediatamente lo stato dell'utente per feedback visivo
                    user.isAttivo = attivo;
                    this.cdr.markForCheck();

                    // Ricarica la lista per sincronizzare con il backend (se il backend restituisce il campo)
                    setTimeout(() => this.loadUsers(), 1000);
                },
                error: (error) => {
                    console.error('Errore durante l\'aggiornamento dello stato:', error);
                    this.snackBar.open('Errore durante l\'aggiornamento dello stato', 'Chiudi', { duration: 3000 });
                    this.cdr.markForCheck();
                }
            });
        });
    }

    /**
     * Mappa il TipoRuolo dell'utente al tipo richiesto dall'API di attivazione.
     * PRODUTTORE, TRASFORMATORE, DISTRIBUTORE → VENDITORE
     */
    private mapTipoRuoloToApiType(tipoRuolo: TipoRuolo | string): 'ACQUIRENTE' | 'VENDITORE' | 'CURATORE' | 'ANIMATORE' | null {
        switch (tipoRuolo) {
            case TipoRuolo.ACQUIRENTE:
            case 'ACQUIRENTE':
                return 'ACQUIRENTE';
            case TipoRuolo.PRODUTTORE:
            case TipoRuolo.TRASFORMATORE:
            case TipoRuolo.DISTRIBUTORE:
            case 'PRODUTTORE':
            case 'TRASFORMATORE':
            case 'DISTRIBUTORE':
                return 'VENDITORE';
            case TipoRuolo.CURATORE:
            case 'CURATORE':
                return 'CURATORE';
            case TipoRuolo.ANIMATORE:
            case 'ANIMATORE':
                return 'ANIMATORE';
            default:
                return null; // GESTORE_PIATTAFORMA non può essere attivato/disattivato
        }
    }

    // Utility methods for template
    getStatoChipColor(stato: string): string {
        return mapStatoAccreditamentoToChipColor(stato);
    }

    getStatoDisplayName(stato: string): string {
        return mapStatoAccreditamentoToDisplayName(stato);
    }

    getStatoIcon(stato: string): string {
        return mapStatoAccreditamentoToIcon(stato);
    }

    getTipoRuoloDisplayName(tipo: string): string {
        return mapTipoRuoloToDisplayName(tipo);
    }

    getTipoRuoloIcon(tipo: string): string {
        return mapTipoRuoloToIcon(tipo);
    }

    getTipoRuoloColor(tipo: string): string {
        return mapTipoRuoloToColor(tipo);
    }
}