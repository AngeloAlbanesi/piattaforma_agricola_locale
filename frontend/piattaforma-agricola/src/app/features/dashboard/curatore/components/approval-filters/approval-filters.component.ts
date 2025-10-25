import { Component, EventEmitter, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MatCardModule } from '@angular/material/card';
import { MatExpansionModule } from '@angular/material/expansion';
import { ApprovazioneFilters } from '../../../../../core/models/curatore.models';

@Component({
    selector: 'app-approval-filters',
    standalone: true,
    imports: [
        CommonModule,
        FormsModule,
        ReactiveFormsModule,
        MatFormFieldModule,
        MatInputModule,
        MatSelectModule,
        MatButtonModule,
        MatIconModule,
        MatDatepickerModule,
        MatNativeDateModule,
        MatCardModule,
        MatExpansionModule
    ],
    templateUrl: './approval-filters.component.html',
    styleUrls: ['./approval-filters.component.scss']
})
export class ApprovalFiltersComponent {
    @Output() filtersChanged = new EventEmitter<ApprovazioneFilters>();
    @Output() filtersReset = new EventEmitter<void>();

    filterForm: FormGroup;
    isExpanded = false;

    statoOptions = [
        { value: 'TUTTI', label: 'Tutti gli stati' },
        { value: 'IN_ATTESA', label: 'In Attesa' },
        { value: 'APPROVATO', label: 'Approvato' },
        { value: 'RIFIUTATO', label: 'Rifiutato' }
    ];

    tipoOptions = [
        { value: 'TUTTI', label: 'Tutti i tipi' },
        { value: 'PRODOTTO', label: 'Prodotti' },
        { value: 'AZIENDA', label: 'Aziende' },
        { value: 'CONTENUTO', label: 'Contenuti' }
    ];

    constructor(private fb: FormBuilder) {
        this.filterForm = this.fb.group({
            search: [''],
            stato: ['IN_ATTESA'],
            tipo: ['TUTTI'],
            dataDa: [null],
            dataA: [null]
        });
    }

    onApplyFilters(): void {
        const formValue = this.filterForm.value;
        const filters: ApprovazioneFilters = {
            search: formValue.search || undefined,
            stato: formValue.stato !== 'TUTTI' ? formValue.stato : undefined,
            tipo: formValue.tipo !== 'TUTTI' ? formValue.tipo : undefined,
            dataDa: formValue.dataDa ? this.formatDate(formValue.dataDa) : undefined,
            dataA: formValue.dataA ? this.formatDate(formValue.dataA) : undefined
        };

        this.filtersChanged.emit(filters);
    }

    onResetFilters(): void {
        this.filterForm.reset({
            search: '',
            stato: 'IN_ATTESA',
            tipo: 'TUTTI',
            dataDa: null,
            dataA: null
        });
        this.filtersReset.emit();
    }

    onSearchChange(): void {
        // Auto-apply on search input (with debounce in real scenario)
        const searchValue = this.filterForm.get('search')?.value;
        if (searchValue === '' || searchValue.length >= 3) {
            this.onApplyFilters();
        }
    }

    toggleExpanded(): void {
        this.isExpanded = !this.isExpanded;
    }

    private formatDate(date: Date): string {
        return date.toISOString().split('T')[0];
    }

    get hasActiveFilters(): boolean {
        const formValue = this.filterForm.value;
        return !!(
            formValue.search ||
            (formValue.stato && formValue.stato !== 'IN_ATTESA') ||
            (formValue.tipo && formValue.tipo !== 'TUTTI') ||
            formValue.dataDa ||
            formValue.dataA
        );
    }
}

