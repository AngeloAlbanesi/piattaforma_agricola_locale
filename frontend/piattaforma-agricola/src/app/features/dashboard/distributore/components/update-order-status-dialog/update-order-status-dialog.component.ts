import { ChangeDetectionStrategy, Component, Inject, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { Subject, takeUntil } from 'rxjs';

import { OrdiniService } from '../../../../../core/services/ordini.service';
import { 
    OrdineDTO, 
    StatoOrdine, 
    UpdateStatoOrdineRequest,
    STATO_ORDINE_LABELS, 
    STATO_ORDINE_COLORS, 
    STATO_ORDINE_ICONS 
} from '../../../../../core/models/ordini.models';

export interface UpdateOrderStatusDialogData {
    ordine: OrdineDTO;
}

@Component({
    selector: 'app-update-order-status-dialog',
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
        MatProgressSpinnerModule,
        MatSnackBarModule
    ],
    templateUrl: './update-order-status-dialog.component.html',
    styleUrls: ['./update-order-status-dialog.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class UpdateOrderStatusDialogComponent implements OnInit, OnDestroy {
    private destroy$ = new Subject<void>();

    statusForm: FormGroup;
    availableStates: StatoOrdine[] = [];
    isLoading = false;

    // Constants
    readonly statoLabels = STATO_ORDINE_LABELS;
    readonly statoColors = STATO_ORDINE_COLORS;
    readonly statoIcons = STATO_ORDINE_ICONS;

    constructor(
        private fb: FormBuilder,
        private ordiniService: OrdiniService,
        private snackBar: MatSnackBar,
        private dialogRef: MatDialogRef<UpdateOrderStatusDialogComponent>,
        @Inject(MAT_DIALOG_DATA) public data: UpdateOrderStatusDialogData
    ) {
        this.statusForm = this.createForm();
    }

    ngOnInit(): void {
        this.loadAvailableStates();
    }

    ngOnDestroy(): void {
        this.destroy$.next();
        this.destroy$.complete();
    }

    private createForm(): FormGroup {
        return this.fb.group({
            nuovoStato: ['', Validators.required],
            note: ['', Validators.maxLength(500)]
        });
    }

    private loadAvailableStates(): void {
        this.availableStates = this.ordiniService.getProssimiStatiPossibili(this.data.ordine.stato);
        
        if (this.availableStates.length === 0) {
            this.snackBar.open('Nessuna transizione di stato disponibile per questo ordine', 'Chiudi', {
                duration: 3000,
                panelClass: 'warning-snackbar'
            });
            this.dialogRef.close();
        }
    }

    onSubmit(): void {
        if (this.statusForm.invalid) {
            this.markFormGroupTouched();
            return;
        }

        this.isLoading = true;
        const formData = this.statusForm.value;

        const request: UpdateStatoOrdineRequest = {
            nuovoStato: formData.nuovoStato,
            note: formData.note || undefined
        };

        this.ordiniService.updateStatoOrdine(this.data.ordine.id, request)
            .pipe(takeUntil(this.destroy$))
            .subscribe({
                next: (updatedOrder) => {
                    this.snackBar.open(
                        `Stato ordine aggiornato a: ${this.getStatoLabel(request.nuovoStato)}`,
                        'Chiudi',
                        { duration: 3000, panelClass: 'success-snackbar' }
                    );
                    this.dialogRef.close({ updated: true, order: updatedOrder });
                },
                error: (error) => {
                    console.error('Errore nell\'aggiornamento stato ordine:', error);
                    this.snackBar.open('Errore nell\'aggiornamento dello stato', 'Chiudi', {
                        duration: 3000,
                        panelClass: 'error-snackbar'
                    });
                    this.isLoading = false;
                }
            });
    }

    onCancel(): void {
        this.dialogRef.close();
    }

    private markFormGroupTouched(): void {
        Object.keys(this.statusForm.controls).forEach(key => {
            const control = this.statusForm.get(key);
            control?.markAsTouched();
        });
    }

    // Utility methods
    getStatoLabel(stato: StatoOrdine): string {
        return this.statoLabels[stato];
    }

    getStatoColor(stato: StatoOrdine): string {
        return this.statoColors[stato];
    }

    getStatoIcon(stato: StatoOrdine): string {
        return this.statoIcons[stato];
    }

    getStatusDescription(stato: StatoOrdine): string {
        const descriptions: Record<StatoOrdine, string> = {
            [StatoOrdine.RICEVUTO]: 'Ordine ricevuto e in attesa di conferma',
            [StatoOrdine.CONFERMATO]: 'Ordine confermato e pronto per essere preparato',
            [StatoOrdine.IN_PREPARAZIONE]: 'Ordine in fase di preparazione',
            [StatoOrdine.PRONTO_PER_CONSEGNA]: 'Ordine pronto per essere consegnato',
            [StatoOrdine.IN_CONSEGNA]: 'Ordine in consegna al cliente',
            [StatoOrdine.CONSEGNATO]: 'Ordine consegnato con successo',
            [StatoOrdine.ANNULLATO]: 'Ordine annullato',
            [StatoOrdine.RIMBORSATO]: 'Ordine rimborsato'
        };
        return descriptions[stato] || '';
    }

    // Getters per template
    get currentStatusLabel(): string {
        return this.getStatoLabel(this.data.ordine.stato);
    }

    get currentStatusColor(): string {
        return this.getStatoColor(this.data.ordine.stato);
    }

    get currentStatusIcon(): string {
        return this.getStatoIcon(this.data.ordine.stato);
    }

    get selectedNewStatus(): StatoOrdine | null {
        return this.statusForm.get('nuovoStato')?.value || null;
    }

    get hasSelectedStatus(): boolean {
        return !!this.selectedNewStatus;
    }

    get noteMaxLength(): number {
        return 500;
    }

    get noteCurrentLength(): number {
        return this.statusForm.get('note')?.value?.length || 0;
    }

    // Validation helpers
    hasError(controlName: string, errorType: string): boolean {
        const control = this.statusForm.get(controlName);
        return !!(control?.hasError(errorType) && control?.touched);
    }

    getErrorMessage(controlName: string): string {
        const control = this.statusForm.get(controlName);
        if (!control?.errors || !control?.touched) return '';

        if (control.hasError('required')) {
            return `${this.getFieldLabel(controlName)} è obbligatorio`;
        }
        if (control.hasError('maxlength')) {
            const maxLength = control.errors['maxlength'].requiredLength;
            return `${this.getFieldLabel(controlName)} non può superare ${maxLength} caratteri`;
        }

        return 'Campo non valido';
    }

    private getFieldLabel(controlName: string): string {
        const labels: { [key: string]: string } = {
            nuovoStato: 'Nuovo stato',
            note: 'Note'
        };
        return labels[controlName] || controlName;
    }

    // Template helpers
    trackByStateValue(index: number, stato: StatoOrdine): StatoOrdine {
        return stato;
    }
}