import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatChipsModule } from '@angular/material/chips';

import { PublicProdottiService } from '../../../../core/services/public-prodotti.service';
import { PublicEventiService } from '../../../../core/services/public-eventi.service';
import { PublicProdottoSummaryDTO, getProdottoId } from '../../../../core/models/public.models';
import { PublicEventoSummaryDTO } from '../../../../core/models/public.models';
import { SearchBoxComponent } from '../../shared/components/search/search-box/search-box.component';
import { ProductCardComponent } from '../../shared/components/cards/product-card/product-card.component';

@Component({
    selector: 'app-home',
    standalone: true,
    imports: [
        CommonModule,
        RouterModule,
        MatButtonModule,
        MatCardModule,
        MatIconModule,
        MatProgressSpinnerModule,
        MatSnackBarModule,
        MatFormFieldModule,
        MatInputModule,
        MatChipsModule,
        SearchBoxComponent,
        ProductCardComponent
    ],
    templateUrl: './home.component.html',
    styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit, OnDestroy {
    prodottiInEvidenza: PublicProdottoSummaryDTO[] = [];
    eventiProssimi: PublicEventoSummaryDTO[] = [];
    loadingProdotti = false;
    loadingEventi = false;
    errorProdotti: string | null = null;
    errorEventi: string | null = null;

    private subscriptions = new Map<string, any>();

    constructor(
        private prodottiService: PublicProdottiService,
        private eventiService: PublicEventiService,
        private router: Router,
        private snackBar: MatSnackBar
    ) { }

    ngOnInit(): void {
        this.loadProdottiInEvidenza();
        this.loadEventiProssimi();
    }

    ngOnDestroy(): void {
        // Pulisci tutte le sottoscrizioni
        this.subscriptions.forEach(subscription => {
            if (subscription && subscription.unsubscribe) {
                subscription.unsubscribe();
            }
        });
        this.subscriptions.clear();
    }

    loadProdottiInEvidenza(): void {
        this.loadingProdotti = true;
        this.errorProdotti = null;

        const prodottiSub = this.prodottiService.getProdotti({ page: 0, size: 6 }).subscribe({
            next: (prodotti) => {
                this.prodottiInEvidenza = prodotti.content || [];
                this.loadingProdotti = false;
            },
            error: (error) => {
                console.error('Errore nel caricamento prodotti in evidenza:', error);
                this.errorProdotti = 'Impossibile caricare i prodotti in evidenza. Riprova più tardi.';
                this.loadingProdotti = false;
                this.snackBar.open(this.errorProdotti, 'Chiudi', {
                    duration: 5000,
                    panelClass: ['error-snackbar']
                });
            }
        });

        this.subscriptions.set('prodotti', prodottiSub);
    }

    loadEventiProssimi(): void {
        this.loadingEventi = true;
        this.errorEventi = null;

        const eventiSub = this.eventiService.getEventi({ page: 0, size: 4 }).subscribe({
            next: (eventi) => {
                this.eventiProssimi = eventi.content || [];
                this.loadingEventi = false;
            },
            error: (error) => {
                console.error('Errore nel caricamento eventi prossimi:', error);
                this.errorEventi = 'Impossibile caricare gli eventi prossimi. Riprova più tardi.';
                this.loadingEventi = false;
                this.snackBar.open(this.errorEventi, 'Chiudi', {
                    duration: 5000,
                    panelClass: ['error-snackbar']
                });
            }
        });

        this.subscriptions.set('eventi', eventiSub);
    }

    onProductSearch(query: string): void {
        if (query && query.trim()) {
            this.router.navigate(['/prodotti'], {
                queryParams: { search: query.trim() }
            });
        }
    }

    onEventSearch(query: string): void {
        if (query && query.trim()) {
            this.router.navigate(['/eventi'], {
                queryParams: { search: query.trim() }
            });
        }
    }

    searchProdotti(query: string): void {
        this.onProductSearch(query);
    }

    searchEventi(query: string): void {
        this.onEventSearch(query);
    }

    // Proprietà per compatibilità con il template
    get loading(): boolean {
        return this.loadingProdotti || this.loadingEventi;
    }

    get error(): string | null {
        return this.errorProdotti || this.errorEventi;
    }

    // Metodi helper per il template
    formatCurrency(prezzo: number): string {
        return `€${prezzo.toFixed(2)}`;
    }

    formatDate(dateString: string): string {
        return new Date(dateString).toLocaleDateString('it-IT', {
            day: '2-digit',
            month: '2-digit',
            year: 'numeric',
            hour: '2-digit',
            minute: '2-digit'
        });
    }

    getStatoColor(stato: string): string {
        return this.getEventoStatusColor(stato);
    }

    getStatoLabel(stato: string): string {
        return this.getEventoStatusText(stato);
    }

    navigateToProdotti(): void {
        this.router.navigate(['/prodotti']);
    }

    navigateToEventi(): void {
        this.router.navigate(['/eventi']);
    }

    navigateToProdottoDetail(prodottoId: number): void {
        this.router.navigate(['/prodotti', prodottoId]);
    }

    /**
     * Helper per navigare ai dettagli del prodotto da oggetto prodotto
     */
    navigateToProdotto(prodotto: PublicProdottoSummaryDTO): void {
        const id = getProdottoId(prodotto);
        this.navigateToProdottoDetail(id);
    }

    navigateToEventoDetail(eventoId: number): void {
        this.router.navigate(['/eventi', eventoId]);
    }

    retryLoadProdotti(): void {
        this.loadProdottiInEvidenza();
    }

    retryLoadEventi(): void {
        this.loadEventiProssimi();
    }

    // Metodo per formattare la data dell'evento
    formatEventData(evento: PublicEventoSummaryDTO): { date: string; time: string } {
        if (!evento.dataOraInizio) return { date: '', time: '' };

        const eventDate = new Date(evento.dataOraInizio);
        const date = eventDate.toLocaleDateString('it-IT', {
            day: '2-digit',
            month: '2-digit',
            year: 'numeric'
        });
        const time = eventDate.toLocaleTimeString('it-IT', {
            hour: '2-digit',
            minute: '2-digit'
        });

        return { date, time };
    }

    // Metodo per ottenere lo stato dell'evento come testo
    getEventoStatusText(stato: string): string {
        switch (stato) {
            case 'PROGRAMMATO':
                return 'Programmato';
            case 'IN_CORSO':
                return 'In corso';
            case 'COMPLETATO':
                return 'Completato';
            case 'ANNULLATO':
                return 'Annullato';
            default:
                return stato;
        }
    }

    // Metodo per ottenere il colore dello stato dell'evento
    getEventoStatusColor(stato: string): string {
        switch (stato) {
            case 'PROGRAMMATO':
                return 'primary';
            case 'IN_CORSO':
                return 'accent';
            case 'COMPLETATO':
                return 'primary';
            case 'ANNULLATO':
                return 'warn';
            default:
                return 'primary';
        }
    }
}