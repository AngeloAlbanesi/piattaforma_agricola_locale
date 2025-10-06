import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, BehaviorSubject } from 'rxjs';
import { environment } from '../../../environments/environment';
import { 
    OrdineDTO, 
    DettaglioOrdineDTO, 
    OrdiniFiltri, 
    UpdateStatoOrdineRequest,
    AggiungiComunicazioneRequest,
    StatisticheOrdiniDTO,
    StatoOrdine,
    TRANSIZIONI_PERMESSE
} from '../models/ordini.models';
import { PaginatedResponse } from '../models/common.models';

@Injectable({
    providedIn: 'root'
})
export class OrdiniService {
    private readonly apiUrl = environment.apiBaseUrl;
    
    // Subject per aggiornamenti real-time
    private ordiniUpdates$ = new BehaviorSubject<OrdineDTO[]>([]);
    private statisticheUpdates$ = new BehaviorSubject<StatisticheOrdiniDTO | null>(null);

    constructor(private http: HttpClient) {}

    /**
     * Ottiene gli ordini del distributore con filtri
     */
    getOrdini(filtri?: OrdiniFiltri): Observable<PaginatedResponse<OrdineDTO>> {
        let params = new HttpParams();
        
        if (filtri) {
            if (filtri.stato?.length) {
                filtri.stato.forEach(stato => {
                    params = params.append('stato', stato);
                });
            }
            if (filtri.dataInizio) {
                params = params.set('dataInizio', filtri.dataInizio);
            }
            if (filtri.dataFine) {
                params = params.set('dataFine', filtri.dataFine);
            }
            if (filtri.numeroOrdine) {
                params = params.set('numeroOrdine', filtri.numeroOrdine);
            }
            if (filtri.acquirente) {
                params = params.set('acquirente', filtri.acquirente);
            }
            if (filtri.importoMin !== undefined) {
                params = params.set('importoMin', filtri.importoMin.toString());
            }
            if (filtri.importoMax !== undefined) {
                params = params.set('importoMax', filtri.importoMax.toString());
            }
            if (filtri.pagina !== undefined) {
                params = params.set('pagina', filtri.pagina.toString());
            }
            if (filtri.elementiPerPagina !== undefined) {
                params = params.set('elementiPerPagina', filtri.elementiPerPagina.toString());
            }
            if (filtri.ordinamento) {
                params = params.set('ordinamento', filtri.ordinamento);
            }
            if (filtri.direzione) {
                params = params.set('direzione', filtri.direzione);
            }
        }

        return this.http.get<PaginatedResponse<OrdineDTO>>(`${this.apiUrl}/ordini/distributore`, { params });
    }

    /**
     * Ottiene i dettagli completi di un ordine
     */
    getDettaglioOrdine(ordineId: number): Observable<DettaglioOrdineDTO> {
        return this.http.get<DettaglioOrdineDTO>(`${this.apiUrl}/ordini/${ordineId}`);
    }

    /**
     * Aggiorna lo stato di un ordine
     */
    updateStatoOrdine(ordineId: number, request: UpdateStatoOrdineRequest): Observable<DettaglioOrdineDTO> {
        return this.http.patch<DettaglioOrdineDTO>(`${this.apiUrl}/ordini/${ordineId}/stato`, request);
    }

    /**
     * Aggiunge una comunicazione a un ordine
     */
    aggiungiComunicazione(ordineId: number, request: AggiungiComunicazioneRequest): Observable<void> {
        return this.http.post<void>(`${this.apiUrl}/ordini/${ordineId}/comunicazioni`, request);
    }

    /**
     * Carica un documento per un ordine
     */
    caricaDocumento(ordineId: number, file: File, tipo: string): Observable<void> {
        const formData = new FormData();
        formData.append('file', file);
        formData.append('tipo', tipo);

        return this.http.post<void>(`${this.apiUrl}/ordini/${ordineId}/documenti`, formData);
    }

    /**
     * Ottiene le statistiche degli ordini
     */
    getStatisticheOrdini(): Observable<StatisticheOrdiniDTO> {
        return this.http.get<StatisticheOrdiniDTO>(`${this.apiUrl}/ordini/distributore/statistiche`);
    }

    /**
     * Ottiene gli ordini che richiedono attenzione immediata
     */
    getOrdiniUrgenti(): Observable<OrdineDTO[]> {
        return this.http.get<OrdineDTO[]>(`${this.apiUrl}/ordini/distributore/urgenti`);
    }

