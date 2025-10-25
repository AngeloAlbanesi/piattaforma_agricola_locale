import { Component, OnInit, OnDestroy, ChangeDetectionStrategy, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatChipsModule } from '@angular/material/chips';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatTooltipModule } from '@angular/material/tooltip';
import { Router, RouterModule } from '@angular/router';
import { Subject, takeUntil, finalize } from 'rxjs';

import { AcquirenteService } from '../../../../../core/services/acquirente.service';
import { OrdineExtendedSummaryDTO } from '../../../../../core/models/acquirente.models';

@Component({
    selector: 'app-recent-orders',
    standalone: true,
    imports: [
        CommonModule,
        RouterModule,
        MatCardModule,
        MatIconModule,
        MatButtonModule,
        MatChipsModule,
        MatProgressSpinnerModule,
        MatTooltipModule
    ],
    changeDetection: ChangeDetectionStrategy.OnPush,
    templateUrl: './recent-orders.component.html',
    styleUrls: ['./recent-orders.component.scss']
})
export class RecentOrdersComponent implements OnInit, OnDestroy {
    private destroy$ = new Subject<void>();

    recentOrders: OrdineExtendedSummaryDTO[] = [];
    isLoading = false;

    constructor(
        private acquirenteService: AcquirenteService,
        private router: Router,
        private cdr: ChangeDetectorRef
    ) { }

    ngOnInit(): void {
        this.loadRecentOrders();
    }

    ngOnDestroy(): void {
        this.destroy$.next();
        this.destroy$.complete();
    }

    loadRecentOrders(): void {
        this.isLoading = true;
        this.cdr.markForCheck();

        this.acquirenteService.getOrders({ page: 0, size: 5, sortBy: 'dataOrdine', sortDirection: 'desc' })
            .pipe(
                takeUntil(this.destroy$),
                finalize(() => {
                    this.isLoading = false;
                    this.cdr.markForCheck();
                })
            )
            .subscribe({
                next: (response) => {
                    this.recentOrders = response.content;
                    this.cdr.markForCheck();
                },
                error: (error) => {
                    console.error('Errore nel caricamento degli ordini recenti:', error);
                    this.cdr.markForCheck();
                }
            });
    }

    viewAllOrders(): void {
        this.router.navigate(['/dashboard/acquirente'], { fragment: 'ordini' });
    }

    viewOrderDetails(orderId: number): void {
        // Per ora navighiamo al tab ordini con il filtro
        this.router.navigate(['/dashboard/acquirente'], { fragment: 'ordini' });
    }

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
            case 'PRONTO_PER_LAVORAZIONE': return 'Pronto';
            case 'IN_LAVORAZIONE': return 'In Lavorazione';
            case 'SPEDITO': return 'Spedito';
            case 'CONSEGNATO': return 'Consegnato';
            case 'ANNULLATO': return 'Annullato';
            case 'RIMBORSATO': return 'Rimborsato';
            default: return stato;
        }
    }

    getOrderId(order: OrdineExtendedSummaryDTO): number {
        return order.idOrdine;
    }

    getOrderTotal(order: OrdineExtendedSummaryDTO): number {
        return order.importoTotale;
    }

    getOrderStatus(order: OrdineExtendedSummaryDTO): string {
        return order.statoCorrente;
    }

    getVendorName(order: OrdineExtendedSummaryDTO): string {
        return order.nomeVenditore;
    }

    get hasOrders(): boolean {
        return this.recentOrders.length > 0;
    }
}
