import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import {
  PublicPacchettoSummaryDTO,
  PublicPacchettoDetailDTO,
  PublicPacchettiResponse,
  PublicPacchettoFilters,
  ComposizionePacchettoDTO,
  ElementoPacchettoDTO
} from '../models/public.models';

/**
 * Servizio per la gestione delle API pubbliche dei pacchetti
 * Fornisce accesso ai dati dei pacchetti visibili pubblicamente
 */
@Injectable({
  providedIn: 'root'
})
export class PublicPacchettiService {
  private readonly apiUrl = this.buildApiUrl('');

  constructor(private http: HttpClient) {}

  // === METODI PRINCIPALI ===

  /**
   * Ottiene tutti i pacchetti pubblici con paginazione e filtri opzionali
   */
  getPacchetti(filters?: PublicPacchettoFilters): Observable<PublicPacchettiResponse> {
    const params = this.buildParamsFromFilters(filters);
    return this.http.get<PublicPacchettiResponse>(`${this.apiUrl}/pacchetti`, { params });
  }

  /**
   * Ottiene i dettagli di un pacchetto specifico
   */
  getPacchettoById(id: number): Observable<PublicPacchettoDetailDTO> {
    return this.http.get<PublicPacchettoDetailDTO>(`${this.apiUrl}/pacchetti/${id}`);
  }

  /**
   * Cerca pacchetti in base a una query testuale
   */
  cercaPacchetti(query: string, filters?: Omit<PublicPacchettoFilters, 'query'>): Observable<PublicPacchettiResponse> {
    const params = this.buildParamsFromFilters({ ...filters, query });
    return this.http.get<PublicPacchettiResponse>(`${this.apiUrl}/pacchetti/cercaPacchetti`, { params });
  }

  /**
   * Ottiene i pacchetti di un distributore specifico
   */
  getPacchettiByDistributore(distributoreId: number, filters?: Omit<PublicPacchettoFilters, 'distributoreId'>): Observable<PublicPacchettiResponse> {
    const params = this.buildParamsFromFilters({ ...filters, distributoreId });
    return this.http.get<PublicPacchettiResponse>(`${this.apiUrl}/pacchetti/distributori/${distributoreId}`, { params });
  }

  /**
   * Ottiene la composizione dettagliata di un pacchetto
   */
  getComposizionePacchetto(pacchettoId: number): Observable<ComposizionePacchettoDTO[]> {
    return this.http.get<ComposizionePacchettoDTO[]>(`${this.apiUrl}/pacchetti/${pacchettoId}/composizione`);
  }

  // === METODI UTILITARI ===

  /**
   * Costruisce l'URL base per le API
   */
  private buildApiUrl(path: string): string {
    const base = (environment.apiBaseUrl ?? '').replace(/\/$/, '');
    const prefix = (environment.apiPrefix ?? '').replace(/\/$/, '');
    // Fix: se path è vuoto, non aggiungere slash
    const sanitizedPath = path ? (path.startsWith('/') ? path : `/${path}`) : '';

    if (base) {
      return `${base}${prefix}${sanitizedPath}`;
    }

    return `${prefix || ''}${sanitizedPath}` || sanitizedPath;
  }

  /**
   * Converte i filtri in HttpParams
   */
  private buildParamsFromFilters(filters?: PublicPacchettoFilters): HttpParams {
    let params = new HttpParams();

    if (filters) {
      if (filters.query) {
        params = params.set('query', filters.query);
      }
      if (filters.distributoreId) {
        params = params.set('distributorId', filters.distributoreId.toString());
      }
      if (filters.prezzoMin !== undefined) {
        params = params.set('prezzoMin', filters.prezzoMin.toString());
      }
      if (filters.prezzoMax !== undefined) {
        params = params.set('prezzoMax', filters.prezzoMax.toString());
      }
      if (filters.categoria) {
        params = params.set('categoria', filters.categoria);
      }
      if (filters.disponibilita !== undefined) {
        params = params.set('disponibilita', filters.disponibilita.toString());
      }
      if (filters.page !== undefined) {
        params = params.set('page', filters.page.toString());
      }
      if (filters.size !== undefined) {
        params = params.set('size', filters.size.toString());
      }
      if (filters.sortBy) {
        params = params.set('sortBy', filters.sortBy);
      }
    }

    return params;
  }

  // === FORMATTERS ===

  /**
   * Formatta il prezzo in formato valuta italiana
   */
  formatCurrency(value: number): string {
    return new Intl.NumberFormat('it-IT', {
      style: 'currency',
      currency: 'EUR',
      minimumFractionDigits: 2,
      maximumFractionDigits: 2
    }).format(value);
  }

  /**
   * Formatta la data in formato italiano
   */
  formatDate(date: string): string {
    return new Date(date).toLocaleDateString('it-IT', {
      year: 'numeric',
      month: 'long',
      day: 'numeric'
    });
  }

