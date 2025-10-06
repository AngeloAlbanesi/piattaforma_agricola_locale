import { Component, Inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MAT_DIALOG_DATA, MatDialogRef, MatDialogModule } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { CertificationDTO } from '@core/models/produttore.models';

@Component({
    selector: 'app-remove-certification-confirmation-dialog',
    standalone: true,
    imports: [
        CommonModule,
        MatDialogModule,
        MatButtonModule,
        MatIconModule
    ],
    template: `
    <h2 mat-dialog-title>
      <mat-icon color="warn">warning</mat-icon>
      Conferma Rimozione Certificazione
    </h2>

    <mat-dialog-content>
      <p class="warning-message">
        Sei sicuro di voler rimuovere la certificazione <strong>{{ data.nomeCertificazione }}</strong>?
      </p>
      <p class="info-message">
        Questa operazione non può essere annullata.
      </p>
    </mat-dialog-content>

    <mat-dialog-actions align="end">
      <button mat-button (click)="onCancel()">Annulla</button>
      <button mat-raised-button color="warn" (click)="onConfirm()">
        <mat-icon>delete</mat-icon>
        Rimuovi
      </button>
    </mat-dialog-actions>
  `,
    styles: [`
    h2 {
      display: flex;
      align-items: center;
      gap: 8px;
    }

    .warning-message {
      font-size: 16px;
      margin-bottom: 12px;
    }

    .info-message {
      color: #666;
      font-size: 14px;
    }

    mat-dialog-actions button {
      display: flex;
      align-items: center;
      gap: 4px;
    }
  `]
})
export class RemoveCertificationConfirmationDialogComponent {
    constructor(
        private dialogRef: MatDialogRef<RemoveCertificationConfirmationDialogComponent>,
        @Inject(MAT_DIALOG_DATA) public data: CertificationDTO
    ) { }

    onConfirm(): void {
        this.dialogRef.close(true);
    }

    onCancel(): void {
        this.dialogRef.close(false);
    }
}

