import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { PaginatedResponse } from '../models/common.models';
import {
    EventoDTO,
    EventoDetailDTO,
    AnimatoreStatsDTO,
    CreaEventoRequestDTO,
    CreateEventoRequestDTO,
    AggiornaEventoRequestDTO,
    EventoFilters,
    PartecipanteEventoDTO,
    EventoPartecipanteDTO,
    FeedbackEventoDTO,
    PromoteRequestDTO,
    ShareResponseDTO,
    AziendaPartecipanteDTO
} from '../models/animatore.models';

@Injectable({
    providedIn: 'root'
})
export class AnimatoreService {
    private readonly apiUrl = this.buildApiUrl('');

    constructor(private http: HttpClient) { }

    // === STATISTICHE ===

    getAnimatoreStats(): Observable<AnimatoreStatsDTO> {
        return this.http.get<AnimatoreStatsDTO>(`${this.apiUrl}/animatore/stats`);
    }

    // === GESTIONE EVENTI ===

    getMyEvents(filters?: EventoFilters): Observable<PaginatedResponse<EventoDTO>> {
        let params = this.buildParamsFromFilters(filters);

        return this.http.get<PaginatedResponse<EventoDTO>>(`${this.apiUrl}/animatore/eventi`, { params });
    }

    getEventById(eventId: number): Observable<EventoDTO> {
        return this.http.get<EventoDTO>(`${this.apiUrl}/animatore/eventi/${eventId}`);
    }

    /**
     * Crea un nuovo evento secondo API_ANIMATORE.md
     * Endpoint: POST /api/eventi/creaEvento
     */
    createEvento(evento: CreateEventoRequestDTO): Observable<EventoDetailDTO> {
        return this.http.post<EventoDetailDTO>(`${this.apiUrl}/eventi/creaEvento`, evento);
    }

    /**
     * Aggiorna un evento esistente secondo API_ANIMATORE.md
     * Endpoint: PUT /api/eventi/{id}
     */
    updateEvento(eventId: number, evento: AggiornaEventoRequestDTO): Observable<EventoDetailDTO> {
        return this.http.put<EventoDetailDTO>(`${this.apiUrl}/eventi/${eventId}`, evento);
    }

    /**
     * Elimina un evento secondo API_ANIMATORE.md
     * Endpoint: DELETE /api/eventi/{id}
     */
    deleteEvento(eventId: number): Observable<void> {
        return this.http.delete<void>(`${this.apiUrl}/eventi/${eventId}`);
    }

    /**
     * Promuove un evento su canali social secondo API_ANIMATORE.md
     * Endpoint: POST /api/eventi/{id}/promote
     */
    promoteEvento(eventId: number, request: PromoteRequestDTO): Observable<ShareResponseDTO> {
        return this.http.post<ShareResponseDTO>(`${this.apiUrl}/eventi/${eventId}/promote`, request);
    }

    // Manteniamo per compatibilità con codice esistente
    publishEvento(eventId: number): Observable<void> {
        return this.http.post<void>(`${this.apiUrl}/animatore/eventi/${eventId}/pubblica`, {});
    }

    // === GESTIONE PARTECIPANTI ===

    /**
     * Visualizza partecipanti utenti secondo API_ANIMATORE.md
     * Endpoint: GET /api/eventi/{id}/partecipanti
     */
    getEventParticipants(eventId: number): Observable<EventoPartecipanteDTO[]> {
        return this.http.get<EventoPartecipanteDTO[]>(`${this.apiUrl}/eventi/${eventId}/partecipanti`);
    }

    confirmParticipant(eventId: number, participantId: number): Observable<void> {
        return this.http.post<void>(`${this.apiUrl}/animatore/eventi/${eventId}/partecipanti/${participantId}/conferma`, {});
    }

    // === GESTIONE STATO EVENTI (secondo API_ANIMATORE.md) ===

