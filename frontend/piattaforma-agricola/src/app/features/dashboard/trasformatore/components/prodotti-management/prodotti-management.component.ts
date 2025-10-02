import { Component, OnInit, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators, FormArray } from '@angular/forms';
import { MatTableModule, MatTableDataSource } from '@angular/material/table';
import { MatPaginatorModule, MatPaginator } from '@angular/material/paginator';
import { MatSortModule, MatSort } from '@angular/material/sort';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDialogModule, MatDialog } from '@angular/material/dialog';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatChipsModule } from '@angular/material/chips';
import { MatCardModule } from '@angular/material/card';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatTooltipModule } from '@angular/material/tooltip';
import { Router } from '@angular/router';
import { ProdottiService } from '@core/services/prodotti.service';
import { ProdottoDTO, ProdottoFilters } from '@core/models/trasformatore.models';
import { ProdottoFormDialogComponent } from '../prodotto-form-dialog/prodotto-form-dialog.component';

@Component({
    selector: 'app-prodotti-management',
    standalone: true,
    imports: [
        CommonModule,
        FormsModule,
        ReactiveFormsModule,
        MatTableModule,
        MatPaginatorModule,
        MatSortModule,
        MatButtonModule,
        MatIconModule,
        MatDialogModule,
        MatSnackBarModule,
        MatFormFieldModule,
        MatInputModule,
        MatSelectModule,
        MatChipsModule,
        MatCardModule,
        MatProgressSpinnerModule,
        MatTooltipModule
    ],
    templateUrl: './prodotti-management.component.html',
    styleUrls: ['./prodotti-management.component.scss']
})
export class ProdottiManagementComponent implements OnInit {
    displayedColumns: string[] = ['nome', 'categoria', 'prezzo', 'quantita', 'stato', 'dataCreazione', 'actions'];
    dataSource = new MatTableDataSource<ProdottoDTO>([]);
    isLoading = false;
    totalElements = 0;
    pageSize = 10;
    pageIndex = 0;

    filterForm: FormGroup;
    stati = [
        { value: 'BOZZA', label: 'Bozza' },
        { value: 'IN_APPROVAZIONE', label: 'In Approvazione' },
        { value: 'APPROVATO', label: 'Approvato' },
        { value: 'RIFIUTATO', label: 'Rifiutato' }
    ];

    @ViewChild(MatPaginator) paginator!: MatPaginator;
    @ViewChild(MatSort) sort!: MatSort;

    constructor(
        private prodottiService: ProdottiService,
        private dialog: MatDialog,
        private snackBar: MatSnackBar,
        private router: Router,
        private fb: FormBuilder
    ) {
        this.filterForm = this.fb.group({
            search: [''],
            stato: [''],
            categoria: ['']
        });
    }

    ngOnInit(): void {
        this.loadProducts();
        this.setupFilters();
    }

    setupFilters(): void {
        this.filterForm.valueChanges.subscribe(() => {
            this.pageIndex = 0;
            this.loadProducts();
        });
    }

    loadProducts(): void {
        this.isLoading = true;
        const filters: ProdottoFilters = {
            search: this.filterForm.get('search')?.value,
            stato: this.filterForm.get('stato')?.value,
            categoria: this.filterForm.get('categoria')?.value,
            page: this.pageIndex,
            size: this.pageSize
        };

        this.prodottiService.getMyProducts(filters).subscribe({
            next: (response) => {
                this.dataSource.data = response.content;
                this.totalElements = response.totalElements;
                this.isLoading = false;
            },
            error: (error) => {
                console.error('Errore nel caricamento dei prodotti:', error);
                this.snackBar.open('Errore nel caricamento dei prodotti', 'Chiudi', { duration: 3000 });
                this.isLoading = false;
            }
        });
    }

    onPageChange(event: any): void {
        this.pageSize = event.pageSize;
        this.pageIndex = event.pageIndex;
        this.loadProducts();
    }

    createProduct(): void {
        const dialogRef = this.dialog.open(ProdottoFormDialogComponent, {
            width: '800px',
            data: { mode: 'create' }
        });

        dialogRef.afterClosed().subscribe(result => {
            if (result) {
                this.loadProducts();
                this.snackBar.open('Prodotto creato con successo', 'Chiudi', { duration: 3000 });
            }
        });
    }

    editProduct(product: ProdottoDTO): void {
        const dialogRef = this.dialog.open(ProdottoFormDialogComponent, {
            width: '800px',
            data: { mode: 'edit', product }
        });

        dialogRef.afterClosed().subscribe(result => {
            if (result) {
                this.loadProducts();
                this.snackBar.open('Prodotto aggiornato con successo', 'Chiudi', { duration: 3000 });
            }
        });
    }

    viewProduct(product: ProdottoDTO): void {
        this.router.navigate(['/dashboard/trasformatore/prodotti', product.id]);
    }

    deleteProduct(product: ProdottoDTO): void {
        if (confirm(`Sei sicuro di voler eliminare il prodotto "${product.nome}"?`)) {
            this.prodottiService.deleteProduct(product.id).subscribe({
                next: () => {
                    this.snackBar.open('Prodotto eliminato con successo', 'Chiudi', { duration: 3000 });
                    this.loadProducts();
                },
                error: (error) => {
                    console.error('Errore nell\'eliminazione del prodotto:', error);
                    this.snackBar.open('Errore nell\'eliminazione del prodotto', 'Chiudi', { duration: 3000 });
                }
            });
        }
    }

    clearFilters(): void {
        this.filterForm.reset();
    }

    getStatoLabel(stato: string): string {
        return this.prodottiService.getStatoLabel(stato);
    }

    getStatoColor(stato: string): 'primary' | 'accent' | 'warn' | undefined {
        return this.prodottiService.getStatoColor(stato);
    }

    formatCurrency(value: number): string {
        return this.prodottiService.formatCurrency(value);
    }

    formatDate(date: string): string {
        return this.prodottiService.formatDate(date);
    }
}
