import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableModule, MatTableDataSource } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatChipsModule } from '@angular/material/chips';
import { MatCardModule } from '@angular/material/card';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { OrdiniVenditoreService } from '@core/services/ordini-venditore.service';
import { OrdineVenditoreDTO } from '@core/models/trasformatore.models';

@Component({
    selector: 'app-ordini-management',
    standalone: true,
    imports: [
        CommonModule,
        MatTableModule,
        MatButtonModule,
        MatIconModule,
        MatChipsModule,
        MatCardModule,
        MatProgressSpinnerModule,
        MatSnackBarModule,
        MatDialogModule
    ],
    template: `
    <mat-card>
      <mat-card-header>
        <mat-card-title>
          <mat-icon>shopping_cart</mat-icon>
          Ordini Ricevuti
        </mat-card-title>
      </mat-card-header>
      <mat-card-content>
        @if (isLoading) {
          <div class="loading"><mat-spinner></mat-spinner></div>
        } @else {
          <table mat-table [dataSource]="dataSource" class="orders-table">
            <ng-container matColumnDef="numeroOrdine">
              <th mat-header-cell *matHeaderCellDef>Numero Ordine</th>
              <td mat-cell *matCellDef="let order">#{{ order.idOrdine }}</td>
            </ng-container>
            <ng-container matColumnDef="cliente">
              <th mat-header-cell *matHeaderCellDef>Cliente</th>
              <td mat-cell *matCellDef="let order">{{ order.nomeAcquirente }}</td>
            </ng-container>
            <ng-container matColumnDef="totale">
              <th mat-header-cell *matHeaderCellDef>Totale</th>
              <td mat-cell *matCellDef="let order">{{ formatCurrency(order.importoTotale) }}</td>
            </ng-container>
            <ng-container matColumnDef="stato">
              <th mat-header-cell *matHeaderCellDef>Stato</th>
              <td mat-cell *matCellDef="let order">
                <mat-chip [color]="getStatoColor(order.statoCorrente)">{{ getStatoLabel(order.statoCorrente) }}</mat-chip>
              </td>
            </ng-container>
            <ng-container matColumnDef="actions">
              <th mat-header-cell *matHeaderCellDef>Azioni</th>
              <td mat-cell *matCellDef="let order">
                <button mat-icon-button (click)="viewOrder(order)"><mat-icon>visibility</mat-icon></button>
                @if (canProcessOrder(order.statoCorrente)) {
                  <button mat-icon-button color="primary" (click)="processOrder(order)"><mat-icon>play_arrow</mat-icon></button>
                }
              </td>
            </ng-container>
            <tr mat-header-row *matHeaderRowDef="displayedColumns"></tr>
            <tr mat-row *matRowDef="let row; columns: displayedColumns;"></tr>
          </table>
        }
      </mat-card-content>
    </mat-card>
  `,
    styles: [`
    .loading { display: flex; justify-content: center; padding: 2rem; }
    .orders-table { width: 100%; }
    mat-card-title { display: flex; align-items: center; gap: 0.5rem; }
  `]
})
export class OrdiniManagementComponent implements OnInit {
    displayedColumns = ['numeroOrdine', 'cliente', 'totale', 'stato', 'actions'];
    dataSource = new MatTableDataSource<OrdineVenditoreDTO>([]);
    isLoading = false;

    constructor(
        private ordiniService: OrdiniVenditoreService,
        private snackBar: MatSnackBar,
        private router: Router
    ) { }

    ngOnInit(): void {
        this.loadOrders();
    }

    loadOrders(): void {
        this.isLoading = true;
        this.ordiniService.getReceivedOrders().subscribe({
            next: (response) => {
                this.dataSource.data = response.content;
                this.isLoading = false;
            },
            error: () => {
                this.snackBar.open('Errore nel caricamento degli ordini', 'Chiudi', { duration: 3000 });
                this.isLoading = false;
            }
        });
    }

    processOrder(order: OrdineVenditoreDTO): void {
        this.ordiniService.processOrder(order.idOrdine).subscribe({
            next: () => {
                this.snackBar.open('Ordine in lavorazione', 'Chiudi', { duration: 3000 });
                this.loadOrders();
            },
            error: () => this.snackBar.open('Errore', 'Chiudi', { duration: 3000 })
        });
    }

    viewOrder(order: OrdineVenditoreDTO): void {
        this.router.navigate(['/dashboard/trasformatore/ordini', order.idOrdine]);
    }

    canProcessOrder(stato: string): boolean {
        return this.ordiniService.canProcessOrder(stato);
    }

    getStatoLabel(stato: string): string {
        return this.ordiniService.getStatoLabel(stato);
    }

    getStatoColor(stato: string) {
        return this.ordiniService.getStatoColor(stato);
    }

    formatCurrency(value: number): string {
        return this.ordiniService.formatCurrency(value);
    }
}
