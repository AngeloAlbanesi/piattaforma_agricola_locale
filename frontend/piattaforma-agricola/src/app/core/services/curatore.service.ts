import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import {
  ApprovazionePendingDTO,
  CuratoreStatsDTO,
  ApprovazioneRequestDTO,
  ApprovazioneFilters,
  ProdottoApprovazioneDTO,
  AziendaApprovazioneDTO,
  ContenutoApprovazioneDTO
} from '../models/curatore.models';

@Injectable({
  providedIn: 'root'
})
export class CuratoreService {
  private readonly apiUrl = this.buildApiUrl('');

  constructor(private http: HttpClient) {}

  // === STATISTICHE ===
  
  getCuratoreStats(): Observable<CuratoreStatsDTO> {
    return this.http.get<CuratoreStatsDTO>(`${this.apiUrl}/api/curatore/stats`);
  }
  
  // === APPROVAZIONI IN CORSO ===
  
  getPendingApprovals(filters?: ApprovazioneFilters): Observable<ApprovazionePendingDTO[]> {
    let params = this.buildParamsFromFilters(filters);
    
    return this.http.get<ApprovazionePendingDTO[]>(`${this.apiUrl}/api/curatore/approvazioni/pending`, { params });
  }
  
  approveElement(elementId: number, tipo: string, request: ApprovazioneRequestDTO): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/api/curatore/approvazioni/${tipo}/${elementId}/approva`, request);
  }
  
  rejectElement(elementId: number, tipo: string, request: ApprovazioneRequestDTO): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/api/curatore/approvazioni/${tipo}/${elementId}/rifiuta`, request);
  }
  
  // === DETTAGLI ELEMENTI DA APPROVARE ===
  
  getProductDetails(productId: number): Observable<ProdottoApprovazioneDTO> {
    return this.http.get<ProdottoApprovazioneDTO>(`${this.apiUrl}/api/curatore/prodotti/${productId}/dettagli`);
  }
  
  getCompanyDetails(companyId: number): Observable<AziendaApprovazioneDTO> {
    return this.http.get<AziendaApprovazioneDTO>(`${this.apiUrl}/api/curatore/aziende/${companyId}/dettagli`);
  }
  
  getContentDetails(contentId: number): Observable<ContenutoApprovazioneDTO> {
    return this.http.get<ContenutoApprovazioneDTO>(`${this.apiUrl}/api/curatore/contenuti/${contentId}/dettagli`);
  }
  
  // === STORICO APPROVAZIONI ===
  
  getApprovalHistory(filters?: ApprovazioneFilters): Observable<ApprovazionePendingDTO[]> {
    let params = this.buildParamsFromFilters(filters);
    
    return this.http.get<ApprovazionePendingDTO[]>(`${this.apiUrl}/api/curatore/approvazioni/storico`, { params });
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

  private buildParamsFromFilters(filters?: ApprovazioneFilters): HttpParams {
    let params = new HttpParams();
    
    if (filters) {
      if (filters.tipo && filters.tipo !== 'TUTTI') {
        params = params.set('tipo', filters.tipo);
      }
      
      if (filters.stato && filters.stato !== 'TUTTI') {
        params = params.set('stato', filters.stato);
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
      
      if (filters.richiedenteId) {
        params = params.set('richiedenteId', filters.richiedenteId.toString());
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