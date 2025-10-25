import { ChangeDetectionStrategy, Component, OnInit, OnDestroy, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MatChipsModule } from '@angular/material/chips';
import { MatTableModule } from '@angular/material/table';
import { MatSortModule } from '@angular/material/sort';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatDialogModule, MatDialog } from '@angular/material/dialog';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatMenuModule } from '@angular/material/menu';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatBadgeModule } from '@angular/material/badge';
import { SelectionModel } from '@angular/cdk/collections';
import { Subject, takeUntil, debounceTime, distinctUntilChanged, startWith, combineLatest } from 'rxjs';

import { OrdiniService } from '../../../../../core/services/ordini.service';
import { 
    OrdineDTO, 
    OrdiniFiltri, 
    StatoOrdine, 
    STATO_ORDINE_LABELS, 
    STATO_ORDINE_COLORS, 
    STATO_ORDINE_ICONS,
    StatisticheOrdiniDTO 
} from '../../../../../core/models/ordini.models';
import { PaginatedResponse } from '../../../../../core/models/common.models';
import { OrderDetailsDialogComponent, OrderDetailsDialogData } from '../order-details-dialog/order-details-dialog.component';
import { UpdateOrderStatusDialogComponent, UpdateOrderStatusDialogData } from '../update-order-status-dialog/update-order-status-dialog.component';

