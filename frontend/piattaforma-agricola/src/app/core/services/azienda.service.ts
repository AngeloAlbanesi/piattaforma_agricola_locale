import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { AziendaDetailDTO, UpdateAziendaRequestDTO } from '../models/trasformatore.models';
import { CertificationDTO, CreateCertificazioneRequestDTO } from '../models/produttore.models';

/**
 * Servizio per la gestione dei dati aziendali.
 * Utilizzato da: Trasformatore, Produttore, Distributore.
 */
@Injectable({
    providedIn: 'root'
})
export class AziendaService {
    // Normalizziamo rimuovendo lo slash finale per evitare doppio slash
    private readonly apiUrl = this.buildApiUrl('').replace(/\/$/, '');

    constructor(private http: HttpClient) { }

    // === GESTIONE AZIENDA ===

    /**
     * Ottiene i dati dell'azienda dell'utente corrente
     */
    getMyCompany(): Observable<AziendaDetailDTO> {
        return this.http.get<AziendaDetailDTO>(`${this.apiUrl}/aziende/mia-azienda`);
    }

    /**
     * Crea i dati dell'azienda
     */
    createCompany(request: UpdateAziendaRequestDTO): Observable<AziendaDetailDTO> {
        return this.http.post<AziendaDetailDTO>(`${this.apiUrl}/azienda`, request);
    }

    /**
     * Aggiorna i dati dell'azienda
     */
    updateCompany(id: number, request: UpdateAziendaRequestDTO): Observable<AziendaDetailDTO> {
        return this.http.put<AziendaDetailDTO>(`${this.apiUrl}/azienda/${id}`, request);
    }

    // === CERTIFICAZIONI AZIENDA ===

    /**
     * Aggiunge una certificazione all'azienda
     */
    addCompanyCertification(aziendaId: number, request: CreateCertificazioneRequestDTO): Observable<CertificationDTO> {
        return this.http.post<CertificationDTO>(`${this.apiUrl}/azienda/${aziendaId}/certificazioni`, request);
    }

    /**
     * Rimuove una certificazione dall'azienda
     */
    removeCompanyCertification(aziendaId: number, certificationId: number): Observable<void> {
        return this.http.delete<void>(`${this.apiUrl}/azienda/${aziendaId}/certificazioni/${certificationId}`);
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

    // === FORMATTERS ===

    formatDate(date: string): string {
        return new Date(date).toLocaleDateString('it-IT', {
            year: 'numeric',
            month: 'long',
            day: 'numeric'
        });
    }

    getStatoAccreditamentoLabel(stato: string): string {
        const labels: Record<string, string> = {
            'PENDING': 'In Attesa',
            'ACCREDITATO': 'Accreditato',
            'RIFIUTATO': 'Rifiutato',
            'SOSPESO': 'Sospeso'
        };
        return labels[stato] || stato;
    }

    getStatoAccreditamentoColor(stato: string): 'primary' | 'accent' | 'warn' | undefined {
        const colors: Record<string, 'primary' | 'accent' | 'warn' | undefined> = {
            'PENDING': 'accent',
            'ACCREDITATO': 'primary',
            'RIFIUTATO': 'warn',
            'SOSPESO': 'warn'
        };
        return colors[stato];
    }

    getTipologiaAziendaLabel(tipo: string): string {
        const labels: Record<string, string> = {
            'TRASFORMAZIONE': 'Trasformazione',
            'PRODUZIONE': 'Produzione',
            'DISTRIBUZIONE': 'Distribuzione'
        };
        return labels[tipo] || tipo;
    }
}
