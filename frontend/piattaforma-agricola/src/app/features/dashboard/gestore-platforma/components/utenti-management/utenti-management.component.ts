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
import { UtenteDTO, UtentiFilters, StatoUtente } from '../../../../../core/models/gestore-platforma.models';
import { PaginatedResponse } from '../../../../../core/models/common.models';
import { GestorePlatformaService } from '../../../../../core/services/gestore-platforma.service';
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
    DatePipe
  ],
  templateUrl: './utenti-management.component.html',
  styleUrls: ['./utenti-management.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class UtentiManagementComponent implements OnInit {
  displayedColumns: string[] = ['id', 'username', 'email', 'ruolo', 'stato', 'dataRegistrazione', 'actions'];
  dataSource = new MatTableDataSource<UtenteDTO>([]);
  isLoading = true;
  totalElements = 0;
  pageSize = 10;
  currentPage = 0;
  pageSizeOptions: number[] = [5, 10, 25, 50];

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  filters: UtentiFilters = {
    pagina: 0,
    elementiPerPagina: 10,
    stato: 'TUTTI'
  };

  private searchTerms = new Subject<string>();

  constructor(
    private gestoreService: GestorePlatformaService,
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
        return this.gestoreService.getUsers(this.filters);
      })
    ).subscribe((data: PaginatedResponse<UtenteDTO>) => {
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
      // Implementare logica di ordinamento se l'API lo supporta
      this.loadUsers();
    });
  }

  loadUsers(): void {
    this.isLoading = true;
    this.gestoreService.getUsers(this.filters).subscribe((data: PaginatedResponse<UtenteDTO>) => {
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

  onStatusChange(status: StatoUtente | 'TUTTI'): void {
    this.filters.stato = status;
    this.filters.pagina = 0;
    this.loadUsers();
  }

  // Metodi per azioni utente (blocca, sospendi, visualizza dettagli)
  viewUserDetails(user: UtenteDTO): void {
    // Implementare l'apertura di un dialog o navigazione per i dettagli utente
    this.snackBar.open(`Visualizza dettagli per ${user.username}`, 'Chiudi', { duration: 2000 });
  }

  blockUser(user: UtenteDTO): void {
    // Implementare logica di blocco utente, probabilmente con un dialog di conferma
    this.snackBar.open(`Blocca utente ${user.username}`, 'Chiudi', { duration: 2000 });
  }

  suspendUser(user: UtenteDTO): void {
    // Implementare logica di sospensione utente, probabilmente con un dialog di conferma
    this.snackBar.open(`Sospendi utente ${user.username}`, 'Chiudi', { duration: 2000 });
  }

  activateUser(user: UtenteDTO): void {
    // Implementare logica di attivazione utente
    this.snackBar.open(`Attiva utente ${user.username}`, 'Chiudi', { duration: 2000 });
  }
}