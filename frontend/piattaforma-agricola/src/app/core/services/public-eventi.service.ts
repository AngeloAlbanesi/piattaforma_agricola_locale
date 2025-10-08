import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import {
  PublicEventoSummaryDTO,
  PublicEventoDetailDTO,
  PublicEventiResponse,
  PublicEventoFilters,
  ProgrammaEventoDTO,
  AziendaPartecipanteDTO,
  ContattoEventoDTO
} from '../models/public.models';

/**
 * Servizio per la gestione delle API pubbliche degli eventi
 * Fornisce accesso ai dati degli eventi visibili pubblicamente
 */
@Injectable({
  providedIn: 'root'
})
export class PublicEventiService {
  private readonly apiUrl = this.buildApiUrl('');

  constructor(private http: HttpClient) {}

  // === METODI PRINCIPALI ===

  /**
   * Ottiene tutti gli eventi pubblici con paginazione e filtri opzionali
   */
  getEventi(filters?: PublicEventoFilters): Observable<PublicEventiResponse> {
    const params = this.buildParamsFromFilters(filters);
    return this.http.get<PublicEventiResponse>(`${this.apiUrl}/eventi`, { params });
  }

  /**
   * Ottiene i dettagli di un evento specifico
   */
  getEventoById(id: number): Observable<PublicEventoDetailDTO> {
    return this.http.get<PublicEventoDetailDTO>(`${this.apiUrl}/eventi/${id}`);
  }

  /**
   * Cerca eventi in base a una query testuale
   */
  cercaEventi(query: string, filters?: Omit<PublicEventoFilters, 'query'>): Observable<PublicEventiResponse> {
    const params = this.buildParamsFromFilters({ ...filters, query });
    return this.http.get<PublicEventiResponse>(`${this.apiUrl}/eventi/cercaEventi`, { params });
  }

