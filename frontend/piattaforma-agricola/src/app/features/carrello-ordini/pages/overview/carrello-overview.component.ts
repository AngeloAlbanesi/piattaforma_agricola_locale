import { ChangeDetectionStrategy, ChangeDetectorRef, Component, OnDestroy, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { Subject } from 'rxjs';
import { takeUntil, finalize } from 'rxjs/operators';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatDividerModule } from '@angular/material/divider';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';

import { AcquirenteService } from '../../../../core/services/acquirente.service';
import { CarrelloDTO, RigaCarrelloDTO, UpdateCartItemRequestDTO } from '../../../../core/models/acquirente.models';

@Component({
    selector: 'app-carrello-overview',
    templateUrl: './carrello-overview.component.html',
    styleUrls: ['./carrello-overview.component.scss'],
    standalone: true,
    imports: [
        CommonModule,
        MatCardModule,
        MatButtonModule,
        MatIconModule,
        MatProgressSpinnerModule,
        MatSnackBarModule,
        MatDividerModule,
        MatTooltipModule,
        MatDialogModule
    ],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class CarrelloOverviewComponent implements OnInit, OnDestroy {
    private destroy$ = new Subject<void>();

    carrello?: CarrelloDTO;
    isLoading = false;
    isUpdating = false;

    constructor(
        private acquirenteService: AcquirenteService,
        private router: Router,
        private snackBar: MatSnackBar,
        private dialog: MatDialog,
        private cdr: ChangeDetectorRef
    ) { }

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
                },
                error: (error) => {
                    console.error('Errore caricamento carrello:', error);
                    this.showError('Errore nel caricamento del carrello');
                }
            });
    }

    /**
     * Aggiorna la quantità di un elemento
     */
    updateQuantity(riga: RigaCarrelloDTO, delta: number): void {
        const newQuantity = riga.quantita + delta;

        if (newQuantity < 1) {
            return; // Non permettere quantità < 1
        }

        this.isUpdating = true;
        this.cdr.markForCheck();

        const request: UpdateCartItemRequestDTO = {
            quantita: newQuantity
        };

        this.acquirenteService.updateCartItemQuantity(riga.idElemento, request)
            .pipe(
                takeUntil(this.destroy$),
                finalize(() => {
                    this.isUpdating = false;
                    this.cdr.markForCheck();
                })
            )
            .subscribe({
                next: (carrello) => {
                    this.carrello = carrello;
                    this.showSuccess('Quantità aggiornata');
                },
                error: (error) => {
                    console.error('Errore aggiornamento quantità:', error);
                    this.showError('Errore nell\'aggiornamento della quantità');
                }
            });
    }

    /**
     * Rimuove un elemento dal carrello
     */
    removeItem(riga: RigaCarrelloDTO): void {
        if (!confirm(`Vuoi rimuovere ${riga.nomeAcquistabile} dal carrello?`)) {
            return;
        }

        this.isUpdating = true;
        this.cdr.markForCheck();

        this.acquirenteService.removeCartItem(riga.idElemento)
            .pipe(
                takeUntil(this.destroy$),
                finalize(() => {
                    this.isUpdating = false;
                    this.cdr.markForCheck();
                })
            )
            .subscribe({
                next: (carrello) => {
                    this.carrello = carrello;
                    this.showSuccess('Elemento rimosso dal carrello');
                },
                error: (error) => {
                    console.error('Errore rimozione elemento:', error);
                    this.showError('Errore nella rimozione dell\'elemento');
                }
            });
    }

    /**
     * Svuota il carrello
     */
    clearCart(): void {
        if (!confirm('Vuoi svuotare completamente il carrello?')) {
            return;
        }

        this.isUpdating = true;
        this.cdr.markForCheck();

        this.acquirenteService.clearCart()
            .pipe(
                takeUntil(this.destroy$),
                finalize(() => {
                    this.isUpdating = false;
                    this.cdr.markForCheck();
                })
            )
            .subscribe({
                next: () => {
                    this.carrello = undefined;
                    this.showSuccess('Carrello svuotato');
                },
                error: (error) => {
                    console.error('Errore svuotamento carrello:', error);
                    this.showError('Errore nello svuotamento del carrello');
                }
            });
    }

    /**
     * Procedi al checkout
     */
    proceedToCheckout(): void {
        this.router.navigate(['/carrello-ordini/checkout']);
    }

    /**
     * Torna al catalogo
     */
    continueShopping(): void {
        this.router.navigate(['/catalogo']);
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
     * Ottiene l'URL dell'immagine o un placeholder
     */
    getImageUrl(immagineUrl?: string): string {
        return immagineUrl || 'assets/images/placeholder-product.png';
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

    get isEmpty(): boolean {
        return !this.carrello || !this.carrello.elementiCarrello || this.carrello.elementiCarrello.length === 0;
    }

    get hasItems(): boolean {
        return !this.isEmpty;
    }

    get totalItems(): number {
        return this.carrello?.totalElementi || 0;
    }

    get totalPrice(): number {
        if (!this.carrello || !this.carrello.elementiCarrello) return 0;
        return this.carrello.elementiCarrello.reduce((sum, item) => sum + (item.prezzoUnitario * item.quantita), 0);
    }
}
