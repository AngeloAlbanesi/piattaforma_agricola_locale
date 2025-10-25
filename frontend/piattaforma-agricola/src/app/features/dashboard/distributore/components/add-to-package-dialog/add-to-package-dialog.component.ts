import { ChangeDetectionStrategy, Component, Inject, OnInit, OnDestroy, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatRadioModule } from '@angular/material/radio';
import { Subject, takeUntil } from 'rxjs';

import { DistributoreService } from '../../../../../core/services/distributore.service';
import { ProdottoSummaryDTO } from '../../../../../core/models/common.models';
import { PacchettoTipicitaDTO } from '../../../../../core/models/distributore.models';

export interface AddToPackageDialogData {
    product: ProdottoSummaryDTO;
}

export interface AddToPackageRequest {
    packageId: number;
    quantity: number;
    action: 'new' | 'existing';
}

@Component({
    selector: 'app-add-to-package-dialog',
    standalone: true,
    imports: [
        CommonModule,
        ReactiveFormsModule,
        MatDialogModule,
        MatFormFieldModule,
        MatInputModule,
        MatSelectModule,
        MatButtonModule,
        MatIconModule,
        MatCardModule,
        MatProgressSpinnerModule,
        MatSnackBarModule,
        MatRadioModule
    ],
    templateUrl: './add-to-package-dialog.component.html',
    styleUrls: ['./add-to-package-dialog.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class AddToPackageDialogComponent implements OnInit, OnDestroy {
    private destroy$ = new Subject<void>();

    addForm: FormGroup;
    availablePackages: PacchettoTipicitaDTO[] = [];
    isLoading = false;
    isLoadingPackages = true;

    constructor(
        private fb: FormBuilder,
        private distributoreService: DistributoreService,
        private snackBar: MatSnackBar,
        private cdr: ChangeDetectorRef,
        private dialogRef: MatDialogRef<AddToPackageDialogComponent>,
        @Inject(MAT_DIALOG_DATA) public data: AddToPackageDialogData
    ) {
        this.addForm = this.createForm();
    }

    ngOnInit(): void {
        this.loadAvailablePackages();
    }

    ngOnDestroy(): void {
        this.destroy$.next();
        this.destroy$.complete();
    }

    private createForm(): FormGroup {
        return this.fb.group({
            action: ['existing', Validators.required],
            packageId: [null],
            quantity: [1, [Validators.required, Validators.min(1)]]
        });
    }

    private loadAvailablePackages(): void {
        this.isLoadingPackages = true;

        this.distributoreService.getMyPackages()
            .pipe(takeUntil(this.destroy$))
            .subscribe({
                next: (packages) => {
                    // Filtro solo i pacchetti in progettazione o attivi
                    this.availablePackages = packages.filter(p => 
                        p.stato === 'IN_PROGETTAZIONE' || p.stato === 'ATTIVO'
                    );
                    
                    // Se ci sono pacchetti disponibili, seleziono il primo
                    if (this.availablePackages.length > 0) {
                        this.addForm.patchValue({
                            packageId: this.availablePackages[0].id
                        });
                    }
                    
                    this.isLoadingPackages = false;
                    this.cdr.markForCheck();
                },
                error: (error) => {
                    console.error('Errore nel caricamento pacchetti:', error);
                    this.snackBar.open('Errore nel caricamento dei pacchetti', 'Chiudi', {
                        duration: 3000,
                        panelClass: 'error-snackbar'
                    });
                    this.isLoadingPackages = false;
                    this.cdr.markForCheck();
                }
            });
    }

    onActionChange(): void {
        const action = this.addForm.get('action')?.value;
        const packageIdControl = this.addForm.get('packageId');

        if (action === 'existing') {
            packageIdControl?.setValidators([Validators.required]);
            if (this.availablePackages.length > 0) {
                packageIdControl?.setValue(this.availablePackages[0].id);
            }
        } else {
            packageIdControl?.clearValidators();
            packageIdControl?.setValue(null);
        }
        
        packageIdControl?.updateValueAndValidity();
    }

    onQuantityChange(): void {
        const quantity = this.addForm.get('quantity')?.value || 0;
        const maxQuantity = this.data.product.quantitaDisponibile;

        if (quantity > maxQuantity) {
            this.addForm.patchValue({ quantity: maxQuantity });
            this.snackBar.open(
                `Quantità massima disponibile: ${maxQuantity}`,
                'Chiudi',
                { duration: 3000, panelClass: 'warning-snackbar' }
            );
        }
    }

    onSubmit(): void {
        if (this.addForm.invalid) {
            this.markFormGroupTouched();
            return;
        }

        this.isLoading = true;
        const formData = this.addForm.value;

        if (formData.action === 'existing') {
            this.addToExistingPackage(formData);
        } else {
            this.createNewPackageWithProduct(formData);
        }
    }

    private addToExistingPackage(formData: any): void {
        const packageId = formData.packageId;
        const quantity = formData.quantity;

        this.distributoreService.addProductToPackage(packageId, this.data.product.id, quantity)
            .pipe(takeUntil(this.destroy$))
            .subscribe({
                next: () => {
                    const selectedPackage = this.availablePackages.find(p => p.id === packageId);
                    this.snackBar.open(
                        `Prodotto aggiunto al pacchetto "${selectedPackage?.nome}"`,
                        'Chiudi',
                        { duration: 3000, panelClass: 'success-snackbar' }
                    );
                    this.dialogRef.close({ success: true, action: 'existing', packageId, quantity });
                },
                error: (error) => {
                    console.error('Errore nell\'aggiunta del prodotto al pacchetto:', error);
                    this.snackBar.open(
                        'Errore nell\'aggiunta del prodotto al pacchetto',
                        'Chiudi',
                        { duration: 3000, panelClass: 'error-snackbar' }
                    );
                    this.isLoading = false;
                    this.cdr.markForCheck();
                }
            });
    }

    private createNewPackageWithProduct(formData: any): void {
        // Questa funzionalità richiederebbe un dialog per creare un nuovo pacchetto
        // Per ora mostriamo un messaggio che reindirizza all'azione
        this.snackBar.open(
            'Usa il pulsante "Crea Pacchetto" nella dashboard per creare un nuovo pacchetto',
            'Chiudi',
            { duration: 5000, panelClass: 'info-snackbar' }
        );
        this.dialogRef.close({ success: false, action: 'redirect-to-create' });
    }

    onCancel(): void {
        this.dialogRef.close({ success: false });
    }

    private markFormGroupTouched(): void {
        Object.keys(this.addForm.controls).forEach(key => {
            const control = this.addForm.get(key);
            control?.markAsTouched();
        });
    }

    // Utility methods
    formatCurrency(value: number): string {
        return this.distributoreService.formatCurrency(value);
    }

    getPackageStatusColor(stato: string): string {
        switch (stato) {
            case 'ATTIVO':
                return '#27ae60';
            case 'IN_PROGETTAZIONE':
                return '#f39c12';
            case 'INATTIVO':
                return '#e74c3c';
            default:
                return '#95a5a6';
        }
    }

    getPackageStatusLabel(stato: string): string {
        switch (stato) {
            case 'ATTIVO':
                return 'Attivo';
            case 'IN_PROGETTAZIONE':
                return 'In Progettazione';
            case 'INATTIVO':
                return 'Inattivo';
            default:
                return stato;
        }
    }

    // Getters per template
    get selectedAction(): string {
        return this.addForm.get('action')?.value || 'existing';
    }

    get isExistingAction(): boolean {
        return this.selectedAction === 'existing';
    }

    get isNewAction(): boolean {
        return this.selectedAction === 'new';
    }

    get hasAvailablePackages(): boolean {
        return this.availablePackages.length > 0;
    }

    get selectedQuantity(): number {
        return this.addForm.get('quantity')?.value || 1;
    }

    get totalPrice(): number {
        return this.data.product.prezzo * this.selectedQuantity;
    }

    get maxQuantity(): number {
        return this.data.product.quantitaDisponibile;
    }

    get quantityValidationMessage(): string {
        const control = this.addForm.get('quantity');
        if (!control?.errors || !control?.touched) return '';

        if (control.hasError('required')) {
            return 'La quantità è obbligatoria';
        }
        if (control.hasError('min')) {
            return 'La quantità deve essere almeno 1';
        }

        return 'Quantità non valida';
    }

    get packageValidationMessage(): string {
        const control = this.addForm.get('packageId');
        if (!control?.errors || !control?.touched) return '';

        if (control.hasError('required')) {
            return 'Seleziona un pacchetto';
        }

        return 'Selezione non valida';
    }
}