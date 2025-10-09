import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { environment } from '../../../environments/environment';
import {
    PacchettoTipicitaDTO,
    DettaglioPacchettoDTO,
    DistributoreStatsDTO,
    CreatePacchettoRequestDTO,
    UpdatePacchettoRequestDTO,
    DistributoreProductDTO,
    CreateDistributoreProductRequestDTO,
    UpdateDistributoreProductRequestDTO
} from '../models/distributore.models';
import { PaginatedResponse } from '../models/common.models';

@Injectable({
    providedIn: 'root'
})
export class DistributoreService {
    private readonly apiUrl = this.buildApiUrl('');

    constructor(private http: HttpClient) { }

    // === PACCHETTI ===

    getMyPackages(): Observable<PacchettoTipicitaDTO[]> {
        return this.http.get<any[]>(`${this.apiUrl}/pacchetti/miei-pacchetti`).pipe(
            map(pacchetti => pacchetti.map(p => this.normalizePacchetto(p)))
        );
    }

    getPackageById(id: number): Observable<DettaglioPacchettoDTO> {
        return this.http.get<any>(`${this.apiUrl}/pacchetti/${id}`).pipe(
            map(p => this.normalizePacchettoDetails(p))
        );
    }

    createPackage(request: CreatePacchettoRequestDTO): Observable<PacchettoTipicitaDTO> {
        return this.http.post<any>(`${this.apiUrl}/pacchetti`, request).pipe(
            map(p => this.normalizePacchetto(p))
        );
    }

    updatePackage(id: number, request: UpdatePacchettoRequestDTO): Observable<PacchettoTipicitaDTO> {
        return this.http.put<any>(`${this.apiUrl}/pacchetti/${id}`, request).pipe(
            map(p => this.normalizePacchetto(p))
        );
    }

    deletePackage(id: number): Observable<void> {
        return this.http.delete<void>(`${this.apiUrl}/pacchetti/${id}`);
    }

    // === GESTIONE PRODOTTI ===

    getMyProducts(): Observable<DistributoreProductDTO[]> {
        return this.http.get<PaginatedResponse<DistributoreProductDTO>>(`${this.apiUrl}/prodotti/miei-prodotti`).pipe(
            map(response => {
                // Estrai il contenuto dalla risposta paginata
                if (response && response.content) {
                    // Normalizza ogni prodotto
                    response.content.forEach(product => this.normalizeProduct(product));
                    return response.content;
                }
                return [];
            })
        );
    }

    getProductById(id: number): Observable<DistributoreProductDTO> {
        return this.http.get<DistributoreProductDTO>(`${this.apiUrl}/prodotti/${id}`).pipe(
            map(product => {
                this.normalizeProduct(product);
                return product;
            })
        );
    }

    createProduct(request: CreateDistributoreProductRequestDTO): Observable<DistributoreProductDTO> {
        return this.http.post<DistributoreProductDTO>(`${this.apiUrl}/prodotti`, request).pipe(
            map(product => {
                this.normalizeProduct(product);
                return product;
            })
        );
    }

    updateProduct(id: number, request: UpdateDistributoreProductRequestDTO): Observable<DistributoreProductDTO> {
        return this.http.put<DistributoreProductDTO>(`${this.apiUrl}/prodotti/${id}`, request).pipe(
            map(product => {
                this.normalizeProduct(product);
                return product;
            })
        );
    }

    deleteProduct(id: number): Observable<void> {
        return this.http.delete<void>(`${this.apiUrl}/prodotti/${id}`);
    }

    // === GESTIONE PRODOTTI NEI PACCHETTI ===

    addProductToPackage(packageId: number, productId: number, quantita: number): Observable<void> {
        return this.http.post<void>(`${this.apiUrl}/pacchetti/${packageId}/prodotti`, {
            idProdotto: productId,
            quantita: quantita
        });
    }

    removeProductFromPackage(packageId: number, productId: number): Observable<void> {
        return this.http.delete<void>(`${this.apiUrl}/pacchetti/${packageId}/prodotti/${productId}`);
    }

    updateProductQuantity(packageId: number, productId: number, quantita: number): Observable<void> {
        return this.http.put<void>(`${this.apiUrl}/pacchetti/${packageId}/prodotti/${productId}`, {
            quantita: quantita
        });
    }

    // === GESTIONE CERTIFICAZIONI PRODOTTI ===

    addCertificationToProduct(productId: number, certification: any): Observable<void> {
        return this.http.post<void>(`${this.apiUrl}/prodotti/${productId}/certificazioni`, certification);
    }

    removeCertificationFromProduct(productId: number, certificationId: number): Observable<void> {
        return this.http.delete<void>(`${this.apiUrl}/prodotti/${productId}/certificazioni/${certificationId}`);
    }

    // === STATISTICHE ===

