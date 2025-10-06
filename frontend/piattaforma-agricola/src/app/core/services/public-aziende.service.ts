import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import {
  PublicAziendaSummaryDTO,
  PublicAziendaDetailDTO,
  PublicAziendeResponse,
  PublicAziendaFilters,
  CertificazioneAziendaDTO,
  CoordinateDTO,
  DistanzaDTO,
  CalcolaDistanzaRequest,
  PublicProdottoSummaryDTO
} from '../models/public.models';

/**
 * Servizio per la gestione delle API pubbliche delle aziende
 * Fornisce accesso ai dati delle aziende visibili pubblicamente
 */
@Injectable({
  providedIn: 'root'
})
export class PublicAziendeService {
  private readonly apiUrl = this.buildApiUrl('');

  constructor(private http: HttpClient) {}

  // === METODI PRINCIPALI ===

  /**
   * Ottiene tutte le aziende pubbliche con paginazione e filtri opzionali
   */
  getAziende(filters?: PublicAziendaFilters): Observable<PublicAziendeResponse> {
    const params = this.buildParamsFromFilters(filters);
    return this.http.get<PublicAziendeResponse>(`${this.apiUrl}/azienda/tutteLeAziende`, { params });
  }

  /**
   * Ottiene i dettagli di un'azienda specifica
   */
  getAziendaById(id: number): Observable<PublicAziendaDetailDTO> {
    return this.http.get<PublicAziendaDetailDTO>(`${this.apiUrl}/azienda/${id}`);
  }

  /**
   * Cerca aziende in base a una query testuale
   */
  cercaAziende(query: string, filters?: Omit<PublicAziendaFilters, 'query'>): Observable<PublicAziendeResponse> {
    const params = this.buildParamsFromFilters({ ...filters, query });
    return this.http.get<PublicAziendeResponse>(`${this.apiUrl}/azienda/cercaAzienda`, { params });
  }

  /**
   * Ottiene le certificazioni di un'azienda specifica
   */
  getCertificazioniAzienda(aziendaId: number): Observable<CertificazioneAziendaDTO[]> {
    return this.http.get<CertificazioneAziendaDTO[]>(`${this.apiUrl}/azienda/${aziendaId}/certificazioni`);
  }

  /**
   * Ottiene i prodotti di un'azienda specifica
   */
  getProdottiAzienda(aziendaId: number, filters?: { page?: number; size?: number }): Observable<{ content: PublicProdottoSummaryDTO[] }> {
    let params = new HttpParams();
    
    if (filters) {
      if (filters.page !== undefined) {
        params = params.set('page', filters.page.toString());
      }
      if (filters.size !== undefined) {
        params = params.set('size', filters.size.toString());
      }
    }
    
    return this.http.get<{ content: PublicProdottoSummaryDTO[] }>(`${this.apiUrl}/azienda/${aziendaId}/prodotti`, { params });
  }

  /**
   * Ottiene le coordinate geografiche di un'azienda
   */
  getGeocodeAzienda(aziendaId: number): Observable<CoordinateDTO> {
    return this.http.get<CoordinateDTO>(`${this.apiUrl}/azienda/${aziendaId}/geocode`);
  }

  /**
   * Calcola la distanza da un'azienda a un indirizzo specificato
   */
  calcolaDistanza(aziendaId: number, indirizzoPartenza: string): Observable<DistanzaDTO> {
    const params = new HttpParams().set('partenza', indirizzoPartenza);
    return this.http.get<DistanzaDTO>(`${this.apiUrl}/azienda/${aziendaId}/distanza`, { params });
  }

  // === METODI UTILITARI ===

  /**
   * Costruisce l'URL base per le API
   */
  private buildApiUrl(path: string): string {
    const base = (environment.apiBaseUrl ?? '').replace(/\/$/, '');
    const prefix = (environment.apiPrefix ?? '').replace(/\/$/, '');
    const sanitizedPath = path.startsWith('/') ? path : `/${path}`;

    if (base) {
      return `${base}${prefix}${sanitizedPath}`;
    }

    return `${prefix || ''}${sanitizedPath}` || sanitizedPath;
  }

