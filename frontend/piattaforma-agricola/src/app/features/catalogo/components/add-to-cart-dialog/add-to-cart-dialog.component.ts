import { Component, Inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';

export interface AddToCartDialogData {
    nome: string;
    prezzo: number;
    quantitaDisponibile: number;
    immagine?: string;
}

export interface AddToCartDialogResult {
    quantita: number;
}

@Component({
    selector: 'app-add-to-cart-dialog',
    standalone: true,
    imports: [
        CommonModule,
        FormsModule,
        MatDialogModule,
        MatButtonModule,
        MatFormFieldModule,
        MatInputModule,
        MatIconModule
    ],
    templateUrl: './add-to-cart-dialog.component.html',
    styleUrls: ['./add-to-cart-dialog.component.scss']
})
export class AddToCartDialogComponent {
    quantita = 1;

    constructor(
        public dialogRef: MatDialogRef<AddToCartDialogComponent>,
        @Inject(MAT_DIALOG_DATA) public data: AddToCartDialogData
    ) {
        // Inizializza quantità a 1, ma non più della disponibilità
        this.quantita = Math.min(1, this.data.quantitaDisponibile);
    }

    /**
     * Incrementa la quantità
     */
    incrementQuantity(): void {
        if (this.quantita < this.data.quantitaDisponibile) {
            this.quantita++;
        }
    }

    /**
     * Decrementa la quantità
     */
    decrementQuantity(): void {
        if (this.quantita > 1) {
            this.quantita--;
        }
    }

    /**
     * Verifica se la quantità è valida
     */
    isQuantityValid(): boolean {
        return this.quantita >= 1 && this.quantita <= this.data.quantitaDisponibile;
    }

    /**
     * Conferma l'aggiunta al carrello
     */
    onConfirm(): void {
        if (this.isQuantityValid()) {
            const result: AddToCartDialogResult = {
                quantita: this.quantita
            };
            this.dialogRef.close(result);
        }
    }

    /**
     * Annulla l'operazione
     */
    onCancel(): void {
        this.dialogRef.close();
    }
}

