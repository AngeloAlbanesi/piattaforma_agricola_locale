import { Component, Input, Output, EventEmitter, ViewChild, ElementRef, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatFormFieldAppearance } from '@angular/material/form-field';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { ReactiveFormsModule, FormsModule, FormControl } from '@angular/forms';
import { debounceTime, distinctUntilChanged } from 'rxjs/operators';

@Component({
    selector: 'app-search-box',
    templateUrl: './search-box.component.html',
    styleUrls: ['./search-box.component.scss'],
    standalone: true,
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        CommonModule,
        MatInputModule,
        MatFormFieldModule,
        MatButtonModule,
        MatIconModule,
        ReactiveFormsModule,
        FormsModule
    ]
})
export class SearchBoxComponent {
    @Input() placeholder = 'Cerca...';
    @Input() label = '';
    @Input() appearance: MatFormFieldAppearance = 'outline';
    @Input() debounceTime = 300;
    @Input() showButton = true;
    @Input() buttonLabel = 'Cerca';
    @Input() buttonIcon = 'search';
    @Input() buttonColor: 'primary' | 'accent' | 'warn' = 'primary';
    @Input() initialValue = '';
    @Input() disabled = false;
    @Input() clearable = true;

    @Output() search = new EventEmitter<string>();
    @Output() clear = new EventEmitter<void>();
    @Output() focus = new EventEmitter<void>();
    @Output() blur = new EventEmitter<void>();

    @ViewChild('searchInput') searchInput!: ElementRef<HTMLInputElement>;

    searchControl = new FormControl('');

    constructor() {
        // Setup debounced search
        this.searchControl.valueChanges.pipe(
            debounceTime(this.debounceTime),
            distinctUntilChanged()
        ).subscribe(value => {
            if (value && value.trim()) {
                this.search.emit(value.trim());
            }
        });
    }

    ngOnInit(): void {
        if (this.initialValue) {
            this.searchControl.setValue(this.initialValue);
        }
    }

    onSearch(): void {
        const value = this.searchControl.value;
        if (value && value.trim()) {
            this.search.emit(value.trim());
        }
    }

    onClear(): void {
        this.searchControl.setValue('');
        this.clear.emit();
        // Focus back to input after clearing
        setTimeout(() => {
            this.searchInput.nativeElement.focus();
        }, 0);
    }

    onFocus(): void {
        this.focus.emit();
    }

    onBlur(): void {
        this.blur.emit();
    }

    onKeydown(event: KeyboardEvent): void {
        if (event.key === 'Enter') {
            this.onSearch();
        }
    }

    focusInput(): void {
        this.searchInput.nativeElement.focus();
    }

    clearInput(): void {
        this.searchControl.setValue('');
    }

    getValue(): string {
        return this.searchControl.value || '';
    }

    setValue(value: string): void {
        this.searchControl.setValue(value);
    }
}