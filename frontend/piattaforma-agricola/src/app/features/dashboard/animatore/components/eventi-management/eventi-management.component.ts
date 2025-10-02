import { ChangeDetectionStrategy, Component, OnInit, ViewChild } from '@angular/core';
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
import { EventoDTO, EventoFilters, StatoEvento } from '../../../../../core/models/animatore.models';
import { PaginatedResponse } from '../../../../../core/models/common.models';
import { AnimatoreService } from '../../../../../core/services/animatore.service';
import { debounceTime, distinctUntilChanged, switchMap } from 'rxjs/operators';
import { Subject } from 'rxjs';
import { Router } from '@angular/router';

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

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  filters: EventoFilters = {
    pagina: 0,
    elementiPerPagina: 10,
    stato: 'TUTTI'
  };

  statiEvento = [
    { value: 'TUTTI', viewValue: 'Tutti' },
    { value: StatoEvento.PUBBLICATO, viewValue: 'Pubblicato' },
    { value: StatoEvento.DA_PUBBLICARE, viewValue: 'Da Pubblicare' },
    { value: StatoEvento.ANNULLATO, viewValue: 'Annullato' }
  ];

  private searchTerms = new Subject<string>();

  constructor(
    private animatoreService: AnimatoreService,
    private dialog: MatDialog,
    private snackBar: MatSnackBar,
    private router: Router
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
    ).subscribe((data: PaginatedResponse<EventoDTO>) => {
      this.dataSource.data = data.content;
      this.totalElements = data.totalElements;
      this.isLoading = false;
    }, (error: any) => {
      this.snackBar.open('Errore durante il caricamento degli eventi.', 'Chiudi', { duration: 3000 });
      this.isLoading = false;
    });
  }

  ngAfterViewInit(): void {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;

    this.paginator.page.subscribe(() => {
      this.filters.pagina = this.paginator.pageIndex;
      this.filters.elementiPerPagina = this.paginator.pageSize;
      this.loadEventi();
    });

    this.sort.sortChange.subscribe(() => {
      this.filters.pagina = 0;
      // Implementare logica di ordinamento se l'API lo supporta
      this.loadEventi();
    });
  }

  loadEventi(): void {
    this.isLoading = true;
    this.animatoreService.getMyEvents(this.filters).subscribe((data: PaginatedResponse<EventoDTO>) => {
      this.dataSource.data = data.content;
      this.totalElements = data.totalElements;
      this.isLoading = false;
    }, (error: any) => {
      this.snackBar.open('Errore durante il caricamento degli eventi.', 'Chiudi', { duration: 3000 });
      this.isLoading = false;
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
    this.router.navigate(['/dashboard/animatore/eventi', evento.id]);
  }

  editEvent(evento: EventoDTO): void {
    this.router.navigate(['/dashboard/animatore/eventi/edit', evento.id]);
  }

  deleteEvent(evento: EventoDTO): void {
    // Implementare logica di eliminazione evento, probabilmente con un dialog di conferma
    this.snackBar.open(`Elimina evento ${evento.titolo}`, 'Chiudi', { duration: 2000 });
  }

  createNewEvent(): void {
    this.router.navigate(['/dashboard/animatore/eventi/nuovo']);
  }

  getStatoClass(stato: string): string {
    switch (stato) {
      case StatoEvento.PUBBLICATO:
        return 'status-published';
      case StatoEvento.DA_PUBBLICARE:
        return 'status-pending';
      case StatoEvento.ANNULLATO:
        return 'status-cancelled';
      default:
        return '';
    }
  }
}