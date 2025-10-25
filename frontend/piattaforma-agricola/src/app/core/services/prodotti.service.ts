import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { environment } from '../../../environments/environment';
import { PaginatedResponse, ProdottoSummaryDTO } from '../models/common.models';
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
    private readonly apiUrl = environment.apiPrefix || '/api';

    constructor(private http: HttpClient) { }

    // === GESTIONE PRODOTTI ===

    /**
     * Ottiene l'elenco dei prodotti dell'utente corrente
     */
    getMyProducts(filters?: ProdottoFilters): Observable<PaginatedResponse<ProdottoDTO>> {
        let params = this.buildParamsFromFilters(filters);
        return this.http.get<PaginatedResponse<ProdottoDTO>>(`${this.apiUrl}/prodotti/miei-prodotti`, { params }).pipe(
            map(response => {
                if (response?.content) {
                    response.content.forEach(product => this.normalizeProduct(product));
                }
                return response;
            })
        );
    }

    /**
     * Ottiene tutti i prodotti disponibili (per distributori)
     */
    getProducts(filters?: {
        pagina?: number;
        elementiPerPagina?: number;
        stato?: string;
        search?: string;
        categoria?: string;
        produttore?: string;
    }): Observable<PaginatedResponse<ProdottoSummaryDTO>> {
        let params = new HttpParams();

        if (filters) {
            if (filters.pagina !== undefined) {
                params = params.set('pagina', filters.pagina.toString());
            }
            if (filters.elementiPerPagina !== undefined) {
                params = params.set('elementiPerPagina', filters.elementiPerPagina.toString());
            }
            if (filters.stato) {
                params = params.set('stato', filters.stato);
            }
            if (filters.search) {
                params = params.set('search', filters.search);
            }
            if (filters.categoria) {
                params = params.set('categoria', filters.categoria);
            }
            if (filters.produttore) {
                params = params.set('produttore', filters.produttore);
            }
        }

        return this.http.get<PaginatedResponse<ProdottoSummaryDTO>>(`${this.apiUrl}/prodotti`, { params }).pipe(
            map(response => {
                if (response?.content) {
                    response.content.forEach(product => this.normalizeProduct(product));
                }
                return response;
            })
        );
    }

    /**
     * Ottiene i dettagli di un prodotto specifico
     */
    getProductById(id: number): Observable<ProdottoDetailDTO> {
        return this.http.get<ProdottoDetailDTO>(`${this.apiUrl}/prodotti/${id}`).pipe(
            map(product => {
                this.normalizeProduct(product);
                return product;
            })
        );
    }

    /**
     * Crea un nuovo prodotto trasformato
     */
    createProduct(request: CreateProdottoRequestDTO): Observable<ProdottoDetailDTO> {
        return this.http.post<ProdottoDetailDTO>(`${this.apiUrl}/prodotti`, request).pipe(
            map(product => {
                this.normalizeProduct(product);
                return product;
            })
        );
    }

    /**
     * Aggiorna un prodotto esistente
     */
    updateProduct(id: number, request: UpdateProdottoRequestDTO): Observable<ProdottoDetailDTO> {
        return this.http.put<ProdottoDetailDTO>(`${this.apiUrl}/prodotti/${id}`, request).pipe(
            map(product => {
                this.normalizeProduct(product);
                return product;
            })
        );
    }

    /**
     * Elimina un prodotto
     */
    deleteProduct(id: number): Observable<void> {
        return this.http.delete<void>(`${this.apiUrl}/prodotti/${id}`);
    }

    // === CERTIFICAZIONI PRODOTTO ===

    /**
     * Aggiunge una certificazione a un prodotto
     */
    addCertification(productId: number, request: AddCertificazioneRequestDTO): Observable<ProdottoDetailDTO> {
        return this.http.post<ProdottoDetailDTO>(`${this.apiUrl}/prodotti/${productId}/certificazioni`, request).pipe(
            map(product => {
                this.normalizeProduct(product);
                return product;
            })
        );
    }

    /**
     * Rimuove una certificazione da un prodotto
     */
    removeCertification(productId: number, certificationId: number): Observable<void> {
        return this.http.delete<void>(`${this.apiUrl}/prodotti/${productId}/certificazioni/${certificationId}`);
    }

    // === TRACCIABILITÀ PRODOTTO ===

    /**
     * Ottiene la tracciabilità completa di un prodotto
     */
    getProductTraceability(productId: number): Observable<TracciabilitaProdottoDTO> {
        return this.http.get<TracciabilitaProdottoDTO>(`${this.apiUrl}/prodotti/${productId}/tracciabilita`);
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

    /**
     * Normalizza un prodotto assicurandosi che certificazioni sia sempre un array
     * Questo risolve il problema NG02200 quando il backend restituisce certificazioni come oggetto
     */
    private normalizeProduct(product: any): void {
        if (product) {
            // Normalizza certificazioni
            if (product.certificazioni && !Array.isArray(product.certificazioni)) {
                console.warn('⚠️ [ProdottiService] certificazioni non è un array, convertendolo:', product.certificazioni);
                if (typeof product.certificazioni === 'object') {
                    product.certificazioni = Object.values(product.certificazioni);
                } else {
                    product.certificazioni = [];
                }
            }
            // Normalizza certificazioniDettagli se presente
            if (product.certificazioniDettagli && !Array.isArray(product.certificazioniDettagli)) {
                console.warn('⚠️ [ProdottiService] certificazioniDettagli non è un array, convertendolo:', product.certificazioniDettagli);
                if (typeof product.certificazioniDettagli === 'object') {
                    product.certificazioniDettagli = Object.values(product.certificazioniDettagli);
                } else {
                    product.certificazioniDettagli = [];
                }
            }
        }
    }
}
