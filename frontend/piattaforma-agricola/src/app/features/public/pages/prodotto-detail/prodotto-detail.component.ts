import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatChipsModule } from '@angular/material/chips';
import { MatTabsModule } from '@angular/material/tabs';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatDividerModule } from '@angular/material/divider';

import { PublicProdottiService } from '../../../../core/services/public-prodotti.service';
import { PublicProdottoDetailDTO, PublicProdottoSummaryDTO, getProdottoId } from '../../../../core/models/public.models';
import { PublicAziendaSummaryDTO } from '../../../../core/models/public.models';

@Component({
    selector: 'app-prodotto-detail',
    standalone: true,
    imports: [
        CommonModule,
        RouterModule,
        MatButtonModule,
        MatCardModule,
        MatIconModule,
        MatProgressSpinnerModule,
        MatSnackBarModule,
        MatChipsModule,
        MatTabsModule,
        MatExpansionModule,
        MatDividerModule
    ],
    templateUrl: './prodotto-detail.component.html',
    styleUrls: ['./prodotto-detail.component.scss']
})
export class ProdottoDetailComponent implements OnInit, OnDestroy {
    prodotto: PublicProdottoDetailDTO | null = null;
    loading = false;
    error: string | null = null;
    prodottoId: number | null = null;

    // Prodotti correlati
    prodottiCorrelati: PublicProdottoSummaryDTO[] = [];
    loadingCorrelati = false;

    // Azienda del produttore
    aziendaProduttrice: PublicAziendaSummaryDTO | null = null;
    loadingAzienda = false;

    private subscriptions = new Map<string, any>();

    constructor(
        private prodottiService: PublicProdottiService,
        private route: ActivatedRoute,
        private router: Router,
        private snackBar: MatSnackBar
    ) { }

    ngOnInit(): void {
        this.route.paramMap.subscribe(params => {
            const id = params.get('id');
            if (id) {
                this.prodottoId = parseInt(id, 10);
                this.loadProdottoDetail();
            } else {
                this.error = 'ID prodotto non valido';
                this.snackBar.open(this.error, 'Chiudi', {
                    duration: 5000,
                    panelClass: ['error-snackbar']
                });
            }
        });
    }

    ngOnDestroy(): void {
        this.subscriptions.forEach(subscription => {
            if (subscription && subscription.unsubscribe) {
                subscription.unsubscribe();
            }
        });
        this.subscriptions.clear();
    }

    loadProdottoDetail(): void {
        if (!this.prodottoId) return;

        this.loading = true;
        this.error = null;

        const prodottoSub = this.prodottiService.getProdottoById(this.prodottoId).subscribe({
            next: (prodotto) => {
                this.prodotto = prodotto;
                this.loading = false;

                // Carica dati correlati
                this.loadProdottiCorrelati();
                this.loadAziendaProduttrice();
            },
            error: (error: any) => {
                console.error('Errore nel caricamento dettaglio prodotto:', error);
                this.error = 'Impossibile caricare i dettagli del prodotto. Riprova più tardi.';
                this.loading = false;
                this.snackBar.open(this.error, 'Chiudi', {
                    duration: 5000,
                    panelClass: ['error-snackbar']
                });
            }
        });

        this.subscriptions.set('prodotto', prodottoSub);
    }

    loadProdottiCorrelati(): void {
        if (!this.prodotto?.categoria) return;

        this.loadingCorrelati = true;

        const correlatiSub = this.prodottiService.getProdotti({
            categoria: this.prodotto.categoria,
            page: 0,
            size: 4
        }).subscribe({
            next: (response) => {
                // Filtra il prodotto corrente dai risultati
                this.prodottiCorrelati = (response.content || [])
                    .filter(p => p.id !== this.prodotto?.id)
                    .slice(0, 3);
                this.loadingCorrelati = false;
            },
            error: (error: any) => {
                console.error('Errore nel caricamento prodotti correlati:', error);
                this.loadingCorrelati = false;
            }
        });

        this.subscriptions.set('correlati', correlatiSub);
    }

