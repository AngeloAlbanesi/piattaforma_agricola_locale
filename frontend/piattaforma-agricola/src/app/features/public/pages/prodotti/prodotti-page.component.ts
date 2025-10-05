import { Component, OnInit, OnDestroy } from '@angular/core';
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

import { PublicProdottiService } from '../../../../core/services/public-prodotti.service';
import { PublicProdottoSummaryDTO } from '../../../../core/models/public.models';
import { PublicProdottoFilters } from '../../../../core/models/public.models';
import { PaginatedResponse } from '../../../../core/models/common.models';
import { SearchBoxComponent } from '../../shared/components/search/search-box/search-box.component';
import { ProductCardComponent } from '../../shared/components/cards/product-card/product-card.component';
import { FiltersPanelComponent } from '../../shared/components/filters/filters-panel/filters-panel.component';

@Component({
  selector: 'app-prodotti-page',
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
    SearchBoxComponent,
    ProductCardComponent,
    FiltersPanelComponent
  ],
  templateUrl: './prodotti-page.component.html',
  styleUrls: ['./prodotti-page.component.scss']
})
export class ProdottiPageComponent implements OnInit, OnDestroy {
  prodotti: PublicProdottoSummaryDTO[] = [];
  loading = false;
  error: string | null = null;
  totalCount = 0;
  currentPage = 0;
  pageSize = 12;
  
  // Filtri
  filters: PublicProdottoFilters = {};
  showFilters = false;
  
  // Opzioni per i filtri
  categorieOptions: string[] = [];
  ordinamentoOptions = [
    { value: 'nome_asc', label: 'Nome A-Z' },
    { value: 'nome_desc', label: 'Nome Z-A' },
    { value: 'prezzo_asc', label: 'Prezzo: dal più basso' },
    { value: 'prezzo_desc', label: 'Prezzo: dal più alto' },
    { value: 'disponibilita', label: 'Disponibilità' }
  ];

  private subscriptions = new Map<string, any>();

  constructor(
    private prodottiService: PublicProdottiService,
    private router: Router,
    private fb: FormBuilder,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.loadProdotti();
    this.loadCategorie();
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => {
      if (subscription && subscription.unsubscribe) {
        subscription.unsubscribe();
      }
    });
    this.subscriptions.clear();
  }

  loadProdotti(): void {
    this.loading = true;
    this.error = null;

    const prodottiSub = this.prodottiService.getProdotti({
      page: this.currentPage,
      size: this.pageSize,
      ...this.filters
    }).subscribe({
      next: (response: PaginatedResponse<PublicProdottoSummaryDTO>) => {
        this.prodotti = response.content || [];
        this.totalCount = response.totalElements || 0;
        this.loading = false;
      },
      error: (error: any) => {
        console.error('Errore nel caricamento prodotti:', error);
        this.error = 'Impossibile caricare i prodotti. Riprova più tardi.';
        this.loading = false;
        this.snackBar.open(this.error, 'Chiudi', {
          duration: 5000,
          panelClass: ['error-snackbar']
        });
      }
    });

    this.subscriptions.set('prodotti', prodottiSub);
  }

  loadCategorie(): void {
    // Carica categorie statiche o da un endpoint dedicato se disponibile
    this.categorieOptions = [
      'Frutta',
      'Verdura',
      'Carne',
      'Pesce',
      'Latticini',
      'Pane e Pasta',
      'Oli e Condimenti',
      'Vini e Bevande',
      'Conserve',
      'Erbe Aromatiche',
      'Miele',
      'Formaggi',
      'Salumi',
      'Cereali',
      'Legumi',
      'Altro'
    ];
  }

  onPageChange(event: PageEvent): void {
    this.currentPage = event.pageIndex;
    this.pageSize = event.pageSize;
    this.loadProdotti();
  }

  onSearch(query: string): void {
    this.filters = { ...this.filters, query: query || undefined };
    this.currentPage = 0;
    this.loadProdotti();
  }

  onFiltersChange(newFilters: any): void {
    this.filters = { ...this.filters, ...newFilters };
    this.currentPage = 0;
    this.loadProdotti();
  }

  onFiltersReset(): void {
    this.filters = {};
    this.currentPage = 0;
    this.loadProdotti();
  }

  toggleFilters(): void {
    this.showFilters = !this.showFilters;
  }

  navigateToProdottoDetail(prodottoId: number): void {
    this.router.navigate(['/prodotti', prodottoId]);
  }

  retryLoad(): void {
    this.loadProdotti();
  }

  // Metodi helper per il template
  formatCurrency(prezzo: number): string {
    return `€${prezzo.toFixed(2)}`;
  }

  isAvailable(quantita: number): boolean {
    return quantita > 0;
  }

  getAvailabilityText(quantita: number): string {
    if (quantita === 0) return 'Non disponibile';
    if (quantita < 5) return `Solo ${quantita} pezzi`;
    return 'Disponibile';
  }

  getAvailabilityColor(quantita: number): string {
    if (quantita === 0) return 'warn';
    if (quantita < 5) return 'accent';
    return 'primary';
  }

  // Metodi per i filtri specifici dei prodotti
  onCategoriaChange(categoria: string): void {
    this.filters = { ...this.filters, categoria: categoria || undefined };
    this.currentPage = 0;
    this.loadProdotti();
  }

  onOrdinamentoChange(ordinamento: string): void {
    this.filters = { ...this.filters, sortBy: ordinamento as any || undefined };
    this.currentPage = 0;
    this.loadProdotti();
  }

  onPrezzoRangeChange(prezzoRange: { min: number; max: number }): void {
    this.filters = {
      ...this.filters,
      prezzoMin: prezzoRange.min || undefined,
      prezzoMax: prezzoRange.max || undefined
    };
    this.currentPage = 0;
    this.loadProdotti();
  }

  onDisponibilitaChange(disponibile: boolean): void {
    this.filters = { ...this.filters, disponibilita: disponibile || undefined };
    this.currentPage = 0;
    this.loadProdotti();
  }

  onCertificazioniChange(certificazioni: string[]): void {
    this.filters = { ...this.filters, certificazione: certificazioni.length > 0 ? certificazioni[0] : undefined };
    this.currentPage = 0;
    this.loadProdotti();
  }

  // Metodo per ottenere il numero di filtri attivi
  getActiveFiltersCount(): number {
    let count = 0;
    if (this.filters.query) count++;
    if (this.filters.categoria) count++;
    if (this.filters.sortBy) count++;
    if (this.filters.prezzoMin !== undefined) count++;
    if (this.filters.prezzoMax !== undefined) count++;
    if (this.filters.disponibilita !== undefined) count++;
    if (this.filters.certificazione) count++;
    return count;
  }

  // Metodo per resettare tutti i filtri
  resetAllFilters(): void {
    this.filters = {};
    this.currentPage = 0;
    this.loadProdotti();
  }

  // Metodo per scrollare in cima alla pagina
  scrollToTop(): void {
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  // Metodo per convertire le categorie in FilterOption
  getCategorieOptions(): any[] {
    return this.categorieOptions.map(cat => ({ value: cat, label: cat }));
  }
}