  /**
   * Formatta la data e ora in formato italiano
   */
  formatDateTime(date: string): string {
    return new Date(date).toLocaleString('it-IT', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  }

  /**
   * Restituisce l'etichetta per la categoria del pacchetto
   */
  getCategoriaLabel(categoria: string): string {
    const labels: Record<string, string> = {
      'FRUTTA': 'Frutta',
      'VERDURA': 'Verdura',
      'MISTO': 'Misto',
      'LATTE_DERIVATI': 'Latte e Derivati',
      'CARNE': 'Carne',
      'PESCE': 'Pesce',
      'CEREALI': 'Cereali',
      'LEGUMI': 'Legumi',
      'CONSERVE': 'Conserve',
      'OLIO': 'Olio',
      'VINO': 'Vino',
      'FORMAGGI': 'Formaggi',
      'PANE': 'Pane e Panificati',
      'DOLCI': 'Dolci e Pasticceria',
      'BIO': 'Biologico',
      'KM0': 'Km0',
      'STAGIONALE': 'Stagionale',
      'REGALO': 'Regalo',
      'ALTRO': 'Altro'
    };
    return labels[categoria] || categoria;
  }

  /**
   * Calcola il prezzo scontato se presente
   */
  calculatePrezzoScontato(prezzo: number, sconto: number): number {
    return prezzo * (1 - sconto / 100);
  }

  /**
   * Calcola il risparmio in euro
   */
  calculateRisparmio(prezzo: number, sconto: number): number {
    return prezzo * (sconto / 100);
  }

  /**
   * Verifica se un pacchetto è disponibile
   */
  isDisponibile(pacchetto: PublicPacchettoSummaryDTO): boolean {
    return pacchetto.quantitaDisponibile > 0;
  }

  /**
   * Verifica se un pacchetto ha uno sconto
   */
  hasSconto(pacchetto: PublicPacchettoSummaryDTO): boolean {
    return !!(pacchetto.sconto && pacchetto.sconto > 0);
  }

  /**
   * Verifica se un pacchetto è in scadenza
   */
  isInScadenza(pacchetto: PublicPacchettoDetailDTO, giorniThreshold: number = 7): boolean {
    if (!pacchetto.dataScadenza) {
      return false;
    }
    
    const dataScadenza = new Date(pacchetto.dataScadenza);
    const oggi = new Date();
    const differenzaGiorni = Math.ceil((dataScadenza.getTime() - oggi.getTime()) / (1000 * 60 * 60 * 24));
    
    return differenzaGiorni <= giorniThreshold && differenzaGiorni > 0;
  }

  /**
   * Genera l'URL per l'immagine del pacchetto con fallback
   */
  getPacchettoImageUrl(pacchetto: PublicPacchettoSummaryDTO): string {
    if (pacchetto.immagineUrl) {
      return pacchetto.immagineUrl;
    }
    
    // Fallback basato sulla categoria
    const categoryImages: Record<string, string> = {
      'FRUTTA': '/assets/images/placeholders/pacchetto-frutta.jpg',
      'VERDURA': '/assets/images/placeholders/pacchetto-verdura.jpg',
      'MISTO': '/assets/images/placeholders/pacchetto-misto.jpg',
      'BIO': '/assets/images/placeholders/pacchetto-bio.jpg',
      'KM0': '/assets/images/placeholders/pacchetto-km0.jpg',
      'STAGIONALE': '/assets/images/placeholders/pacchetto-stagionale.jpg',
      'REGALO': '/assets/images/placeholders/pacchetto-regalo.jpg'
    };
    
    return categoryImages[pacchetto.categoria || ''] || '/assets/images/placeholders/pacchetto-generico.jpg';
  }

  /**
   * Calcola il valore totale dei prodotti in un pacchetto
   */
  calculateValoreProdotti(prodotti: ElementoPacchettoDTO[]): number {
    return prodotti.reduce((totale, elemento) => {
      return totale + (elemento.prodotto.prezzo * elemento.quantita);
    }, 0);
  }

  /**
   * Calcola il risparmio percentuale del pacchetto
   */
  calculateRisparmioPercentuale(pacchetto: PublicPacchettoDetailDTO): number {
    const valoreProdotti = this.calculateValoreProdotti(pacchetto.prodotti);
    if (valoreProdotti === 0) return 0;
    
    return Math.round(((valoreProdotti - pacchetto.prezzo) / valoreProdotti) * 100);
  }

  /**
   * Verifica se un pacchetto contiene prodotti di una certa categoria
   */
  contieneCategoria(pacchetto: PublicPacchettoDetailDTO, categoria: string): boolean {
    return pacchetto.prodotti.some(elemento => 
      elemento.prodotto.categoria === categoria
    );
  }

  /**
   * Ottiene le categorie uniche dei prodotti in un pacchetto
   */
  getCategorieUniche(pacchetto: PublicPacchettoDetailDTO): string[] {
    const categorie = pacchetto.prodotti.map(elemento => elemento.prodotto.categoria).filter(Boolean) as string[];
    return [...new Set(categorie)];
  }

  /**
   * Verifica se un pacchetto contiene prodotti biologici
   */
  contieneProdottiBiologici(pacchetto: PublicPacchettoDetailDTO): boolean {
    return pacchetto.prodotti.some(elemento => 
      elemento.prodotto.certificazioni?.includes('BIOLOGICO')
    );
  }

  /**
   * Verifica se un pacchetto contiene prodotti DOP/IGP
   */
  contieneProdottiDOP(pacchetto: PublicPacchettoDetailDTO): boolean {
    return pacchetto.prodotti.some(elemento => 
      elemento.prodotto.certificazioni?.some(cert => 
        cert === 'DOP' || cert === 'IGP'
      )
    );
  }
}