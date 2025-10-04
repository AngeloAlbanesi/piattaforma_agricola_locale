import { ChangeDetectionStrategy, Component, Inject, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatSelectModule } from '@angular/material/select';
import { MatChipsModule } from '@angular/material/chips';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { Subject, takeUntil } from 'rxjs';

import { DistributoreService } from '../../../../../core/services/distributore.service';
import { ProdottiService } from '../../../../../core/services/prodotti.service';
import { 
    PacchettoTipicitaDTO, 
    CreatePacchettoRequestDTO, 
    UpdatePacchettoRequestDTO 
} from '../../../../../core/models/distributore.models';
import { ProductSelectorComponent } from '../product-selector/product-selector.component';

export interface PackageFormDialogData {
    mode: 'create' | 'edit';
    package?: PacchettoTipicitaDTO;
}

export interface SelectedProduct {
    id: number;
    nome: string;
    prezzo: number;
    quantita: number;
    maxQuantita?: number;
}

@Component({
    selector: 'app-package-form-dialog',
    standalone: true,
    imports: [
        CommonModule,
        ReactiveFormsModule,
        MatDialogModule,
        MatFormFieldModule,
        MatInputModule,
        MatButtonModule,
        MatIconModule,
        MatSelectModule,
        MatChipsModule,
        MatProgressSpinnerModule,
        MatSnackBarModule,
        ProductSelectorComponent
    ],
    templateUrl: './package-form-dialog.component.html',
    styleUrls: ['./package-form-dialog.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class PackageFormDialogComponent implements OnInit, OnDestroy {
    private destroy$ = new Subject<void>();

    packageForm: FormGroup;
    selectedProducts: SelectedProduct[] = [];
    isLoading = false;
    isEditMode: boolean;

    constructor(
        private fb: FormBuilder,
        private distributoreService: DistributoreService,
        private prodottiService: ProdottiService,
        private snackBar: MatSnackBar,
        private dialogRef: MatDialogRef<PackageFormDialogComponent>,
        @Inject(MAT_DIALOG_DATA) public data: PackageFormDialogData
    ) {
        this.isEditMode = data.mode === 'edit';
        this.packageForm = this.createForm();
    }

    ngOnInit(): void {
        if (this.isEditMode && this.data.package) {
            this.populateFormForEdit();
        }
    }

    ngOnDestroy(): void {
        this.destroy$.next();
        this.destroy$.complete();
    }

    private createForm(): FormGroup {
        return this.fb.group({
            nome: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(100)]],
            descrizione: ['', [Validators.required, Validators.minLength(10), Validators.maxLength(500)]],
            prezzo: [0, [Validators.required, Validators.min(0.01)]]
        });
    }

    private populateFormForEdit(): void {
        if (!this.data.package) return;

        const pkg = this.data.package;
        this.packageForm.patchValue({
            nome: pkg.nome,
            descrizione: pkg.descrizione,
            prezzo: pkg.prezzo
        });

        // Popoliamo i prodotti selezionati
        this.selectedProducts = pkg.prodotti.map(p => ({
            id: p.id,
            nome: p.nome,
            prezzo: p.prezzo,
            quantita: p.quantita
        }));
    }

    onProductsSelected(products: SelectedProduct[]): void {
        this.selectedProducts = products;
        this.updateTotalPrice();
    }

    onProductQuantityChanged(productId: number, newQuantity: number): void {
        const product = this.selectedProducts.find(p => p.id === productId);
        if (product) {
            product.quantita = newQuantity;
            this.updateTotalPrice();
        }
    }

    removeProduct(productId: number): void {
        this.selectedProducts = this.selectedProducts.filter(p => p.id !== productId);
        this.updateTotalPrice();
    }

    private updateTotalPrice(): void {
        const totalProductsPrice = this.selectedProducts.reduce(
            (sum, product) => sum + (product.prezzo * product.quantita), 
            0
        );
        
        // Aggiungiamo un margine del 20% sul prezzo dei prodotti come prezzo suggerito
        const suggestedPrice = Math.round(totalProductsPrice * 1.2 * 100) / 100;
        
        this.packageForm.patchValue({
            prezzo: suggestedPrice
        });
    }

    onSubmit(): void {
        if (this.packageForm.invalid || this.selectedProducts.length === 0) {
            this.markFormGroupTouched();
            
            if (this.selectedProducts.length === 0) {
                this.snackBar.open('Seleziona almeno un prodotto per il pacchetto', 'Chiudi', {
                    duration: 3000,
                    panelClass: 'error-snackbar'
                });
            }
            return;
        }

        this.isLoading = true;
        const formData = this.packageForm.value;

        if (this.isEditMode) {
            this.updatePackage(formData);
        } else {
            this.createPackage(formData);
        }
    }

    private createPackage(formData: any): void {
        const request: CreatePacchettoRequestDTO = {
            nome: formData.nome,
            descrizione: formData.descrizione,
            prezzo: formData.prezzo,
            prodotti: this.selectedProducts.map(p => ({
                id: p.id,
                quantita: p.quantita
            }))
        };

        this.distributoreService.createPackage(request)
            .pipe(takeUntil(this.destroy$))
            .subscribe({
                next: (result) => {
                    this.snackBar.open('Pacchetto creato con successo!', 'Chiudi', {
                        duration: 3000,
                        panelClass: 'success-snackbar'
                    });
                    this.dialogRef.close(result);
                },
                error: (error) => {
                    console.error('Errore nella creazione del pacchetto:', error);
                    this.snackBar.open('Errore nella creazione del pacchetto', 'Chiudi', {
                        duration: 3000,
                        panelClass: 'error-snackbar'
                    });
                    this.isLoading = false;
                }
            });
    }

    private updatePackage(formData: any): void {
        if (!this.data.package) return;

        const request: UpdatePacchettoRequestDTO = {
            nome: formData.nome,
            descrizione: formData.descrizione,
            prezzo: formData.prezzo,
            prodotti: this.selectedProducts.map(p => ({
                id: p.id,
                quantita: p.quantita
            }))
        };

        this.distributoreService.updatePackage(this.data.package.id, request)
            .pipe(takeUntil(this.destroy$))
            .subscribe({
                next: (result) => {
                    this.snackBar.open('Pacchetto aggiornato con successo!', 'Chiudi', {
                        duration: 3000,
                        panelClass: 'success-snackbar'
                    });
                    this.dialogRef.close(result);
                },
                error: (error) => {
                    console.error('Errore nell\'aggiornamento del pacchetto:', error);
                    this.snackBar.open('Errore nell\'aggiornamento del pacchetto', 'Chiudi', {
                        duration: 3000,
                        panelClass: 'error-snackbar'
                    });
                    this.isLoading = false;
                }
            });
    }

    private markFormGroupTouched(): void {
        Object.keys(this.packageForm.controls).forEach(key => {
            const control = this.packageForm.get(key);
            control?.markAsTouched();
        });
    }

    onCancel(): void {
        this.dialogRef.close();
    }

    // Getters per template
    get dialogTitle(): string {
        return this.isEditMode ? 'Modifica Pacchetto' : 'Crea Nuovo Pacchetto';
    }

    get submitButtonText(): string {
        return this.isEditMode ? 'Aggiorna' : 'Crea';
    }

    get totalProducts(): number {
        return this.selectedProducts.length;
    }

    get totalQuantity(): number {
        return this.selectedProducts.reduce((sum, p) => sum + p.quantita, 0);
    }

    get estimatedProductsCost(): number {
        return this.selectedProducts.reduce((sum, p) => sum + (p.prezzo * p.quantita), 0);
    }

    formatCurrency(value: number): string {
        return this.distributoreService.formatCurrency(value);
    }

    // Validatori di template
    hasError(controlName: string, errorType: string): boolean {
        const control = this.packageForm.get(controlName);
        return !!(control?.hasError(errorType) && control?.touched);
    }

    getErrorMessage(controlName: string): string {
        const control = this.packageForm.get(controlName);
        if (!control?.errors || !control?.touched) return '';

        if (control.hasError('required')) {
            return `${this.getFieldLabel(controlName)} è obbligatorio`;
        }
        if (control.hasError('minlength')) {
            const minLength = control.errors['minlength'].requiredLength;
            return `${this.getFieldLabel(controlName)} deve essere di almeno ${minLength} caratteri`;
        }
        if (control.hasError('maxlength')) {
            const maxLength = control.errors['maxlength'].requiredLength;
            return `${this.getFieldLabel(controlName)} non può superare ${maxLength} caratteri`;
        }
        if (control.hasError('min')) {
            return `${this.getFieldLabel(controlName)} deve essere maggiore di 0`;
        }

        return 'Campo non valido';
    }

    private getFieldLabel(controlName: string): string {
        const labels: { [key: string]: string } = {
            nome: 'Nome',
            descrizione: 'Descrizione',
            prezzo: 'Prezzo'
        };
        return labels[controlName] || controlName;
    }

    // TrackBy functions
    trackByProductId(index: number, product: SelectedProduct): number {
        return product.id;
    }
}