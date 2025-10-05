import { Component, OnInit, OnDestroy, ChangeDetectorRef } from '@angular/core';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDividerModule } from '@angular/material/divider';
import { MatChipsModule } from '@angular/material/chips';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatTabsModule } from '@angular/material/tabs';
import { MatExpansionModule } from '@angular/material/expansion';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';

import { PublicAziendeService } from '../../../../core/services/public-aziende.service';
import { PublicAziendaDetailDTO } from '../../../../core/models/public.models';

@Component({
  selector: 'app-azienda-detail',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatDividerModule,
    MatChipsModule,
    MatProgressSpinnerModule,
    MatTooltipModule,
    MatSnackBarModule,
    MatTabsModule,
    MatExpansionModule
  ],
  templateUrl: './azienda-detail.component.html',
  styleUrls: ['./azienda-detail.component.scss']
})
export class AziendaDetailComponent implements OnInit, OnDestroy {
  azienda: PublicAziendaDetailDTO | null = null;
  isLoading = true;
  error: string | null = null;
  aziendaId: number | null = null;
  
  // Gestione mappa
  mapCenter: { lat: number; lng: number } | null = null;
  mapZoom = 15;
  showMap = false;
  mapError: string | null = null;

  // Distanza calcolata
  userLocation: { lat: number; lng: number } | null = null;
  calculatedDistance: number | null = null;
  isLoadingDistance = false;