    loadAziendaProduttrice(): void {
        if (!this.prodotto?.produttore?.id) return;

        this.loadingAzienda = true;

        // Nota: Questo servizio dovrebbe essere implementato nel PublicAziendeService
        // Per ora simuliamo il caricamento
        setTimeout(() => {
            this.aziendaProduttrice = {
                id: this.prodotto!.produttore!.id,
                nomeAzienda: this.prodotto!.produttore!.nomeAzienda,
                descrizione: 'Azienda agricola specializzata in prodotti di alta qualità',
                tipologia: 'Agricola',
                indirizzo: {
                    via: 'Via Agricola, 1',
                    citta: 'Città',
                    provincia: 'Provincia',
                    cap: '12345'
                },
                numeroProdotti: 15,
                rating: 4.5
            };
            this.loadingAzienda = false;
        }, 500);
    }

    navigateToProdotto(prodottoId: number): void {
        this.router.navigate(['/prodotti', prodottoId]);
    }

    /**
     * Helper per navigare ai dettagli del prodotto da oggetto prodotto
     */
    navigateToProdottoObj(prodotto: PublicProdottoSummaryDTO): void {
        const id = getProdottoId(prodotto);
        this.navigateToProdotto(id);
    }

    navigateToAzienda(aziendaId: number): void {
        this.router.navigate(['/aziende', aziendaId]);
    }

    addToCart(): void {
        if (!this.prodotto) return;

        // TODO: Implementare logica carrello
        this.snackBar.open(`"${this.prodotto.nome}" aggiunto al carrello`, 'OK', {
            duration: 3000,
            panelClass: ['success-snackbar']
        });
    }

    retryLoad(): void {
        this.loadProdottoDetail();
    }

    goBack(): void {
        this.router.navigate(['/prodotti']);
    }

    // Metodi helper per il template
    formatCurrency(prezzo: number): string {
        return `€${prezzo.toFixed(2)}`;
    }

    formatDate(dataString: string): string {
        return new Date(dataString).toLocaleDateString('it-IT', {
            day: '2-digit',
            month: '2-digit',
            year: 'numeric'
        });
    }

    isAvailable(quantita: number): boolean {
        return quantita > 0;
    }

    getAvailabilityText(quantita: number): string {
        if (quantita === 0) return 'Non disponibile';
        if (quantita < 5) return `Solo ${quantita} pezzi disponibili`;
        return 'Disponibile';
    }

    getAvailabilityColor(quantita: number): string {
        if (quantita === 0) return 'warn';
        if (quantita < 5) return 'accent';
        return 'primary';
    }

    hasImages(): boolean {
        return !!(this.prodotto?.immagineUrl);
    }

    getMainImage(): string {
        return this.prodotto?.immagineUrl || '/assets/images/placeholder-product.jpg';
    }

    hasCertificazioni(): boolean {
        return !!(this.prodotto?.certificazioniDettagli && this.prodotto.certificazioniDettagli.length > 0);
    }

    hasTracciabilita(): boolean {
        return !!(this.prodotto?.tracciabilita);
    }

    hasMetodoColtivazione(): boolean {
        return !!(this.prodotto?.metodoColtivazione);
    }

    getStatoCertificazione(dataScadenza: string): 'valid' | 'expiring' | 'expired' {
        const oggi = new Date();
        const scadenza = new Date(dataScadenza);
        const giorniAllaScadenza = Math.ceil((scadenza.getTime() - oggi.getTime()) / (1000 * 60 * 60 * 24));

        if (giorniAllaScadenza < 0) return 'expired';
        if (giorniAllaScadenza <= 30) return 'expiring';
        return 'valid';
    }

    getStatoCertificazioneText(dataScadenza: string): string {
        const stato = this.getStatoCertificazione(dataScadenza);
        switch (stato) {
            case 'valid': return 'Valida';
            case 'expiring': return 'In scadenza';
            case 'expired': return 'Scaduta';
            default: return 'Sconosciuto';
        }
    }

    getStatoCertificazioneColor(dataScadenza: string): string {
        const stato = this.getStatoCertificazione(dataScadenza);
        switch (stato) {
            case 'valid': return 'primary';
            case 'expiring': return 'warn';
            case 'expired': return 'warn';
            default: return 'primary';
        }
    }
}