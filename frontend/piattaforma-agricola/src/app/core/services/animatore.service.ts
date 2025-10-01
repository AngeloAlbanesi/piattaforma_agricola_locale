import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import {
  EventoDTO,
  AnimatoreStatsDTO,
  CreaEventoRequestDTO,
  AggiornaEventoRequestDTO,
  EventoFilters,
  PartecipanteEventoDTO,
  FeedbackEventoDTO
} from '../models/animatore.models';

@Injectable({
  providedIn: 'root'
})
export class AnimatoreService {
  private readonly apiUrl = this.buildApiUrl('');

  constructor(private http: HttpClient) {}

  // === STATISTICHE ===
  
  getAnimatoreStats(): Observable<AnimatoreStatsDTO> {
    return this.http.get<AnimatoreStatsDTO>(`${this.apiUrl}/api/animatore/stats`);
  }
  
  // === GESTIONE EVENTI ===
  
  getMyEvents(filters?: EventoFilters): Observable<EventoDTO[]> {
    let params = this.buildParamsFromFilters(filters);
    
    return this.http.get<EventoDTO[]>(`${this.apiUrl}/api/animatore/eventi`, { params });
  }
  
  getEventById(eventId: number): Observable<EventoDTO> {
    return this.http.get<EventoDTO>(`${this.apiUrl}/api/animatore/eventi/${eventId}`);
  }
  
  createEvento(evento: CreaEventoRequestDTO): Observable<EventoDTO> {
    return this.http.post<EventoDTO>(`${this.apiUrl}/api/animatore/eventi`, evento);
  }
  
  updateEvento(eventId: number, evento: AggiornaEventoRequestDTO): Observable<EventoDTO> {
    return this.http.put<EventoDTO>(`${this.apiUrl}/api/animatore/eventi/${eventId}`, evento);
  }
  
  deleteEvento(eventId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/api/animatore/eventi/${eventId}`);
  }
  
  publishEvento(eventId: number): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/api/animatore/eventi/${eventId}/pubblica`, {});
  }
  
  // === GESTIONE PARTECIPANTI ===
  
  getEventParticipants(eventId: number): Observable<PartecipanteEventoDTO[]> {
    return this.http.get<PartecipanteEventoDTO[]>(`${this.apiUrl}/api/animatore/eventi/${eventId}/partecipanti`);
  }
  
  confirmParticipant(eventId: number, participantId: number): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/api/animatore/eventi/${eventId}/partecipanti/${participantId}/conferma`, {});
  }
  
  // === FEEDBACK ===
  
  getEventFeedback(eventId: number): Observable<FeedbackEventoDTO[]> {
    return this.http.get<FeedbackEventoDTO[]>(`${this.apiUrl}/api/animatore/eventi/${eventId}/feedback`);
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

  private buildParamsFromFilters(filters?: EventoFilters): HttpParams {
    let params = new HttpParams();
    
    if (filters) {
      if (filters.stato && filters.stato !== 'TUTTI') {
        params = params.set('stato', filters.stato);
      }
      
      if (filters.categoria) {
        params = params.set('categoria', filters.categoria);
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