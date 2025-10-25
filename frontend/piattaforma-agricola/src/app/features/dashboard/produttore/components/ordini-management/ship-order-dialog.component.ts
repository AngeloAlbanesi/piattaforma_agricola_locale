import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatDialogRef, MatDialogModule } from '@angular/material/dialog';
import { FormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';

@Component({
    standalone: true,
    selector: 'app-ship-order-dialog',
    imports: [CommonModule, MatDialogModule, FormsModule, MatFormFieldModule, MatInputModule, MatButtonModule],
    template: `
    <h2 mat-dialog-title>Dettagli Spedizione</h2>
    <mat-dialog-content>
      <mat-form-field appearance="outline" style="width:100%">
        <mat-label>Tracking number</mat-label>
        <input matInput [(ngModel)]="trackingNumber" />
      </mat-form-field>

      <mat-form-field appearance="outline" style="width:100%">
        <mat-label>Corriere</mat-label>
        <input matInput [(ngModel)]="carrier" />
      </mat-form-field>

      <mat-form-field appearance="outline" style="width:100%">
        <mat-label>Data prevista consegna (YYYY-MM-DD)</mat-label>
        <input matInput [(ngModel)]="estimatedDeliveryDate" placeholder="2025-10-01" />
      </mat-form-field>
    </mat-dialog-content>
    <mat-dialog-actions align="end">
      <button mat-button (click)="onCancel()">Annulla</button>
      <button mat-flat-button color="primary" (click)="onConfirm()" [disabled]="!isValid()">Conferma</button>
    </mat-dialog-actions>
  `
})
export class ShipOrderDialogComponent {
    trackingNumber = '';
    carrier = '';
    estimatedDeliveryDate = '';

    constructor(private dialogRef: MatDialogRef<ShipOrderDialogComponent>) { }

    isValid(): boolean {
        return !!this.trackingNumber && !!this.carrier && !!this.estimatedDeliveryDate;
    }

    onConfirm(): void {
        this.dialogRef.close({ trackingNumber: this.trackingNumber, carrier: this.carrier, estimatedDeliveryDate: this.estimatedDeliveryDate });
    }

    onCancel(): void {
        this.dialogRef.close(null);
    }
}
