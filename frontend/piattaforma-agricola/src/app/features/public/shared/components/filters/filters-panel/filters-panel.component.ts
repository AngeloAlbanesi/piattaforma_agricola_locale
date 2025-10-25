import { Component, Input, Output, EventEmitter, ChangeDetectionStrategy, OnChanges, SimpleChanges } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { MatInputModule } from '@angular/material/input';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatSliderModule } from '@angular/material/slider';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatCommonModule } from '@angular/material/core';
import { ReactiveFormsModule, FormBuilder, FormGroup, FormControl } from '@angular/forms';
import { debounceTime } from 'rxjs/operators';

export interface FilterOption {
    value: string;
    label: string;
}

export interface RangeFilter {
    min: number;
    max: number;
}

@Component({
    selector: 'app-filters-panel',
    templateUrl: './filters-panel.component.html',
    styleUrls: ['./filters-panel.component.scss'],
    standalone: true,
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        CommonModule,
        MatFormFieldModule,
        MatSelectModule,
        MatInputModule,
        MatCheckboxModule,
        MatSliderModule,
        MatButtonModule,
        MatIconModule,
        MatExpansionModule,
        MatCommonModule,
        ReactiveFormsModule
    ]
})
export class FiltersPanelComponent implements OnChanges {
    @Input() title = 'Filtri';
    @Input() expanded = true;
    @Input() showReset = true;
    @Input() collapsible = true;

    // Text search filter
    @Input() searchPlaceholder = 'Cerca...';
    @Input() searchLabel = 'Ricerca';

    // Select filters
    @Input() categoryOptions: FilterOption[] = [];
    @Input() categoryLabel = 'Categoria';
    @Input() categoryPlaceholder = 'Seleziona categoria';

    @Input() sortOptions: FilterOption[] = [];
    @Input() sortLabel = 'Ordina per';
    @Input() sortPlaceholder = 'Seleziona ordinamento';

    // Range filters
    @Input() priceRange: RangeFilter = { min: 0, max: 1000 };
    @Input() priceRangeLabel = 'Prezzo';
    @Input() priceRangeStep = 10;

    // Checkbox filters
    @Input() checkboxOptions: FilterOption[] = [];

    @Output() filtersChange = new EventEmitter<any>();
    @Output() reset = new EventEmitter<void>();

    filtersForm: FormGroup;

    constructor(private fb: FormBuilder) {
        this.filtersForm = this.fb.group({
            search: [''],
            category: [''],
            sortBy: [''],
            priceMin: [this.priceRange.min],
            priceMax: [this.priceRange.max],
            checkboxes: this.fb.group({})
        });

        // Setup form change detection with debounce
        this.filtersForm.valueChanges.pipe(
            debounceTime(300)
        ).subscribe(value => {
            this.emitFiltersChange();
        });
    }

    ngOnChanges(changes: SimpleChanges): void {
        if (changes['checkboxOptions']) {
            this.updateCheckboxGroup();
        }

        if (changes['priceRange']) {
            this.updatePriceRange();
        }
    }

    private updateCheckboxGroup(): void {
        const checkboxGroup = this.filtersForm.get('checkboxes') as FormGroup;

        // Remove existing controls
        Object.keys(checkboxGroup.controls).forEach(key => {
            checkboxGroup.removeControl(key);
        });

        // Add new controls
        this.checkboxOptions.forEach(option => {
            checkboxGroup.addControl(option.value, this.fb.control(false));
        });
    }

    private updatePriceRange(): void {
        this.filtersForm.patchValue({
            priceMin: this.priceRange.min,
            priceMax: this.priceRange.max
        });
    }

    onReset(): void {
        this.filtersForm.reset({
            search: '',
            category: '',
            sortBy: '',
            priceMin: this.priceRange.min,
            priceMax: this.priceRange.max
        });

        // Reset checkboxes
        const checkboxGroup = this.filtersForm.get('checkboxes') as FormGroup;
        Object.keys(checkboxGroup.controls).forEach(key => {
            checkboxGroup.get(key)?.setValue(false);
        });

        this.reset.emit();
    }

    onCategoryChange(): void {
        this.emitFiltersChange();
    }

    onSortChange(): void {
        this.emitFiltersChange();
    }

    onPriceRangeChange(): void {
        this.emitFiltersChange();
    }

    onCheckboxChange(): void {
        this.emitFiltersChange();
    }

    onSearchChange(): void {
        this.emitFiltersChange();
    }

    private emitFiltersChange(): void {
        const formValue = this.filtersForm.value;
        const filters: any = {};

        // Add search filter
        if (formValue.search && formValue.search.trim()) {
            filters.search = formValue.search.trim();
        }

        // Add category filter
        if (formValue.category) {
            filters.category = formValue.category;
        }

        // Add sort filter
        if (formValue.sortBy) {
            filters.sortBy = formValue.sortBy;
        }

        // Add price range filter
        if (formValue.priceMin > this.priceRange.min || formValue.priceMax < this.priceRange.max) {
            filters.priceMin = formValue.priceMin;
            filters.priceMax = formValue.priceMax;
        }

        // Add checkbox filters
        const checkboxGroup = formValue.checkboxes as any;
        const activeCheckboxes = Object.keys(checkboxGroup)
            .filter(key => checkboxGroup[key])
            .map(key => key);

        if (activeCheckboxes.length > 0) {
            filters.checkboxes = activeCheckboxes;
        }

        this.filtersChange.emit(filters);
    }

    isAnyFilterActive(): boolean {
        const formValue = this.filtersForm.value;

        return !!(
            (formValue.search && formValue.search.trim()) ||
            formValue.category ||
            formValue.sortBy ||
            formValue.priceMin > this.priceRange.min ||
            formValue.priceMax < this.priceRange.max ||
            Object.values(formValue.checkboxes as any).some(value => value)
        );
    }

    getActiveFiltersCount(): number {
        const formValue = this.filtersForm.value;
        let count = 0;

        if (formValue.search && formValue.search.trim()) count++;
        if (formValue.category) count++;
        if (formValue.sortBy) count++;
        if (formValue.priceMin > this.priceRange.min || formValue.priceMax < this.priceRange.max) count++;

        count += Object.values(formValue.checkboxes as any).filter(value => value).length;

        return count;
    }

    getFormattedPriceRange(): string {
        const min = this.filtersForm.get('priceMin')?.value || this.priceRange.min;
        const max = this.filtersForm.get('priceMax')?.value || this.priceRange.max;

        if (min === this.priceRange.min && max === this.priceRange.max) {
            return 'Qualsiasi prezzo';
        }

        return `€${min} - €${max}`;
    }
}