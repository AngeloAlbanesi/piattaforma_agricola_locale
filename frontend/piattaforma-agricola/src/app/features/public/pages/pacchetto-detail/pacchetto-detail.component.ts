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
import { MatListModule } from '@angular/material/list';

import { PublicPacchettiService } from '../../../../core/services/public-pacchetti.service';
import { PublicPacchettoDetailDTO, PublicPacchettoSummaryDTO } from '../../../../core/models/public.models';
import { PublicProdottoSummaryDTO } from '../../../../core/models/public.models';
import { PublicAziendaSummaryDTO } from '../../../../core/models/public.models';

@Component({
  selector: 'app-pacchetto-detail',
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
    MatDividerModule,
    MatListModule
  ],
  templateUrl: './pacchetto-detail.component.html',
  styleUrls: ['./pacchetto-detail.component.scss']
})
export class PacchettoDetailComponent implements OnInit, OnDestroy {
  pacchetto: PublicPacchettoDetailDTO | null = null;
  loading = false;
  error: string | null = null;
  pacchettoId: number | null = null;
  
  // Pacchetti correlati
  pacchettiCorrelati: PublicPacchettoSummaryDTO[] = [];
  loadingCorrelati = false;
  
  // Azienda del distributore
  aziendaDistributrice: PublicAziendaSummaryDTO | null = null;
  loadingAzienda = false;

  private subscriptions = new Map<string, any>();

  constructor(
    private pacchettiService: PublicPacchettiService,
    private route: ActivatedRoute,
    private router: Router,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      const id = params.get('id');
      if (id) {
        this.pacchettoId = parseInt(id, 10);
        this.loadPacchettoDetail();
      } else {
        this.error = 'ID pacchetto non valido';
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

  loadPacchettoDetail(): void {
    if (!this.pacchettoId) return;

    this.loading = true;
    this.error = null;

    const pacchettoSub = this.pacchettiService.getPacchettoById(this.pacchettoId).subscribe({
      next: (pacchetto) => {
        this.pacchetto = pacchetto;
        this.loading = false;
        
        // Carica dati correlati
        this.loadPacchettiCorrelati();
        this.loadAziendaDistributrice();
      },
      error: (error: any) => {
        console.error('Errore nel caricamento dettaglio pacchetto:', error);
        this.error = 'Impossibile caricare i dettagli del pacchetto. Riprova più tardi.';
        this.loading = false;
        this.snackBar.open(this.error, 'Chiudi', {
          duration: 5000,
          panelClass: ['error-snackbar']
        });
      }
    });

    this.subscriptions.set('pacchetto', pacchettoSub);
  }

  loadPacchettiCorrelati(): void {
    if (!this.pacchetto?.categoria) return;

    this.loadingCorrelati = true;

    const correlatiSub = this.pacchettiService.getPacchetti({
      categoria: this.pacchetto.categoria,
      page: 0,
      size: 4
    }).subscribe({
      next: (response) => {
        // Filtra il pacchetto corrente dai risultati
        this.pacchettiCorrelati = (response.content || [])
          .filter(p => p.id !== this.pacchetto?.id)
          .slice(0, 3);
        this.loadingCorrelati = false;
      },
      error: (error: any) => {
        console.error('Errore nel caricamento pacchetti correlati:', error);
        this.loadingCorrelati = false;
      }
    });

    this.subscriptions.set('correlati', correlatiSub);
  }

  loadAziendaDistributrice(): void {
    if (!this.pacchetto?.distributore?.id) return;

    this.loadingAzienda = true;

    // Nota: Questo servizio dovrebbe essere implementato nel PublicAziendeService
    // Per ora simuliamo il caricamento
    setTimeout(() => {
      this.aziendaDistributrice = {
        id: this.pacchetto!.distributore!.id,
        nomeAzienda: this.pacchetto!.distributore!.nomeAzienda,
        descrizione: 'Azienda specializzata nella distribuzione di pacchetti agricoli di alta qualità',
        tipologia: 'Distributore',
        indirizzo: {
          via: 'Via Distribuzione, 1',
          citta: 'Città',
          provincia: 'Provincia',
          cap: '12345'
        },
        numeroProdotti: 25,
        rating: 4.7
      };
      this.loadingAzienda = false;
    }, 500);
  }

  navigateToPacchetto(pacchettoId: number): void {
    this.router.navigate(['/pacchetti', pacchettoId]);
  }

  navigateToAzienda(aziendaId: number): void {
    this.router.navigate(['/aziende', aziendaId]);
  }

  navigateToProdotto(prodottoId: number): void {
    this.router.navigate(['/prodotti', prodottoId]);
  }

  addToCart(): void {
    if (!this.pacchetto) return;

    // TODO: Implementare logica carrello
    this.snackBar.open(`"${this.pacchetto.nome}" aggiunto al carrello`, 'OK', {
      duration: 3000,
      panelClass: ['success-snackbar']
    });
  }

  retryLoad(): void {
    this.loadPacchettoDetail();
  }

  goBack(): void {
    this.router.navigate(['/pacchetti']);
  }

  // Metodi helper per il template
  formatCurrency(prezzo: number): string {
    return `€${prezzo.toFixed(2)}`;
  }

  calculateSconto(prezzo: number, prezzoScontato: number): number {
    if (!prezzo || !prezzoScontato || prezzoScontato >= prezzo) return 0;
    return Math.round(((prezzo - prezzoScontato) / prezzo) * 100);
  }

  isAvailable(quantita: number): boolean {
    return quantita > 0;
  }

  getAvailabilityText(quantita: number): string {
    if (quantita === 0) return 'Non disponibile';
    if (quantita < 5) return `Solo ${quantita} pezzi`;
    return 'Disponibile';
  }

  getAvailabilityColor(quantita: number): string {
    if (quantita === 0) return 'warn';
    if (quantita < 5) return 'accent';
    return 'primary';
  }

  hasImages(): boolean {
    return !!(this.pacchetto?.immagineUrl);
  }

  getMainImage(): string {
    return this.pacchetto?.immagineUrl || '/assets/images/placeholder-package.jpg';
  }

  hasSconto(): boolean {
    return !!(this.pacchetto?.prezzoScontato && this.pacchetto.prezzoScontato < this.pacchetto.prezzo);
  }

  getTotalProducts(): number {
    return this.pacchetto?.prodotti?.length || 0;
  }

  getTotalValue(): number {
    if (!this.pacchetto?.prodotti) return 0;
    return this.pacchetto.prodotti.reduce((total, prodotto) => {
      return total + (prodotto.prodotto.prezzo * prodotto.quantita);
    }, 0);
  }

  getSavingsAmount(): number {
    if (!this.hasSconto()) return 0;
    return this.pacchetto!.prezzo - this.pacchetto!.prezzoScontato!;
  }

  getSavingsPercentage(): number {
    return this.calculateSconto(this.pacchetto!.prezzo, this.pacchetto!.prezzoScontato!);
  }

  getProductImage(prodotto: PublicProdottoSummaryDTO): string {
    return prodotto.immagineUrl || '/assets/images/placeholder-product.jpg';
  }

  // Metodi per la gestione dei prodotti nel pacchetto
  getProductQuantity(prodotto: PublicProdottoSummaryDTO): number {
    const elemento = this.pacchetto?.prodotti?.find(p => p.id === prodotto.id);
    return elemento?.quantita || 1;
  }

  getProductTotal(prodotto: PublicProdottoSummaryDTO): number {
    const quantita = this.getProductQuantity(prodotto);
    return prodotto.prezzo * quantita;
  }

  // Metodo per scrollare in cima alla pagina
  scrollToTop(): void {
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }
}