import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { PaginatedResponse } from '../models/common.models';
import {
    ProduttoreProductSummaryDTO,
    ProduttoreProductDetailDTO,
    CertificationDTO,
    CreateCertificazioneRequestDTO,
    MetodoDiColtivazioneDTO,
    CreateMetodoDiColtivazioneRequestDTO,
    OrdineRiepilogoDTO,
    ProduttoreStatsDTO,
    ProduttoreProductFilters,
    CreateProductRequestDTO,
    UpdateProductRequestDTO,
    ProductQuantityUpdateDTO
} from '../models/produttore.models';

@Injectable({
    providedIn: 'root'
})
export class ProduttoreService {
    private readonly apiUrl = this.buildApiUrl('');

    constructor(private http: HttpClient) { }

    // === PRODOTTI ===

    getMyProducts(filters?: ProduttoreProductFilters & { pagina?: number, elementiPerPagina?: number }): Observable<PaginatedResponse<ProduttoreProductSummaryDTO>> {
        let params = this.buildParamsFromFilters(filters);

        // Aggiungo i parametri di paginazione se presenti
        if (filters?.pagina !== undefined) {
            params = params.set('pagina', filters.pagina.toString());
        }
        if (filters?.elementiPerPagina !== undefined) {
            params = params.set('elementiPerPagina', filters.elementiPerPagina.toString());
        }

        return this.http.get<PaginatedResponse<ProduttoreProductSummaryDTO>>(`${this.apiUrl}/prodotti/miei-prodotti`, { params });
    }

    getProductById(id: number): Observable<ProduttoreProductDetailDTO> {
        return this.http.get<ProduttoreProductDetailDTO>(`${this.apiUrl}/prodotti/${id}`);
    }

    createProduct(request: CreateProductRequestDTO): Observable<ProduttoreProductDetailDTO> {
        return this.http.post<ProduttoreProductDetailDTO>(`${this.apiUrl}/prodotti`, request);
    }

    updateProduct(id: number, request: UpdateProductRequestDTO): Observable<ProduttoreProductDetailDTO> {
        return this.http.put<ProduttoreProductDetailDTO>(`${this.apiUrl}/prodotti/${id}`, request);
    }

    deleteProduct(id: number): Observable<void> {
        return this.http.delete<void>(`${this.apiUrl}/prodotti/${id}`);
    }

    updateProductQuantity(id: number, request: ProductQuantityUpdateDTO): Observable<ProduttoreProductDetailDTO> {
        return this.http.put<ProduttoreProductDetailDTO>(`${this.apiUrl}/prodotti/${id}/quantita`, request);
    }

    // === CERTIFICAZIONI ===

    addCertificationToProduct(productId: number, request: CreateCertificazioneRequestDTO): Observable<CertificationDTO> {
        return this.http.post<CertificationDTO>(`${this.apiUrl}/prodotti/${productId}/certificazioni`, request);
    }

    removeCertificationFromProduct(productId: number, certificationId: number): Observable<void> {
        return this.http.delete<void>(`${this.apiUrl}/prodotti/${productId}/certificazioni/${certificationId}`);
    }

    getProductCertifications(productId: number): Observable<CertificationDTO[]> {
        return this.http.get<CertificationDTO[]>(`${this.apiUrl}/prodotti/${productId}/certificazioni`);
    }

    getAllMyCertifications(): Observable<CertificationDTO[]> {
        return this.http.get<CertificationDTO[]>(`${this.apiUrl}/prodotti/miei-prodotti/certificazioni`);
    }

    // === METODI DI COLTIVAZIONE ===

    createCultivationMethod(productId: number, request: CreateMetodoDiColtivazioneRequestDTO): Observable<MetodoDiColtivazioneDTO> {
        return this.http.post<MetodoDiColtivazioneDTO>(`${this.apiUrl}/prodotti/${productId}/metodi-coltivazione`, request);
    }

    updateCultivationMethod(productId: number, request: CreateMetodoDiColtivazioneRequestDTO): Observable<MetodoDiColtivazioneDTO> {
        return this.http.put<MetodoDiColtivazioneDTO>(`${this.apiUrl}/prodotti/${productId}/metodi-coltivazione`, request);
    }

