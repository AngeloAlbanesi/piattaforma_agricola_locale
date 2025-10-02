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
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatChipsModule } from '@angular/material/chips';
import { DatePipe } from '@angular/common';
import { ProcessoTrasformazioneSummaryDTO, ProcessoFilters, StatoProcesso } from '../../../../../core/models/trasformatore.models';
import { PaginatedResponse } from '../../../../../core/models/common.models';
import { TrasformatoreService } from '../../../../../core/services/trasformatore.service';
import { debounceTime, distinctUntilChanged, switchMap } from 'rxjs/operators';
import { Subject } from 'rxjs';
import { Router } from '@angular/router';

@Component({
  selector: 'app-processi-management',
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
    MatTooltipModule,
    MatChipsModule,
    DatePipe
  ],
  templateUrl: './processi-management.component.html',
  styleUrls: ['./processi-management.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProcessiManagementComponent implements OnInit {
  displayedColumns: string[] = ['id', 'nome', 'stato', 'dataCreazione', 'numeroFasi', 'actions'];
  dataSource = new MatTableDataSource<ProcessoTrasformazioneSummaryDTO>([]);
  isLoading = true;
  totalElements = 0;
  pageSize = 10;
  currentPage = 0;
  pageSizeOptions: number[] = [5, 10, 25, 50];

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  filters: ProcessoFilters & { pagina: number, elementiPerPagina: number } = {
    pagina: 0,
    elementiPerPagina: 10,
    stato: 'TUTTI'
  } as ProcessoFilters & { pagina: number, elementiPerPagina: number };

  private searchTerms = new Subject<string>();

  constructor(
    private trasformatoreService: TrasformatoreService,
    private dialog: MatDialog,
    private snackBar: MatSnackBar,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.loadProcessi();
    this.searchTerms.pipe(
      debounceTime(300),
      distinctUntilChanged(),
      switchMap((term: string) => {
        this.filters.search = term;
        this.filters.pagina = 0;
        return this.trasformatoreService.getMyProcesses(this.filters);
      })
    ).subscribe((data: PaginatedResponse<ProcessoTrasformazioneSummaryDTO>) => {
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
      this.loadProcessi();
    });

    this.sort.sortChange.subscribe(() => {
      this.filters.pagina = 0;
      // Implementare logica di ordinamento se l'API lo supporta
      this.loadProcessi();
    });
  }

  loadProcessi(): void {
    this.isLoading = true;
    this.trasformatoreService.getMyProcesses(this.filters).subscribe((data: PaginatedResponse<ProcessoTrasformazioneSummaryDTO>) => {
      this.dataSource.data = data.content;
      this.totalElements = data.totalElements;
      this.isLoading = false;
    }, (error: any) => {
      this.snackBar.open('Errore durante il caricamento dei processi.', 'Chiudi', { duration: 3000 });
      this.isLoading = false;
    });
  }

  applyFilter(event: Event): void {
    const filterValue = (event.target as HTMLInputElement).value;
    this.searchTerms.next(filterValue.trim().toLowerCase());
  }

  onStatusChange(status: StatoProcesso | 'TUTTI'): void {
    this.filters.stato = status;
    this.filters.pagina = 0;
    this.loadProcessi();
  }

  viewProcessDetails(processo: ProcessoTrasformazioneSummaryDTO): void {
    this.router.navigate(['/dashboard/trasformatore/processi', processo.id]);
  }

  editProcess(processo: ProcessoTrasformazioneSummaryDTO): void {
    this.router.navigate(['/dashboard/trasformatore/processi/edit', processo.id]);
  }

  deleteProcess(processo: ProcessoTrasformazioneSummaryDTO): void {
    // Implementare logica di eliminazione processo, probabilmente con un dialog di conferma
    this.snackBar.open(`Elimina processo ${processo.nome}`, 'Chiudi', { duration: 2000 });
  }

  createNewProcess(): void {
    this.router.navigate(['/dashboard/trasformatore/processi/nuovo']);
  }
}