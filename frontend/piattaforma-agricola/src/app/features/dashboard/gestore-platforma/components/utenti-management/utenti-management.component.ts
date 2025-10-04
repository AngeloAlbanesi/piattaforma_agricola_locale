import { ChangeDetectionStrategy, Component, OnInit, ViewChild } from '@angular/core';
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
import { DatePipe } from '@angular/common';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { PaginatedResponse } from '../../../../../core/models/common.models';
import { AdminService } from '../../../../../core/services/admin.service';
import { AdminUserDTO } from '../../../../../core/models/admin.models';
import { BanUserDialogComponent } from '../ban-user-dialog/ban-user-dialog.component';
import { ActivationDialogComponent } from '../activation-dialog/activation-dialog.component';
import { debounceTime, distinctUntilChanged, switchMap } from 'rxjs/operators';
import { Subject } from 'rxjs';

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
        MatSlideToggleModule,
        DatePipe
    ],
    templateUrl: './utenti-management.component.html',
    styleUrls: ['./utenti-management.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class UtentiManagementComponent implements OnInit {
    displayedColumns: string[] = ['idUtente', 'nome', 'cognome', 'email', 'ruolo', 'accreditato', 'attivo', 'dataRegistrazione', 'actions'];
    dataSource = new MatTableDataSource<AdminUserDTO>([]);
    isLoading = true;
    totalElements = 0;
    pageSize = 10;
    currentPage = 0;
    pageSizeOptions: number[] = [5, 10, 25, 50];

    @ViewChild(MatPaginator) paginator!: MatPaginator;
    @ViewChild(MatSort) sort!: MatSort;

    filters: { search?: string; soloAttivi?: boolean; pagina: number; elementiPerPagina: number } = {
        pagina: 0,
        elementiPerPagina: 10
    };

    private searchTerms = new Subject<string>();

    constructor(
        private adminService: AdminService,
        private dialog: MatDialog,
        private snackBar: MatSnackBar
    ) { }

    ngOnInit(): void {
        this.loadUsers();
        this.searchTerms.pipe(
            debounceTime(300),
            distinctUntilChanged(),
            switchMap((term: string) => {
                this.filters.search = term;
                this.filters.pagina = 0;
                return this.adminService.listUsers(this.filters.search, this.filters.soloAttivi, this.filters.pagina, this.filters.elementiPerPagina);
            })
        ).subscribe((data: PaginatedResponse<AdminUserDTO>) => {
            this.dataSource.data = data.content;
            this.totalElements = data.totalElements;
            this.isLoading = false;
        });
    }

    ngAfterViewInit(): void {
        this.dataSource.paginator = this.paginator;
        this.dataSource.sort = this.sort;

        this.paginator.page.subscribe(() => {
            this.filters.pagina = this.paginator.pageIndex;
            this.filters.elementiPerPagina = this.paginator.pageSize;
            this.loadUsers();
        });

        this.sort.sortChange.subscribe(() => {
            this.filters.pagina = 0;
            // Ordinamento client-side opzionale: non applicato (API non espone sort)
            this.loadUsers();
        });
    }

    loadUsers(): void {
        this.isLoading = true;
        this.adminService.listUsers(this.filters.search, this.filters.soloAttivi, this.filters.pagina, this.filters.elementiPerPagina)
            .subscribe((data: PaginatedResponse<AdminUserDTO>) => {
                this.dataSource.data = data.content;
                this.totalElements = data.totalElements;
                this.isLoading = false;
            }, (error: any) => {
                this.snackBar.open('Errore durante il caricamento degli utenti.', 'Chiudi', { duration: 3000 });
                this.isLoading = false;
            });
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

    // Metodi per azioni utente (blocca, sospendi, visualizza dettagli)
    viewUserDetails(user: AdminUserDTO): void {
        // Implementare l'apertura di un dialog o navigazione per i dettagli utente
        this.snackBar.open(`Visualizza dettagli per ${user.nome} ${user.cognome}`, 'Chiudi', { duration: 2000 });
    }

    blockUser(user: AdminUserDTO): void {
        const ref = this.dialog.open(BanUserDialogComponent, { data: { displayName: `${user.nome} ${user.cognome}` } });
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

    toggleActivation(user: AdminUserDTO, desiredActive?: boolean): void {
        const attivo = typeof desiredActive === 'boolean' ? desiredActive : !user.attivo;
        const ref = this.dialog.open(ActivationDialogComponent, { data: { displayName: `${user.nome} ${user.cognome}`, attivo } });
        ref.afterClosed().subscribe((result?: { tipo: 'ACQUIRENTE' | 'VENDITORE' | 'CURATORE' | 'ANIMATORE' }) => {
            if (!result?.tipo) { return; }
            this.adminService.setUserActivation(user.idUtente, result.tipo, attivo).subscribe({
                next: (msg) => {
                    this.snackBar.open(msg || `Utente ${attivo ? 'attivato' : 'disattivato'} con successo`, 'Chiudi', { duration: 2500 });
                    this.loadUsers();
                },
                error: () => this.snackBar.open('Errore durante l\'aggiornamento dello stato', 'Chiudi', { duration: 3000 })
            });
        });
    }
}