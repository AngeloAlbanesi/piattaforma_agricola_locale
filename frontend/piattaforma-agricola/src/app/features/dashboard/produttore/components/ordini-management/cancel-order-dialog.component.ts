import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatDialogRef, MatDialogModule } from '@angular/material/dialog';
import { FormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';

@Component({
    standalone: true,
    selector: 'app-cancel-order-dialog',
    imports: [CommonModule, MatDialogModule, FormsModule, MatFormFieldModule, MatInputModule, MatButtonModule],
    template: `
    <h2 mat-dialog-title>Motivo Annullamento</h2>
    <mat-dialog-content>
      <mat-form-field appearance="outline" style="width:100%">
        <mat-label>Motivo</mat-label>
        <input matInput [(ngModel)]="motivo" />
      </mat-form-field>
    </mat-dialog-content>
    <mat-dialog-actions align="end">
      <button mat-button (click)="onCancel()">Annulla</button>
      <button mat-flat-button color="warn" (click)="onConfirm()" [disabled]="!motivo">Conferma annullamento</button>
    </mat-dialog-actions>
  `
})
export class CancelOrderDialogComponent {
    motivo = '';

    constructor(private dialogRef: MatDialogRef<CancelOrderDialogComponent>) { }

    onConfirm(): void {
        this.dialogRef.close({ motivoAnnullamento: this.motivo });
    }

    onCancel(): void {
        this.dialogRef.close(null);
    }
}
