import { ChangeDetectionStrategy, ChangeDetectorRef, Component, OnDestroy, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Subject } from 'rxjs';
import { takeUntil, finalize } from 'rxjs/operators';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatDividerModule } from '@angular/material/divider';
import { MatChipsModule } from '@angular/material/chips';
import { MatTableModule } from '@angular/material/table';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';

import { OrdiniVenditoreService } from '../../../../../core/services/ordini-venditore.service';
import { OrdineVenditoreDetailDTO, SpedizioneRequestDTO } from '../../../../../core/models/trasformatore.models';

@Component({
    selector: 'app-ordine-venditore-detail',
    templateUrl: './ordine-venditore-detail.component.html',
    styleUrls: ['./ordine-venditore-detail.component.scss'],
    standalone: true,
    imports: [
        CommonModule,
        ReactiveFormsModule,
        MatCardModule,
        MatButtonModule,
        MatIconModule,
        MatProgressSpinnerModule,
        MatSnackBarModule,
        MatDividerModule,
        MatChipsModule,
        MatTableModule,
        MatDialogModule,
        MatFormFieldModule,
        MatInputModule
    ],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class OrdineVenditoreDetailComponent implements OnInit, OnDestroy {
    private destroy$ = new Subject<void>();

    ordine?: OrdineVenditoreDetailDTO;
    isLoading = false;
    isProcessing = false;
    showShippingForm = false;

    shippingForm: FormGroup;
    displayedColumns = ['prodotto', 'quantita', 'prezzoUnitario', 'subtotale'];

    constructor(
        private fb: FormBuilder,
        private route: ActivatedRoute,
        private router: Router,
        private ordiniService: OrdiniVenditoreService,
        private snackBar: MatSnackBar,
        private dialog: MatDialog,
        private cdr: ChangeDetectorRef
    ) {
        this.shippingForm = this.fb.group({
            trackingNumber: ['', Validators.required],
            courier: ['', Validators.required],
            estimatedDelivery: ['', Validators.required]
        });
    }

    ngOnInit(): void {
        this.route.params.pipe(takeUntil(this.destroy$)).subscribe(params => {
            const ordineId = +params['id'];
            if (ordineId) {
                this.loadOrder(ordineId);
            }
        });
    }

    ngOnDestroy(): void {
        this.destroy$.next();
        this.destroy$.complete();
    }

    /**
     * Carica i dettagli dell'ordine
     */
    loadOrder(id: number): void {
        this.isLoading = true;
        this.cdr.markForCheck();

        this.ordiniService.getOrderById(id)
            .pipe(
                takeUntil(this.destroy$),
                finalize(() => {
                    this.isLoading = false;
                    this.cdr.markForCheck();
                })
            )
            .subscribe({
                next: (ordine) => {
                    this.ordine = ordine;
                },
                error: (error) => {
                    console.error('Errore caricamento ordine:', error);
                    this.showError('Errore nel caricamento dell\'ordine');
                    this.goBack();
                }
            });
    }

    /**
     * Inizia la lavorazione dell'ordine
     */
    processOrder(): void {
        if (!this.ordine) return;

        this.isProcessing = true;
        this.cdr.markForCheck();

        this.ordiniService.processOrder(this.ordine.id)
            .pipe(
                takeUntil(this.destroy$),
                finalize(() => {
                    this.isProcessing = false;
                    this.cdr.markForCheck();
                })
            )
            .subscribe({
                next: () => {
                    this.showSuccess('Ordine in lavorazione');
                    this.loadOrder(this.ordine!.id);
                },
                error: (error) => {
                    console.error('Errore:', error);
                    this.showError('Errore nell\'aggiornamento dello stato');
                }
            });
    }

    /**
     * Mostra/nasconde il form di spedizione
     */
    toggleShippingForm(): void {
        this.showShippingForm = !this.showShippingForm;
        this.cdr.markForCheck();
    }

    /**
     * Spedisce l'ordine
     */
    shipOrder(): void {
        if (!this.ordine || this.shippingForm.invalid) {
            this.showError('Compila tutti i campi per la spedizione');
            return;
        }

        this.isProcessing = true;
        this.cdr.markForCheck();

        const request: SpedizioneRequestDTO = this.shippingForm.value;

        this.ordiniService.shipOrder(this.ordine.id, request)
            .pipe(
                takeUntil(this.destroy$),
                finalize(() => {
                    this.isProcessing = false;
                    this.cdr.markForCheck();
                })
            )
            .subscribe({
                next: () => {
                    this.showSuccess('Ordine spedito');
                    this.showShippingForm = false;
                    this.shippingForm.reset();
                    this.loadOrder(this.ordine!.id);
                },
                error: (error) => {
                    console.error('Errore:', error);
                    this.showError('Errore nella spedizione dell\'ordine');
                }
            });
    }

    /**
     * Conferma la consegna dell'ordine
     */
    deliverOrder(): void {
        if (!this.ordine) return;

        if (!confirm('Confermare la consegna dell\'ordine?')) {
            return;
        }

        this.isProcessing = true;
        this.cdr.markForCheck();

        this.ordiniService.deliverOrder(this.ordine.id)
            .pipe(
                takeUntil(this.destroy$),
                finalize(() => {
                    this.isProcessing = false;
                    this.cdr.markForCheck();
                })
            )
            .subscribe({
                next: () => {
                    this.showSuccess('Consegna confermata');
                    this.loadOrder(this.ordine!.id);
                },
                error: (error) => {
                    console.error('Errore:', error);
                    this.showError('Errore nella conferma della consegna');
                }
            });
    }

    /**
     * Annulla l'ordine
     */
    cancelOrder(): void {
        if (!this.ordine) return;

        const motivo = prompt('Inserisci il motivo dell\'annullamento:');
        if (!motivo) {
            return;
        }

        this.isProcessing = true;
        this.cdr.markForCheck();

        this.ordiniService.cancelOrder(this.ordine.id)
            .pipe(
                takeUntil(this.destroy$),
                finalize(() => {
                    this.isProcessing = false;
                    this.cdr.markForCheck();
                })
            )
            .subscribe({
                next: () => {
                    this.showSuccess('Ordine annullato');
                    this.loadOrder(this.ordine!.id);
                },
                error: (error) => {
                    console.error('Errore:', error);
                    this.showError('Errore nell\'annullamento dell\'ordine');
                }
            });
    }

    /**
     * Torna indietro
     */
    goBack(): void {
        this.router.navigate(['..'], { relativeTo: this.route });
    }

    /**
     * Formatta il prezzo
     */
    formatPrice(price: number): string {
        return this.ordiniService.formatCurrency(price);
    }

    /**
     * Formatta la data
     */
    formatDate(date: string): string {
        return this.ordiniService.formatDate(date);
    }

    /**
     * Ottiene l'etichetta dello stato
     */
    getStatoLabel(stato: string): string {
        return this.ordiniService.getStatoLabel(stato);
    }

    /**
     * Ottiene il colore dello stato
     */
    getStatoColor(stato: string) {
        return this.ordiniService.getStatoColor(stato);
    }

    /**
     * Mostra messaggio di successo
     */
    private showSuccess(message: string): void {
        this.snackBar.open(message, 'Chiudi', {
            duration: 3000,
            horizontalPosition: 'end',
            verticalPosition: 'bottom',
            panelClass: ['success-snackbar']
        });
    }

    /**
     * Mostra messaggio di errore
     */
    private showError(message: string): void {
        this.snackBar.open(message, 'Chiudi', {
            duration: 5000,
            horizontalPosition: 'end',
            verticalPosition: 'bottom',
            panelClass: ['error-snackbar']
        });
    }

    // === GETTERS ===

    get canProcess(): boolean {
        return this.ordine ? this.ordiniService.canProcessOrder(this.ordine.stato) : false;
    }

    get canShip(): boolean {
        return this.ordine ? this.ordiniService.canShipOrder(this.ordine.stato) : false;
    }

    get canDeliver(): boolean {
        return this.ordine ? this.ordiniService.canDeliverOrder(this.ordine.stato) : false;
    }

    get canCancel(): boolean {
        return this.ordine ? this.ordiniService.canCancelOrder(this.ordine.stato) : false;
    }
}

