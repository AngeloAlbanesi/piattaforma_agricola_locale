import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ProcessoTrasformazionePublicDTO } from '../models/public.models';

/**
 * Servizio per gestire le chiamate API pubbliche ai processi di trasformazione
 */
@Injectable({
    providedIn: 'root'
})
export class PublicProcessiService {
    private readonly apiUrl = '/api/processi-trasformazione';

    constructor(private http: HttpClient) { }

    /**
     * Ottiene i dettagli pubblici di un processo di trasformazione
     * @param id ID del processo
     * @returns Observable con i dettagli del processo
     */
    getProcessoById(id: number): Observable<ProcessoTrasformazionePublicDTO> {
        return this.http.get<ProcessoTrasformazionePublicDTO>(`${this.apiUrl}/pubblico/${id}`);
    }
}
