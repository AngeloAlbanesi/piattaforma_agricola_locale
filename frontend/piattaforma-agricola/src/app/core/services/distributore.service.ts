import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { environment } from '../../../environments/environment';
import {
    PacchettoTipicitaDTO,
    DettaglioPacchettoDTO,
    DistributoreStatsDTO,
    CreatePacchettoRequestDTO,
    UpdatePacchettoRequestDTO,
    DistributoreProductDTO,
    CreateDistributoreProductRequestDTO,
    UpdateDistributoreProductRequestDTO
} from '../models/distributore.models';
import { PaginatedResponse } from '../models/common.models';

@Injectable({
    providedIn: 'root'
})
export class DistributoreService {
    private readonly apiUrl = this.buildApiUrl('');

    constructor(private http: HttpClient) { }

    // === PACCHETTI ===

    getMyPackages(): Observable<PacchettoTipicitaDTO[]> {
        return this.http.get<PacchettoTipicitaDTO[]>(`${this.apiUrl}/pacchetti/miei-pacchetti`);
    }

    getPackageById(id: number): Observable<DettaglioPacchettoDTO> {
        return this.http.get<DettaglioPacchettoDTO>(`${this.apiUrl}/pacchetti/${id}`);
    }

    createPackage(request: CreatePacchettoRequestDTO): Observable<PacchettoTipicitaDTO> {
        return this.http.post<PacchettoTipicitaDTO>(`${this.apiUrl}/pacchetti`, request);
    }

    updatePackage(id: number, request: UpdatePacchettoRequestDTO): Observable<PacchettoTipicitaDTO> {
        return this.http.put<PacchettoTipicitaDTO>(`${this.apiUrl}/pacchetti/${id}`, request);
    }

    deletePackage(id: number): Observable<void> {
        return this.http.delete<void>(`${this.apiUrl}/pacchetti/${id}`);
    }

    // === GESTIONE PRODOTTI ===

    getMyProducts(): Observable<DistributoreProductDTO[]> {
        return this.http.get<PaginatedResponse<DistributoreProductDTO>>(`${this.apiUrl}/prodotti/miei-prodotti`).pipe(
            map(response => {
                // Estrai il contenuto dalla risposta paginata
                if (response && response.content) {
                    // Normalizza ogni prodotto
                    response.content.forEach(product => this.normalizeProduct(product));
                    return response.content;
                }
                return [];
            })
        );
    }

    getProductById(id: number): Observable<DistributoreProductDTO> {
        return this.http.get<DistributoreProductDTO>(`${this.apiUrl}/prodotti/${id}`).pipe(
            map(product => {
                this.normalizeProduct(product);
                return product;
            })
        );
    }

    createProduct(request: CreateDistributoreProductRequestDTO): Observable<DistributoreProductDTO> {
        return this.http.post<DistributoreProductDTO>(`${this.apiUrl}/prodotti`, request).pipe(
            map(product => {
                this.normalizeProduct(product);
                return product;
            })
        );
    }

    updateProduct(id: number, request: UpdateDistributoreProductRequestDTO): Observable<DistributoreProductDTO> {
        return this.http.put<DistributoreProductDTO>(`${this.apiUrl}/prodotti/${id}`, request).pipe(
            map(product => {
                this.normalizeProduct(product);
                return product;
            })
        );
    }

    deleteProduct(id: number): Observable<void> {
        return this.http.delete<void>(`${this.apiUrl}/prodotti/${id}`);
    }

    // === GESTIONE PRODOTTI NEI PACCHETTI ===

    addProductToPackage(packageId: number, productId: number, quantita: number): Observable<void> {
        return this.http.post<void>(`${this.apiUrl}/pacchetti/${packageId}/prodotti`, {
            idProdotto: productId,
            quantita: quantita
        });
    }

    removeProductFromPackage(packageId: number, productId: number): Observable<void> {
        return this.http.delete<void>(`${this.apiUrl}/pacchetti/${packageId}/prodotti/${productId}`);
    }

    updateProductQuantity(packageId: number, productId: number, quantita: number): Observable<void> {
        return this.http.put<void>(`${this.apiUrl}/pacchetti/${packageId}/prodotti/${productId}`, {
            quantita: quantita
        });
    }

    // === GESTIONE CERTIFICAZIONI PRODOTTI ===

    addCertificationToProduct(productId: number, certification: any): Observable<void> {
        return this.http.post<void>(`${this.apiUrl}/prodotti/${productId}/certificazioni`, certification);
    }

    removeCertificationFromProduct(productId: number, certificationId: number): Observable<void> {
        return this.http.delete<void>(`${this.apiUrl}/prodotti/${productId}/certificazioni/${certificationId}`);
    }

    // === STATISTICHE ===

    getDistributoreStats(): Observable<DistributoreStatsDTO> {
        return this.http.get<DistributoreStatsDTO>(`${this.apiUrl}/distributore/stats`);
    }

    // === UTILITIES ===

    formatCurrency(value: number): string {
        return new Intl.NumberFormat('it-IT', {
            style: 'currency',
            currency: 'EUR'
        }).format(value);
    }

    formatDate(date: string): string {
        return new Date(date).toLocaleDateString('it-IT', {
            year: 'numeric',
            month: 'long',
            day: 'numeric'
        });
    }

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
     * Normalizza i dati del prodotto assicurandosi che certificazioni sia sempre un array
     * Questo risolve il problema NG02200 quando il backend restituisce certificazioni come oggetto
     */
    private normalizeProduct(product: DistributoreProductDTO | any): void {
        if (product && product.certificazioni) {
            if (!Array.isArray(product.certificazioni)) {
                console.warn('⚠️ [DistributoreService] certificazioni non è un array, convertendolo:', product.certificazioni);
                // Se certificazioni è un oggetto, prova a convertirlo in array
                if (typeof product.certificazioni === 'object') {
                    // Se è un oggetto con chiavi, converti in array
                    product.certificazioni = Object.values(product.certificazioni);
                } else {
                    // Altrimenti usa un array vuoto
                    product.certificazioni = [];
                }
            }
        }
    }
}
