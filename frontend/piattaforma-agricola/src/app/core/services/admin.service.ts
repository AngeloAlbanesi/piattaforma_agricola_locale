import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { environment } from '../../../environments/environment';
import { PaginatedResponse } from '../models/common.models';
import {
    AdminUserDTO,
    UserPublicDTO,
    CompanyModerationDTO,
    ModerationDecisionDTO,
    AccreditamentoStato,
    UtenteTipo,
    TipoRuolo
} from '../models/admin.models';

@Injectable({ providedIn: 'root' })
export class AdminService {
    private readonly baseAdmin = this.buildApiUrl('/admin/gestore');
    private readonly baseAdminUsers = this.buildApiUrl('/admin/utenti');

    constructor(private http: HttpClient) { }

    // === UTENTI ===
    listUsers(search?: string, soloAttivi?: boolean, pagina = 0, size = 10): Observable<PaginatedResponse<UserPublicDTO>> {
        // L'API fornisce liste non paginate: adatto lato client a PaginatedResponse
        const hasSearch = !!search && search.trim().length > 0;
        if (hasSearch) {
            const params = new HttpParams().set('search', search!.trim());
            return this.http.get<UserPublicDTO[]>(`${this.baseAdmin}/utenti/search`, { params })
                .pipe(map(list => this.toPage(list, pagina, size)));
        }

        let params = new HttpParams();
        if (typeof soloAttivi === 'boolean') {
            params = params.set('soloAttivi', String(soloAttivi));
        }
        return this.http.get<UserPublicDTO[]>(`${this.baseAdmin}/utenti`, { params })
            .pipe(map(list => this.toPage(list, pagina, size)));
    }

    getUsersByType(tipoUtente: TipoRuolo | string, pagina = 0, size = 10): Observable<PaginatedResponse<UserPublicDTO>> {
        const params = new HttpParams().set('tipoUtente', tipoUtente.toString());
        return this.http.get<UserPublicDTO[]>(`${this.baseAdmin}/utenti/tipo`, { params })
            .pipe(map(list => this.toPage(list, pagina, size)));
    }

    banUser(id: number, motivazione: string): Observable<string> {
        const body: ModerationDecisionDTO = { motivazione };
        return this.http.put(`${this.baseAdminUsers}/${id}/ban`, body, { responseType: 'text' });
    }

    setUserActivation(id: number, tipo: UtenteTipo, attivo: boolean): Observable<string> {
        const params = new HttpParams()
            .set('tipo', tipo)
            .set('attivo', String(attivo));
        return this.http.put(`${this.baseAdmin}/utenti/${id}/attivazione`, null, { params, responseType: 'text' });
    }

    // === ACCREDITAMENTI VENDITORI ===
    getPendingVenditori(): Observable<UserPublicDTO[]> {
        return this.http.get<UserPublicDTO[]>(`${this.baseAdmin}/venditori/pending`);
    }

    updateAccreditamentoVenditore(id: number, stato: AccreditamentoStato | string): Observable<string> {
        const params = new HttpParams().set('stato', stato.toString());
        return this.http.put(`${this.baseAdmin}/venditori/${id}/accreditamento`, null, { params, responseType: 'text' });
    }

    getVendorCompanyData(id: number): Observable<CompanyModerationDTO> {
        return this.http.get<CompanyModerationDTO>(`${this.baseAdmin}/venditori/${id}/azienda`);
    }

    // === ACCREDITAMENTI CURATORI ===
    getPendingCuratori(): Observable<UserPublicDTO[]> {
        return this.http.get<UserPublicDTO[]>(`${this.baseAdmin}/curatori/pending`);
    }

    updateAccreditamentoCuratore(id: number, stato: AccreditamentoStato | string): Observable<string> {
        const params = new HttpParams().set('stato', stato.toString());
        return this.http.put(`${this.baseAdmin}/curatori/${id}/accreditamento`, null, { params, responseType: 'text' });
    }

    // === ACCREDITAMENTI ANIMATORI ===
    getPendingAnimatori(): Observable<UserPublicDTO[]> {
        return this.http.get<UserPublicDTO[]>(`${this.baseAdmin}/animatori/pending`);
    }

    updateAccreditamentoAnimatore(id: number, stato: AccreditamentoStato | string): Observable<string> {
        const params = new HttpParams().set('stato', stato.toString());
        return this.http.put(`${this.baseAdmin}/animatori/${id}/accreditamento`, null, { params, responseType: 'text' });
    }

    // === Helpers ===
    private toPage<T>(items: T[], page: number, size: number): PaginatedResponse<T> {
        const start = page * size;
        const end = start + size;
        const content = items.slice(start, end);
        const totalElements = items.length;
        const totalPages = Math.max(1, Math.ceil(totalElements / size));

        return {
            content,
            totalElements,
            totalPages,
            size,
            number: page,
            first: page === 0,
            last: page >= totalPages - 1,
            empty: content.length === 0
        };
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
