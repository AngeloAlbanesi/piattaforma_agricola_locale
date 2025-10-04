import { ChangeDetectionStrategy, Component, Inject, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { MatChipsModule } from '@angular/material/chips';
import { MatTabsModule } from '@angular/material/tabs';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { Subject, takeUntil } from 'rxjs';

import { ProdottiService } from '../../../../../core/services/prodotti.service';
import { ProdottoSummaryDTO } from '../../../../../core/models/common.models';

export interface ProductDetailDialogData {
    productId: number;
}

// Interfaccia per i dettagli estesi del prodotto
interface ProductDetailDTO extends ProdottoSummaryDTO {
    ingredienti?: string[];
    allergeni?: string[];
    valoriNutrizionali?: {
        calorie?: number;
        proteine?: number;
        carboidrati?: number;
        grassi?: number;
        fibre?: number;
        sale?: number;
    };
    metodiConservazione?: string;
    dataScadenza?: string;
    numeroLotto?: string;
    recensioni?: Array<{
        id: number;
        valutazione: number;
        commento: string;
        autore: string;
        data: string;
    }>;
    mediaValutazione?: number;
    numeroRecensioni?: number;
}

@Component({
    selector: 'app-product-detail-dialog',
    standalone: true,
    imports: [
        CommonModule,
        MatDialogModule,
        MatButtonModule,
        MatIconModule,
        MatCardModule,
        MatChipsModule,
        MatTabsModule,
        MatProgressSpinnerModule,
        MatSnackBarModule
    ],
    templateUrl: './product-detail-dialog.component.html',
    styleUrls: ['./product-detail-dialog.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProductDetailDialogComponent implements OnInit, OnDestroy {
    private destroy$ = new Subject<void>();

    productDetails: ProductDetailDTO | null = null;
    isLoading = true;

    constructor(
        private prodottiService: ProdottiService,
        private snackBar: MatSnackBar,
        private dialogRef: MatDialogRef<ProductDetailDialogComponent>,
        @Inject(MAT_DIALOG_DATA) public data: ProductDetailDialogData
    ) {}

    ngOnInit(): void {
        this.loadProductDetails();
    }

    ngOnDestroy(): void {
        this.destroy$.next();
        this.destroy$.complete();
    }

    private loadProductDetails(): void {
        this.isLoading = true;

        // Simulo il caricamento dei dettagli - in un'app reale useremmo l'ID per fare una chiamata API
        // Per ora uso getProducts per ottenere almeno le info base
        this.prodottiService.getProducts({ 
            elementiPerPagina: 1000,
            stato: 'APPROVATO' 
        })
        .pipe(takeUntil(this.destroy$))
        .subscribe({
            next: (response) => {
                const product = response.content?.find(p => p.id === this.data.productId);
                if (product) {
                    // Estendo il prodotto con dati mock per il demo
                    this.productDetails = this.enhanceProductWithMockData(product);
                } else {
                    this.snackBar.open('Prodotto non trovato', 'Chiudi', {
                        duration: 3000,
                        panelClass: 'error-snackbar'
                    });
                    this.dialogRef.close();
                }
                this.isLoading = false;
            },
            error: (error) => {
                console.error('Errore nel caricamento dettagli prodotto:', error);
                this.snackBar.open('Errore nel caricamento dei dettagli', 'Chiudi', {
                    duration: 3000,
                    panelClass: 'error-snackbar'
                });
                this.isLoading = false;
                this.dialogRef.close();
            }
        });
    }

    private enhanceProductWithMockData(product: ProdottoSummaryDTO): ProductDetailDTO {
        // Aggiungo dati mock per il demo
        return {
            ...product,
            ingredienti: [
                'Pomodori San Marzano',
                'Basilico fresco',
                'Olio extravergine d\'oliva',
                'Aglio',
                'Sale marino'
            ],
            allergeni: ['Tracce di sedano'],
            valoriNutrizionali: {
                calorie: 85,
                proteine: 2.1,
                carboidrati: 8.2,
                grassi: 4.5,
                fibre: 2.0,
                sale: 0.8
            },
            metodiConservazione: 'Conservare in luogo fresco e asciutto. Dopo l\'apertura, conservare in frigorifero e consumare entro 3 giorni.',
            dataScadenza: '2024-12-31',
            numeroLotto: 'LOT2024001',
            mediaValutazione: 4.6,
            numeroRecensioni: 24,
            recensioni: [
                {
                    id: 1,
                    valutazione: 5,
                    commento: 'Prodotto eccellente, sapore autentico della tradizione locale.',
                    autore: 'Marco R.',
                    data: '2024-01-15'
                },
                {
                    id: 2,
                    valutazione: 4,
                    commento: 'Molto buono, consigliato per chi ama i sapori genuini.',
                    autore: 'Anna M.',
                    data: '2024-01-10'
                },
                {
                    id: 3,
                    valutazione: 5,
                    commento: 'Qualità superiore, si sente la differenza con i prodotti industriali.',
                    autore: 'Giuseppe T.',
                    data: '2024-01-08'
                }
            ]
        };
    }

    onClose(): void {
        this.dialogRef.close();
    }

    onAddToPackage(): void {
        this.dialogRef.close({ action: 'addToPackage', product: this.productDetails });
    }

    // Utility methods
    formatCurrency(value: number): string {
        return new Intl.NumberFormat('it-IT', {
            style: 'currency',
            currency: 'EUR'
        }).format(value);
    }

    formatDate(date: string): string {
        return new Date(date).toLocaleDateString('it-IT', {
            year: 'numeric',
            month: 'long',
            day: 'numeric'
        });
    }

    getAvailabilityStatus(quantity: number): { label: string; color: string; icon: string } {
        if (quantity === 0) {
            return { label: 'Esaurito', color: '#f44336', icon: 'remove_circle' };
        } else if (quantity < 10) {
            return { label: 'Scorte limitate', color: '#ff9800', icon: 'warning' };
        } else {
            return { label: 'Disponibile', color: '#4caf50', icon: 'check_circle' };
        }
    }

    // Getters per template
    get hasProduct(): boolean {
        return !!this.productDetails;
    }

    get hasIngredients(): boolean {
        return !!(this.productDetails?.ingredienti?.length);
    }

    get hasAllergens(): boolean {
        return !!(this.productDetails?.allergeni?.length);
    }

    get hasNutritionalValues(): boolean {
        return !!this.productDetails?.valoriNutrizionali;
    }

    get hasReviews(): boolean {
        return !!(this.productDetails?.recensioni?.length);
    }

    get hasCertifications(): boolean {
        return !!(this.productDetails?.certificazioni?.length);
    }

    get averageRating(): number {
        return this.productDetails?.mediaValutazione || 0;
    }

    get reviewsCount(): number {
        return this.productDetails?.numeroRecensioni || 0;
    }

    // Rating stars helper
    getRatingStars(rating: number): string[] {
        const stars = [];
        const fullStars = Math.floor(rating);
        const hasHalfStar = rating % 1 >= 0.5;

        for (let i = 0; i < fullStars; i++) {
            stars.push('star');
        }
        
        if (hasHalfStar) {
            stars.push('star_half');
        }
        
        const emptyStars = 5 - stars.length;
        for (let i = 0; i < emptyStars; i++) {
            stars.push('star_border');
        }

        return stars;
    }

    trackByReviewId(index: number, review: any): number {
        return review.id;
    }
}