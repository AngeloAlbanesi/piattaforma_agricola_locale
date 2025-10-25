import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import {
  PublicProdottoSummaryDTO,
  PublicProdottoDetailDTO,
  PublicProdottiResponse,
  PublicProdottoFilters,
  CertificazioneProdottoDTO,
  MetodoColtivazioneDTO,
  TracciabilitaProdottoDTO
} from '../models/public.models';

/**
 * Servizio per la gestione delle API pubbliche dei prodotti
 * Fornisce accesso ai dati dei prodotti visibili pubblicamente
 */
@Injectable({
  providedIn: 'root'
})
export class PublicProdottiService {
  private readonly apiUrl = this.buildApiUrl('');

  constructor(private http: HttpClient) {}

  // === METODI PRINCIPALI ===

  /**
   * Ottiene tutti i prodotti pubblici con paginazione e filtri opzionali
   */
  getProdotti(filters?: PublicProdottoFilters): Observable<PublicProdottiResponse> {
    const params = this.buildParamsFromFilters(filters);
    return this.http.get<PublicProdottiResponse>(`${this.apiUrl}/prodotti`, { params });
  }

  /**
   * Ottiene i dettagli di un prodotto specifico
   */
  getProdottoById(id: number): Observable<PublicProdottoDetailDTO> {
    return this.http.get<PublicProdottoDetailDTO>(`${this.apiUrl}/prodotti/${id}`);
  }

  /**
   * Cerca prodotti in base a una query testuale
   */
  cercaProdotti(query: string, filters?: Omit<PublicProdottoFilters, 'query'>): Observable<PublicProdottiResponse> {
    const params = this.buildParamsFromFilters({ ...filters, query });
    return this.http.get<PublicProdottiResponse>(`${this.apiUrl}/prodotti/cercaProdotti`, { params });
  }

  /**
   * Ottiene i prodotti di un venditore specifico
   */
  getProdottiByVenditore(venditoreId: number, filters?: Omit<PublicProdottoFilters, 'produttoreId'>): Observable<PublicProdottiResponse> {
    const params = this.buildParamsFromFilters({ ...filters, produttoreId: venditoreId });
    return this.http.get<PublicProdottiResponse>(`${this.apiUrl}/prodotti/venditori/${venditoreId}`, { params });
  }

  // === METODI PER CERTIFICAZIONI E TRACCIABILITÀ ===

  /**
   * Ottiene le certificazioni di un prodotto specifico
   */
  getCertificazioniProdotto(prodottoId: number): Observable<CertificazioneProdottoDTO[]> {
    return this.http.get<CertificazioneProdottoDTO[]>(`${this.apiUrl}/prodotti/${prodottoId}/certificazioni`);
  }

  /**
   * Ottiene il metodo di coltivazione di un prodotto specifico
   */
  getMetodoColtivazione(prodottoId: number): Observable<MetodoColtivazioneDTO> {
    return this.http.get<MetodoColtivazioneDTO>(`${this.apiUrl}/prodotti/${prodottoId}/metodi-coltivazione`);
  }

  /**
   * Ottiene la tracciabilità completa di un prodotto
   */
  getTracciabilitaProdotto(prodottoId: number): Observable<TracciabilitaProdottoDTO> {
    return this.http.get<TracciabilitaProdottoDTO>(`${this.apiUrl}/prodotti/${prodottoId}/tracciabilita`);
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
  private buildParamsFromFilters(filters?: PublicProdottoFilters): HttpParams {
    let params = new HttpParams();

    if (filters) {
      if (filters.query) {
        params = params.set('query', filters.query);
      }
      if (filters.categoria) {
        params = params.set('categoria', filters.categoria);
      }
      if (filters.prezzoMin !== undefined) {
        params = params.set('prezzoMin', filters.prezzoMin.toString());
      }
      if (filters.prezzoMax !== undefined) {
        params = params.set('prezzoMax', filters.prezzoMax.toString());
      }
      if (filters.produttoreId) {
        params = params.set('produttoreId', filters.produttoreId.toString());
      }
      if (filters.certificazione) {
        params = params.set('certificazione', filters.certificazione);
      }
      if (filters.luogoOrigine) {
        params = params.set('luogoOrigine', filters.luogoOrigine);
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
   * Restituisce l'etichetta per la categoria del prodotto
   */
  getCategoriaLabel(categoria: string): string {
    const labels: Record<string, string> = {
      'FRUTTA': 'Frutta',
      'VERDURA': 'Verdura',
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
      'ALTRO': 'Altro'
    };
    return labels[categoria] || categoria;
  }

  /**
   * Restituisce l'etichetta per l'unità di misura
   */
  getUnitaMisuraLabel(unita: string): string {
    const labels: Record<string, string> = {
      'KG': 'kg',
      'G': 'g',
      'L': 'L',
      'ML': 'ml',
      'PZ': 'pezzi',
      'M': 'm',
      'CM': 'cm'
    };
    return labels[unita] || unita;
  }

  /**
   * Verifica se un prodotto è disponibile
   */
  isDisponibile(prodotto: PublicProdottoSummaryDTO): boolean {
    return prodotto.quantitaDisponibile > 0;
  }

  /**
   * Verifica se un prodotto ha certificazioni
   */
  hasCertificazioni(prodotto: PublicProdottoSummaryDTO): boolean {
    return !!(prodotto.certificazioni && prodotto.certificazioni.length > 0);
  }

  /**
   * Calcola il prezzo scontato se presente
   */
  calculatePrezzoScontato(prezzo: number, sconto: number): number {
    return prezzo * (1 - sconto / 100);
  }

  /**
   * Genera l'URL per l'immagine del prodotto
   */
  getProdottoImageUrl(prodotto: PublicProdottoSummaryDTO): string {
    return prodotto.immagineUrl || '';
  }
}