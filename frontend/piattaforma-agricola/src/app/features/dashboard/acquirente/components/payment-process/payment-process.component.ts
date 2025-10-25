import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { FormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatRadioModule } from '@angular/material/radio';
import { MatStepperModule } from '@angular/material/stepper';
import { MatListModule } from '@angular/material/list';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Subject, takeUntil, finalize } from 'rxjs';
import { Router } from '@angular/router';

import { AcquirenteService } from '../../../../../core/services/acquirente.service';
import {
    PagamentoRequestDTO,
    CreateOrdineRequestDTO,
    OrdineDetailDTO,
    MetodoPagamento,
    CarrelloDTO
} from '../../../../../core/models/acquirente.models';

@Component({
    selector: 'app-payment-process',
    standalone: true,
    imports: [
        CommonModule,
        ReactiveFormsModule,
        FormsModule,
        MatCardModule,
        MatFormFieldModule,
        MatInputModule,
        MatButtonModule,
        MatIconModule,
        MatProgressSpinnerModule,
        MatRadioModule,
        MatStepperModule,
        MatListModule,
        MatCheckboxModule
    ],
    templateUrl: './payment-process.component.html',
    styleUrls: ['./payment-process.component.scss']
})
export class PaymentProcessComponent implements OnInit, OnDestroy {
    private destroy$ = new Subject<void>();

    paymentForm: FormGroup = new FormGroup({});
    cardForm: FormGroup = new FormGroup({});
    paypalForm: FormGroup = new FormGroup({});
    cartForm: FormGroup = new FormGroup({});

    cart: CarrelloDTO | null = null;
    isProcessing = false;
    currentStep = 0;
    selectedMethod: MetodoPagamento = MetodoPagamento.CARTA_CREDITO;

    createdOrders: OrdineDetailDTO[] = [];
    paymentCompleted = false;
    showMultiOrderDialog = false;
    termsAccepted = false;

    // Enum per accesso nel template
    MetodoPagamento = MetodoPagamento;

    constructor(
        private fb: FormBuilder,
        private acquirenteService: AcquirenteService,
        private snackBar: MatSnackBar,
        private router: Router
    ) {
        this.initializeForms();
    }

    ngOnInit(): void {
        this.loadCart();
    }

    ngOnDestroy(): void {
        this.destroy$.next();
        this.destroy$.complete();
    }

    // === INIZIALIZZAZIONE ===

    private initializeForms(): void {
        // Form per il carrello (semplice validazione)
        this.cartForm = this.fb.group({});

        // Form principale per la creazione dell'ordine
        this.paymentForm = this.fb.group({
            metodoPagamento: [MetodoPagamento.CARTA_CREDITO, Validators.required]
        });

        // Form per carta di credito
        this.cardForm = this.fb.group({
            numero: ['', [Validators.required, Validators.pattern('^\\d{16}$')]],
            intestatario: ['', Validators.required],
            dataScadenza: ['', [Validators.required, Validators.pattern('^(0[1-9]|1[0-2])\\/\\d{2}$')]],
            cvv: ['', [Validators.required, Validators.pattern('^\\d{3}$')]]
        });

        // Form per PayPal
        this.paypalForm = this.fb.group({
            email: ['', [Validators.required, Validators.email]],
            password: ['', Validators.required]
        });

        // Ascolta i cambiamenti del metodo di pagamento
        this.paymentForm.get('metodoPagamento')?.valueChanges.subscribe(method => {
            this.selectedMethod = method;
            this.validatePaymentForms();
        });
    }

    private loadCart(): void {
        this.acquirenteService.getCart().subscribe({
            next: (cart) => {
                this.cart = cart;
            },
            error: (error) => {
                console.error('Errore nel caricamento del carrello:', error);
                this.snackBar.open('Errore nel caricamento del carrello', 'Chiudi', {
                    duration: 3000,
                    panelClass: 'error-snackbar'
                });
            }
        });
    }

    // === GESTIONE PAGAMENTO ===

