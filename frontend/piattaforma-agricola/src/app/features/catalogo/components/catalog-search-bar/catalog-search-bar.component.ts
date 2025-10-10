import { Component, Output, EventEmitter, OnInit, OnDestroy, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { Subject } from 'rxjs';
import { debounceTime, distinctUntilChanged, takeUntil } from 'rxjs/operators';

/**
 * Componente per la barra di ricerca del catalogo
 */
@Component({
    selector: 'app-catalog-search-bar',
    standalone: true,
    imports: [
        CommonModule,
        ReactiveFormsModule,
        MatFormFieldModule,
        MatInputModule,
        MatIconModule,
        MatButtonModule,
        MatAutocompleteModule
    ],
    templateUrl: './catalog-search-bar.component.html',
    styleUrls: ['./catalog-search-bar.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class CatalogSearchBarComponent implements OnInit, OnDestroy {
    @Output() search = new EventEmitter<string>();
    @Output() clear = new EventEmitter<void>();

    searchControl = new FormControl('');
    private destroy$ = new Subject<void>();

    ngOnInit(): void {
        // Emette la ricerca con debounce
        this.searchControl.valueChanges.pipe(
            debounceTime(300),
            distinctUntilChanged(),
            takeUntil(this.destroy$)
        ).subscribe(value => {
            this.search.emit(value || '');
        });
    }

    ngOnDestroy(): void {
        this.destroy$.next();
        this.destroy$.complete();
    }

    /**
     * Pulisce il campo di ricerca
     */
    onClear(): void {
        this.searchControl.setValue('');
        this.clear.emit();
    }

    /**
     * Verifica se c'è una query di ricerca
     */
    get hasQuery(): boolean {
        return !!(this.searchControl.value && this.searchControl.value.length > 0);
    }
}

