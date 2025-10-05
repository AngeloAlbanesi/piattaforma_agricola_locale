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

import { PublicPacchettiService } from '../../../../core/services/public-pacchetti.service';
import { PublicPacchettoSummaryDTO } from '../../../../core/models/public.models';
import { PublicPacchettoFilters } from '../../../../core/models/public.models';
import { PaginatedResponse } from '../../../../core/models/common.models';
import { SearchBoxComponent } from '../../shared/components/search/search-box/search-box.component';
import { FiltersPanelComponent } from '../../shared/components/filters/filters-panel/filters-panel.component';

@Component({
    selector: 'app-pacchetti-page',
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
        FiltersPanelComponent
    ],
    templateUrl: './pacchetti-page.component.html',
    styleUrls: ['./pacchetti-page.component.scss']
})
export class PacchettiPageComponent implements OnInit, OnDestroy {
    pacchetti: PublicPacchettoSummaryDTO[] = [];
    loading = false;
    error: string | null = null;
    totalCount = 0;
    currentPage = 0;
    pageSize = 12;

    // Filtri
    filters: PublicPacchettoFilters = {};
    showFilters = false;

    // Opzioni per i filtri
    categorieOptions: string[] = [];
    ordinamentoOptions = [
        { value: 'nome_asc', label: 'Nome A-Z' },
        { value: 'nome_desc', label: 'Nome Z-A' },
        { value: 'prezzo_asc', label: 'Prezzo: dal più basso' },
        { value: 'prezzo_desc', label: 'Prezzo: dal più alto' },
        { value: 'sconto_desc', label: 'Sconto maggiore' },
        { value: 'disponibilita', label: 'Disponibilità' }
    ];

    private subscriptions = new Map<string, any>();

    constructor(
        private pacchettiService: PublicPacchettiService,
        private router: Router,
        private fb: FormBuilder,
        private snackBar: MatSnackBar
    ) { }

    ngOnInit(): void {
        this.loadPacchetti();
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

    loadPacchetti(): void {
        this.loading = true;
        this.error = null;

        const pacchettiSub = this.pacchettiService.getPacchetti({
            page: this.currentPage,
            size: this.pageSize,
            ...this.filters
        }).subscribe({
            next: (response: PaginatedResponse<PublicPacchettoSummaryDTO>) => {
                this.pacchetti = response.content || [];
                this.totalCount = response.totalElements || 0;
                this.loading = false;
            },
            error: (error: any) => {
                console.error('Errore nel caricamento pacchetti:', error);
                this.error = 'Impossibile caricare i pacchetti. Riprova più tardi.';
                this.loading = false;
                this.snackBar.open(this.error, 'Chiudi', {
                    duration: 5000,
                    panelClass: ['error-snackbar']
                });
            }
        });

        this.subscriptions.set('pacchetti', pacchettiSub);
    }

    loadCategorie(): void {
        // Carica categorie statiche o da un endpoint dedicato se disponibile
        this.categorieOptions = [
            'Frutta e Verdura',
            'Carne e Salumi',
            'Formaggi e Latticini',
            'Pane e Pasta',
            'Oli e Condimenti',
            'Vini e Bevande',
            'Dolci e Dessert',
            'Erbe Aromatiche',
            'Miele e Marmellate',
            'Conserve',
            'Mix Regionali',
            'Cesti Regalo',
            'Altro'
        ];
    }

    onPageChange(event: PageEvent): void {
        this.currentPage = event.pageIndex;
        this.pageSize = event.pageSize;
        this.loadPacchetti();
    }

    onSearch(query: string): void {
        this.filters = { ...this.filters, query: query || undefined };
        this.currentPage = 0;
        this.loadPacchetti();
    }

    onFiltersChange(newFilters: any): void {
        this.filters = { ...this.filters, ...newFilters };
        this.currentPage = 0;
        this.loadPacchetti();
    }

    onFiltersReset(): void {
        this.filters = {};
        this.currentPage = 0;
        this.loadPacchetti();
    }

    toggleFilters(): void {
        this.showFilters = !this.showFilters;
    }

    navigateToPacchettoDetail(pacchettoId: number): void {
        this.router.navigate(['/pacchetti', pacchettoId]);
    }

    retryLoad(): void {
        this.loadPacchetti();
    }

    // Metodi helper per il template
    formatCurrency(prezzo: number | undefined): string {
        const p = prezzo ?? 0;
        return `€${p.toFixed(2)}`;
    }

    calculateSconto(prezzo: number | undefined, prezzoScontato: number | undefined): number {
        const p = prezzo ?? 0;
        const ps = prezzoScontato ?? 0;
        if (p <= 0 || ps <= 0 || ps >= p) return 0;
        return Math.round(((p - ps) / p) * 100);
    }

    isAvailable(quantita: number | undefined): boolean {
        return (quantita ?? 0) > 0;
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

    hasSconto(prezzo: number | undefined, prezzoScontato: number | undefined): boolean {
        const p = prezzo ?? 0;
        const ps = prezzoScontato ?? 0;
        return ps > 0 && ps < p;
    }

    // Metodi per i filtri specifici dei pacchetti
    onCategoriaChange(categoria: string): void {
        this.filters = { ...this.filters, categoria: categoria || undefined };
        this.currentPage = 0;
        this.loadPacchetti();
    }

    onOrdinamentoChange(ordinamento: string): void {
        this.filters = { ...this.filters, sortBy: ordinamento as any || undefined };
        this.currentPage = 0;
        this.loadPacchetti();
    }

    onPrezzoRangeChange(prezzoRange: { min: number; max: number }): void {
        this.filters = {
            ...this.filters,
            prezzoMin: prezzoRange.min || undefined,
            prezzoMax: prezzoRange.max || undefined
        };
        this.currentPage = 0;
        this.loadPacchetti();
    }

    onDisponibilitaChange(disponibile: boolean): void {
        this.filters = { ...this.filters, disponibilita: disponibile || undefined };
        this.currentPage = 0;
        this.loadPacchetti();
    }

    onScontoChange(scontoMinimo: number): void {
        // this.filters = { ...this.filters, scontoMinimo: scontoMinimo || undefined };
        this.currentPage = 0;
        this.loadPacchetti();
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
        // if (this.filters.scontoMinimo !== undefined) count++;
        return count;
    }

    // Metodo per resettare tutti i filtri
    resetAllFilters(): void {
        this.filters = {};
        this.currentPage = 0;
        this.loadPacchetti();
    }

    // Metodo per convertire le categorie in FilterOption
    getCategorieOptions(): any[] {
        return this.categorieOptions.map(cat => ({ value: cat, label: cat }));
    }

    // Metodo per scrollare in cima alla pagina
    scrollToTop(): void {
        window.scrollTo({ top: 0, behavior: 'smooth' });
    }
}
