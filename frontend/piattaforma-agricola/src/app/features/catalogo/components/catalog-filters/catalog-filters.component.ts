import { Component, Input, Output, EventEmitter, OnInit, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, FormsModule } from '@angular/forms';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { MatSliderModule } from '@angular/material/slider';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatChipsModule } from '@angular/material/chips';

import { CatalogFilters, CatalogFilterOptions, DEFAULT_CATALOG_FILTERS } from '../../../../core/models/catalog.models';

/**
 * Componente per i filtri del catalogo
 */
@Component({
    selector: 'app-catalog-filters',
    standalone: true,
    imports: [
        CommonModule,
        ReactiveFormsModule,
        FormsModule,
        MatExpansionModule,
        MatFormFieldModule,
        MatSelectModule,
        MatSliderModule,
        MatCheckboxModule,
        MatButtonModule,
        MatIconModule,
        MatInputModule,
        MatChipsModule
    ],
    templateUrl: './catalog-filters.component.html',
    styleUrls: ['./catalog-filters.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class CatalogFiltersComponent implements OnInit {
    @Input() options!: CatalogFilterOptions;
    @Input() currentFilters: CatalogFilters = DEFAULT_CATALOG_FILTERS;

    @Output() filtersChange = new EventEmitter<Partial<CatalogFilters>>();
    @Output() resetFilters = new EventEmitter<void>();

    filtersForm!: FormGroup;
    aziendaSearchQuery = '';

    constructor(private fb: FormBuilder) { }

    ngOnInit(): void {
        this.initializeForm();
    }

    /**
     * Inizializza il form dei filtri
     */
    private initializeForm(): void {
        this.filtersForm = this.fb.group({
            tipo: [this.currentFilters.tipo || 'TUTTI'],
            aziende: [this.currentFilters.aziende || []],
            categorie: [this.currentFilters.categorie || []],
            prezzoMin: [this.currentFilters.prezzoMin || this.options.prezzoMin],
            prezzoMax: [this.currentFilters.prezzoMax || this.options.prezzoMax],
            certificazioni: [this.currentFilters.certificazioni || []],
            disponibilitaSolo: [this.currentFilters.disponibilitaSolo || false]
        });

        // Ascolta i cambiamenti e emette i nuovi filtri
        this.filtersForm.valueChanges.subscribe(values => {
            this.emitFilters(values);
        });
    }

    /**
     * Emette i filtri aggiornati
     */
    private emitFilters(values: any): void {
        const filters: Partial<CatalogFilters> = {
            tipo: values.tipo,
            aziende: values.aziende?.length > 0 ? values.aziende : undefined,
            categorie: values.categorie?.length > 0 ? values.categorie : undefined,
            prezzoMin: values.prezzoMin,
            prezzoMax: values.prezzoMax,
            certificazioni: values.certificazioni?.length > 0 ? values.certificazioni : undefined,
            disponibilitaSolo: values.disponibilitaSolo,
            page: 0 // Reset page quando cambiano i filtri
        };
        this.filtersChange.emit(filters);
    }

    /**
     * Resetta tutti i filtri
     */
    onResetFilters(): void {
        this.filtersForm.patchValue({
            tipo: 'TUTTI',
            aziende: [],
            categorie: [],
            prezzoMin: this.options.prezzoMin,
            prezzoMax: this.options.prezzoMax,
            certificazioni: [],
            disponibilitaSolo: false
        });
        this.aziendaSearchQuery = '';
        this.resetFilters.emit();
    }

    /**
     * Filtra le aziende in base alla ricerca
     */
    get filteredAziende() {
        if (!this.aziendaSearchQuery) {
            return this.options.aziende || [];
        }
        const query = this.aziendaSearchQuery.toLowerCase();
        return (this.options.aziende || []).filter(a =>
            a.nome.toLowerCase().includes(query)
        );
    }

    /**
     * Verifica se un'azienda è selezionata
     */
    isAziendaSelected(aziendaId: number): boolean {
        const selectedAziende = this.filtersForm.get('aziende')?.value || [];
        return selectedAziende.includes(aziendaId);
    }

    /**
     * Toggle selezione azienda
     */
    toggleAzienda(aziendaId: number): void {
        const currentAziende = this.filtersForm.get('aziende')?.value || [];
        const index = currentAziende.indexOf(aziendaId);

        if (index > -1) {
            currentAziende.splice(index, 1);
        } else {
            currentAziende.push(aziendaId);
        }

        this.filtersForm.patchValue({ aziende: currentAziende });
    }

    /**
     * Formatta il valore dello slider prezzo per display
     */
    formatPrezzoLabel(value: number): string {
        return `€${value}`;
    }

    /**
     * Conta i filtri attivi
     */
    get activeFiltersCount(): number {
        let count = 0;
        const values = this.filtersForm.value;

        if (values.tipo && values.tipo !== 'TUTTI') count++;
        if (values.aziende?.length > 0) count++;
        if (values.categorie?.length > 0) count++;
        if (values.prezzoMin > this.options.prezzoMin || values.prezzoMax < this.options.prezzoMax) count++;
        if (values.certificazioni?.length > 0) count++;
        if (values.disponibilitaSolo) count++;

        return count;
    }

    /**
     * Ottiene l'icona per la tipologia azienda
     */
    getAziendaTipologiaIcon(tipologia: string): string {
        const icons: Record<string, string> = {
            'PRODUZIONE': 'agriculture',
            'TRASFORMAZIONE': 'factory',
            'DISTRIBUZIONE': 'store'
        };
        return icons[tipologia] || 'business';
    }
}

