import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatChipsModule } from '@angular/material/chips';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { Subject, debounceTime, distinctUntilChanged, takeUntil } from 'rxjs';

import { PublicAziendeService } from '../../../../core/services/public-aziende.service';
import { PublicAziendaSummaryDTO, PublicAziendaFilters } from '../../../../core/models/public.models';
import { SearchBoxComponent } from '../../shared/components/search/search-box/search-box.component';

@Component({
  selector: 'app-aziende-page',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    RouterModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatInputModule,
    MatSelectModule,
    MatFormFieldModule,
    MatPaginatorModule,
    MatProgressSpinnerModule,
    MatTooltipModule,
    MatChipsModule,
    MatSnackBarModule,
    SearchBoxComponent
  ],
  templateUrl: './aziende-page.component.html',
  styleUrls: ['./aziende-page.component.scss'],
})
export class AziendePageComponent implements OnInit, OnDestroy {
  aziende: PublicAziendaSummaryDTO[] = [];
  isLoading = true;
  error: string | null = null;
  
  // Paginazione
  totalElements = 0;
  pageSize = 12;
  currentPage = 0;
  pageSizeOptions = [6, 12, 24, 48];

  // Filtri
  filters: PublicAziendaFilters = {
    page: 0,
    size: this.pageSize,
    sortBy: 'nome_asc'
  };

  // Opzioni filtri
  tipologieAzienda = [
    { value: 'PRODUZIONE', label: 'Produzione' },
    { value: 'TRASFORMAZIONE', label: 'Trasformazione' },
    { value: 'DISTRIBUZIONE', label: 'Distribuzione' }
  ];

  opzioniOrdinamento = [
    { value: 'nome_asc', label: 'Nome (A-Z)' },
    { value: 'nome_desc', label: 'Nome (Z-A)' },
    { value: 'rating_desc', label: 'Rating più alto' },
    { value: 'prodotti_desc', label: 'Più prodotti' }
  ];

  // Stato UI
  showFilters = false;
  activeFiltersCount = 0;
  searchSubject = new Subject<string>();

  private destroy$ = new Subject<void>();

  constructor(
    private aziendeService: PublicAziendeService,
    private router: Router,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.loadAziende();
    this.setupSearchDebouncer();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private setupSearchDebouncer(): void {
    this.searchSubject
      .pipe(
        debounceTime(300),
        distinctUntilChanged(),
        takeUntil(this.destroy$)
      )
      .subscribe(searchTerm => {
        this.filters.query = searchTerm || undefined;
        this.currentPage = 0;
        this.filters.page = 0;
        this.loadAziende();
      });
  }

  loadAziende(): void {
    this.isLoading = true;
    this.error = null;

    this.aziendeService.getAziende(this.filters).subscribe({
      next: (response) => {
        this.aziende = response.content;
        this.totalElements = response.totalElements;
        this.isLoading = false;
        this.calculateActiveFilters();
      },
      error: (err) => {
        console.error('Errore nel caricamento delle aziende:', err);
        this.error = 'Impossibile caricare le aziende. Riprova più tardi.';
        this.isLoading = false;
      }
    });
  }

  onSearch(searchTerm: string): void {
    this.searchSubject.next(searchTerm);
  }

  onFilterChange(): void {
    this.currentPage = 0;
    this.filters.page = 0;
    this.loadAziende();
  }

  onPageChange(event: PageEvent): void {
    this.currentPage = event.pageIndex;
    this.pageSize = event.pageSize;
    this.filters.page = this.currentPage;
    this.filters.size = this.pageSize;
    this.loadAziende();
  }

  toggleFilters(): void {
    this.showFilters = !this.showFilters;
  }

  resetFilters(): void {
    this.filters = {
      page: this.currentPage,
      size: this.pageSize,
      sortBy: this.filters.sortBy
    };
    this.loadAziende();
  }

  private calculateActiveFilters(): void {
    this.activeFiltersCount = 0;
    
    if (this.filters.query) this.activeFiltersCount++;
    if (this.filters.tipologia) this.activeFiltersCount++;
    if (this.filters.citta) this.activeFiltersCount++;
    if (this.filters.provincia) this.activeFiltersCount++;
    if (this.filters.certificazione) this.activeFiltersCount++;
  }

  getTipologiaLabel(tipologia: string): string {
    const tipologiaMap: { [key: string]: string } = {
      'PRODUZIONE': 'Produzione',
      'TRASFORMAZIONE': 'Trasformazione',
      'DISTRIBUZIONE': 'Distribuzione'
    };
    return tipologiaMap[tipologia] || tipologia;
  }

  getTipologiaIcon(tipologia: string): string {
    const iconMap: { [key: string]: string } = {
      'PRODUZIONE': 'agriculture',
      'TRASFORMAZIONE': 'factory',
      'DISTRIBUZIONE': 'local_shipping'
    };
    return iconMap[tipologia] || 'business';
  }

  getTipologiaColor(tipologia: string): string {
    const colorMap: { [key: string]: string } = {
      'PRODUZIONE': '#4caf50',  // verde
      'TRASFORMAZIONE': '#ff9800', // arancione
      'DISTRIBUZIONE': '#2196f3'   // blu
    };
    return colorMap[tipologia] || '#757575';
  }

  getRatingStars(rating: number): number[] {
    const stars = [];
    const fullStars = Math.floor(rating);
    const hasHalfStar = rating % 1 >= 0.5;

    for (let i = 0; i < fullStars; i++) {
      stars.push(1);
    }
    
    if (hasHalfStar && fullStars < 5) {
      stars.push(0.5);
    }
    
    const emptyStars = 5 - stars.length;
    for (let i = 0; i < emptyStars; i++) {
      stars.push(0);
    }
    
    return stars;
  }

  getRatingColor(rating: number): string {
    if (rating >= 4.5) return '#4caf50';  // verde
    if (rating >= 3.5) return '#ff9800';  // arancione
    if (rating >= 2.5) return '#ffc107';  // giallo
    return '#f44336'; // rosso
  }

  getFormattedAddress(indirizzo: { via: string; citta: string; provincia: string; cap: string }): string {
    return `${indirizzo.via}, ${indirizzo.cap} ${indirizzo.citta} (${indirizzo.provincia})`;
  }

  viewAziendaDetail(aziendaId: number): void {
    this.router.navigate(['/aziende', aziendaId]);
  }

  retry(): void {
    this.loadAziende();
  }

  // Metodi per accessibilità
  getAriaLabelForAzienda(azienda: PublicAziendaSummaryDTO): string {
    return `Azienda ${azienda.nomeAzienda}, ${this.getTipologiaLabel(azienda.tipologia)}${azienda.rating ? `, Rating: ${azienda.rating} stelle` : ''}${azienda.numeroProdotti ? `, ${azienda.numeroProdotti} prodotti` : ''}`;
  }

  // Metodi per sharing
  shareAzienda(azienda: PublicAziendaSummaryDTO): void {
    if (navigator.share) {
      navigator.share({
        title: azienda.nomeAzienda,
        text: azienda.descrizione?.substring(0, 200) + '...',
        url: `${window.location.origin}/aziende/${azienda.id}`
      }).catch(err => console.log('Errore nella condivisione:', err));
    } else {
      this.copyToClipboard(`${window.location.origin}/aziende/${azienda.id}`);
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
}