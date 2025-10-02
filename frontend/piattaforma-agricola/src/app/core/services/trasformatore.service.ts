import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { PaginatedResponse } from '../models/common.models';
import {
  ProcessoTrasformazioneSummaryDTO,
  ProcessoTrasformazioneDetailDTO,
  FaseLavorazioneDTO,
  CreateFaseLavorazioneRequestDTO,
  UpdateFaseLavorazioneRequestDTO,
  TracciabilitaDTO,
  CreateTracciabilitaRequestDTO,
  CertificationDTO,
  CreateCertificazioneRequestDTO,
  CostoProcessoDTO,
  ResaProcessoDTO,
  TrasformatoreStatsDTO,
  ProcessoFilters,
  CreateProcessoRequestDTO,
  UpdateProcessoRequestDTO,
  UpdateStatoProcessoRequestDTO
} from '../models/trasformatore.models';

@Injectable({
  providedIn: 'root'
})
export class TrasformatoreService {
  private readonly apiUrl = this.buildApiUrl('');

  constructor(private http: HttpClient) {}

  // === PROCESSI DI TRASFORMAZIONE ===
  
  getMyProcesses(filters?: ProcessoFilters & { pagina?: number, elementiPerPagina?: number }): Observable<PaginatedResponse<ProcessoTrasformazioneSummaryDTO>> {
    let params = new HttpParams();
    
    if (filters) {
      if (filters.search) {
        params = params.set('search', filters.search);
      }
      if (filters.stato) {
        params = params.set('stato', filters.stato);
      }
      if (filters.dataInizio) {
        params = params.set('dataInizio', filters.dataInizio);
      }
      if (filters.dataFine) {
        params = params.set('dataFine', filters.dataFine);
      }
      if (filters.ordinamento) {
        params = params.set('ordinamento', filters.ordinamento);
      }
    }
    
    // Aggiungo i parametri di paginazione se presenti
    if (filters?.pagina !== undefined) {
      params = params.set('pagina', filters.pagina.toString());
    }
    if (filters?.elementiPerPagina !== undefined) {
      params = params.set('elementiPerPagina', filters.elementiPerPagina.toString());
    }

    return this.http.get<PaginatedResponse<ProcessoTrasformazioneSummaryDTO>>(`${this.apiUrl}/api/processi-trasformazione/miei-processi`, { params });
  }

  getProcessById(id: number): Observable<ProcessoTrasformazioneDetailDTO> {
    return this.http.get<ProcessoTrasformazioneDetailDTO>(`${this.apiUrl}/api/processi-trasformazione/${id}`);
  }

  createProcess(request: CreateProcessoRequestDTO): Observable<ProcessoTrasformazioneDetailDTO> {
    return this.http.post<ProcessoTrasformazioneDetailDTO>(`${this.apiUrl}/api/processi-trasformazione`, request);
  }

  updateProcess(id: number, request: UpdateProcessoRequestDTO): Observable<ProcessoTrasformazioneDetailDTO> {
    return this.http.put<ProcessoTrasformazioneDetailDTO>(`${this.apiUrl}/api/processi-trasformazione/${id}`, request);
  }

