import { ChangeDetectionStrategy, Component, OnInit, ViewChild } from '@angular/core';
import { CommonModule, CurrencyPipe } from '@angular/common';
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
import { ProduttoreProductSummaryDTO, ProduttoreProductFilters, StatoVerifica, TipoOrigineProdotto } from '../../../../../core/models/produttore.models';
import { PaginatedResponse } from '../../../../../core/models/common.models';
import { ProduttoreService } from '../../../../../core/services/produttore.service';
import { debounceTime, distinctUntilChanged, switchMap } from 'rxjs/operators';
import { Subject } from 'rxjs';
import { Router } from '@angular/router';

@Component({
  selector: 'app-prodotti-management',
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
    CurrencyPipe
  ],
  templateUrl: './prodotti-management.component.html',
  styleUrl: './prodotti-management.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProdottiManagementComponent implements OnInit {
  displayedColumns: string[] = ['id', 'nome', 'prezzo', 'quantitaDisponibile', 'stato', 'origine', 'actions'];
  dataSource = new MatTableDataSource<ProduttoreProductSummaryDTO>([]);
  isLoading = true;
  totalElements = 0;
  pageSize = 10;
  currentPage = 0;

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  filters: ProduttoreProductFilters & { pagina: number, elementiPerPagina: number } = {
    pagina: 0,
    elementiPerPagina: 10,
    statoVerifica: 'TUTTI',
    tipoOrigine: 'TUTTI'
  } as ProduttoreProductFilters & { pagina: number, elementiPerPagina: number };

  statiProdotto = [
    { value: 'TUTTI', viewValue: 'Tutti' },
    { value: StatoVerifica.APPROVATO, viewValue: 'Approvato' },
    { value: StatoVerifica.IN_ATTESA, viewValue: 'In Attesa di Verifica' },
    { value: StatoVerifica.RESPINTO, viewValue: 'Respinto' }
  ];

  tipiOrigine = [
    { value: 'TUTTI', viewValue: 'Tutti' },
    { value: TipoOrigineProdotto.COLTIVATO, viewValue: 'Coltivato' },
    { value: TipoOrigineProdotto.TRASFORMATO, viewValue: 'Trasformato' },
    { value: TipoOrigineProdotto.ARTIGIANALE, viewValue: 'Artigianale' }
  ];

  private searchTerms = new Subject<string>();

  constructor(
    private produttoreService: ProduttoreService,
    private dialog: MatDialog,
    private snackBar: MatSnackBar,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.loadProdotti();
    this.searchTerms.pipe(
      debounceTime(300),
      distinctUntilChanged(),
      switchMap((term: string) => {
        this.filters.search = term;
        this.filters.pagina = 0;
        return this.produttoreService.getMyProducts(this.filters);
      })
    ).subscribe((data: PaginatedResponse<ProduttoreProductSummaryDTO>) => {
      this.dataSource.data = data.content;
      this.totalElements = data.totalElements;
      this.isLoading = false;
    }, (error: any) => {
      this.snackBar.open('Errore durante il caricamento dei prodotti.', 'Chiudi', { duration: 3000 });
      this.isLoading = false;
    });
  }

  ngAfterViewInit(): void {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;

    this.paginator.page.subscribe(() => {
      this.filters.pagina = this.paginator.pageIndex;
      this.filters.elementiPerPagina = this.paginator.pageSize;
      this.loadProdotti();
    });

    this.sort.sortChange.subscribe(() => {
      this.filters.pagina = 0;
      // Implementare logica di ordinamento se l'API lo supporta
      this.loadProdotti();
    });
  }

  loadProdotti(): void {
    this.isLoading = true;
    this.produttoreService.getMyProducts(this.filters).subscribe((data: PaginatedResponse<ProduttoreProductSummaryDTO>) => {
      this.dataSource.data = data.content;
      this.totalElements = data.totalElements;
      this.isLoading = false;
    }, (error: any) => {
      this.snackBar.open('Errore durante il caricamento dei prodotti.', 'Chiudi', { duration: 3000 });
      this.isLoading = false;
    });
  }

  applyFilter(event: Event): void {
    const filterValue = (event.target as HTMLInputElement).value;
    this.searchTerms.next(filterValue.trim().toLowerCase());
  }

  onStatusChange(status: StatoVerifica | 'TUTTI'): void {
    this.filters.statoVerifica = status;
    this.filters.pagina = 0;
    this.loadProdotti();
  }

  onOriginChange(origin: TipoOrigineProdotto | 'TUTTI'): void {
    this.filters.tipoOrigine = origin;
    this.filters.pagina = 0;
    this.loadProdotti();
  }

  viewProductDetails(prodotto: ProduttoreProductSummaryDTO): void {
    this.router.navigate(['/dashboard/produttore/prodotti', prodotto.id]);
  }

  editProduct(prodotto: ProduttoreProductSummaryDTO): void {
    this.router.navigate(['/dashboard/produttore/prodotti/edit', prodotto.id]);
  }

  deleteProduct(prodotto: ProduttoreProductSummaryDTO): void {
    // Implementare logica di eliminazione prodotto, probabilmente con un dialog di conferma
    this.snackBar.open(`Elimina prodotto ${prodotto.nome}`, 'Chiudi', { duration: 2000 });
  }

  createNewProduct(): void {
    this.router.navigate(['/dashboard/produttore/prodotti/nuovo']);
  }

  getStatoClass(stato: string): string {
    switch (stato) {
      case StatoVerifica.APPROVATO:
        return 'status-approved';
      case StatoVerifica.IN_ATTESA:
        return 'status-pending';
      case StatoVerifica.RESPINTO:
        return 'status-rejected';
      default:
        return '';
    }
  }
}