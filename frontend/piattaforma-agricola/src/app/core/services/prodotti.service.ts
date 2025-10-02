import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { PaginatedResponse } from '../models/common.models';
import {
    ProdottoDTO,
    ProdottoDetailDTO,
    CreateProdottoRequestDTO,
    UpdateProdottoRequestDTO,
    AddCertificazioneRequestDTO,
    CertificazioneProdottoDTO,
    TracciabilitaProdottoDTO,
    ProdottoFilters
} from '../models/trasformatore.models';

/**
 * Servizio per la gestione dei prodotti.
 * Può essere utilizzato da diversi ruoli: Trasformatore, Produttore, Distributore.
 */
@Injectable({
    providedIn: 'root'
})
export class ProdottiService {
    private readonly apiUrl = this.buildApiUrl('');

    constructor(private http: HttpClient) { }

    // === GESTIONE PRODOTTI ===

    /**
     * Ottiene l'elenco dei prodotti dell'utente corrente
     */
    getMyProducts(filters?: ProdottoFilters): Observable<PaginatedResponse<ProdottoDTO>> {
        let params = this.buildParamsFromFilters(filters);
        return this.http.get<PaginatedResponse<ProdottoDTO>>(`${this.apiUrl}/api/prodotti/miei-prodotti`, { params });
    }

    /**
     * Ottiene i dettagli di un prodotto specifico
     */
    getProductById(id: number): Observable<ProdottoDetailDTO> {
        return this.http.get<ProdottoDetailDTO>(`${this.apiUrl}/api/prodotti/${id}`);
    }

    /**
     * Crea un nuovo prodotto trasformato
     */
    createProduct(request: CreateProdottoRequestDTO): Observable<ProdottoDetailDTO> {
        return this.http.post<ProdottoDetailDTO>(`${this.apiUrl}/api/prodotti`, request);
    }

    /**
     * Aggiorna un prodotto esistente
     */
    updateProduct(id: number, request: UpdateProdottoRequestDTO): Observable<ProdottoDetailDTO> {
        return this.http.put<ProdottoDetailDTO>(`${this.apiUrl}/api/prodotti/${id}`, request);
    }

    /**
     * Elimina un prodotto
     */
    deleteProduct(id: number): Observable<void> {
        return this.http.delete<void>(`${this.apiUrl}/api/prodotti/${id}`);
    }

    // === CERTIFICAZIONI PRODOTTO ===

    /**
     * Aggiunge una certificazione a un prodotto
     */
    addCertification(productId: number, request: AddCertificazioneRequestDTO): Observable<ProdottoDetailDTO> {
        return this.http.post<ProdottoDetailDTO>(`${this.apiUrl}/api/prodotti/${productId}/certificazioni`, request);
    }

    /**
     * Rimuove una certificazione da un prodotto
     */
    removeCertification(productId: number, certificationId: number): Observable<void> {
        return this.http.delete<void>(`${this.apiUrl}/api/prodotti/${productId}/certificazioni/${certificationId}`);
    }

    // === TRACCIABILITÀ PRODOTTO ===

    /**
     * Ottiene la tracciabilità completa di un prodotto
     */
    getProductTraceability(productId: number): Observable<TracciabilitaProdottoDTO> {
        return this.http.get<TracciabilitaProdottoDTO>(`${this.apiUrl}/api/prodotti/${productId}/tracciabilita`);
    }

    // === UTILITIES ===

    private buildApiUrl(path: string): string {
        const base = (environment.apiBaseUrl ?? '').replace(/\/$/, '');
        const prefix = (environment.apiPrefix ?? '').replace(/\/$/, '');
        const sanitizedPath = path.startsWith('/') ? path : `/${path}`;

        if (base) {
            return `${base}${prefix}${sanitizedPath}`;
        }

        return `${prefix || ''}${sanitizedPath}` || sanitizedPath;
    }

    private buildParamsFromFilters(filters?: ProdottoFilters): HttpParams {
        let params = new HttpParams();

        if (filters) {
            if (filters.search) {
                params = params.set('search', filters.search);
            }
            if (filters.categoria) {
                params = params.set('categoria', filters.categoria.toString());
            }
            if (filters.stato) {
                params = params.set('stato', filters.stato);
            }
            if (filters.prezzoMin !== undefined) {
                params = params.set('prezzoMin', filters.prezzoMin.toString());
            }
            if (filters.prezzoMax !== undefined) {
                params = params.set('prezzoMax', filters.prezzoMax.toString());
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

    // === FORMATTERS ===

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

    getStatoLabel(stato: string): string {
        const labels: Record<string, string> = {
            'BOZZA': 'Bozza',
            'IN_APPROVAZIONE': 'In Approvazione',
            'APPROVATO': 'Approvato',
            'RIFIUTATO': 'Rifiutato'
        };
        return labels[stato] || stato;
    }

    getStatoColor(stato: string): 'primary' | 'accent' | 'warn' | undefined {
        const colors: Record<string, 'primary' | 'accent' | 'warn' | undefined> = {
            'BOZZA': undefined,
            'IN_APPROVAZIONE': 'accent',
            'APPROVATO': 'primary',
            'RIFIUTATO': 'warn'
        };
        return colors[stato];
    }
}
