import { Component, OnInit, OnDestroy, Inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { FormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatTableModule } from '@angular/material/table';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatSelectModule } from '@angular/material/select';
import { MatOptionModule } from '@angular/material/core';
import { MatChipsModule } from '@angular/material/chips';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatDialog, MatDialogModule, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { Subject, takeUntil, finalize } from 'rxjs';

import { AcquirenteService } from '../../../../../core/services/acquirente.service';
import {
    OrdineExtendedSummaryDTO,
    OrdineDetailDTO,
    OrderStatusDTO,
    CancelOrderRequestDTO,
    PaginationParams
} from '../../../../../core/models/acquirente.models';

@Component({
    selector: 'app-orders-management',
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
        MatPaginatorModule,
        MatSelectModule,
        MatOptionModule,
        MatChipsModule
    ],
    templateUrl: './orders-management.component.html',
    styleUrls: ['./orders-management.component.scss']
})
export class OrdersManagementComponent implements OnInit, OnDestroy {
    private destroy$ = new Subject<void>();

    displayedColumns: string[] = ['id', 'data', 'venditore', 'stato', 'totale', 'azioni'];
    ordersList: OrdineExtendedSummaryDTO[] = [];
    selectedOrder: OrdineDetailDTO | null = null;
    orderStatus: OrderStatusDTO | null = null;
    isLoading = false;
    isUpdating = false;
    showDetails = false;

    // Filtro e paginazione
    // Definite assignment assertion: inizializzato nel costruttore tramite initializeFilterForm()
    filterForm!: FormGroup;
    pagination: PaginationParams = {
        page: 0,
        size: 10,
        sortBy: 'dataOrdine',
        sortDirection: 'desc'
    };
    totalElements = 0;
    pageSize = 10;

    constructor(
        private fb: FormBuilder,
        private acquirenteService: AcquirenteService,
        private snackBar: MatSnackBar,
        private dialog: MatDialog
    ) {
        this.initializeFilterForm();
    }

    ngOnInit(): void {
        this.loadOrders();
    }

    ngOnDestroy(): void {
        this.destroy$.next();
        this.destroy$.complete();
    }

    // === INIZIALIZZAZIONE ===

    private initializeFilterForm(): void {
        this.filterForm = this.fb.group({
            search: [''],
            stato: [''],
            sortBy: ['dataOrdine'],
            sortDirection: ['desc']
        });

        this.filterForm.valueChanges.subscribe(() => {
            this.pagination.page = 0;
            this.loadOrders();
        });
    }

    // === GESTIONE ORDINI ===

    loadOrders(): void {
        this.isLoading = true;
        const filters = this.filterForm.value;

        // Aggiorna i parametri di paginazione con i filtri
        this.pagination = {
            ...this.pagination,
            sortBy: filters.sortBy,
            sortDirection: filters.sortDirection
        };

        this.acquirenteService.getOrders(this.pagination)
            .pipe(
                takeUntil(this.destroy$),
                finalize(() => this.isLoading = false)
            )
            .subscribe({
                next: (response) => {
                    this.ordersList = response.content;
                    this.totalElements = response.totalElements;
                },
                error: (error) => {
                    this.snackBar.open('Errore nel caricamento degli ordini', 'Chiudi', {
                        duration: 3000,
                        panelClass: 'error-snackbar'
                    });
                }
            });
    }

    loadOrderDetails(orderId: number): void {
        this.isUpdating = true;

        this.acquirenteService.getOrderById(orderId)
            .pipe(
                takeUntil(this.destroy$),
                finalize(() => this.isUpdating = false)
            )
            .subscribe({
                next: (order) => {
                    this.selectedOrder = order;
                    this.showDetails = true;
                    this.loadOrderStatus(orderId);
                },
                error: (error) => {
                    this.snackBar.open('Errore nel caricamento dei dettagli dell\'ordine', 'Chiudi', {
                        duration: 3000,
                        panelClass: 'error-snackbar'
                    });
                }
            });
    }

    loadOrderStatus(orderId: number): void {
        this.acquirenteService.getOrderStatus(orderId)
            .pipe(takeUntil(this.destroy$))
            .subscribe({
                next: (status) => {
                    this.orderStatus = status;
                },
                error: (error) => {
                    console.error('Errore nel caricamento dello stato ordine:', error);
                }
            });
    }