    createOrder(): void {
        if (this.paymentForm.invalid) {
            this.markFormGroupTouched(this.paymentForm);
            return;
        }

        this.isProcessing = true;
        const orderRequest: CreateOrdineRequestDTO = {
            metodoPagamento: this.paymentForm.value.metodoPagamento
        };

        this.acquirenteService.createOrderFromCart(orderRequest)
            .pipe(
                takeUntil(this.destroy$),
                finalize(() => this.isProcessing = false)
            )
            .subscribe({
                next: (orders) => {
                    this.createdOrders = orders;
                    this.currentStep = 1; // Passa al passo di pagamento
                    this.snackBar.open('Ordini creati con successo', 'Chiudi', {
                        duration: 3000,
                        panelClass: 'success-snackbar'
                    });
                },
                error: (error) => {
                    this.snackBar.open('Errore nella creazione degli ordini', 'Chiudi', {
                        duration: 3000,
                        panelClass: 'error-snackbar'
                    });
                }
            });
    }

    processPayment(): void {
        // Valida il form specifico per il metodo di pagamento selezionato
        const formToValidate = this.selectedMethod === MetodoPagamento.CARTA_CREDITO ? this.cardForm : this.paypalForm;

        if (formToValidate.invalid) {
            this.markFormGroupTouched(formToValidate);
            return;
        }

        this.isProcessing = true;

        // Processa ogni ordine creato
        const paymentPromises = this.createdOrders.map(order => {
            const paymentRequest: PagamentoRequestDTO = this.buildPaymentRequest();
            return this.acquirenteService.confirmOrderPayment(order.id, paymentRequest);
        });

        // Esegui tutti i pagamenti in parallelo
        Promise.all(paymentPromises)
            .then(results => {
                this.paymentCompleted = true;
                this.currentStep = 2; // Passa alla conferma
                this.snackBar.open('Pagamento completato con successo', 'Chiudi', {
                    duration: 3000,
                    panelClass: 'success-snackbar'
                });
            })
            .catch(error => {
                this.snackBar.open('Errore durante il processo di pagamento', 'Chiudi', {
                    duration: 3000,
                    panelClass: 'error-snackbar'
                });
            })
            .finally(() => {
                this.isProcessing = false;
            });
    }

    private buildPaymentRequest(): PagamentoRequestDTO {
        if (this.selectedMethod === MetodoPagamento.CARTA_CREDITO) {
            return {
                metodoPagamento: MetodoPagamento.CARTA_CREDITO,
                datiCartaCredito: {
                    numeroCartaCredito: this.cardForm.value.numero,
                    intestatario: this.cardForm.value.intestatario,
                    dataScadenza: this.cardForm.value.dataScadenza,
                    cvv: this.cardForm.value.cvv
                }
            };
        } else {
            return {
                metodoPagamento: MetodoPagamento.PAYPAL,
                datiPayPal: {
                    emailPayPal: this.paypalForm.value.email,
                    passwordPayPal: this.paypalForm.value.password
                }
            };
        }
    }

    // === NAVIGAZIONE ===

    previousStep(): void {
        if (this.currentStep > 0) {
            this.currentStep--;
        }
    }

    resetPayment(): void {
        this.currentStep = 0;
        this.createdOrders = [];
        this.paymentCompleted = false;
        this.paymentForm.reset();
        this.cardForm.reset();
        this.paypalForm.reset();
    }

    goToOrders(): void {
        // this.router.navigate(['/ordini']);
        this.snackBar.open('Reindirizzamento agli ordini...', 'Chiudi', {
            duration: 2000,
            panelClass: 'info-snackbar'
        });
    }

    // === METODI DI VALIDAZIONE ===

    validatePaymentForms(): void {
        if (this.selectedMethod === MetodoPagamento.CARTA_CREDITO) {
            this.cardForm.updateValueAndValidity();
        } else {
            this.paypalForm.updateValueAndValidity();
        }
    }

    private markFormGroupTouched(formGroup: FormGroup): void {
        Object.values(formGroup.controls).forEach(control => {
            control.markAsTouched();
            control.updateValueAndValidity();
        });
    }

    // === UTILITIES ===

