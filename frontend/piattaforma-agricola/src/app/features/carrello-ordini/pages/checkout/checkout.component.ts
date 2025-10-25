import { ChangeDetectionStrategy, ChangeDetectorRef, Component, OnDestroy, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { Subject } from 'rxjs';
import { takeUntil, finalize } from 'rxjs/operators';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatStepperModule } from '@angular/material/stepper';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatRadioModule } from '@angular/material/radio';
import { MatDividerModule } from '@angular/material/divider';

import { AcquirenteService } from '../../../../core/services/acquirente.service';
import {
    CarrelloDTO,
    CreateOrdineRequestDTO,
    PagamentoRequestDTO,
    DatiCartaCreditoDTO,
    DatiPayPalDTO,
    OrdineDetailDTO
} from '../../../../core/models/acquirente.models';

@Component({
    selector: 'app-checkout',
    templateUrl: './checkout.component.html',
    styleUrls: ['./checkout.component.scss'],
    standalone: true,
    imports: [
        CommonModule,
        ReactiveFormsModule,
        MatCardModule,
        MatButtonModule,
        MatIconModule,
        MatProgressSpinnerModule,
        MatSnackBarModule,
        MatStepperModule,
        MatFormFieldModule,
        MatInputModule,
        MatRadioModule,
        MatDividerModule
    ],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class CheckoutComponent implements OnInit, OnDestroy {
    private destroy$ = new Subject<void>();

    carrello?: CarrelloDTO;
    isLoading = false;
    isProcessing = false;
    ordiniCreati: OrdineDetailDTO[] = [];
    checkoutCompleted = false;

    // Forms
    paymentMethodForm: FormGroup;
    creditCardForm: FormGroup;
    paypalForm: FormGroup;
    notesForm: FormGroup;

    constructor(
        private fb: FormBuilder,
        private acquirenteService: AcquirenteService,
        private router: Router,
        private snackBar: MatSnackBar,
        private cdr: ChangeDetectorRef
    ) {
        // Payment Method Form
        this.paymentMethodForm = this.fb.group({
            metodoPagamento: ['SIMULATO', Validators.required]
        });

        // Credit Card Form
        this.creditCardForm = this.fb.group({
            numeroCartaCredito: ['', [Validators.required, Validators.pattern(/^\d{16}$/)]],
            intestatario: ['', Validators.required],
            dataScadenza: ['', [Validators.required, Validators.pattern(/^\d{2}\/\d{2}$/)]],
            cvv: ['', [Validators.required, Validators.pattern(/^\d{3}$/)]]
        });

        // PayPal Form
        this.paypalForm = this.fb.group({
            emailPayPal: ['', [Validators.required, Validators.email]],
            passwordPayPal: ['', Validators.required]
        });

        // Notes Form
        this.notesForm = this.fb.group({
            noteAggiuntive: ['']
        });
    }

    ngOnInit(): void {
        this.loadCart();
    }

    ngOnDestroy(): void {
        this.destroy$.next();
        this.destroy$.complete();
    }

    /**
     * Carica il carrello
     */
    loadCart(): void {
        this.isLoading = true;
        this.cdr.markForCheck();

        this.acquirenteService.getCart()
            .pipe(
                takeUntil(this.destroy$),
                finalize(() => {
                    this.isLoading = false;
                    this.cdr.markForCheck();
                })
            )
            .subscribe({
                next: (carrello) => {
                    this.carrello = carrello;

                    // Se il carrello è vuoto, torna indietro
                    if (!carrello || !carrello.elementiCarrello || carrello.elementiCarrello.length === 0) {
                        this.showError('Il carrello è vuoto');
                        this.router.navigate(['/carrello-ordini/overview']);
                    }
                },
                error: (error) => {
                    console.error('Errore caricamento carrello:', error);
                    this.showError('Errore nel caricamento del carrello');
                    this.router.navigate(['/carrello-ordini/overview']);
                }
            });
    }

    /**
     * Processa il checkout
     */
    processCheckout(): void {
        if (this.paymentMethodForm.invalid) {
            this.showError('Seleziona un metodo di pagamento');
            return;
        }

        const metodoPagamento = this.paymentMethodForm.value.metodoPagamento;

        // Validazione form specifici
        if (metodoPagamento === 'CARTA_CREDITO' && this.creditCardForm.invalid) {
            this.showError('Compila tutti i campi della carta di credito');
            return;
        }

        if (metodoPagamento === 'PAYPAL' && this.paypalForm.invalid) {
            this.showError('Compila tutti i campi PayPal');
            return;
        }

        this.isProcessing = true;
        this.cdr.markForCheck();

        // Step 1: Crea ordini
        const createOrderRequest: CreateOrdineRequestDTO = {
            metodoPagamento: metodoPagamento,
            noteAggiuntive: this.notesForm.value.noteAggiuntive || undefined
        };

        this.acquirenteService.createOrderFromCart(createOrderRequest)
            .pipe(
                takeUntil(this.destroy$),
                finalize(() => {
                    this.isProcessing = false;
                    this.cdr.markForCheck();
                })
            )
            .subscribe({
                next: (ordini) => {
                    this.ordiniCreati = ordini;

                    // Step 2: Se non è simulato, processa i pagamenti
                    if (metodoPagamento !== 'SIMULATO') {
                        this.processPayments(ordini, metodoPagamento);
                    } else {
                        this.completeCheckout();
                    }
                },
                error: (error) => {
                    console.error('Errore creazione ordini:', error);
                    this.showError(error.message || 'Errore nella creazione degli ordini');
                }
            });
    }

    /**
     * Processa i pagamenti per gli ordini creati
     */
    private processPayments(ordini: OrdineDetailDTO[], metodoPagamento: string): void {
        let paymentRequest: PagamentoRequestDTO;

        if (metodoPagamento === 'CARTA_CREDITO') {
            const datiCarta: DatiCartaCreditoDTO = this.creditCardForm.value;
            paymentRequest = {
                metodoPagamento: 'CARTA_CREDITO',
                datiCartaCredito: datiCarta
            };
        } else if (metodoPagamento === 'PAYPAL') {
            const datiPayPal: DatiPayPalDTO = this.paypalForm.value;
            paymentRequest = {
                metodoPagamento: 'PAYPAL',
                datiPayPal: datiPayPal
            };
        } else {
            this.completeCheckout();
            return;
        }

        // Processa pagamento per ogni ordine
        let completed = 0;
        const total = ordini.length;

        ordini.forEach(ordine => {
            this.acquirenteService.confirmOrderPayment(ordine.id, paymentRequest)
                .pipe(takeUntil(this.destroy$))
                .subscribe({
                    next: () => {
                        completed++;
                        if (completed === total) {
                            this.completeCheckout();
                        }
                    },
                    error: (error) => {
                        console.error(`Errore pagamento ordine ${ordine.id}:`, error);
                        this.showError(`Errore nel pagamento dell'ordine ${ordine.id}`);
                    }
                });
        });
    }

    /**
     * Completa il checkout
     */
    private completeCheckout(): void {
        this.checkoutCompleted = true;
        this.cdr.markForCheck();
        this.showSuccess('Ordine completato con successo!');
    }

    /**
     * Visualizza ordini
     */
    viewOrders(): void {
        this.router.navigate(['/dashboard/acquirente']);
    }

    /**
     * Torna al catalogo
     */
    backToCatalog(): void {
        this.router.navigate(['/catalogo']);
    }

    /**
     * Torna al carrello
     */
    backToCart(): void {
        this.router.navigate(['/carrello-ordini/overview']);
    }

    /**
     * Formatta il prezzo
     */
    formatPrice(price: number): string {
        return new Intl.NumberFormat('it-IT', {
            style: 'currency',
            currency: 'EUR',
            minimumFractionDigits: 2,
            maximumFractionDigits: 2
        }).format(price);
    }

    /**
     * Mostra messaggio di successo
     */
    private showSuccess(message: string): void {
        this.snackBar.open(message, 'Chiudi', {
            duration: 5000,
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

    get selectedPaymentMethod(): string {
        return this.paymentMethodForm.value.metodoPagamento;
    }

    get totalPrice(): number {
        if (!this.carrello || !this.carrello.elementiCarrello) return 0;
        return this.carrello.elementiCarrello.reduce((sum, item) => sum + (item.prezzoUnitario * item.quantita), 0);
    }

    get totalItems(): number {
        return this.carrello?.totalElementi || 0;
    }
}

