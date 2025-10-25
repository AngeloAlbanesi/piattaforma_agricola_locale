import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from '../../../environments/environment';
import { PaginatedResponse } from '../models/common.models';
import {
    OrdineVenditoreDTO,
    OrdineVenditoreDetailDTO,
    SpedizioneRequestDTO,
    AnnullaOrdineRequestDTO,
    OrdineFilters
} from '../models/trasformatore.models';

/**
 * Servizio per la gestione degli ordini dal punto di vista del venditore.
 * Utilizzato da: Trasformatore, Produttore, Distributore.
 */
@Injectable({
    providedIn: 'root'
})
export class OrdiniVenditoreService {
    private readonly apiUrl = this.buildApiUrl('');

    constructor(private http: HttpClient) { }

    // === GESTIONE ORDINI RICEVUTI ===

    /**
     * Ottiene l'elenco degli ordini ricevuti dal venditore
     */
    getReceivedOrders(filters?: OrdineFilters): Observable<PaginatedResponse<OrdineVenditoreDTO>> {
        let params = this.buildParamsFromFilters(filters);
        return this.http.get<PaginatedResponse<OrdineVenditoreDTO>>(
            `${this.apiUrl}/ordini/venditori`,
            { params }
        );
    }

    /**
     * Ottiene i dettagli di un ordine specifico
     */
    getOrderById(id: number): Observable<OrdineVenditoreDetailDTO> {
        return this.http.get<any>(`${this.apiUrl}/ordini/venditori/${id}`).pipe(
            map(order => this.mapOrderDetailDTO(order))
        );
    }

    /**
     * Maps order detail DTO to add computed properties for template compatibility
     */
    private mapOrderDetailDTO(order: any): OrdineVenditoreDetailDTO {
        return {
            ...order,
            // Add computed getters for backward compatibility
            get id() { return this.idOrdine; },
            get totale() { return this.importoTotale; },
            get stato() { return this.statoCorrente; },
            get clienteNome() { return `${this.nomeAcquirente} ${this.cognomeAcquirente}`; },
            get clienteEmail() { return this.emailAcquirente; },
            get articoli() {
                return this.articoliAcquistati?.map((item: any) => ({
                    ...item,
                    get prodottoNome() { return this.nomeAcquistabile; },
                    get prodottoImmagine() { return undefined; }, // Not provided by backend
                    get quantita() { return this.quantitaOrdinata; },
                    get subtotale() { return this.prezzoTotale; }
                })) || [];
            }
        };
    }

    /**
     * Ottiene le statistiche degli ordini del venditore
     */
    getStatistiche(): Observable<any> {
        return this.http.get<any>(`${this.apiUrl}/ordini/venditori/statistiche`);
    }

    /**
     * Inizia la lavorazione di un ordine
     */
    processOrder(id: number): Observable<OrdineVenditoreDTO> {
        return this.http.put<OrdineVenditoreDTO>(`${this.apiUrl}/ordini/venditori/${id}/process`, {});
    }

    /**
     * Spedisce un ordine
     */
    shipOrder(id: number, request: SpedizioneRequestDTO): Observable<OrdineVenditoreDTO> {
        return this.http.put<OrdineVenditoreDTO>(`${this.apiUrl}/ordini/venditori/${id}/ship`, request);
    }

    /**
     * Conferma la consegna di un ordine
     */
    deliverOrder(id: number): Observable<OrdineVenditoreDTO> {
        return this.http.put<OrdineVenditoreDTO>(`${this.apiUrl}/ordini/venditori/${id}/deliver`, {});
    }

    /**
     * Annulla un ordine
     */
    cancelOrder(id: number): Observable<OrdineVenditoreDTO> {
        return this.http.put<OrdineVenditoreDTO>(`${this.apiUrl}/ordini/venditori/${id}/cancel`, {});
    }

    // === UTILITIES ===

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

    private buildParamsFromFilters(filters?: OrdineFilters): HttpParams {
        let params = new HttpParams();

        if (filters) {
            if (filters.stato) {
                params = params.set('stato', filters.stato);
            }
            if (filters.dataInizio) {
                params = params.set('dataInizio', filters.dataInizio);
            }
            if (filters.dataFine) {
                params = params.set('dataFine', filters.dataFine);
            }
            if (filters.clienteId) {
                params = params.set('clienteId', filters.clienteId.toString());
            }
            if (filters.page !== undefined) {
                params = params.set('page', filters.page.toString());
            }
            if (filters.size !== undefined) {
                params = params.set('size', filters.size.toString());
            }
        }

        return params;
    }

    // === FORMATTERS E HELPERS ===

    formatCurrency(value: number): string {
        return new Intl.NumberFormat('it-IT', {
            style: 'currency',
            currency: 'EUR',
            minimumFractionDigits: 2,
            maximumFractionDigits: 2
        }).format(value);
    }

    formatDate(date: string): string {
        return new Date(date).toLocaleDateString('it-IT', {
            year: 'numeric',
            month: 'long',
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

    getStatoLabel(stato: string): string {
        const labels: Record<string, string> = {
            'ATTESA_PAGAMENTO': 'Attesa Pagamento',
            'PRONTO_PER_LAVORAZIONE': 'Pronto per Lavorazione',
            'IN_LAVORAZIONE': 'In Lavorazione',
            'SPEDITO': 'Spedito',
            'CONSEGNATO': 'Consegnato',
            'ANNULLATO': 'Annullato',
            'RIMBORSATO': 'Rimborsato'
        };
        return labels[stato] || stato;
    }

    getStatoColor(stato: string): 'primary' | 'accent' | 'warn' | undefined {
        const colors: Record<string, 'primary' | 'accent' | 'warn' | undefined> = {
            'ATTESA_PAGAMENTO': 'accent',
            'PRONTO_PER_LAVORAZIONE': 'accent',
            'IN_LAVORAZIONE': 'primary',
            'SPEDITO': 'primary',
            'CONSEGNATO': 'primary',
            'ANNULLATO': 'warn',
            'RIMBORSATO': 'warn'
        };
        return colors[stato];
    }

    canProcessOrder(stato: string): boolean {
        return stato === 'PRONTO_PER_LAVORAZIONE';
    }

    canShipOrder(stato: string): boolean {
        return stato === 'IN_LAVORAZIONE';
    }

    canDeliverOrder(stato: string): boolean {
        return stato === 'SPEDITO';
    }

    canCancelOrder(stato: string): boolean {
        return ['PRONTO_PER_LAVORAZIONE', 'IN_LAVORAZIONE'].includes(stato);
    }
}