  deleteProcess(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/api/processi-trasformazione/${id}`);
  }

  updateProcessStatus(id: number, request: UpdateStatoProcessoRequestDTO): Observable<ProcessoTrasformazioneDetailDTO> {
    return this.http.put<ProcessoTrasformazioneDetailDTO>(`${this.apiUrl}/api/processi-trasformazione/${id}/stato`, request);
  }

  // === FASI DI LAVORAZIONE ===
  
  getProcessPhases(processId: number): Observable<FaseLavorazioneDTO[]> {
    return this.http.get<FaseLavorazioneDTO[]>(`${this.apiUrl}/api/processi-trasformazione/${processId}/fasi`);
  }

  createPhase(processId: number, request: CreateFaseLavorazioneRequestDTO): Observable<FaseLavorazioneDTO> {
    return this.http.post<FaseLavorazioneDTO>(`${this.apiUrl}/api/processi-trasformazione/${processId}/fasi`, request);
  }

  updatePhase(processId: number, phaseId: number, request: UpdateFaseLavorazioneRequestDTO): Observable<FaseLavorazioneDTO> {
    return this.http.put<FaseLavorazioneDTO>(`${this.apiUrl}/api/processi-trasformazione/${processId}/fasi/${phaseId}`, request);
  }

  deletePhase(processId: number, phaseId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/api/processi-trasformazione/${processId}/fasi/${phaseId}`);
  }

  updatePhaseStatus(processId: number, phaseId: number, stato: string): Observable<FaseLavorazioneDTO> {
    return this.http.put<FaseLavorazioneDTO>(`${this.apiUrl}/api/processi-trasformazione/${processId}/fasi/${phaseId}/stato`, { stato });
  }

  // === TRACCIABILITÀ ===
  
  getProcessTraceability(processId: number): Observable<TracciabilitaDTO[]> {
    return this.http.get<TracciabilitaDTO[]>(`${this.apiUrl}/api/processi-trasformazione/${processId}/tracciabilita`);
  }

  createTraceability(processId: number, request: CreateTracciabilitaRequestDTO): Observable<TracciabilitaDTO> {
    return this.http.post<TracciabilitaDTO>(`${this.apiUrl}/api/processi-trasformazione/${processId}/tracciabilita`, request);
  }

  getTraceabilityById(id: number): Observable<TracciabilitaDTO> {
    return this.http.get<TracciabilitaDTO>(`${this.apiUrl}/api/tracciabilita/${id}`);
  }

  // === CERTIFICAZIONI ===
  
  getProcessCertifications(processId: number): Observable<CertificationDTO[]> {
    return this.http.get<CertificationDTO[]>(`${this.apiUrl}/api/processi-trasformazione/${processId}/certificazioni`);
  }

  addCertificationToProcess(processId: number, request: CreateCertificazioneRequestDTO): Observable<CertificationDTO> {
    return this.http.post<CertificationDTO>(`${this.apiUrl}/api/processi-trasformazione/${processId}/certificazioni`, request);
  }

  removeCertificationFromProcess(processId: number, certificationId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/api/processi-trasformazione/${processId}/certificazioni/${certificationId}`);
  }

  // === COSTI E RESA ===
  
  getProcessCosts(processId: number): Observable<CostoProcessoDTO[]> {
    return this.http.get<CostoProcessoDTO[]>(`${this.apiUrl}/api/processi-trasformazione/${processId}/costi`);
  }

  addCostToProcess(processId: number, request: any): Observable<CostoProcessoDTO> {
    return this.http.post<CostoProcessoDTO>(`${this.apiUrl}/api/processi-trasformazione/${processId}/costi`, request);
  }

  getProcessResa(processId: number): Observable<ResaProcessoDTO[]> {
    return this.http.get<ResaProcessoDTO[]>(`${this.apiUrl}/api/processi-trasformazione/${processId}/resa`);
  }

  addResaToProcess(processId: number, request: any): Observable<ResaProcessoDTO> {
    return this.http.post<ResaProcessoDTO>(`${this.apiUrl}/api/processi-trasformazione/${processId}/resa`, request);
  }

  // === STATISTICHE ===
  
  getTrasformatoreStats(): Observable<TrasformatoreStatsDTO> {
    return this.http.get<TrasformatoreStatsDTO>(`${this.apiUrl}/api/trasformatore/stats`);
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

  formatDuration(minutes: number): string {
    if (minutes < 60) {
      return `${minutes} min`;
    } else if (minutes < 1440) {
      const hours = Math.floor(minutes / 60);
      const mins = minutes % 60;
      return mins > 0 ? `${hours}h ${mins}min` : `${hours}h`;
    } else {
      const days = Math.floor(minutes / 1440);
      const hours = Math.floor((minutes % 1440) / 60);
      return hours > 0 ? `${days}g ${hours}h` : `${days}g`;
    }
  }

  // Converte i filtri in HttpParams per le chiamate API
  private buildParamsFromFilters(filters?: ProcessoFilters): HttpParams {
    let params = new HttpParams();
    
    if (filters) {
      if (filters.search) {
        params = params.set('search', filters.search);
      }
      if (filters.stato) {
        params = params.set('stato', filters.stato);
      }
      if (filters.dataInizio) {
        params = params.set('dataInizio', filters.dataInizio);
      }
      if (filters.dataFine) {
        params = params.set('dataFine', filters.dataFine);
      }
      if (filters.prodottiInput && filters.prodottiInput.length > 0) {
        filters.prodottiInput.forEach(prod => {
          params = params.append('prodottiInput', prod);
        });
      }
      if (filters.prodottiOutput && filters.prodottiOutput.length > 0) {
        filters.prodottiOutput.forEach(prod => {
          params = params.append('prodottiOutput', prod);
        });
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