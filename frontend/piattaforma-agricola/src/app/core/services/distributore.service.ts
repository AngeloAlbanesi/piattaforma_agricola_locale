import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import {
  PacchettoTipicitaDTO,
  DettaglioPacchettoDTO,
  DistributoreStatsDTO,
  CreatePacchettoRequestDTO,
  UpdatePacchettoRequestDTO
} from '../models/distributore.models';

@Injectable({
  providedIn: 'root'
})
export class DistributoreService {
  private readonly apiUrl = this.buildApiUrl('');

  constructor(private http: HttpClient) {}

  // === PACCHETTI ===
  
  getMyPackages(): Observable<PacchettoTipicitaDTO[]> {
    return this.http.get<PacchettoTipicitaDTO[]>(`${this.apiUrl}/api/pacchetti/miei-pacchetti`);
  }

  getPackageById(id: number): Observable<DettaglioPacchettoDTO> {
    return this.http.get<DettaglioPacchettoDTO>(`${this.apiUrl}/api/pacchetti/${id}`);
  }

  createPackage(request: CreatePacchettoRequestDTO): Observable<PacchettoTipicitaDTO> {
    return this.http.post<PacchettoTipicitaDTO>(`${this.apiUrl}/api/pacchetti`, request);
  }

  updatePackage(id: number, request: UpdatePacchettoRequestDTO): Observable<PacchettoTipicitaDTO> {
    return this.http.put<PacchettoTipicitaDTO>(`${this.apiUrl}/api/pacchetti/${id}`, request);
  }

  deletePackage(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/api/pacchetti/${id}`);
  }
  
  // === GESTIONE PRODOTTI NEI PACCHETTI ===
  
  addProductToPackage(packageId: number, productId: number, quantita: number): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/api/pacchetti/${packageId}/prodotti`, {
      idProdotto: productId,
      quantita: quantita
    });
  }

  removeProductFromPackage(packageId: number, productId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/api/pacchetti/${packageId}/prodotti/${productId}`);
  }

  updateProductQuantity(packageId: number, productId: number, quantita: number): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/api/pacchetti/${packageId}/prodotti/${productId}`, {
      quantita: quantita
    });
  }
  
  // === STATISTICHE ===
  
  getDistributoreStats(): Observable<DistributoreStatsDTO> {
    return this.http.get<DistributoreStatsDTO>(`${this.apiUrl}/api/distributore/stats`);
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
    const sanitizedPath = path.startsWith('/') ? path : `/${path}`;

    if (base) {
      return `${base}${prefix}${sanitizedPath}`;
    }

    return `${prefix || ''}${sanitizedPath}` || sanitizedPath;
  }
}