import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import {
  ProductSummaryDTO,
  ProductDetailDTO,
  CarrelloDTO,
  ElementoCarrelloDTO,
  AddToCartRequestDTO,
  OrdineSummaryDTO,
  OrdineDetailDTO,
  CreateOrdineRequestDTO,
  EventoSummaryDTO,
  EventoDetailDTO,
  EventoRegistrazioneRequestDTO,
  PacchettoSummaryDTO,
  PacchettoDetailDTO,
  AcquirenteStatsDTO,
  ProductFilters,
  PaginationParams,
  PaginatedResponse
} from '../models/acquirente.models';

@Injectable({
  providedIn: 'root'
})
export class AcquirenteService {
  private readonly apiUrl = this.buildApiUrl('');

  constructor(private http: HttpClient) {}

  // === PRODOTTI ===
  
  getProducts(filters?: ProductFilters, pagination?: PaginationParams): Observable<PaginatedResponse<ProductSummaryDTO>> {
    let params = new HttpParams();
    
    if (pagination) {
      params = params.set('page', pagination.page.toString());
      params = params.set('size', pagination.size.toString());
      if (pagination.sortBy) {
        params = params.set('sortBy', pagination.sortBy);
      }
      if (pagination.sortDirection) {
        params = params.set('sortDirection', pagination.sortDirection);
      }
    }
    
    if (filters?.search) {
      params = params.set('search', filters.search);
    }
    
    if (filters?.venditoreId) {
      params = params.set('vendorId', filters.venditoreId.toString());
    }
    
    if (filters?.categoria) {
      params = params.set('categoria', filters.categoria);
    }
    
    return this.http.get<PaginatedResponse<ProductSummaryDTO>>(`${this.apiUrl}/api/prodotti`, { params });
  }

  getProductById(id: number): Observable<ProductDetailDTO> {
    return this.http.get<ProductDetailDTO>(`${this.apiUrl}/api/prodotti/${id}`);
  }

  searchProducts(query: string): Observable<ProductSummaryDTO[]> {
    return this.http.get<ProductSummaryDTO[]>(`${this.apiUrl}/api/prodotti/cercaProdotti`, {
      params: { query }
    });
  }

  getProductsByVendor(vendorId: number): Observable<ProductSummaryDTO[]> {
    return this.http.get<ProductSummaryDTO[]>(`${this.apiUrl}/api/prodotti/venditori/${vendorId}`);
  }

  // === CARRELLO ===
  
  getCart(): Observable<CarrelloDTO> {
    return this.http.get<CarrelloDTO>(`${this.apiUrl}/api/carrello`);
  }

  addToCart(request: AddToCartRequestDTO): Observable<{ message: string }> {
    return this.http.post<{ message: string }>(`${this.apiUrl}/api/carrello/elementi`, request);
  }

  updateCartItem(itemId: number, quantita: number): Observable<{ message: string }> {
    return this.http.put<{ message: string }>(`${this.apiUrl}/api/carrello/elementi/${itemId}`, { quantita });
  }

  removeFromCart(itemId: number): Observable<{ message: string }> {
    return this.http.delete<{ message: string }>(`${this.apiUrl}/api/carrello/elementi/${itemId}`);
  }

  clearCart(): Observable<{ message: string }> {
    return this.http.delete<{ message: string }>(`${this.apiUrl}/api/carrello`);
  }

  getCartSummary(): Observable<{
    totalElementi: number;
    totale: number;
    ultimaModifica: string;
  }> {
    return this.http.get<{
      totalElementi: number;
      totale: number;
      ultimaModifica: string;
    }>(`${this.apiUrl}/api/carrello/sommario`);
  }

  // === ORDINI ===
  
  getOrders(pagination?: PaginationParams): Observable<PaginatedResponse<OrdineSummaryDTO>> {
    let params = new HttpParams();
    
    if (pagination) {
      params = params.set('page', pagination.page.toString());
      params = params.set('size', pagination.size.toString());
      if (pagination.sortBy) {
        params = params.set('sortBy', pagination.sortBy);
      }
      if (pagination.sortDirection) {
        params = params.set('sortDirection', pagination.sortDirection);
      }
    }
    
    return this.http.get<PaginatedResponse<OrdineSummaryDTO>>(`${this.apiUrl}/api/ordini`, { params });
  }

  getOrderById(id: number): Observable<OrdineDetailDTO> {
    return this.http.get<OrdineDetailDTO>(`${this.apiUrl}/api/ordini/${id}`);
  }