  /**
   * Converte i filtri in HttpParams
   */
  private buildParamsFromFilters(filters?: PublicAziendaFilters): HttpParams {
    let params = new HttpParams();

    if (filters) {
      if (filters.query) {
        params = params.set('query', filters.query);
      }
      if (filters.tipologia) {
        params = params.set('tipologia', filters.tipologia);
      }
      if (filters.citta) {
        params = params.set('citta', filters.citta);
      }
      if (filters.provincia) {
        params = params.set('provincia', filters.provincia);
      }
      if (filters.certificazione) {
        params = params.set('certificazione', filters.certificazione);
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
   * Restituisce l'etichetta per la tipologia di azienda
   */
  getTipologiaLabel(tipologia: string): string {
    const labels: Record<string, string> = {
      'PRODUZIONE': 'Azienda Agricola',
      'TRASFORMAZIONE': 'Azienda di Trasformazione',
      'DISTRIBUZIONE': 'Azienda di Distribuzione'
    };
    return labels[tipologia] || tipologia;
  }

  /**
   * Restituisce l'icona per la tipologia di azienda
   */
  getTipologiaIcon(tipologia: string): string {
    const icons: Record<string, string> = {
      'PRODUZIONE': 'agriculture',
      'TRASFORMAZIONE': 'factory',
      'DISTRIBUZIONE': 'store'
    };
    return icons[tipologia] || 'business';
  }

  /**
   * Formatta l'indirizzo completo dell'azienda
   */
  formatIndirizzoCompleto(azienda: PublicAziendaSummaryDTO | PublicAziendaDetailDTO): string {
    return `${azienda.indirizzo.via}, ${azienda.indirizzo.cap} ${azienda.indirizzo.citta} (${azienda.indirizzo.provincia})`;
  }

  /**
   * Estrae la città dall'indirizzo
   */
  getCittaFromIndirizzo(azienda: PublicAziendaSummaryDTO | PublicAziendaDetailDTO): string {
    return azienda.indirizzo.citta;
  }

  /**
   * Estrae la provincia dall'indirizzo
   */
  getProvinciaFromIndirizzo(azienda: PublicAziendaSummaryDTO | PublicAziendaDetailDTO): string {
    return azienda.indirizzo.provincia;
  }

  /**
   * Formatta il rating con stelle
   */
  formatRating(rating: number): string {
    const fullStars = Math.floor(rating);
    const halfStar = rating % 1 >= 0.5 ? 1 : 0;
    const emptyStars = 5 - fullStars - halfStar;
    
    return '★'.repeat(fullStars) + (halfStar ? '☆' : '') + '☆'.repeat(emptyStars);
  }

  /**
   * Formatta la distanza in km
   */
  formatDistanza(distanzaKm: number): string {
    if (distanzaKm < 1) {
      return `${Math.round(distanzaKm * 1000)} m`;
    }
    return `${distanzaKm.toFixed(1)} km`;
  }

  /**
   * Genera l'URL per il logo dell'azienda con fallback
   */
  getAziendaLogoUrl(azienda: PublicAziendaSummaryDTO | PublicAziendaDetailDTO): string {
    if (azienda.logo) {
      return azienda.logo;
    }
    
    // Fallback basato sulla tipologia
    const logoFallbacks: Record<string, string> = {
      'PRODUZIONE': '/assets/images/placeholders/logo-azienda-agricola.png',
      'TRASFORMAZIONE': '/assets/images/placeholders/logo-azienda-trasformazione.png',
      'DISTRIBUZIONE': '/assets/images/placeholders/logo-azienda-distribuzione.png'
    };
    
    return logoFallbacks[azienda.tipologia] || '/assets/images/placeholders/logo-azienda-generico.png';
  }

  /**
   * Verifica se un'azienda ha certificazioni
   */
  hasCertificazioni(azienda: PublicAziendaDetailDTO): boolean {
    return !!(azienda.certificazioni && azienda.certificazioni.length > 0);
  }

  /**
   * Verifica se un'azienda ha prodotti
   */
  hasProdotti(azienda: PublicAziendaDetailDTO): boolean {
    return !!(azienda.prodotti && azienda.prodotti.content && azienda.prodotti.content.length > 0);
  }

  /**
   * Verifica se un'azienda ha coordinate geografiche
   */
  hasCoordinate(azienda: PublicAziendaDetailDTO): boolean {
    return !!(azienda.coordinate && azienda.coordinate.latitudine && azienda.coordinate.longitudine);
  }

  /**
   * Verifica se un'azienda ha contatti (email, telefono, sito)
   */
  hasContatti(azienda: PublicAziendaDetailDTO): boolean {
    return !!(azienda.email || azienda.telefono || azienda.sito);
  }

  /**
   * Verifica se un'azienda ha profili social
   */
  hasSocial(azienda: PublicAziendaDetailDTO): boolean {
    return !!(azienda.social && azienda.social.length > 0);
  }

  /**
   * Verifica se un'azienda ha una certificazione specifica
   */
  hasCertificazione(azienda: PublicAziendaDetailDTO, tipoCertificazione: string): boolean {
    if (!azienda.certificazioni) return false;
    return azienda.certificazioni.some(cert => 
      cert.tipoCertificazione.toLowerCase().includes(tipoCertificazione.toLowerCase())
    );
  }

  /**
   * Verifica se un'azienda è certificata come biologica
   */
  isBiologica(azienda: PublicAziendaDetailDTO): boolean {
    return this.hasCertificazione(azienda, 'BIOLOGICO');
  }

  /**
   * Verifica se un'azienda ha prodotti DOP/IGP
   */
  hasProdottiDOP(azienda: PublicAziendaDetailDTO): boolean {
    if (!azienda.prodotti || !azienda.prodotti.content) return false;
    return azienda.prodotti.content.some(prodotto => 
      prodotto.certificazioni?.some(cert => 
        cert === 'DOP' || cert === 'IGP'
      )
    );
  }

  /**
   * Calcola gli anni di attività di un'azienda
   */
  getAnniAttivita(azienda: PublicAziendaDetailDTO): number {
    if (!azienda.dataFondazione) return 0;
    
    const dataFondazione = new Date(azienda.dataFondazione);
    const oggi = new Date();
    return oggi.getFullYear() - dataFondazione.getFullYear();
  }

  /**
   * Formatta gli anni di attività in testo
   */
  formatAnniAttivita(azienda: PublicAziendaDetailDTO): string {
    const anni = this.getAnniAttivita(azienda);
    if (anni === 0) return 'Attività recente';
    if (anni === 1) return '1 anno di attività';
    return `${anni} anni di attività`;
  }

  /**
   * Ottiene le certificazioni principali di un'azienda (massimo 3)
   */
  getCertificazioniPrincipali(azienda: PublicAziendaDetailDTO): CertificazioneAziendaDTO[] {
    if (!azienda.certificazioni) return [];
    return azienda.certificazioni.slice(0, 3);
  }

  /**
   * Verifica se una certificazione è in scadenza (entro 6 mesi)
   */
  isCertificazioneInScadenza(certificazione: CertificazioneAziendaDTO): boolean {
    const dataScadenza = new Date(certificazione.dataScadenza);
    const oggi = new Date();
    const seiMesiFa = new Date();
    seiMesiFa.setMonth(oggi.getMonth() + 6);
    
    return dataScadenza <= seiMesiFa;
  }

  /**
   * Ottiene l'icona per un tipo di social media
   */
  getSocialIcon(tipo: string): string {
    const icons: Record<string, string> = {
      'facebook': 'facebook',
      'instagram': 'photo_camera',
      'twitter': 'alternate_email',
      'linkedin': 'work',
      'youtube': 'play_circle'
    };
    return icons[tipo.toLowerCase()] || 'public';
  }

  /**
   * Genera l'URL completa per un social media
   */
  getSocialUrl(social: { tipo: string; url: string }): string {
    if (social.url.startsWith('http://') || social.url.startsWith('https://')) {
      return social.url;
    }
    
    // Aggiunge il protocollo se mancante
    const baseUrls: Record<string, string> = {
      'facebook': 'https://facebook.com/',
      'instagram': 'https://instagram.com/',
      'twitter': 'https://twitter.com/',
      'linkedin': 'https://linkedin.com/company/',
      'youtube': 'https://youtube.com/channel/'
    };
    
    const baseUrl = baseUrls[social.tipo.toLowerCase()];
    return baseUrl ? `${baseUrl}${social.url}` : `https://${social.url}`;
  }

  /**
   * Verifica se un'azienda è "vicina" (entro 50km)
   */
  isVicina(azienda: PublicAziendaDetailDTO): boolean {
    return !!(azienda.distanza && azienda.distanza <= 50);
  }

  /**
   * Ordina le aziende per distanza
   */
  ordinaPerDistanza(aziende: PublicAziendaDetailDTO[]): PublicAziendaDetailDTO[] {
    return aziende
      .filter(azienda => azienda.distanza !== undefined)
      .sort((a, b) => (a.distanza || 0) - (b.distanza || 0));
  }

  /**
   * Ottiene le aziende più vicine (massimo 5)
   */
  getAziendeVicine(aziende: PublicAziendaDetailDTO[], limite: number = 5): PublicAziendaDetailDTO[] {
    return this.ordinaPerDistanza(aziende).slice(0, limite);
  }
}