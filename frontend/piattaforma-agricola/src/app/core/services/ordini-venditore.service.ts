import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
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
            `${this.apiUrl}/ordini-venditore/ricevuti`,
            { params }
        );
    }

    /**
     * Ottiene i dettagli di un ordine specifico
     */
    getOrderById(id: number): Observable<OrdineVenditoreDetailDTO> {
        return this.http.get<OrdineVenditoreDetailDTO>(`${this.apiUrl}/ordini-venditore/${id}`);
    }

    /**
     * Accetta un ordine ricevuto
     */
    acceptOrder(id: number): Observable<OrdineVenditoreDTO> {
        return this.http.put<OrdineVenditoreDTO>(`${this.apiUrl}/ordini-venditore/${id}/accetta`, {});
    }

    /**
     * Segna un ordine come pronto per la spedizione
     */
    markAsReadyForShipment(id: number): Observable<OrdineVenditoreDTO> {
        return this.http.put<OrdineVenditoreDTO>(
            `${this.apiUrl}/ordini-venditore/${id}/pronto-spedizione`,
            {}
        );
    }

    /**
     * Spedisce un ordine
     */
    shipOrder(id: number, request: SpedizioneRequestDTO): Observable<OrdineVenditoreDTO> {
        return this.http.put<OrdineVenditoreDTO>(`${this.apiUrl}/ordini-venditore/${id}/spedisci`, request);
    }

    /**
     * Conferma la consegna di un ordine
     */
    confirmDelivery(id: number): Observable<OrdineVenditoreDTO> {
        return this.http.put<OrdineVenditoreDTO>(`${this.apiUrl}/ordini-venditore/${id}/consegna`, {});
    }

    /**
     * Annulla un ordine
     */
    cancelOrder(id: number, request: AnnullaOrdineRequestDTO): Observable<OrdineVenditoreDTO> {
        return this.http.put<OrdineVenditoreDTO>(`${this.apiUrl}/ordini-venditore/${id}/annulla`, request);
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
            'PENDING': 'In Attesa',
            'IN_LAVORAZIONE': 'In Lavorazione',
            'PRONTO_SPEDIZIONE': 'Pronto per Spedizione',
            'SPEDITO': 'Spedito',
            'CONSEGNATO': 'Consegnato',
            'ANNULLATO': 'Annullato'
        };
        return labels[stato] || stato;
    }

    getStatoColor(stato: string): 'primary' | 'accent' | 'warn' | undefined {
        const colors: Record<string, 'primary' | 'accent' | 'warn' | undefined> = {
            'PENDING': 'accent',
            'IN_LAVORAZIONE': 'accent',
            'PRONTO_SPEDIZIONE': 'primary',
            'SPEDITO': 'primary',
            'CONSEGNATO': 'primary',
            'ANNULLATO': 'warn'
        };
        return colors[stato];
    }

    canAcceptOrder(stato: string): boolean {
        return stato === 'PENDING';
    }

    canPrepareShipment(stato: string): boolean {
        return stato === 'IN_LAVORAZIONE';
    }

    canShipOrder(stato: string): boolean {
        return stato === 'PRONTO_SPEDIZIONE';
    }

    canConfirmDelivery(stato: string): boolean {
        return stato === 'SPEDITO';
    }

    canCancelOrder(stato: string): boolean {
        return ['PENDING', 'IN_LAVORAZIONE', 'PRONTO_SPEDIZIONE'].includes(stato);
    }
}