    /**
     * Avvia un evento (IN_PROGRAMMA → IN_CORSO)
     * Endpoint: PATCH /api/eventi/{id}/inizia
     */
    iniziaEvento(eventId: number): Observable<EventoDetailDTO> {
        return this.http.patch<EventoDetailDTO>(`${this.apiUrl}/eventi/${eventId}/inizia`, {});
    }

    /**
     * Termina un evento (IN_CORSO → CONCLUSO)
     * Endpoint: PATCH /api/eventi/{id}/termina
     */
    terminaEvento(eventId: number): Observable<EventoDetailDTO> {
        return this.http.patch<EventoDetailDTO>(`${this.apiUrl}/eventi/${eventId}/termina`, {});
    }

    /**
     * Annulla un evento
     * Endpoint: PATCH /api/eventi/{id}/annulla
     */
    annullaEvento(eventId: number): Observable<EventoDetailDTO> {
        return this.http.patch<EventoDetailDTO>(`${this.apiUrl}/eventi/${eventId}/annulla`, {});
    }

    // === GESTIONE AZIENDE PARTECIPANTI (secondo API_ANIMATORE.md) ===

    /**
     * Aggiungi azienda partecipante a un evento
     * Endpoint: POST /api/eventi/{id}/partecipanti-azienda/{venditorId}
     */
    addAziendaPartecipante(eventId: number, venditorId: number): Observable<void> {
        return this.http.post<void>(`${this.apiUrl}/eventi/${eventId}/partecipanti-azienda/${venditorId}`, {});
    }

    /**
     * Rimuovi azienda partecipante da un evento
     * Endpoint: DELETE /api/eventi/{id}/partecipanti-azienda/{venditorId}
     */
    removeAziendaPartecipante(eventId: number, venditorId: number): Observable<void> {
        return this.http.delete<void>(`${this.apiUrl}/eventi/${eventId}/partecipanti-azienda/${venditorId}`);
    }

    /**
     * Visualizza aziende partecipanti a un evento
     * Endpoint: GET /api/eventi/{id}/partecipanti-azienda
     */
    getAziendePartecipanti(eventId: number): Observable<AziendaPartecipanteDTO[]> {
        return this.http.get<AziendaPartecipanteDTO[]>(`${this.apiUrl}/eventi/${eventId}/partecipanti-azienda`);
    }

    // === FEEDBACK ===

    getEventFeedback(eventId: number): Observable<FeedbackEventoDTO[]> {
        return this.http.get<FeedbackEventoDTO[]>(`${this.apiUrl}/animatore/eventi/${eventId}/feedback`);
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

            if (filters.pagina !== undefined) {
                params = params.set('pagina', filters.pagina.toString());
            }

            if (filters.elementiPerPagina) {
                params = params.set('elementiPerPagina', filters.elementiPerPagina.toString());
            }
        }

        return params;
    }

    // === UTILITY METHODS ===

    /**
     * Determina se un evento può essere avviato
     */
    canStartEvent(evento: EventoDTO): boolean {
        return evento.stato === 'IN_PROGRAMMA';
    }

    /**
     * Determina se un evento può essere terminato
     */
    canEndEvent(evento: EventoDTO): boolean {
        return evento.stato === 'IN_CORSO';
    }

    /**
     * Determina se un evento può essere annullato
     */
    canCancelEvent(evento: EventoDTO): boolean {
        return evento.stato !== 'CONCLUSO' && evento.stato !== 'ANNULLATO';
    }

    /**
     * Ottiene il nome visualizzabile dell'evento
     */
    getEventoNome(evento: EventoDTO): string {
        return evento.nomeEvento || evento.titolo || 'Evento senza nome';
    }

    /**
     * Ottiene la data di inizio formattata
     */
    getDataInizio(evento: EventoDTO): string {
        return evento.dataOraInizio || evento.dataInizio || '';
    }

    /**
     * Ottiene la data di fine formattata
     */
    getDataFine(evento: EventoDTO): string {
        return evento.dataOraFine || evento.dataFine || '';
    }

    /**
     * Ottiene il luogo dell'evento
     */
    getLuogo(evento: EventoDTO): string {
        return evento.luogoEvento || evento.luogo || '';
    }
}