    /**
     * Conferma multipli ordini
     */
    confermaOrdiniMultipli(ordiniIds: number[]): Observable<void> {
        return this.http.post<void>(`${this.apiUrl}/ordini/distributore/conferma-multipla`, { ordiniIds });
    }

    /**
     * Esporta ordini in formato CSV/Excel
     */
    esportaOrdini(filtri?: OrdiniFiltri, formato: 'csv' | 'excel' = 'csv'): Observable<Blob> {
        let params = new HttpParams();
        params = params.set('formato', formato);
        
        if (filtri) {
            // Applica gli stessi filtri della ricerca
            if (filtri.stato?.length) {
                filtri.stato.forEach(stato => {
                    params = params.append('stato', stato);
                });
            }
            if (filtri.dataInizio) {
                params = params.set('dataInizio', filtri.dataInizio);
            }
            if (filtri.dataFine) {
                params = params.set('dataFine', filtri.dataFine);
            }
        }

        return this.http.get(`${this.apiUrl}/ordini/distributore/esporta`, { 
            params, 
            responseType: 'blob' 
        });
    }

    /**
     * Observable per aggiornamenti real-time degli ordini
     */
    getOrdiniUpdates(): Observable<OrdineDTO[]> {
        return this.ordiniUpdates$.asObservable();
    }

    /**
     * Observable per aggiornamenti real-time delle statistiche
     */
    getStatisticheUpdates(): Observable<StatisticheOrdiniDTO | null> {
        return this.statisticheUpdates$.asObservable();
    }

    /**
     * Marca una comunicazione come letta
     */
    marcaComunicazioneLetta(ordineId: number, comunicazioneId: number): Observable<void> {
        return this.http.patch<void>(`${this.apiUrl}/ordini/${ordineId}/comunicazioni/${comunicazioneId}/letta`, {});
    }

    /**
     * Ottiene il conteggio di ordini per stato
     */
    getConteggioPerStato(): Observable<Record<StatoOrdine, number>> {
        return this.http.get<Record<StatoOrdine, number>>(`${this.apiUrl}/ordini/distributore/conteggio-stati`);
    }

    // Metodi di utilità
    formatCurrency(value: number): string {
        return new Intl.NumberFormat('it-IT', {
            style: 'currency',
            currency: 'EUR'
        }).format(value);
    }

    formatDate(date: string): string {
        return new Date(date).toLocaleDateString('it-IT', {
            year: 'numeric',
            month: 'short',
            day: 'numeric'
        });
    }

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
     * Verifica se una transizione di stato è permessa
     */
    isTransizionePermessa(statoAttuale: StatoOrdine, nuovoStato: StatoOrdine): boolean {
        return TRANSIZIONI_PERMESSE[statoAttuale]?.includes(nuovoStato) || false;
    }

    /**
     * Ottiene i prossimi stati possibili per un ordine
     */
    getProssimiStatiPossibili(statoAttuale: StatoOrdine): StatoOrdine[] {
        return TRANSIZIONI_PERMESSE[statoAttuale] || [];
    }

    /**
     * Calcola il tempo trascorso da una data
     */
    calcolaTempoTrascorso(data: string): string {
        const now = new Date();
        const orderDate = new Date(data);
        const diffMs = now.getTime() - orderDate.getTime();
        const diffHours = Math.floor(diffMs / (1000 * 60 * 60));
        const diffDays = Math.floor(diffHours / 24);

        if (diffDays > 0) {
            return `${diffDays} giorni fa`;
        } else if (diffHours > 0) {
            return `${diffHours} ore fa`;
        } else {
            const diffMinutes = Math.floor(diffMs / (1000 * 60));
            return `${diffMinutes} minuti fa`;
        }
    }

    /**
     * Calcola la priorità di un ordine basata su stato e tempo
     */
    calcolaPriorita(ordine: OrdineDTO): 'ALTA' | 'MEDIA' | 'BASSA' {
        const now = new Date();
        const orderDate = new Date(ordine.dataOrdine);
        const diffHours = (now.getTime() - orderDate.getTime()) / (1000 * 60 * 60);

        // Ordini ricevuti da più di 24 ore hanno priorità alta
        if (ordine.stato === StatoOrdine.RICEVUTO && diffHours > 24) {
            return 'ALTA';
        }

        // Ordini in preparazione da più di 48 ore hanno priorità alta
        if (ordine.stato === StatoOrdine.IN_PREPARAZIONE && diffHours > 48) {
            return 'ALTA';
        }

        // Ordini pronti per consegna hanno priorità media
        if (ordine.stato === StatoOrdine.PRONTO_PER_CONSEGNA) {
            return 'MEDIA';
        }

        return 'BASSA';
    }
}