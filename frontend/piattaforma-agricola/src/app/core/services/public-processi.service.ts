import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import {
  PublicProcessoSummaryDTO,
  PublicProcessoDetailDTO,
  PublicProcessiResponse,
  PublicProcessoFilters,
  TracciabilitaProcessoDTO
} from '../models/public.models';

/**
 * Servizio per la gestione delle API pubbliche dei processi di trasformazione
 * Fornisce accesso ai dati dei processi visibili pubblicamente
 */
@Injectable({
  providedIn: 'root'
})
export class PublicProcessiService {
  private readonly apiUrl = this.buildApiUrl('');

  constructor(private http: HttpClient) {}

  // === METODI PRINCIPALI ===

  /**
   * Ottiene tutti i processi di trasformazione pubblici con paginazione e filtri opzionali
   */
  getProcessi(filters?: PublicProcessoFilters): Observable<PublicProcessiResponse> {
    const params = this.buildParamsFromFilters(filters);
    return this.http.get<PublicProcessiResponse>(`${this.apiUrl}/processi-trasformazione`, { params });
  }

  /**
   * Ottiene i dettagli di un processo di trasformazione specifico
   */
  getProcessoById(id: number): Observable<PublicProcessoDetailDTO> {
    return this.http.get<PublicProcessoDetailDTO>(`${this.apiUrl}/processi-trasformazione/${id}`);
  }