    cancelOrder(orderId: number): void {
        const dialogRef = this.dialog.open(CancelOrderDialogComponent, {
            width: '400px',
            data: { orderId }
        });

        dialogRef.afterClosed().subscribe(result => {
            if (result) {
                this.isUpdating = true;
                const request: CancelOrderRequestDTO = {
                    motivoAnnullamento: result.motivo
                };

                this.acquirenteService.cancelOrderWithReason(orderId, request)
                    .pipe(
                        takeUntil(this.destroy$),
                        finalize(() => this.isUpdating = false)
                    )
                    .subscribe({
                        next: () => {
                            this.snackBar.open('Ordine annullato con successo', 'Chiudi', {
                                duration: 3000,
                                panelClass: 'success-snackbar'
                            });
                            this.loadOrders();
                            if (this.selectedOrder?.idOrdine === orderId) {
                                this.showDetails = false;
                                this.selectedOrder = null;
                            }
                        },
                        error: (error) => {
                            this.snackBar.open('Errore nell\'annullamento dell\'ordine', 'Chiudi', {
                                duration: 3000,
                                panelClass: 'error-snackbar'
                            });
                        }
                    });
            }
        });
    }

    // === UTILITIES ===

    formatCurrency(value: number): string {
        return new Intl.NumberFormat('it-IT', {
            style: 'currency',
            currency: 'EUR'
        }).format(value);
    }

    formatDate(dateString: string): string {
        return new Date(dateString).toLocaleDateString('it-IT', {
            day: '2-digit',
            month: '2-digit',
            year: 'numeric'
        });
    }

    formatDateTime(dateString: string): string {
        return new Date(dateString).toLocaleString('it-IT', {
            day: '2-digit',
            month: '2-digit',
            year: 'numeric',
            hour: '2-digit',
            minute: '2-digit'
        });
    }

    getStatusColor(stato: string): string {
        switch (stato) {
            case 'ATTESA_PAGAMENTO': return 'warn';
            case 'PRONTO_PER_LAVORAZIONE': return 'primary';
            case 'IN_LAVORAZIONE': return 'accent';
            case 'SPEDITO': return 'primary';
            case 'CONSEGNATO': return 'primary';
            case 'ANNULLATO': return 'warn';
            case 'RIMBORSATO': return 'accent';
            default: return 'primary';
        }
    }

    getStatusLabel(stato: string): string {
        switch (stato) {
            case 'ATTESA_PAGAMENTO': return 'Attesa Pagamento';
            case 'PRONTO_PER_LAVORAZIONE': return 'Pronto per Lavorazione';
            case 'IN_LAVORAZIONE': return 'In Lavorazione';
            case 'SPEDITO': return 'Spedito';
            case 'CONSEGNATO': return 'Consegnato';
            case 'ANNULLATO': return 'Annullato';
            case 'RIMBORSATO': return 'Rimborsato';
            default: return stato;
        }
    }

    canCancelOrder(order: OrdineExtendedSummaryDTO): boolean {
        return order.statoCorrente === 'ATTESA_PAGAMENTO' || order.statoCorrente === 'PRONTO_PER_LAVORAZIONE';
    }

    canCancelOrderById(orderId: number, stato: string): boolean {
        return stato === 'ATTESA_PAGAMENTO' || stato === 'PRONTO_PER_LAVORAZIONE';
    }

    // === PAGINAZIONE ===

    onPageChange(event: any): void {
        this.pagination.page = event.pageIndex;
        this.pagination.size = event.pageSize;
        this.pageSize = event.pageSize;
        this.loadOrders();
    }

    // === GETTERS PER TEMPLATE ===

    get hasOrders(): boolean {
        return this.ordersList.length > 0;
    }

    get isFilterActive(): boolean {
        const form = this.filterForm.value;
        return form.search || form.stato;
    }
}

// Dialog per annullamento ordine
@Component({
    selector: 'app-cancel-order-dialog',
    template: `
    <h2 mat-dialog-title>Annulla Ordine</h2>
    <mat-dialog-content>
      <p>Sei sicuro di voler annullare questo ordine?</p>
      <mat-form-field appearance="outline" style="width: 100%; margin-top: 16px;">
        <mat-label>Motivo dell'annullamento</mat-label>
        <textarea matInput [(ngModel)]="data.motivo" placeholder="Inserisci il motivo..." rows="3"></textarea>
      </mat-form-field>
    </mat-dialog-content>
    <mat-dialog-actions>
      <button mat-button (click)="onNoClick()">Annulla</button>
            <button mat-raised-button color="warn" (click)="onYesClick()" [disabled]="!data.motivo.trim()">
        Conferma Annullamento
      </button>
    </mat-dialog-actions>
  `,
    standalone: true,
    imports: [MatButtonModule, MatDialogModule, MatFormFieldModule, MatInputModule, FormsModule],
})
export class CancelOrderDialogComponent {
    constructor(
        public dialogRef: MatDialogRef<CancelOrderDialogComponent>,
        @Inject(MAT_DIALOG_DATA) public data: { motivo: string }
    ) { }

    onNoClick(): void {
        this.dialogRef.close();
    }

    onYesClick(): void {
        this.dialogRef.close(this.data);
    }
}