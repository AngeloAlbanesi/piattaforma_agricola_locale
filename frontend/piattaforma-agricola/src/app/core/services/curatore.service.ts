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
    ContenutoApprovazioneDTO,
    ProductSummaryDTO,
    CompanyModerationDTO,
    ModerationDecisionDTO,
    mapProductSummaryToPending,
    mapCompanyModerationToPending,
} from '../models/curatore.models';

@Injectable({
    providedIn: 'root'
})
export class CuratoreService {
    private readonly apiUrl = this.buildApiUrl('');

    constructor(private http: HttpClient) { }

    // === STATISTICHE ===

    getCuratoreStats(): Observable<CuratoreStatsDTO> {
        return this.http.get<CuratoreStatsDTO>(`${this.apiUrl}/curatore/stats`);
    }

    // === APPROVAZIONI IN CORSO ===

    getPendingApprovals(filters?: ApprovazioneFilters): Observable<ApprovazionePendingDTO[]> {
        // Questo metodo esistente viene ora implementato componendo le chiamate ai due endpoint documentati
        const tipo = filters?.tipo;
        const params = this.buildParamsFromFilters(filters);

        if (tipo === 'PRODOTTO') {
            return this.http.get<ProductSummaryDTO[]>(`/api/admin/prodotti/pending`, { params })
                .pipe(
                    // mappa array prodotti → approvazioni pending
                    // notare: la doc non specifica paginazione via query, ma manteniamo params per future estensioni
                    (source) => new Observable<ApprovazionePendingDTO[]>(subscriber => {
                        source.subscribe({
                            next: products => subscriber.next(products.map(mapProductSummaryToPending)),
                            error: err => subscriber.error(err),
                            complete: () => subscriber.complete()
                        });
                    })
                );
        }

        if (tipo === 'AZIENDA') {
            return this.http.get<CompanyModerationDTO[]>(`/api/admin/aziende/pending`, { params })
                .pipe(
                    (source) => new Observable<ApprovazionePendingDTO[]>(subscriber => {
                        source.subscribe({
                            next: companies => subscriber.next(companies.map(mapCompanyModerationToPending)),
                            error: err => subscriber.error(err),
                            complete: () => subscriber.complete()
                        });
                    })
                );
        }

        // Default: combina entrambe in parallelo (semplificato senza RxJS forkJoin per evitare import extra qui)
        return new Observable<ApprovazionePendingDTO[]>(subscriber => {
            let prod: ApprovazionePendingDTO[] = [];
            let az: ApprovazionePendingDTO[] = [];
            let done = 0;

            const completeIfDone = () => {
                done++;
                if (done === 2) {
                    subscriber.next([...prod, ...az]);
                    subscriber.complete();
                }
            };

            this.http.get<ProductSummaryDTO[]>(`/api/admin/prodotti/pending`, { params }).subscribe({
                next: products => { prod = products.map(mapProductSummaryToPending); },
                error: err => subscriber.error(err),
                complete: completeIfDone
            });

            this.http.get<CompanyModerationDTO[]>(`/api/admin/aziende/pending`, { params }).subscribe({
                next: companies => { az = companies.map(mapCompanyModerationToPending); },
                error: err => subscriber.error(err),
                complete: completeIfDone
            });
        });
    }

    approveElement(elementId: number, tipo: string, request: ApprovazioneRequestDTO | ModerationDecisionDTO): Observable<any> {
        if (tipo === 'PRODOTTO') {
            const body: ModerationDecisionDTO = toDecision(request, true);
            return this.http.put(`/api/admin/prodotti/${elementId}/approva`, body, { responseType: 'text' });
        }
        if (tipo === 'AZIENDA') {
            const body: ModerationDecisionDTO = toDecision(request, true);
            return this.http.put(`/api/admin/aziende/${elementId}/approva`, body, { responseType: 'text' });
        }
        // Altri tipi non documentati
        return new Observable<any>(subscriber => {
            subscriber.error(new Error('Tipo non supportato per approvazione'));
        });
    }

    rejectElement(elementId: number, tipo: string, request: ApprovazioneRequestDTO | ModerationDecisionDTO): Observable<any> {
        if (tipo === 'PRODOTTO') {
            const body: ModerationDecisionDTO = toDecision(request, false);
            return this.http.put(`/api/admin/prodotti/${elementId}/rifiuta`, body, { responseType: 'text' });
        }
        if (tipo === 'AZIENDA') {
            const body: ModerationDecisionDTO = toDecision(request, false);
            return this.http.put(`/api/admin/aziende/${elementId}/rifiuta`, body, { responseType: 'text' });
        }
        return new Observable<any>(subscriber => {
            subscriber.error(new Error('Tipo non supportato per rifiuto'));
        });
    }

    // === STATISTICHE COMPLETE ===

    getCuratorStatsFromBackend(): Observable<CuratoreStatsDTO> {
        return this.http.get<CuratoreStatsDTO>(`/api/admin/stats`);
    }

    // === DETTAGLI ELEMENTI DA APPROVARE ===

    getProductDetails(productId: number): Observable<ProdottoApprovazioneDTO> {
        return this.http.get<ProdottoApprovazioneDTO>(`${this.apiUrl}/curatore/prodotti/${productId}/dettagli`);
    }

    getCompanyDetails(companyId: number): Observable<AziendaApprovazioneDTO> {
        return this.http.get<AziendaApprovazioneDTO>(`${this.apiUrl}/curatore/aziende/${companyId}/dettagli`);
    }

    getContentDetails(contentId: number): Observable<ContenutoApprovazioneDTO> {
        return this.http.get<ContenutoApprovazioneDTO>(`${this.apiUrl}/curatore/contenuti/${contentId}/dettagli`);
    }

    // === DETTAGLI PER APPROVAZIONE (endpoint pubblici) ===

    getProductDetailsForApproval(productId: number): Observable<any> {
        return this.http.get<any>(`/api/prodotti/${productId}`);
    }

    getCompanyDetailsForApproval(companyId: number): Observable<CompanyModerationDTO> {
        // Utilizziamo l'endpoint che ritorna tutte le aziende pending e filtriamo
        return new Observable<CompanyModerationDTO>(subscriber => {
            this.http.get<CompanyModerationDTO[]>(`/api/admin/aziende/pending`).subscribe({
                next: (companies) => {
                    const company = companies.find(c => c.id === companyId);
                    if (company) {
                        subscriber.next(company);
                        subscriber.complete();
                    } else {
                        subscriber.error(new Error('Azienda non trovata'));
                    }
                },
                error: (err) => subscriber.error(err)
            });
        });
    }

    // === STORICO APPROVAZIONI ===

    getApprovalHistory(filters?: ApprovazioneFilters): Observable<ApprovazionePendingDTO[]> {
        let params = this.buildParamsFromFilters(filters);

        return this.http.get<ApprovazionePendingDTO[]>(`${this.apiUrl}/curatore/approvazioni/storico`, { params });
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
        // Fix: se path è vuoto, non aggiungere slash
        const sanitizedPath = path ? (path.startsWith('/') ? path : `/${path}`) : '';

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

// Helper per tradurre i request model interni in ModerationDecisionDTO documentato
function toDecision(request: ApprovazioneRequestDTO | ModerationDecisionDTO, approved: boolean): ModerationDecisionDTO {
    if ('motivazione' in request) return request as ModerationDecisionDTO;
    const r = request as ApprovazioneRequestDTO;
    return { motivazione: approved ? (r.note || 'Approvato') : (r.motivoReiezione || 'Rifiutato') };
}