@Component({
    selector: 'app-orders-management',
    standalone: true,
    imports: [
        CommonModule,
        ReactiveFormsModule,
        MatCardModule,
        MatButtonModule,
        MatIconModule,
        MatFormFieldModule,
        MatInputModule,
        MatSelectModule,
        MatDatepickerModule,
        MatNativeDateModule,
        MatChipsModule,
        MatTableModule,
        MatSortModule,
        MatPaginatorModule,
        MatProgressSpinnerModule,
        MatTooltipModule,
        MatDialogModule,
        MatSnackBarModule,
        MatMenuModule,
        MatCheckboxModule,
        MatBadgeModule
    ],
    templateUrl: './orders-management.component.html',
    styleUrls: ['./orders-management.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class OrdersManagementComponent implements OnInit, OnDestroy {
    private destroy$ = new Subject<void>();

    // Data
    ordini: OrdineDTO[] = [];
    totalOrdini = 0;
    statistiche: StatisticheOrdiniDTO | null = null;
    isLoading = false;
    errorMessage: string | null = null;

    // Selection
    selection = new SelectionModel<OrdineDTO>(true, []);

    // Pagination
    currentPage = 0;
    pageSize = 10;
    pageSizeOptions = [5, 10, 25, 50];

    // Filters
    searchControl = new FormControl('');
    statoControl = new FormControl<StatoOrdine[]>([]);
    dataInizioControl = new FormControl<Date | null>(null);
    dataFineControl = new FormControl<Date | null>(null);
    importoMinControl = new FormControl<number | null>(null);
    importoMaxControl = new FormControl<number | null>(null);

    // View options
    viewMode: 'table' | 'cards' = 'table';
    ordinamento: 'dataOrdine' | 'totale' | 'numeroOrdine' = 'dataOrdine';
    direzione: 'ASC' | 'DESC' = 'DESC';

    // Constants
    readonly stati = Object.values(StatoOrdine);
    readonly statoLabels = STATO_ORDINE_LABELS;
    readonly statoColors = STATO_ORDINE_COLORS;
    readonly statoIcons = STATO_ORDINE_ICONS;

    // Table columns
    displayedColumns: string[] = [
        'select', 'numeroOrdine', 'dataOrdine', 'acquirente', 
        'stato', 'totale', 'priorita', 'actions'
    ];

    constructor(
        private ordiniService: OrdiniService,
        private dialog: MatDialog,
        private snackBar: MatSnackBar,
        private cdr: ChangeDetectorRef
    ) {}

    ngOnInit(): void {
        this.initializeFilters();
        this.loadOrdini();
        this.loadStatistiche();
        this.setupRealTimeUpdates();
    }

    ngOnDestroy(): void {
        this.destroy$.next();
        this.destroy$.complete();
    }

    private initializeFilters(): void {
        // Combina tutti i controlli filtro con debounce
        combineLatest([
            this.searchControl.valueChanges.pipe(startWith(''), debounceTime(300), distinctUntilChanged()),
            this.statoControl.valueChanges.pipe(startWith([])),
            this.dataInizioControl.valueChanges.pipe(startWith(null)),
            this.dataFineControl.valueChanges.pipe(startWith(null)),
            this.importoMinControl.valueChanges.pipe(startWith(null)),
            this.importoMaxControl.valueChanges.pipe(startWith(null))
        ])
        .pipe(takeUntil(this.destroy$))
        .subscribe(() => {
            this.currentPage = 0;
            this.selection.clear();
            this.loadOrdini();
        });
    }

    private loadOrdini(): void {
        this.isLoading = true;
        this.errorMessage = null;

        const filtri = this.buildFiltri();

        this.ordiniService.getOrdini(filtri)
            .pipe(takeUntil(this.destroy$))
            .subscribe({
                next: (response: PaginatedResponse<OrdineDTO>) => {
                    this.ordini = response.content || [];
                    this.totalOrdini = response.totalElements || 0;
                    this.isLoading = false;
                    this.cdr.markForCheck();
                },
                error: (error) => {
                    console.error('Errore nel caricamento ordini:', error);
                    this.errorMessage = 'Impossibile caricare gli ordini. Riprova più tardi.';
                    this.isLoading = false;
                    this.cdr.markForCheck();
                }
            });
    }

    private loadStatistiche(): void {
        this.ordiniService.getStatisticheOrdini()
            .pipe(takeUntil(this.destroy$))
            .subscribe({
                next: (statistiche) => {
                    this.statistiche = statistiche;
                    this.cdr.markForCheck();
                },
                error: (error) => {
                    console.error('Errore nel caricamento statistiche:', error);
                }
            });
    }

    private setupRealTimeUpdates(): void {
        // Setup per aggiornamenti real-time (WebSocket, polling, etc.)
        this.ordiniService.getOrdiniUpdates()
            .pipe(takeUntil(this.destroy$))
            .subscribe(ordiniAggiornati => {
                if (ordiniAggiornati.length > 0) {
                    this.loadOrdini();
                    this.loadStatistiche();
                }
            });
    }

    private buildFiltri(): OrdiniFiltri {
        const filtri: OrdiniFiltri = {
            pagina: this.currentPage,
            elementiPerPagina: this.pageSize,
            ordinamento: this.ordinamento,
            direzione: this.direzione
        };

        const search = this.searchControl.value?.trim();
        if (search) {
            if (search.startsWith('ORD-')) {
                filtri.numeroOrdine = search;
            } else {
                filtri.acquirente = search;
            }
        }

        const stati = this.statoControl.value;
        if (stati && stati.length > 0) {
            filtri.stato = stati;
        }

        const dataInizio = this.dataInizioControl.value;
        if (dataInizio) {
            filtri.dataInizio = dataInizio.toISOString().split('T')[0];
        }

        const dataFine = this.dataFineControl.value;
        if (dataFine) {
            filtri.dataFine = dataFine.toISOString().split('T')[0];
        }

        const importoMin = this.importoMinControl.value;
        if (importoMin !== null && importoMin > 0) {
            filtri.importoMin = importoMin;
        }

        const importoMax = this.importoMaxControl.value;
        if (importoMax !== null && importoMax > 0) {
            filtri.importoMax = importoMax;
        }

        return filtri;
    }

    // Event handlers
    onPageChange(event: PageEvent): void {
        this.currentPage = event.pageIndex;
        this.pageSize = event.pageSize;
        this.loadOrdini();
    }

    onSortChange(ordinamento: 'dataOrdine' | 'totale' | 'numeroOrdine'): void {
        if (this.ordinamento === ordinamento) {
            this.direzione = this.direzione === 'ASC' ? 'DESC' : 'ASC';
        } else {
            this.ordinamento = ordinamento;
            this.direzione = 'DESC';
        }
        this.loadOrdini();
    }

    toggleViewMode(): void {
        this.viewMode = this.viewMode === 'table' ? 'cards' : 'table';
        this.cdr.markForCheck();
    }

    clearFilters(): void {
        this.searchControl.setValue('');
        this.statoControl.setValue([]);
        this.dataInizioControl.setValue(null);
        this.dataFineControl.setValue(null);
        this.importoMinControl.setValue(null);
        this.importoMaxControl.setValue(null);
    }

    refreshOrdini(): void {
        this.loadOrdini();
        this.loadStatistiche();
    }

    // Selection methods
    isAllSelected(): boolean {
        const numSelected = this.selection.selected.length;
        const numRows = this.ordini.length;
        return numSelected === numRows;
    }

    masterToggle(): void {
        if (this.isAllSelected()) {
            this.selection.clear();
        } else {
            this.ordini.forEach(ordine => this.selection.select(ordine));
        }
    }

    // Order actions
    viewOrderDetails(ordine: OrdineDTO): void {
        const dialogData: OrderDetailsDialogData = {
            ordineId: ordine.id
        };

        const dialogRef = this.dialog.open(OrderDetailsDialogComponent, {
            width: '900px',
            maxWidth: '95vw',
            maxHeight: '90vh',
            data: dialogData
        });

        dialogRef.afterClosed().subscribe(result => {
            if (result?.updated) {
                this.loadOrdini();
                this.loadStatistiche();
            }
        });
    }

    updateOrderStatus(ordine: OrdineDTO): void {
        const dialogData: UpdateOrderStatusDialogData = {
            ordine: ordine
        };

        const dialogRef = this.dialog.open(UpdateOrderStatusDialogComponent, {
            width: '500px',
            maxWidth: '95vw',
            data: dialogData
        });

        dialogRef.afterClosed().subscribe(result => {
            if (result?.updated) {
                this.loadOrdini();
                this.loadStatistiche();
                this.snackBar.open('Stato ordine aggiornato con successo', 'Chiudi', {
                    duration: 3000,
                    panelClass: 'success-snackbar'
                });
            }
        });
    }

    confirmSelectedOrders(): void {
        const selectedOrders = this.selection.selected.filter(o => o.stato === StatoOrdine.RICEVUTO);
        
        if (selectedOrders.length === 0) {
            this.snackBar.open('Seleziona almeno un ordine ricevuto da confermare', 'Chiudi', {
                duration: 3000,
                panelClass: 'warning-snackbar'
            });
            return;
        }

        const ordiniIds = selectedOrders.map(o => o.id);

        this.ordiniService.confermaOrdiniMultipli(ordiniIds)
            .pipe(takeUntil(this.destroy$))
            .subscribe({
                next: () => {
                    this.snackBar.open(`${selectedOrders.length} ordini confermati con successo`, 'Chiudi', {
                        duration: 3000,
                        panelClass: 'success-snackbar'
                    });
                    this.selection.clear();
                    this.loadOrdini();
                    this.loadStatistiche();
                },
                error: (error) => {
                    console.error('Errore nella conferma multipla:', error);
                    this.snackBar.open('Errore nella conferma degli ordini', 'Chiudi', {
                        duration: 3000,
                        panelClass: 'error-snackbar'
                    });
                }
            });
    }

    exportOrders(): void {
        const filtri = this.buildFiltri();
        
        this.ordiniService.esportaOrdini(filtri, 'excel')
            .pipe(takeUntil(this.destroy$))
            .subscribe({
                next: (blob) => {
                    const url = window.URL.createObjectURL(blob);
                    const a = document.createElement('a');
                    a.href = url;
                    a.download = `ordini_${new Date().toISOString().split('T')[0]}.xlsx`;
                    document.body.appendChild(a);
                    a.click();
                    document.body.removeChild(a);
                    window.URL.revokeObjectURL(url);
                    
                    this.snackBar.open('Export completato con successo', 'Chiudi', {
                        duration: 3000,
                        panelClass: 'success-snackbar'
                    });
                },
                error: (error) => {
                    console.error('Errore nell\'export:', error);
                    this.snackBar.open('Errore nell\'export degli ordini', 'Chiudi', {
                        duration: 3000,
                        panelClass: 'error-snackbar'
                    });
                }
            });
    }

    // Utility methods
    formatCurrency(value: number): string {
        return this.ordiniService.formatCurrency(value);
    }

    formatDate(date: string): string {
        return this.ordiniService.formatDate(date);
    }

    formatDateTime(date: string): string {
        return this.ordiniService.formatDateTime(date);
    }

    getStatoLabel(stato: StatoOrdine): string {
        return this.statoLabels[stato];
    }

    getStatoColor(stato: StatoOrdine): string {
        return this.statoColors[stato];
    }

    getStatoIcon(stato: StatoOrdine): string {
        return this.statoIcons[stato];
    }

    getPriorita(ordine: OrdineDTO): 'ALTA' | 'MEDIA' | 'BASSA' {
        return this.ordiniService.calcolaPriorita(ordine);
    }

    getPrioritaColor(priorita: 'ALTA' | 'MEDIA' | 'BASSA'): string {
        switch (priorita) {
            case 'ALTA': return '#f44336';
            case 'MEDIA': return '#ff9800';
            case 'BASSA': return '#4caf50';
            default: return '#666';
        }
    }

    getTempoTrascorso(data: string): string {
        return this.ordiniService.calcolaTempoTrascorso(data);
    }

    // Getters for template
    get hasFilters(): boolean {
        return !!(
            this.searchControl.value ||
            (this.statoControl.value && this.statoControl.value.length > 0) ||
            this.dataInizioControl.value ||
            this.dataFineControl.value ||
            this.importoMinControl.value ||
            this.importoMaxControl.value
        );
    }

    get filteredOrdersText(): string {
        if (this.totalOrdini === 0) {
            return 'Nessun ordine trovato';
        }
        
        const start = (this.currentPage * this.pageSize) + 1;
        const end = Math.min((this.currentPage + 1) * this.pageSize, this.totalOrdini);
        
        return `${start}-${end} di ${this.totalOrdini} ordini`;
    }

    get selectedOrdersCount(): number {
        return this.selection.selected.length;
    }

    get canConfirmSelected(): boolean {
        return this.selection.selected.some(o => o.stato === StatoOrdine.RICEVUTO);
    }

    get isTableView(): boolean {
        return this.viewMode === 'table';
    }

    get isCardsView(): boolean {
        return this.viewMode === 'cards';
    }

    trackByOrderId(index: number, ordine: OrdineDTO): number {
        return ordine.id;
    }

    removeStatoFilter(statoToRemove: StatoOrdine): void {
        const currentValues = this.statoControl.value || [];
        const newValues = currentValues.filter(s => s !== statoToRemove);
        this.statoControl.setValue(newValues);
    }
}