  createOrder(request: CreateOrdineRequestDTO): Observable<OrdineDetailDTO> {
    return this.http.post<OrdineDetailDTO>(`${this.apiUrl}/api/ordini`, request);
  }

  cancelOrder(id: number): Observable<{ message: string }> {
    return this.http.delete<{ message: string }>(`${this.apiUrl}/api/ordini/${id}`);
  }

  // === EVENTI ===
  
  getEvents(pagination?: PaginationParams): Observable<PaginatedResponse<EventoSummaryDTO>> {
    let params = new HttpParams();
    
    if (pagination) {
      params = params.set('page', pagination.page.toString());
      params = params.set('size', pagination.size.toString());
      if (pagination.sortBy) {
        params = params.set('sortBy', pagination.sortBy);
      }
      if (pagination.sortDirection) {
        params = params.set('sortDirection', pagination.sortDirection);
      }
    }
    
    return this.http.get<PaginatedResponse<EventoSummaryDTO>>(`${this.apiUrl}/api/eventi`, { params });
  }

  getEventById(id: number): Observable<EventoDetailDTO> {
    return this.http.get<EventoDetailDTO>(`${this.apiUrl}/api/eventi/${id}`);
  }

  searchEvents(query: string): Observable<EventoSummaryDTO[]> {
    return this.http.get<EventoSummaryDTO[]>(`${this.apiUrl}/api/eventi/cercaEventi`, {
      params: { query }
    });
  }

  registerForEvent(eventId: number, request: EventoRegistrazioneRequestDTO): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/api/eventi/${eventId}/registra`, request);
  }

  cancelEventRegistration(eventId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/api/eventi/${eventId}/registra`);
  }

  // === PACCHETTI ===
  
  getPackages(pagination?: PaginationParams): Observable<PaginatedResponse<PacchettoSummaryDTO>> {
    let params = new HttpParams();
    
    if (pagination) {
      params = params.set('page', pagination.page.toString());
      params = params.set('size', pagination.size.toString());
      if (pagination.sortBy) {
        params = params.set('sortBy', pagination.sortBy);
      }
      if (pagination.sortDirection) {
        params = params.set('sortDirection', pagination.sortDirection);
      }
    }
    
    return this.http.get<PaginatedResponse<PacchettoSummaryDTO>>(`${this.apiUrl}/api/pacchetti`, { params });
  }

  getPackageById(id: number): Observable<PacchettoDetailDTO> {
    return this.http.get<PacchettoDetailDTO>(`${this.apiUrl}/api/pacchetti/${id}`);
  }

  searchPackages(query: string): Observable<PacchettoSummaryDTO[]> {
    return this.http.get<PacchettoSummaryDTO[]>(`${this.apiUrl}/api/pacchetti/cercaPacchetti`, {
      params: { query }
    });
  }

  getPackageComposition(id: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/api/pacchetti/${id}/composizione`);
  }

  // === STATISTICHE ACQUIRENTE ===
  
  getAcquirenteStats(): Observable<AcquirenteStatsDTO> {
    return this.http.get<AcquirenteStatsDTO>(`${this.apiUrl}/api/acquirente/stats`);
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

  // Converte i filtri in HttpParams per le chiamate API
  private buildParamsFromFilters(filters?: ProductFilters, pagination?: PaginationParams): HttpParams {
    let params = new HttpParams();
    
    if (pagination) {
      params = params.set('page', pagination.page.toString());
      params = params.set('size', pagination.size.toString());
      if (pagination.sortBy) {
        params = params.set('sortBy', pagination.sortBy);
      }
      if (pagination.sortDirection) {
        params = params.set('sortDirection', pagination.sortDirection);
      }
    }
    
    if (filters) {
      if (filters.search) {
        params = params.set('search', filters.search);
      }
      if (filters.categoria) {
        params = params.set('categoria', filters.categoria);
      }
      if (filters.prezzoMin !== undefined) {
        params = params.set('prezzoMin', filters.prezzoMin.toString());
      }
      if (filters.prezzoMax !== undefined) {
        params = params.set('prezzoMax', filters.prezzoMax.toString());
      }
      if (filters.venditoreId) {
        params = params.set('venditoreId', filters.venditoreId.toString());
      }
      if (filters.soloDisponibili) {
        params = params.set('soloDisponibili', 'true');
      }
      if (filters.certificazioni && filters.certificazioni.length > 0) {
        filters.certificazioni.forEach(cert => {
          params = params.append('certificazioni', cert);
        });
      }
      if (filters.ordinamento) {
        params = params.set('ordinamento', filters.ordinamento);
      }
    }
    
    return params;
  }
}