    deleteCultivationMethod(productId: number): Observable<void> {
        return this.http.delete<void>(`${this.apiUrl}/prodotti/${productId}/metodi-coltivazione`);
    }

    getCultivationMethod(productId: number): Observable<MetodoDiColtivazioneDTO> {
        return this.http.get<MetodoDiColtivazioneDTO>(`${this.apiUrl}/prodotti/${productId}/metodi-coltivazione`);
    }

    // === ORDINI ===

    getMyOrders(): Observable<OrdineRiepilogoDTO[]> {
        return this.http.get<OrdineRiepilogoDTO[]>(`${this.apiUrl}/ordini-venditore/miei-ordini`);
    }

    getOrderById(id: number): Observable<OrdineRiepilogoDTO> {
        return this.http.get<OrdineRiepilogoDTO>(`${this.apiUrl}/ordini-venditore/${id}`);
    }

    updateOrderStatus(orderId: number, status: string): Observable<OrdineRiepilogoDTO> {
        return this.http.put<OrdineRiepilogoDTO>(`${this.apiUrl}/ordini-venditore/${orderId}/stato`, { status });
    }

    // Metodi che seguono la specifica in docs/API_PRODUTTORE.md
    processOrder(orderId: number): Observable<OrdineRiepilogoDTO> {
        // Cambia stato da PRONTO_PER_LAVORAZIONE a IN_LAVORAZIONE
        return this.http.put<OrdineRiepilogoDTO>(`${this.apiUrl}/ordini/venditori/${orderId}/process`, {});
    }

    shipOrder(orderId: number, request: { trackingNumber: string; carrier: string; estimatedDeliveryDate: string }): Observable<OrdineRiepilogoDTO> {
        return this.http.put<OrdineRiepilogoDTO>(`${this.apiUrl}/ordini/venditori/${orderId}/ship`, request);
    }

    deliverOrder(orderId: number): Observable<OrdineRiepilogoDTO> {
        return this.http.put<OrdineRiepilogoDTO>(`${this.apiUrl}/ordini/venditori/${orderId}/deliver`, {});
    }

    cancelOrder(orderId: number, request: { motivoAnnullamento: string }): Observable<OrdineRiepilogoDTO> {
        return this.http.put<OrdineRiepilogoDTO>(`${this.apiUrl}/ordini/venditori/${orderId}/cancel`, request);
    }

    // === STATISTICHE ===

    getProduttoreStats(): Observable<ProduttoreStatsDTO> {
        return this.http.get<ProduttoreStatsDTO>(`${this.apiUrl}/produttore/stats`);
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

    // Formatta valori per display
    formatCurrency(value: number): string {
        return new Intl.NumberFormat('it-IT', {
            style: 'currency',
            currency: 'EUR',
            minimumFractionDigits: 0,
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

    // Converte i filtri in HttpParams per le chiamate API
    private buildParamsFromFilters(filters?: ProduttoreProductFilters): HttpParams {
        let params = new HttpParams();

        if (filters) {
            if (filters.search) {
                params = params.set('search', filters.search);
            }
            if (filters.statoVerifica && filters.statoVerifica !== 'TUTTI') {
                params = params.set('statoVerifica', filters.statoVerifica);
            }
            if (filters.tipoOrigine && filters.tipoOrigine !== 'TUTTI') {
                params = params.set('tipoOrigine', filters.tipoOrigine);
            }
            if (filters.certificazioni && filters.certificazioni.length > 0) {
                filters.certificazioni.forEach(cert => {
                    params = params.append('certificazioni', cert);
                });
            }
            if (filters.periodo) {
                params = params.set('periodo', filters.periodo);
            }
            if (filters.prezzoMin !== undefined) {
                params = params.set('prezzoMin', filters.prezzoMin.toString());
            }
            if (filters.prezzoMax !== undefined) {
                params = params.set('prezzoMax', filters.prezzoMax.toString());
            }
            if (filters.disponibilita !== undefined) {
                params = params.set('disponibilita', filters.disponibilita.toString());
            }
            if (filters.ordinamento) {
                params = params.set('ordinamento', filters.ordinamento);
            }
        }

        return params;
    }
}