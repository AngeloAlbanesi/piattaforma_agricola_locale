import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatTableModule } from '@angular/material/table';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Subject, takeUntil, finalize } from 'rxjs';

import { AcquirenteService } from '../../../../../core/services/acquirente.service';
import { 
  CarrelloDTO, 
  RigaCarrelloDTO, 
  UpdateCartItemRequestDTO 
} from '../../../../../core/models/acquirente.models';

@Component({
  selector: 'app-shopping-cart',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatTableModule,
    MatPaginatorModule
  ],
  templateUrl: './shopping-cart.component.html',
  styleUrls: ['./shopping-cart.component.scss']
})
export class ShoppingCartComponent implements OnInit, OnDestroy {
  private destroy$ = new Subject<void>();
  
  displayedColumns: string[] = ['prodotto', 'prezzo', 'quantita', 'totale', 'azioni'];
  cartData: CarrelloDTO | null = null;
  isLoading = false;
  isUpdating = false;

  constructor(
    private acquirenteService: AcquirenteService,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.loadCart();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  // === GESTIONE CARRELLO ===

  loadCart(): void {
    this.isLoading = true;

    this.acquirenteService.getCart()
      .pipe(
        takeUntil(this.destroy$),
        finalize(() => this.isLoading = false)
      )
      .subscribe({
        next: (cart) => {
          this.cartData = cart;
        },
        error: (error) => {
          this.snackBar.open('Errore nel caricamento del carrello', 'Chiudi', {
            duration: 3000,
            panelClass: 'error-snackbar'
          });
        }
      });
  }

  updateQuantity(rigaId: number, newQuantity: number): void {
    if (newQuantity < 1 || !this.cartData) return;

    this.isUpdating = true;
    const request: UpdateCartItemRequestDTO = { quantita: newQuantity };

    this.acquirenteService.updateCartItemQuantity(rigaId, request)
      .pipe(
        takeUntil(this.destroy$),
        finalize(() => this.isUpdating = false)
      )
      .subscribe({
        next: (updatedCart) => {
          this.cartData = updatedCart;
          this.snackBar.open('Quantità aggiornata', 'Chiudi', {
            duration: 2000,
            panelClass: 'success-snackbar'
          });
        },
        error: (error) => {
          this.snackBar.open('Errore nell\'aggiornamento della quantità', 'Chiudi', {
            duration: 3000,
            panelClass: 'error-snackbar'
          });
        }
      });
  }

  removeItem(rigaId: number): void {
    if (!this.cartData) return;

    this.isUpdating = true;

    this.acquirenteService.removeCartItem(rigaId)
      .pipe(
        takeUntil(this.destroy$),
        finalize(() => this.isUpdating = false)
      )
      .subscribe({
        next: (updatedCart) => {
          this.cartData = updatedCart;
          this.snackBar.open('Articolo rimosso dal carrello', 'Chiudi', {
            duration: 2000,
            panelClass: 'success-snackbar'
          });
        },
        error: (error) => {
          this.snackBar.open('Errore nella rimozione dell\'articolo', 'Chiudi', {
            duration: 3000,
            panelClass: 'error-snackbar'
          });
        }
      });
  }

  clearCart(): void {
    if (!this.cartData || this.cartData.righeCarrello.length === 0) return;

    if (confirm('Sei sicuro di voler svuotare completamente il carrello?')) {
      this.isUpdating = true;

      this.acquirenteService.clearCart()
        .pipe(
          takeUntil(this.destroy$),
          finalize(() => this.isUpdating = false)
        )
        .subscribe({
          next: () => {
            this.cartData = null;
            this.snackBar.open('Carrello svuotato', 'Chiudi', {
              duration: 2000,
              panelClass: 'success-snackbar'
            });
          },
          error: (error) => {
            this.snackBar.open('Errore nello svuotamento del carrello', 'Chiudi', {
              duration: 3000,
              panelClass: 'error-snackbar'
            });
          }
        });
    }
  }

  proceedToCheckout(): void {
    // Naviga alla pagina di checkout
    // this.router.navigate(['/checkout']);
    this.snackBar.open('Funzionalità di checkout in sviluppo', 'Chiudi', {
      duration: 3000,
      panelClass: 'info-snackbar'
    });
  }

  // === UTILITIES ===

  formatCurrency(value: number): string {
    return new Intl.NumberFormat('it-IT', {
      style: 'currency',
      currency: 'EUR'
    }).format(value);
  }

  getProductImage(riga: RigaCarrelloDTO): string {
    return riga.acquistabile.immagineUrl || '/assets/images/default-product.png';
  }

  // === GETTERS PER TEMPLATE ===

  get isCartEmpty(): boolean {
    return !this.cartData || this.cartData.righeCarrello.length === 0;
  }

  get cartTotal(): number {
    return this.cartData?.totale || 0;
  }

  get cartItemsCount(): number {
    return this.cartData?.numeroArticoli || 0;
  }

  onImageError(event: Event): void {
    const img = event.target as HTMLImageElement;
    img.src = '/assets/images/default-product.png';
  }
}