  /**
   * Ottiene gli eventi di un organizzatore specifico
   */
  getEventiByOrganizzatore(organizzatoreId: number, filters?: Omit<PublicEventoFilters, 'organizzatoreId'>): Observable<PublicEventiResponse> {
    const params = this.buildParamsFromFilters({ ...filters, organizzatoreId });
    return this.http.get<PublicEventiResponse>(`${this.apiUrl}/eventi/organizzatori/${organizzatoreId}`, { params });
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
  private buildParamsFromFilters(filters?: PublicEventoFilters): HttpParams {
    let params = new HttpParams();

    if (filters) {
      if (filters.query) {
        params = params.set('query', filters.query);
      }
      if (filters.organizzatoreId) {
        params = params.set('organizzatoreId', filters.organizzatoreId.toString());
      }
      if (filters.dataInizio) {
        params = params.set('dataInizio', filters.dataInizio);
      }
      if (filters.dataFine) {
        params = params.set('dataFine', filters.dataFine);
      }
      if (filters.luogo) {
        params = params.set('luogo', filters.luogo);
      }
      if (filters.gratuito !== undefined) {
        params = params.set('gratuito', filters.gratuito.toString());
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
   * Formatta solo l'ora in formato italiano
   */
  formatTime(date: string): string {
    return new Date(date).toLocaleTimeString('it-IT', {
      hour: '2-digit',
      minute: '2-digit'
    });
  }

  /**
   * Restituisce l'etichetta per lo stato dell'evento
   */
  getStatoLabel(stato: string): string {
    const labels: Record<string, string> = {
      'PROGRAMMATO': 'Programmato',
      'IN_CORSO': 'In Corso',
      'COMPLETATO': 'Completato',
      'ANNULLATO': 'Annullato'
    };
    return labels[stato] || stato;
  }

  /**
   * Restituisce il colore per lo stato dell'evento
   */
  getStatoColor(stato: string): 'primary' | 'accent' | 'warn' | undefined {
    const colors: Record<string, 'primary' | 'accent' | 'warn' | undefined> = {
      'PROGRAMMATO': 'primary',
      'IN_CORSO': 'accent',
      'COMPLETATO': undefined,
      'ANNULLATO': 'warn'
    };
    return colors[stato];
  }

  /**
   * Restituisce l'etichetta per la modalità di iscrizione
   */
  getModalitaIscrizioneLabel(modalita: string): string {
    const labels: Record<string, string> = {
      'GRATUITA': 'Iscrizione Gratuita',
      'A_PAGAMENTO': 'Iscrizione a Pagamento',
      'SU_INVITO': 'Su Invito'
    };
    return labels[modalita] || modalita;
  }

  /**
   * Verifica se un evento è gratuito
   */
  isGratuito(evento: PublicEventoDetailDTO): boolean {
    return !evento.costo || evento.costo === 0;
  }

  /**
   * Verifica se un evento ha posti disponibili
   */
  haPostiDisponibili(evento: PublicEventoSummaryDTO): boolean {
    if (!evento.numeroMassimoPartecipanti || !evento.numeroPartecipanti) {
      return true; // Se non c'è limite massimo, assumiamo che ci siano posti
    }
    return evento.numeroPartecipanti < evento.numeroMassimoPartecipanti;
  }

  /**
   * Calcola i posti rimanenti per un evento
   */
  getPostiRimanenti(evento: PublicEventoSummaryDTO): number {
    if (!evento.numeroMassimoPartecipanti || !evento.numeroPartecipanti) {
      return -1; // Indefinito
    }
    return Math.max(0, evento.numeroMassimoPartecipanti - evento.numeroPartecipanti);
  }

  /**
   * Calcola la percentuale di posti occupati
   */
  getPercentualePostiOccupati(evento: PublicEventoSummaryDTO): number {
    if (!evento.numeroMassimoPartecipanti || !evento.numeroPartecipanti) {
      return 0;
    }
    return Math.round((evento.numeroPartecipanti / evento.numeroMassimoPartecipanti) * 100);
  }

  /**
   * Verifica se un evento è imminente (entro 7 giorni)
   */
  isImminente(evento: PublicEventoSummaryDTO, giorniThreshold: number = 7): boolean {
    const dataEvento = new Date(evento.dataOraInizio);
    const oggi = new Date();
    const differenzaGiorni = Math.ceil((dataEvento.getTime() - oggi.getTime()) / (1000 * 60 * 60 * 24));
    
    return differenzaGiorni <= giorniThreshold && differenzaGiorni >= 0;
  }

  /**
   * Verifica se un evento è in corso
   */
  isInCorso(evento: PublicEventoSummaryDTO): boolean {
    const ora = new Date();
    const inizio = new Date(evento.dataOraInizio);
    const fine = new Date(evento.dataOraFine);
    
    return ora >= inizio && ora <= fine;
  }

  /**
   * Verifica se un evento è passato
   */
  isPassato(evento: PublicEventoSummaryDTO): boolean {
    const ora = new Date();
    const fine = new Date(evento.dataOraFine);
    
    return ora > fine;
  }

  /**
   * Genera l'URL per l'immagine dell'evento con fallback
   */
  getEventoImageUrl(evento: PublicEventoSummaryDTO): string {
    if (evento.immagineUrl) {
      return evento.immagineUrl;
    }
    
    // Fallback basato sul tipo di evento (se disponibile)
    const fallbackImages = [
      '/assets/images/placeholders/evento-agricolo.jpg',
      '/assets/images/placeholders/evento-degustazione.jpg',
      '/assets/images/placeholders/evento-fiera.jpg',
      '/assets/images/placeholders/evento-visita.jpg'
    ];
    
    // Seleziona un'immagine casuale come fallback
    const randomIndex = Math.floor(Math.random() * fallbackImages.length);
    return fallbackImages[randomIndex];
  }

  /**
   * Formatta la durata di un evento
   */
  formatDurata(dataInizio: string, dataFine: string): string {
    const inizio = new Date(dataInizio);
    const fine = new Date(dataFine);
    const differenzaOre = Math.round((fine.getTime() - inizio.getTime()) / (1000 * 60 * 60));
    
    if (differenzaOre < 24) {
      return `${differenzaOre} ${differenzaOre === 1 ? 'ora' : 'ore'}`;
    } else {
      const giorni = Math.floor(differenzaOre / 24);
      const oreRimanenti = differenzaOre % 24;
      if (oreRimanenti === 0) {
        return `${giorni} ${giorni === 1 ? 'giorno' : 'giorni'}`;
      } else {
        return `${giorni} ${giorni === 1 ? 'giorno' : 'giorni'} e ${oreRimanenti} ${oreRimanenti === 1 ? 'ora' : 'ore'}`;
      }
    }
  }

  /**
   * Formatta l'intervallo di date per un evento
   */
  formatIntervalloDate(dataInizio: string, dataFine: string): string {
    const inizio = new Date(dataInizio);
    const fine = new Date(dataFine);
    
    // Se stesso giorno
    if (inizio.toDateString() === fine.toDateString()) {
      return `${this.formatDate(dataInizio)}, ${this.formatTime(dataInizio)} - ${this.formatTime(dataFine)}`;
    }
    
    // Se giorni diversi
    return `${this.formatDate(dataInizio)} ${this.formatTime(dataInizio)} - ${this.formatDate(dataFine)} ${this.formatTime(dataFine)}`;
  }

  /**
   * Estrae la città dall'indirizzo dell'evento
   */
  getCittaFromIndirizzo(indirizzo: string): string {
    // Pattern per estrarre la città (di solito dopo il CAP)
    const match = indirizzo.match(/(\d{5})\s+(.+?)(?:,|$)/);
    if (match && match[2]) {
      return match[2].trim();
    }
    
    // Fallback: prende l'ultima parte dell'indirizzo
    const parti = indirizzo.split(',');
    return parti[parti.length - 1].trim();
  }

  /**
   * Verifica se un evento ha un programma dettagliato
   */
  haProgrammaDettagliato(evento: PublicEventoDetailDTO): boolean {
    return !!(evento.programma && evento.programma.length > 0);
  }

  /**
   * Verifica se un evento ha aziende partecipanti
   */
  haAziendePartecipanti(evento: PublicEventoDetailDTO): boolean {
    return !!(evento.aziendePartecipanti && evento.aziendePartecipanti.length > 0);
  }

  /**
   * Verifica se un evento ha contatti
   */
  haContatti(evento: PublicEventoDetailDTO): boolean {
    return !!(evento.contatti && evento.contatti.length > 0);
  }

  /**
   * Ottiene il contatto principale di un evento
   */
  getContattoPrincipale(evento: PublicEventoDetailDTO): ContattoEventoDTO | null {
    if (!evento.contatti || evento.contatti.length === 0) {
      return null;
    }
    
    // Priorità: email > telefono > sito
    const email = evento.contatti.find(c => c.tipo === 'email');
    if (email) return email;
    
    const telefono = evento.contatti.find(c => c.tipo === 'telefono');
    if (telefono) return telefono;
    
    return evento.contatti[0];
  }
}