import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import {
  GestorePlatformaStatsDTO,
  UtenteDTO,
  ProdottoDTO,
  AziendaDTO,
  EventoDTO,
  UtentiFilters,
  ProdottiFilters,
  AziendeFilters,
  EventiFilters,
  BloccaUtenteRequestDTO,
  SospendiUtenteRequestDTO
} from '../models/gestore-platforma.models';

@Injectable({
  providedIn: 'root'
})
export class GestorePlatformaService {
  private readonly apiUrl = this.buildApiUrl('');

  constructor(private http: HttpClient) {}

  // === STATISTICHE ===
  
  getGestoreStats(): Observable<GestorePlatformaStatsDTO> {
    return this.http.get<GestorePlatformaStatsDTO>(`${this.apiUrl}/api/gestore/stats`);
  }
  
  // === GESTIONE UTENTI ===
  
  getUsers(filters?: UtentiFilters): Observable<UtenteDTO[]> {
    let params = this.buildParamsFromFilters(filters);
    
    return this.http.get<UtenteDTO[]>(`${this.apiUrl}/api/gestore/utenti`, { params });
  }
  
  getUserById(userId: number): Observable<UtenteDTO> {
    return this.http.get<UtenteDTO>(`${this.apiUrl}/api/gestore/utenti/${userId}`);
  }
  
  bloccaUtente(userId: number, request: BloccaUtenteRequestDTO): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/api/gestore/utenti/${userId}/blocca`, request);
  }
  
  sospendiUtente(userId: number, request: SospendiUtenteRequestDTO): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/api/gestore/utenti/${userId}/sospendi`, request);
  }
  
  attivaUtente(userId: number): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/api/gestore/utenti/${userId}/attiva`, {});
  }
  
  // === GESTIONE PRODOTTI ===
  
  getProducts(filters?: ProdottiFilters): Observable<ProdottoDTO[]> {
    let params = this.buildParamsFromFilters(filters);
    
    return this.http.get<ProdottoDTO[]>(`${this.apiUrl}/api/gestore/prodotti`, { params });
  }
  
  getProductById(productId: number): Observable<ProdottoDTO> {
    return this.http.get<ProdottoDTO>(`${this.apiUrl}/api/gestore/prodotti/${productId}`);
  }
  
  approveProduct(productId: number): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/api/gestore/prodotti/${productId}/approva`, {});
  }
  
  rejectProduct(productId: number, motivo: string): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/api/gestore/prodotti/${productId}/rifiuta`, { motivo });
  }
  
  // === GESTIONE AZIENDE ===
  
  getCompanies(filters?: AziendeFilters): Observable<AziendaDTO[]> {
    let params = this.buildParamsFromFilters(filters);
    
    return this.http.get<AziendaDTO[]>(`${this.apiUrl}/api/gestore/aziende`, { params });
  }
  
  getCompanyById(companyId: number): Observable<AziendaDTO> {
    return this.http.get<AziendaDTO>(`${this.apiUrl}/api/gestore/aziende/${companyId}`);
  }
  
  approveCompany(companyId: number): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/api/gestore/aziende/${companyId}/approva`, {});
  }
  
  rejectCompany(companyId: number, motivo: string): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/api/gestore/aziende/${companyId}/rifiuta`, { motivo });
  }
  
  // === GESTIONE EVENTI ===
  
  getEvents(filters?: EventiFilters): Observable<EventoDTO[]> {
    let params = this.buildParamsFromFilters(filters);
    
    return this.http.get<EventoDTO[]>(`${this.apiUrl}/api/gestore/eventi`, { params });
  }
  
  getEventById(eventId: number): Observable<EventoDTO> {
    return this.http.get<EventoDTO>(`${this.apiUrl}/api/gestore/eventi/${eventId}`);
  }
  
  approveEvent(eventId: number): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/api/gestore/eventi/${eventId}/approva`, {});
  }
  
  rejectEvent(eventId: number, motivo: string): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/api/gestore/eventi/${eventId}/rifiuta`, { motivo });
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

  formatDateTime(date: string): string {
    return new Date(date).toLocaleString('it-IT', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  }

  private buildApiUrl(path: string): string {
    const base = (environment.apiBaseUrl ?? '').replace(/\/$/, '');
    const prefix = (environment.apiPrefix ?? '').replace(/\/$/, '');
    const sanitizedPath = path.startsWith('/') ? path : `/${path}`;

    if (base) {
      return `${base}${prefix}${sanitizedPath}`;
    }

    return `${prefix || ''}${sanitizedPath}` || sanitizedPath;
  }

  private buildParamsFromFilters(filters?: any): HttpParams {
    let params = new HttpParams();
    
    if (filters) {
      if (filters.stato && filters.stato !== 'TUTTI') {
        params = params.set('stato', filters.stato);
      }
      
      if (filters.categoria) {
        params = params.set('categoria', filters.categoria);
      }
      
      if (filters.ruolo) {
        params = params.set('ruolo', filters.ruolo);
      }
      
      if (filters.search) {
        params = params.set('search', filters.search);
      }
      
      if (filters.dataDa) {
        params = params.set('dataDa', filters.dataDa);
      }
      
      if (filters.dataA) {
        params = params.set('dataA', filters.dataA);
      }
      
      if (filters.venditoreId) {
        params = params.set('venditoreId', filters.venditoreId.toString());
      }
      
      if (filters.organizzatoreId) {
        params = params.set('organizzatoreId', filters.organizzatoreId.toString());
      }
      
      if (filters.pagina) {
        params = params.set('pagina', filters.pagina.toString());
      }
      
      if (filters.elementiPerPagina) {
        params = params.set('elementiPerPagina', filters.elementiPerPagina.toString());
      }
    }
    
    return params;
  }
}