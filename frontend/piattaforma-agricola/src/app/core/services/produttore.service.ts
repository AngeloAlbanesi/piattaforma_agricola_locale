import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
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

  constructor(private http: HttpClient) {}

  // === PRODOTTI ===
  
  getMyProducts(filters?: ProduttoreProductFilters): Observable<ProduttoreProductSummaryDTO[]> {
    let params = new HttpParams();
    
    if (filters) {
      if (filters.search) {
        params = params.set('search', filters.search);
      }
      if (filters.statoVerifica) {
        params = params.set('statoVerifica', filters.statoVerifica);
      }
      if (filters.tipoOrigine) {
        params = params.set('tipoOrigine', filters.tipoOrigine);
      }
      if (filters.disponibilita !== undefined) {
        params = params.set('disponibilita', filters.disponibilita.toString());
      }
      if (filters.ordinamento) {
        params = params.set('ordinamento', filters.ordinamento);
      }
    }
    
    return this.http.get<ProduttoreProductSummaryDTO[]>(`${this.apiUrl}/api/prodotti/miei-prodotti`, { params });
  }

  getProductById(id: number): Observable<ProduttoreProductDetailDTO> {
    return this.http.get<ProduttoreProductDetailDTO>(`${this.apiUrl}/api/prodotti/${id}`);
  }

  createProduct(request: CreateProductRequestDTO): Observable<ProduttoreProductDetailDTO> {
    return this.http.post<ProduttoreProductDetailDTO>(`${this.apiUrl}/api/prodotti`, request);
  }

  updateProduct(id: number, request: UpdateProductRequestDTO): Observable<ProduttoreProductDetailDTO> {
    return this.http.put<ProduttoreProductDetailDTO>(`${this.apiUrl}/api/prodotti/${id}`, request);
  }

  deleteProduct(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/api/prodotti/${id}`);
  }

  updateProductQuantity(id: number, request: ProductQuantityUpdateDTO): Observable<ProduttoreProductDetailDTO> {
    return this.http.put<ProduttoreProductDetailDTO>(`${this.apiUrl}/api/prodotti/${id}/quantita`, request);
  }

  // === CERTIFICAZIONI ===
  
  addCertificationToProduct(productId: number, request: CreateCertificazioneRequestDTO): Observable<CertificationDTO> {
    return this.http.post<CertificationDTO>(`${this.apiUrl}/api/prodotti/${productId}/certificazioni`, request);
  }

  removeCertificationFromProduct(productId: number, certificationId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/api/prodotti/${productId}/certificazioni/${certificationId}`);
  }

  getProductCertifications(productId: number): Observable<CertificationDTO[]> {
    return this.http.get<CertificationDTO[]>(`${this.apiUrl}/api/prodotti/${productId}/certificazioni`);
  }

  // === METODI DI COLTIVAZIONE ===
  
  createCultivationMethod(productId: number, request: CreateMetodoDiColtivazioneRequestDTO): Observable<MetodoDiColtivazioneDTO> {
    return this.http.post<MetodoDiColtivazioneDTO>(`${this.apiUrl}/api/prodotti/${productId}/metodi-coltivazione`, request);
  }

  updateCultivationMethod(productId: number, request: CreateMetodoDiColtivazioneRequestDTO): Observable<MetodoDiColtivazioneDTO> {
    return this.http.put<MetodoDiColtivazioneDTO>(`${this.apiUrl}/api/prodotti/${productId}/metodi-coltivazione`, request);
  }

  deleteCultivationMethod(productId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/api/prodotti/${productId}/metodi-coltivazione`);
  }

  getCultivationMethod(productId: number): Observable<MetodoDiColtivazioneDTO> {
    return this.http.get<MetodoDiColtivazioneDTO>(`${this.apiUrl}/api/prodotti/${productId}/metodi-coltivazione`);
  }

  // === ORDINI ===
  
  getMyOrders(): Observable<OrdineRiepilogoDTO[]> {
    return this.http.get<OrdineRiepilogoDTO[]>(`${this.apiUrl}/api/ordini-venditore/miei-ordini`);
  }

  getOrderById(id: number): Observable<OrdineRiepilogoDTO> {
    return this.http.get<OrdineRiepilogoDTO>(`${this.apiUrl}/api/ordini-venditore/${id}`);
  }

  updateOrderStatus(orderId: number, status: string): Observable<OrdineRiepilogoDTO> {
    return this.http.put<OrdineRiepilogoDTO>(`${this.apiUrl}/api/ordini-venditore/${orderId}/stato`, { status });
  }

  // === STATISTICHE ===
  
  getProduttoreStats(): Observable<ProduttoreStatsDTO> {
    return this.http.get<ProduttoreStatsDTO>(`${this.apiUrl}/api/produttore/stats`);
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
      if (filters.statoVerifica) {
        params = params.set('statoVerifica', filters.statoVerifica);
      }
      if (filters.tipoOrigine) {
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