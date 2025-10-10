import { Component, Input, Output, EventEmitter, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatSelectModule } from '@angular/material/select';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatButtonToggleModule } from '@angular/material/button-toggle';
import { MatTooltipModule } from '@angular/material/tooltip';

import { CatalogSortBy, CatalogViewMode } from '../../../../core/models/catalog.models';

/**
 * Componente per l'header di ordinamento e selezione vista
 */
@Component({
    selector: 'app-catalog-sort-header',
    standalone: true,
    imports: [
        CommonModule,
        MatButtonModule,
        MatIconModule,
        MatSelectModule,
        MatFormFieldModule,
        MatButtonToggleModule,
        MatTooltipModule
    ],
    templateUrl: './catalog-sort-header.component.html',
    styleUrls: ['./catalog-sort-header.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class CatalogSortHeaderComponent {
    @Input() totalResults = 0;
    @Input() currentPage = 0;
    @Input() pageSize = 20;
    @Input() sortBy: CatalogSortBy = 'nome_asc';
    @Input() viewMode: CatalogViewMode = 'grid';

    @Output() sortChange = new EventEmitter<CatalogSortBy>();
    @Output() viewModeChange = new EventEmitter<CatalogViewMode>();

    sortOptions: Array<{ value: CatalogSortBy; label: string }> = [
        { value: 'nome_asc', label: 'Nome (A-Z)' },
        { value: 'nome_desc', label: 'Nome (Z-A)' },
        { value: 'prezzo_asc', label: 'Prezzo (crescente)' },
        { value: 'prezzo_desc', label: 'Prezzo (decrescente)' },
        { value: 'disponibilita_desc', label: 'Disponibilità' },
        { value: 'sconto_desc', label: 'Sconto' }
    ];

    /**
     * Gestisce il cambio di ordinamento
     */
    onSortChange(newSort: CatalogSortBy): void {
        this.sortChange.emit(newSort);
    }

    /**
     * Gestisce il cambio di modalità vista
     */
    onViewModeChange(newMode: CatalogViewMode): void {
        this.viewModeChange.emit(newMode);
    }

    /**
     * Ottiene il range di risultati visualizzati
     */
    get resultsRange(): string {
        if (this.totalResults === 0) {
            return 'Nessun risultato';
        }

        const start = this.currentPage * this.pageSize + 1;
        const end = Math.min((this.currentPage + 1) * this.pageSize, this.totalResults);

        return `${start}-${end} di ${this.totalResults}`;
    }
}