  // Tab selezionata
  selectedTab = 0;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private aziendeService: PublicAziendeService,
    private sanitizer: DomSanitizer,
    private cdr: ChangeDetectorRef,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.loadAzienda();
    this.getUserLocation();
  }

  ngOnDestroy(): void {
    // Cleanup if needed
  }

  private loadAzienda(): void {
    this.isLoading = true;
    this.error = null;

    // Ottieni ID azienda dai parametri del route
    this.route.paramMap.subscribe(params => {
      const id = params.get('id');
      if (id) {
        this.aziendaId = +id;
        this.loadAziendaData();
      } else {
        this.error = 'ID azienda non valido';
        this.isLoading = false;
      }
    });
  }

  private loadAziendaData(): void {
    if (!this.aziendaId) return;

    this.aziendeService.getAziendaById(this.aziendaId).subscribe({
      next: (azienda) => {
        this.azienda = azienda;
        this.setupMap();
        this.isLoading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Errore nel caricamento dell\'azienda:', err);
        this.error = 'Impossibile caricare i dettagli dell\'azienda. Riprova più tardi.';
        this.isLoading = false;
        this.cdr.detectChanges();
      }
    });
  }

  private setupMap(): void {
    if (!this.azienda?.coordinate) {
      this.mapError = 'Coordinate non disponibili per questa azienda';
      return;
    }

    this.mapCenter = {
      lat: this.azienda.coordinate.latitudine,
      lng: this.azienda.coordinate.longitudine
    };
    
    this.showMap = true;
  }

  private getUserLocation(): void {
    if (!navigator.geolocation) {
      console.warn('Geolocalizzazione non supportata dal browser');
      return;
    }

    navigator.geolocation.getCurrentPosition(
      (position) => {
        this.userLocation = {
          lat: position.coords.latitude,
          lng: position.coords.longitude
        };
        
        if (this.azienda) {
          this.calculateDistance();
        }
      },
      (error) => {
        console.warn('Impossibile ottenere la posizione utente:', error);
      }
    );
  }

  calculateDistance(): void {
    if (!this.userLocation || !this.aziendaId) return;

    this.isLoadingDistance = true;

    this.aziendeService.calcolaDistanza(this.aziendaId, `${this.userLocation.lat}, ${this.userLocation.lng}`).subscribe({
      next: (distanceData: any) => {
        this.calculatedDistance = distanceData.distanzaKm;
        this.isLoadingDistance = false;
        this.cdr.detectChanges();
      },
      error: (err: any) => {
        console.error('Errore nel calcolo della distanza:', err);
        this.isLoadingDistance = false;
        this.cdr.detectChanges();
      }
    });
  }

  getFormattedAddress(indirizzo: { via: string; citta: string; provincia: string; cap: string }): string {
    return `${indirizzo.via}, ${indirizzo.cap} ${indirizzo.citta} (${indirizzo.provincia})`;
  }

  getFormattedDate(dateString: string): string {
    const date = new Date(dateString);
    const options: Intl.DateTimeFormatOptions = {
      year: 'numeric',
      month: 'long',
      day: 'numeric'
    };
    return date.toLocaleDateString('it-IT', options);
  }

  getTipologiaLabel(tipologia: string): string {
    const tipologiaMap: { [key: string]: string } = {
      'PRODUZIONE': 'Produzione',
      'TRASFORMAZIONE': 'Trasformazione',
      'DISTRIBUZIONE': 'Distribuzione'
    };
    return tipologiaMap[tipologia] || tipologia;
  }

  getTipologiaIcon(tipologia: string): string {
    const iconMap: { [key: string]: string } = {
      'PRODUZIONE': 'agriculture',
      'TRASFORMAZIONE': 'factory',
      'DISTRIBUZIONE': 'local_shipping'
    };
    return iconMap[tipologia] || 'business';
  }

  getTipologiaColor(tipologia: string): string {
    const colorMap: { [key: string]: string } = {
      'PRODUZIONE': '#4caf50',  // verde
      'TRASFORMAZIONE': '#ff9800', // arancione
      'DISTRIBUZIONE': '#2196f3'   // blu
    };
    return colorMap[tipologia] || '#757575';
  }

  getRatingStars(rating: number): number[] {
    const stars = [];
    const fullStars = Math.floor(rating);
    const hasHalfStar = rating % 1 >= 0.5;

    for (let i = 0; i < fullStars; i++) {
      stars.push(1);
    }
    
    if (hasHalfStar && fullStars < 5) {
      stars.push(0.5);
    }
    
    const emptyStars = 5 - stars.length;
    for (let i = 0; i < emptyStars; i++) {
      stars.push(0);
    }
    
    return stars;
  }

  getRatingColor(rating: number): string {
    if (rating >= 4.5) return '#4caf50';  // verde
    if (rating >= 3.5) return '#ff9800';  // arancione
    if (rating >= 2.5) return '#ffc107';  // giallo
    return '#f44336'; // rosso
  }

  sanitizeHtml(html: string): SafeHtml {
    return this.sanitizer.bypassSecurityTrustHtml(html);
  }

  hasDescription(): boolean {
    return !!this.azienda?.descrizione && this.azienda.descrizione.trim().length > 0;
  }

  hasContactInfo(): boolean {
    return !!(this.azienda?.telefono || this.azienda?.email || this.azienda?.sito);
  }

  hasCertificazioni(): boolean {
    return !!(this.azienda?.certificazioni && this.azienda.certificazioni.length > 0);
  }

  hasProdotti(): boolean {
    return !!(this.azienda?.prodotti && this.azienda.prodotti.content.length > 0);
  }

  hasSocial(): boolean {
    return !!(this.azienda?.social && this.azienda.social.length > 0);
  }

  hasStoria(): boolean {
    return !!this.azienda?.storia && this.azienda.storia.trim().length > 0;
  }

  hasValori(): boolean {
    return !!(this.azienda?.valori && this.azienda.valori.length > 0);
  }

  retry(): void {
    this.loadAzienda();
  }

  goBack(): void {
    this.router.navigate(['/aziende']);
  }

  shareAzienda(): void {
    if (!this.azienda) return;

    if (navigator.share) {
      navigator.share({
        title: this.azienda.nomeAzienda,
        text: this.azienda.descrizione?.substring(0, 200) + '...',
        url: window.location.href
      }).catch(err => console.log('Errore nella condivisione:', err));
    } else {
      // Fallback: copia negli appunti
      this.copyToClipboard(window.location.href);
    }
  }

  private copyToClipboard(text: string): void {
    navigator.clipboard.writeText(text).then(() => {
      this.snackBar.open('Link copiato negli appunti!', 'Chiudi', {
        duration: 3000,
        horizontalPosition: 'center',
        verticalPosition: 'bottom'
      });
    }).catch(err => {
      console.error('Errore nella copia negli appunti:', err);
    });
  }

  openGoogleMaps(): void {
    if (!this.azienda?.coordinate) return;

    const { latitudine, longitudine } = this.azienda.coordinate;
    window.open(`https://www.google.com/maps/search/?api=1&query=${latitudine},${longitudine}`, '_blank');
  }

  openDirections(): void {
    if (!this.azienda?.coordinate) return;

    const { latitudine, longitudine } = this.azienda.coordinate;
    
    if (this.userLocation) {
      // Direzioni dalla posizione utente
      window.open(`https://www.google.com/maps/dir/${this.userLocation.lat},${this.userLocation.lng}/${latitudine},${longitudine}`, '_blank');
    } else {
      // Direzioni generiche
      window.open(`https://www.google.com/maps/dir/?api=1&destination=${latitudine},${longitudine}`, '_blank');
    }
  }

  openExternalLink(url: string): void {
    if (!url) return;
    
    // Assicura che l'URL abbia il protocollo
    const fullUrl = url.startsWith('http') ? url : `https://${url}`;
    window.open(fullUrl, '_blank');
  }

  sendEmail(): void {
    if (!this.azienda?.email) return;

    const subject = encodeURIComponent(`Informazioni sull'azienda: ${this.azienda.nomeAzienda}`);
    const body = encodeURIComponent(`Buongiorno,\n\nVorrei ricevere maggiori informazioni sulla vostra azienda "${this.azienda.nomeAzienda}".\n\nGrazie.`);
    
    window.open(`mailto:${this.azienda.email}?subject=${subject}&body=${body}`);
  }

  callPhone(): void {
    if (!this.azienda?.telefono) return;
    window.open(`tel:${this.azienda.telefono}`);
  }

  getSocialIcon(tipo: string): string {
    const iconMap: { [key: string]: string } = {
      'facebook': 'facebook',
      'instagram': 'photo_camera',
      'twitter': 'alternate_email',
      'linkedin': 'work',
      'youtube': 'smart_display'
    };
    return iconMap[tipo] || 'language';
  }

  getSocialLabel(tipo: string): string {
    const labelMap: { [key: string]: string } = {
      'facebook': 'Facebook',
      'instagram': 'Instagram',
      'twitter': 'Twitter',
      'linkedin': 'LinkedIn',
      'youtube': 'YouTube'
    };
    return labelMap[tipo] || tipo;
  }

  viewProdottoDetail(prodottoId: number): void {
    this.router.navigate(['/prodotti', prodottoId]);
  }

  // Metodi per accessibilità
  getAriaLabelForProdotto(prodotto: any): string {
    return `Prodotto: ${prodotto.nome}, Prezzo: €${prodotto.prezzo}, ${prodotto.quantitaDisponibile} unità disponibili`;
  }

  // Gestione tab
  onTabChange(event: any): void {
    this.selectedTab = event.index;
  }

  // Ricarica distanza quando cambia la posizione utente
  refreshDistance(): void {
    this.getUserLocation();
  }
}