  /**
   * Ottiene la tracciabilità completa di un processo di trasformazione
   */
  getTracciabilitaProcesso(processoId: number): Observable<TracciabilitaProcessoDTO> {
    return this.http.get<TracciabilitaProcessoDTO>(`${this.apiUrl}/processi-trasformazione/${processoId}/tracciabilita`);
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
  private buildParamsFromFilters(filters?: PublicProcessoFilters): HttpParams {
    let params = new HttpParams();

    if (filters) {
      if (filters.query) {
        params = params.set('query', filters.query);
      }
      if (filters.trasformatoreId) {
        params = params.set('trasformatoreId', filters.trasformatoreId.toString());
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
      if (filters.sortDirection) {
        params = params.set('sortDirection', filters.sortDirection);
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
   * Restituisce l'etichetta per lo stato del processo
   */
  getStatoLabel(stato: string): string {
    const labels: Record<string, string> = {
      'IN_PROGETTAZIONE': 'In Progettazione',
      'IN_CORSO': 'In Corso',
      'COMPLETATO': 'Completato',
      'SOSPESO': 'Sospeso',
      'ANNULLATO': 'Annullato'
    };
    return labels[stato] || stato;
  }

  /**
   * Restituisce il colore per lo stato del processo
   */
  getStatoColor(stato: string): 'primary' | 'accent' | 'warn' | undefined {
    const colors: Record<string, 'primary' | 'accent' | 'warn' | undefined> = {
      'IN_PROGETTAZIONE': undefined,
      'IN_CORSO': 'accent',
      'COMPLETATO': 'primary',
      'SOSPESO': 'warn',
      'ANNULLATO': 'warn'
    };
    return colors[stato];
  }

  /**
   * Formatta la durata totale del processo
   */
  formatDurataTotale(ore: number): string {
    if (ore < 24) {
      return `${ore} ${ore === 1 ? 'ora' : 'ore'}`;
    } else {
      const giorni = Math.floor(ore / 24);
      const oreRimanenti = ore % 24;
      if (oreRimanenti === 0) {
        return `${giorni} ${giorni === 1 ? 'giorno' : 'giorni'}`;
      } else {
        return `${giorni} ${giorni === 1 ? 'giorno' : 'giorni'} e ${oreRimanenti} ${oreRimanenti === 1 ? 'ora' : 'ore'}`;
      }
    }
  }

  /**
   * Formatta la resa del processo in percentuale
   */
  formatResa(resa: number): string {
    return `${Math.round(resa * 100)}%`;
  }

  /**
   * Verifica se un processo è attivo
   */
  isAttivo(processo: PublicProcessoSummaryDTO): boolean {
    return processo.stato === 'IN_CORSO';
  }

  /**
   * Verifica se un processo è completato
   */
  isCompletato(processo: PublicProcessoSummaryDTO): boolean {
    return processo.stato === 'COMPLETATO';
  }

  /**
   * Verifica se un processo è sospeso o annullato
   */
  isSospesoOAnnullato(processo: PublicProcessoSummaryDTO): boolean {
    return processo.stato === 'SOSPESO' || processo.stato === 'ANNULLATO';
  }

  /**
   * Calcola il progresso del processo basato sulle fasi
   */
  calcolaProgresso(processo: PublicProcessoDetailDTO): number {
    if (!processo.fasi || processo.fasi.length === 0) {
      return 0;
    }

    const fasiCompletate = processo.fasi.filter(fase => 
      fase.stato === 'COMPLETATA'
    ).length;

    return Math.round((fasiCompletate / processo.fasi.length) * 100);
  }

  /**
   * Ottiene la fase corrente del processo
   */
  getFaseCorrente(processo: PublicProcessoDetailDTO): any {
    if (!processo.fasi || processo.fasi.length === 0) {
      return null;
    }

    // Cerca la prima fase non completata
    return processo.fasi.find(fase => fase.stato !== 'COMPLETATA') || 
           processo.fasi[processo.fasi.length - 1]; // Se tutte sono completate, restituisce l'ultima
  }

  /**
   * Verifica se un processo ha prodotti di input
   */
  haProdottiInput(processo: PublicProcessoSummaryDTO): boolean {
    return !!(processo.prodottiInput && processo.prodottiInput.length > 0);
  }

  /**
   * Verifica se un processo ha prodotti di output
   */
  haProdottiOutput(processo: PublicProcessoSummaryDTO): boolean {
    return !!(processo.prodottiOutput && processo.prodottiOutput.length > 0);
  }

  /**
   * Formatta l'elenco dei prodotti di input
   */
  formatProdottiInput(processo: PublicProcessoSummaryDTO): string {
    if (!this.haProdottiInput(processo)) {
      return 'Nessuno';
    }
    
    const prodotti = processo.prodottiInput!;
    if (prodotti.length <= 3) {
      return prodotti.join(', ');
    }
    
    return `${prodotti.slice(0, 3).join(', ')} e altri ${prodotti.length - 3}`;
  }

  /**
   * Formatta l'elenco dei prodotti di output
   */
  formatProdottiOutput(processo: PublicProcessoSummaryDTO): string {
    if (!this.haProdottiOutput(processo)) {
      return 'Nessuno';
    }
    
    const prodotti = processo.prodottiOutput!;
    if (prodotti.length <= 3) {
      return prodotti.join(', ');
    }
    
    return `${prodotti.slice(0, 3).join(', ')} e altri ${prodotti.length - 3}`;
  }

  /**
   * Verifica se un processo ha certificazioni
   */
  hasCertificazioni(processo: PublicProcessoDetailDTO): boolean {
    return !!(processo.certificazioni && processo.certificazioni.length > 0);
  }

  /**
   * Verifica se un processo ha tracciabilità completa
   */
  hasTracciabilitaCompleta(processo: PublicProcessoDetailDTO): boolean {
    return !!(processo.tracciabilita && processo.tracciabilita.length > 0);
  }

  /**
   * Ottiene le tecniche principali del processo
   */
  getTecnichePrincipali(processo: PublicProcessoDetailDTO, limite: number = 5): string[] {
    if (!processo.fasi || processo.fasi.length === 0) {
      return [];
    }

    const tutteTecniche = processo.fasi.flatMap(fase => fase.tecniche || []);
    const tecnicheUniche = [...new Set(tutteTecniche)];
    
    return tecnicheUniche.slice(0, limite);
  }

  /**
   * Ottiene le attrezzature principali del processo
   */
  getAttrezzaturePrincipali(processo: PublicProcessoDetailDTO, limite: number = 5): string[] {
    if (!processo.fasi || processo.fasi.length === 0) {
      return [];
    }

    const tutteAttrezzature = processo.fasi.flatMap(fase => fase.attrezzature || []);
    const attrezzatureUniche = [...new Set(tutteAttrezzature)];
    
    return attrezzatureUniche.slice(0, limite);
  }

  /**
   * Calcola il numero di giorni trascorsi dalla creazione del processo
   */
  getGiorniDallaCreazione(processo: PublicProcessoSummaryDTO): number {
    const dataCreazione = new Date(processo.dataCreazione);
    const oggi = new Date();
    const differenzaTempo = oggi.getTime() - dataCreazione.getTime();
    return Math.ceil(differenzaTempo / (1000 * 60 * 60 * 24));
  }

  /**
   * Formatta il tempo trascorso dalla creazione del processo
   */
  formatTempoTrascorso(processo: PublicProcessoSummaryDTO): string {
    const giorni = this.getGiorniDallaCreazione(processo);
    
    if (giorni === 0) {
      return 'Oggi';
    } else if (giorni === 1) {
      return 'Ieri';
    } else if (giorni < 7) {
      return `${giorni} giorni fa`;
    } else if (giorni < 30) {
      const settimane = Math.floor(giorni / 7);
      return `${settimane} ${settimane === 1 ? 'settimana' : 'settimane'} fa`;
    } else if (giorni < 365) {
      const mesi = Math.floor(giorni / 30);
      return `${mesi} ${mesi === 1 ? 'mese' : 'mesi'} fa`;
    } else {
      const anni = Math.floor(giorni / 365);
      return `${anni} ${anni === 1 ? 'anno' : 'anni'} fa`;
    }
  }

  /**
   * Verifica se un processo ha una buona resa (sopra la soglia specificata)
   */
  haBuonaResa(processo: PublicProcessoDetailDTO, soglia: number = 0.8): boolean {
    return !!(processo.resa && processo.resa >= soglia);
  }

  /**
   * Ordina i processi per data di creazione (dal più recente)
   */
  ordinaPerDataRecente(processi: PublicProcessoSummaryDTO[]): PublicProcessoSummaryDTO[] {
    return processi.sort((a, b) => 
      new Date(b.dataCreazione).getTime() - new Date(a.dataCreazione).getTime()
    );
  }

  /**
   * Ottiene i processi più recenti (massimo 5)
   */
  getProcessiRecenti(processi: PublicProcessoSummaryDTO[], limite: number = 5): PublicProcessoSummaryDTO[] {
    return this.ordinaPerDataRecente(processi).slice(0, limite);
  }

  /**
   * Filtra i processi per stato
   */
  filtraPerStato(processi: PublicProcessoSummaryDTO[], stato: string): PublicProcessoSummaryDTO[] {
    return processi.filter(processo => processo.stato === stato);
  }

  /**
   * Filtra i processi attivi
   */
  getProcessiAttivi(processi: PublicProcessoSummaryDTO[]): PublicProcessoSummaryDTO[] {
    return this.filtraPerStato(processi, 'IN_CORSO');
  }

  /**
   * Filtra i processi completati
   */
  getProcessiCompletati(processi: PublicProcessoSummaryDTO[]): PublicProcessoSummaryDTO[] {
    return this.filtraPerStato(processi, 'COMPLETATO');
  }
}