import { ChangeDetectionStrategy, ChangeDetectorRef, Component, OnInit, ViewChild } from '@angular/core';
import { CommonModule, DatePipe, CurrencyPipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatOptionModule } from '@angular/material/core';
import { MatPaginator, MatPaginatorModule } from '@angular/material/paginator';
import { MatSort, MatSortModule } from '@angular/material/sort';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { MatDialog } from '@angular/material/dialog';
import { ShipOrderDialogComponent } from './ship-order-dialog.component';
import { CancelOrderDialogComponent } from './cancel-order-dialog.component';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatChipsModule } from '@angular/material/chips';
import { MatMenuModule } from '@angular/material/menu';
import { MatTooltipModule } from '@angular/material/tooltip';
import { OrdineRiepilogoDTO, StatoOrdineProduttore } from '../../../../../core/models/produttore.models';
import { ProduttoreService } from '../../../../../core/services/produttore.service';
import { debounceTime, distinctUntilChanged, switchMap, catchError } from 'rxjs/operators';
import { Subject, of } from 'rxjs';
import { Router } from '@angular/router';

@Component({
    selector: 'app-ordini-management',
    standalone: true,
    imports: [
        CommonModule,
        FormsModule,
        MatCardModule,
        MatIconModule,
        MatButtonModule,
        MatFormFieldModule,
        MatInputModule,
        MatSelectModule,
        MatOptionModule,
        MatPaginatorModule,
        MatSortModule,
        MatTableModule,
        MatSnackBarModule,
        MatProgressSpinnerModule,
        MatChipsModule,
        DatePipe,
        CurrencyPipe,
        MatMenuModule,
        MatTooltipModule
    ],
    templateUrl: './ordini-management.component.html',
    styleUrl: './ordini-management.component.scss',
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class OrdiniManagementComponent implements OnInit {
    displayedColumns: string[] = ['id', 'dataOrdine', 'acquirente', 'totale', 'stato', 'actions'];
    dataSource = new MatTableDataSource<OrdineRiepilogoDTO>([]);
    isLoading = true;
    totalElements = 0;
    pageSize = 10;
    currentPage = 0;
    pageSizeOptions: number[] = [5, 10, 25, 100];

    @ViewChild(MatPaginator) paginator!: MatPaginator;
    @ViewChild(MatSort) sort!: MatSort;

    filters = {
        pagina: 0,
        elementiPerPagina: 10,
        stato: 'TUTTI',
        search: ''
    };

    statiOrdine = [
        { value: 'TUTTI', viewValue: 'Tutti' },
        { value: StatoOrdineProduttore.NUOVO, viewValue: 'Nuovo (In Attesa Pagamento)' },
        { value: StatoOrdineProduttore.PAGATO, viewValue: 'Pagato (Pronto Lavorazione)' },
        { value: StatoOrdineProduttore.IN_LAVORAZIONE, viewValue: 'In Lavorazione' },
        { value: StatoOrdineProduttore.SPEDITO, viewValue: 'Spedito' },
        { value: StatoOrdineProduttore.CONSEGNATO, viewValue: 'Consegnato' },
        { value: StatoOrdineProduttore.ANNULLATO, viewValue: 'Annullato' }
    ];

    private searchTerms = new Subject<string>();

    constructor(
        private produttoreService: ProduttoreService,
        private dialog: MatDialog,
        private snackBar: MatSnackBar,
        private router: Router,
        private cdr: ChangeDetectorRef
    ) { }

    ngOnInit(): void {
        this.loadOrdini();

        // Gestione ricerca con debounce
        this.searchTerms.pipe(
            debounceTime(300),
            distinctUntilChanged(),
            switchMap((term: string) => {
                this.filters.search = term;
                this.filters.pagina = 0;
                this.isLoading = true;
                return this.produttoreService.getMyOrders();
            }),
            catchError((error: any) => {
                console.error('Errore durante la ricerca degli ordini:', error);
                this.isLoading = false;
                // Non mostrare snackbar per ogni ricerca fallita, solo loggare
                return of([]);
            })
        ).subscribe((data: OrdineRiepilogoDTO[]) => {
            this.dataSource.data = data;
            this.totalElements = data.length;
            this.isLoading = false;
        });
    }

    ngAfterViewInit(): void {
        this.dataSource.paginator = this.paginator;
        this.dataSource.sort = this.sort;

        if (this.paginator) {
            this.paginator.page.subscribe(() => {
                this.filters.pagina = this.paginator.pageIndex;
                this.filters.elementiPerPagina = this.paginator.pageSize;
                this.loadOrdini();
            });
        }

        if (this.sort) {
            this.sort.sortChange.subscribe(() => {
                this.filters.pagina = 0;
                // Implementare logica di ordinamento se l'API lo supporta
                this.loadOrdini();
            });
        }
    }

    loadOrdini(): void {
        this.isLoading = true;
        this.cdr.markForCheck();

        this.produttoreService.getMyOrders()
            .pipe(
                catchError((error: any) => {
                    console.error('Errore durante il caricamento degli ordini:', error);

                    // Non mostrare snackbar - evita notifiche bloccate
                    // L'errore viene loggato in console per il debug

                    this.isLoading = false;
                    this.cdr.markForCheck();
                    return of([]);
                })
            )
            .subscribe((data: OrdineRiepilogoDTO[]) => {
                this.dataSource.data = data;
                this.totalElements = data.length;
                this.isLoading = false;
                this.cdr.markForCheck();
            });
    }

    applyFilter(event: Event): void {
        const filterValue = (event.target as HTMLInputElement).value;
        this.searchTerms.next(filterValue.trim().toLowerCase());
    }

    onStatusChange(status: StatoOrdineProduttore | 'TUTTI'): void {
        this.filters.stato = status;
        this.filters.pagina = 0;
        this.loadOrdini();
    }

    viewOrderDetails(ordine: OrdineRiepilogoDTO): void {
        this.router.navigate(['/dashboard/produttore/ordini', ordine.id]);
    }

    updateStatus(ordine: OrdineRiepilogoDTO, nuovoStato: StatoOrdineProduttore): void {
        // Logica per aggiornare lo stato dell'ordine utilizzando ProduttoreService
        switch (nuovoStato) {
            case StatoOrdineProduttore.IN_LAVORAZIONE:
                // Metti in lavorazione
                this.produttoreService.processOrder(ordine.id).subscribe({
                    next: (updated) => {
                        this.snackBar.open(`Ordine ${ordine.id} impostato in lavorazione`, 'Chiudi', { duration: 3000 });
                        this.loadOrdini();
                    },
                    error: () => {
                        this.snackBar.open('Errore durante l\'aggiornamento dello stato.', 'Chiudi', { duration: 3000 });
                    }
                });
                break;
            case StatoOrdineProduttore.SPEDITO:
                // Apri dialog per raccogliere i dati di spedizione
                const shipRef = this.dialog.open(ShipOrderDialogComponent, { width: '480px' });
                shipRef.afterClosed().subscribe(result => {
                    if (result) {
                        this.produttoreService.shipOrder(ordine.id, result).subscribe({
                            next: () => {
                                this.snackBar.open(`Ordine ${ordine.id} segnato come spedito`, 'Chiudi', { duration: 3000 });
                                this.loadOrdini();
                            },
                            error: () => {
                                this.snackBar.open('Errore durante la spedizione dell\'ordine.', 'Chiudi', { duration: 3000 });
                            }
                        });
                    }
                });
                break;
            case StatoOrdineProduttore.CONSEGNATO:
                this.produttoreService.deliverOrder(ordine.id).subscribe({
                    next: () => {
                        this.snackBar.open(`Ordine ${ordine.id} segnato come consegnato`, 'Chiudi', { duration: 3000 });
                        this.loadOrdini();
                    },
                    error: () => {
                        this.snackBar.open('Errore durante l\'aggiornamento a consegnato.', 'Chiudi', { duration: 3000 });
                    }
                });
                break;
            case StatoOrdineProduttore.ANNULLATO:
                const cancelRef = this.dialog.open(CancelOrderDialogComponent, { width: '480px' });
                cancelRef.afterClosed().subscribe(result => {
                    if (result) {
                        this.produttoreService.cancelOrder(ordine.id, result).subscribe({
                            next: () => {
                                this.snackBar.open(`Ordine ${ordine.id} annullato`, 'Chiudi', { duration: 3000 });
                                this.loadOrdini();
                            },
                            error: () => {
                                this.snackBar.open('Errore durante l\'annullamento dell\'ordine.', 'Chiudi', { duration: 3000 });
                            }
                        });
                    }
                });
                break;
            default:
                // Per altri stati, logica generica
                this.produttoreService.updateOrderStatus(ordine.id, nuovoStato).subscribe({
                    next: () => {
                        this.snackBar.open(`Stato ordine ${ordine.id} aggiornato`, 'Chiudi', { duration: 2000 });
                        this.loadOrdini();
                    },
                    error: () => {
                        this.snackBar.open('Errore durante l\'aggiornamento dello stato.', 'Chiudi', { duration: 3000 });
                    }
                });
        }
    }

    getStatoClass(stato: string): string {
        switch (stato) {
            case StatoOrdineProduttore.CONSEGNATO:
                return 'status-delivered';
            case StatoOrdineProduttore.SPEDITO:
                return 'status-shipped';
            case StatoOrdineProduttore.IN_LAVORAZIONE:
                return 'status-processing';
            case StatoOrdineProduttore.PAGATO:
                return 'status-paid';
            case StatoOrdineProduttore.NUOVO:
                return 'status-new';
            case StatoOrdineProduttore.ANNULLATO:
                return 'status-cancelled';
            default:
                return '';
        }
    }
}