    getCartTotal(): number {
        if (!this.cart || !this.cart.elementiCarrello) return 0;
        return this.cart.elementiCarrello.reduce((sum, item) => sum + (item.prezzoUnitario * item.quantita), 0);
    }

    formatCardNumber(cardNumber: string): string {
        if (!cardNumber) return '';
        // Nasconde tutti i numeri tranne gli ultimi 4
        return '**** **** **** ' + cardNumber.slice(-4);
    }

    formatExpiryDate(expiry: string): string {
        if (!expiry) return '';
        // Formatta MM/YY -> MM/AAAA
        const [month, year] = expiry.split('/');
        return `${month}/20${year}`;
    }

    // === GETTERS PER TEMPLATE ===

    get isCartaCredito(): boolean {
        return this.selectedMethod === MetodoPagamento.CARTA_CREDITO;
    }

    get isPayPal(): boolean {
        return this.selectedMethod === MetodoPagamento.PAYPAL;
    }

    get isFormValid(): boolean {
        const mainFormValid = this.paymentForm.valid;
        const paymentFormValid = this.selectedMethod === MetodoPagamento.CARTA_CREDITO
            ? this.cardForm.valid
            : this.paypalForm.valid;

        return mainFormValid && paymentFormValid;
    }

    get totalAmount(): number {
        return this.createdOrders.reduce((total, order) => total + order.totale, 0);
    }

    get ordersCount(): number {
        return this.createdOrders.length;
    }

    // Metodi helper per il template
    isPaymentFormValid(): boolean {
        const metodoPagamento = this.paymentForm.value.metodoPagamento;

        if (metodoPagamento === MetodoPagamento.CARTA_CREDITO) {
            return this.cardForm.valid;
        } else if (metodoPagamento === MetodoPagamento.PAYPAL) {
            return this.paypalForm.valid;
        }

        return false;
    }

    getPaymentMethodLabel(): string {
        const metodoPagamento = this.paymentForm.value.metodoPagamento;

        switch (metodoPagamento) {
            case MetodoPagamento.CARTA_CREDITO:
                return 'Carta di Credito';
            case MetodoPagamento.PAYPAL:
                return 'PayPal';
            default:
                return '';
        }
    }

    getTotalPaymentAmount(): number {
        return this.createdOrders.reduce((total, order) => total + order.totale, 0);
    }

    cancelMultiOrderPayment(): void {
        this.showMultiOrderDialog = false;
        this.createdOrders = [];
        this.isProcessing = false;
    }

    confirmMultiOrderPayment(): void {
        this.isProcessing = true;

        // Prepara i dati di pagamento
        const pagamentoData = this.preparePaymentData();

        // Esegui il pagamento per tutti gli ordini
        this.executeMultiOrderPayment(pagamentoData);
    }

    private preparePaymentData(): PagamentoRequestDTO {
        return this.buildPaymentRequest();
    }

    private executeMultiOrderPayment(pagamentoData: PagamentoRequestDTO): void {
        // Processa ogni ordine singolarmente
        const paymentPromises = this.createdOrders.map(order => {
            return this.acquirenteService.confirmOrderPayment(order.id, pagamentoData).toPromise();
        });

        Promise.all(paymentPromises)
            .then(() => {
                this.showSuccess('Pagamento completato con successo!');
                this.showMultiOrderDialog = false;

                // Svuota il carrello dopo il pagamento
                this.acquirenteService.clearCart().subscribe(() => {
                    this.loadCart();
                });

                // Reindirizza alla pagina degli ordini
                setTimeout(() => {
                    this.router.navigate(['/dashboard/acquirente/ordini']);
                }, 2000);
            })
            .catch((error) => {
                console.error('Errore durante il pagamento:', error);
                this.showError('Errore durante il pagamento: ' + (error.message || 'Errore sconosciuto'));
                this.isProcessing = false;
            });
    }

    private showSuccess(message: string): void {
        this.snackBar.open(message, 'Chiudi', {
            duration: 3000,
            panelClass: 'success-snackbar'
        });
    }

    private showError(message: string): void {
        this.snackBar.open(message, 'Chiudi', {
            duration: 3000,
            panelClass: 'error-snackbar'
        });
    }
}