    getDistributoreStats(): Observable<DistributoreStatsDTO> {
        return this.http.get<DistributoreStatsDTO>(`${this.apiUrl}/distributore/stats`);
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
        // Fix: se path è vuoto, non aggiungere slash
        const sanitizedPath = path ? (path.startsWith('/') ? path : `/${path}`) : '';

        if (base) {
            return `${base}${prefix}${sanitizedPath}`;
        }

        return `${prefix || ''}${sanitizedPath}` || sanitizedPath;
    }

    /**
     * Normalizza i dati del pacchetto mappando i campi del backend a quelli del frontend
     */
    private normalizePacchetto(pacchetto: any): PacchettoTipicitaDTO {
        console.log('🔧 [DistributoreService] Normalizzazione pacchetto prima:', pacchetto);

        const normalized: any = {
            id: pacchetto.idPacchetto || pacchetto.id,
            nome: pacchetto.nome,
            descrizione: pacchetto.descrizione,
            prezzo: pacchetto.prezzoPacchetto || pacchetto.prezzo,
            quantitaDisponibile: pacchetto.quantitaDisponibile,
            immagineUrl: pacchetto.immagineUrl,
            stato: pacchetto.stato || 'IN_PROGETTAZIONE',
            dataCreazione: pacchetto.dataCreazione || new Date().toISOString(),
            dataUltimaModifica: pacchetto.dataUltimaModifica || new Date().toISOString(),
            prodotti: this.normalizeElementiToProdotti(pacchetto.elementiInclusi || []),
            distributore: pacchetto.distributore ? {
                id: pacchetto.distributore.idUtente || pacchetto.distributore.id,
                nomeAzienda: pacchetto.distributore.nomeAzienda ||
                    `${pacchetto.distributore.nome || ''} ${pacchetto.distributore.cognome || ''}`.trim(),
                partitaIva: pacchetto.distributore.partitaIva || 'N/A'
            } : undefined
        };

        // Preserva numeroElementi se presente (da PacchettoSummaryDTO)
        if (pacchetto.numeroElementi !== undefined) {
            normalized.numeroElementi = pacchetto.numeroElementi;
        }

        console.log('✅ [DistributoreService] Normalizzazione pacchetto dopo:', normalized);
        return normalized as PacchettoTipicitaDTO;
    }

    /**
     * Normalizza gli elementi inclusi nel pacchetto mappandoli ai prodotti
     */
    private normalizeElementiToProdotti(elementi: any[]): any[] {
        if (!Array.isArray(elementi)) return [];

        return elementi
            .filter(el => el.tipoElemento === 'PRODOTTO')
            .map(el => ({
                id: el.idElemento,
                nome: el.nomeElemento,
                descrizione: el.descrizioneElemento,
                prezzo: el.prezzoElemento,
                quantita: el.quantita || 1,
                produttore: {
                    nomeAzienda: 'N/A'
                }
            }));
    }

    /**
     * Normalizza i dati dettagliati del pacchetto per la visualizzazione
     */
    private normalizePacchettoDetails(pacchetto: any): DettaglioPacchettoDTO {
        return {
            ...this.normalizePacchetto(pacchetto),
            certificazioni: pacchetto.certificazioni || [],
            recensioni: pacchetto.recensioni || [],
            statisticheVendite: pacchetto.statisticheVendite || {
                venditeTotali: 0,
                mediaValutazione: 0,
                numeroRecensioni: 0
            }
        } as DettaglioPacchettoDTO;
    }

    /**
     * Normalizza i dati del prodotto assicurandosi che certificazioni sia sempre un array
     * e che i campi abbiano i nomi corretti
     * Questo risolve il problema NG02200 quando il backend restituisce certificazioni come oggetto
     * e il problema di mapping tra idProdotto->id e statoVerifica->stato
     */
    private normalizeProduct(product: DistributoreProductDTO | any): void {
        if (!product) return;

        console.log('🔧 [DistributoreService] Normalizzazione prodotto prima:', product);

        // Mappa idProdotto -> id
        if (product.idProdotto !== undefined && product.id === undefined) {
            product.id = product.idProdotto;
            console.log('🔄 [DistributoreService] Mappato idProdotto -> id:', product.id);
        }

        // Mappa statoVerifica -> stato
        if (product.statoVerifica !== undefined && product.stato === undefined) {
            product.stato = product.statoVerifica;
            console.log('🔄 [DistributoreService] Mappato statoVerifica -> stato:', product.stato);
        }

        // Normalizza certificazioni
        if (product.certificazioni) {
            if (!Array.isArray(product.certificazioni)) {
                console.warn('⚠️ [DistributoreService] certificazioni non è un array, convertendolo:', product.certificazioni);
                // Se certificazioni è un oggetto, prova a convertirlo in array
                if (typeof product.certificazioni === 'object') {
                    // Se è un oggetto con chiavi, converti in array
                    product.certificazioni = Object.values(product.certificazioni);
                } else {
                    // Altrimenti usa un array vuoto
                    product.certificazioni = [];
                }
            }
        }

        console.log('✅ [DistributoreService] Normalizzazione prodotto dopo:', product);
    }
}
