import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, catchError, throwError } from 'rxjs';
import { environment } from '../../../environments/environment';
import {
  ProductSummaryDTO,
  ProductDetailDTO,
  CarrelloDTO,
  RigaCarrelloDTO,
  AddToCartRequestDTO,
  UpdateCartItemRequestDTO,
  OrdineSummaryDTO,
  OrdineExtendedSummaryDTO,
  OrdineDetailDTO,
  CreateOrdineRequestDTO,
  OrderStatusDTO,
  CancelOrderRequestDTO,
  PagamentoRequestDTO,
  EventoSummaryDTO,
  EventoDetailDTO,
  EventoRegistrazioneRequestDTO,
  PacchettoSummaryDTO,
  PacchettoDetailDTO,
  AcquirenteStatsDTO,
  UserDetailDTO,
  UserUpdateDTO,
  ShareRequestDTO,
  ShareResponseDTO,
  ProductFilters,
  PaginationParams,
  PaginatedResponse
} from '../models/acquirente.models';

@Injectable({
  providedIn: 'root'
})
export class AcquirenteService {
  private readonly apiUrl = this.buildApiUrl('');

  constructor(private http: HttpClient) {}

  // === GESTIONE PROFILO ===
  
  /**
   * Ottiene i dettagli del profilo utente autenticato
   * @returns Observable<UserDetailDTO> - Dettagli del profilo utente
   */
  getProfile(): Observable<UserDetailDTO> {
    return this.http.get<UserDetailDTO>(`${this.apiUrl}/api/auth/profile`).pipe(
      catchError(this.handleError)
    );
  }

  /**
   * Aggiorna i dati del profilo utente
   * @param profileData - Dati da aggiornare del profilo
   * @returns Observable<UserDetailDTO> - Profilo aggiornato
   */
  updateProfile(profileData: UserUpdateDTO): Observable<UserDetailDTO> {
    return this.http.put<UserDetailDTO>(`${this.apiUrl}/api/auth/profile`, profileData).pipe(
      catchError(this.handleError)
    );
  }

  // === PRODOTTI ===
  
  getProducts(filters?: ProductFilters, pagination?: PaginationParams): Observable<PaginatedResponse<ProductSummaryDTO>> {
    let params = new HttpParams();
    
    if (pagination) {
      params = params.set('page', pagination.page.toString());
      params = params.set('size', pagination.size.toString());
      if (pagination.sortBy) {
        params = params.set('sortBy', pagination.sortBy);
      }
      if (pagination.sortDirection) {
        params = params.set('sortDirection', pagination.sortDirection);
      }
    }
    
    if (filters?.search) {
      params = params.set('search', filters.search);
    }
    
    if (filters?.venditoreId) {
      params = params.set('vendorId', filters.venditoreId.toString());
    }
    
    if (filters?.categoria) {
      params = params.set('categoria', filters.categoria);
    }
    
    return this.http.get<PaginatedResponse<ProductSummaryDTO>>(`${this.apiUrl}/api/prodotti`, { params });
  }

  getProductById(id: number): Observable<ProductDetailDTO> {
    return this.http.get<ProductDetailDTO>(`${this.apiUrl}/api/prodotti/${id}`).pipe(
      catchError(this.handleError)
    );
  }

  searchProducts(query: string): Observable<ProductSummaryDTO[]> {
    return this.http.get<ProductSummaryDTO[]>(`${this.apiUrl}/api/prodotti/cercaProdotti`, {
      params: { query }
    }).pipe(
      catchError(this.handleError)
    );
  }

  getProductsByVendor(vendorId: number): Observable<ProductSummaryDTO[]> {
    return this.http.get<ProductSummaryDTO[]>(`${this.apiUrl}/api/prodotti/venditori/${vendorId}`).pipe(
      catchError(this.handleError)
    );
  }

  // === CARRELLO ===
  
  /**
   * Ottiene il contenuto del carrello dell'acquirente
   * @returns Observable<CarrelloDTO> - Contenuto del carrello
   */
  getCart(): Observable<CarrelloDTO> {
    return this.http.get<CarrelloDTO>(`${this.apiUrl}/api/carrello`).pipe(
      catchError(this.handleError)
    );
  }

  /**
   * Aggiunge un prodotto al carrello
   * @param prodottoId - ID del prodotto da aggiungere
   * @param request - Dati della richiesta (quantità)
   * @returns Observable<CarrelloDTO> - Carrello aggiornato
   */
  addProductToCart(prodottoId: number, request: AddToCartRequestDTO): Observable<CarrelloDTO> {
    return this.http.post<CarrelloDTO>(`${this.apiUrl}/api/carrello/prodotti/${prodottoId}`, request).pipe(
      catchError(this.handleError)
    );
  }

  /**
   * Aggiunge un pacchetto al carrello
   * @param pacchettoId - ID del pacchetto da aggiungere
   * @param request - Dati della richiesta (quantità)
   * @returns Observable<CarrelloDTO> - Carrello aggiornato
   */
  addPackageToCart(pacchettoId: number, request: AddToCartRequestDTO): Observable<CarrelloDTO> {
    return this.http.post<CarrelloDTO>(`${this.apiUrl}/api/carrello/pacchetti/${pacchettoId}`, request).pipe(
      catchError(this.handleError)
    );
  }

  /**
   * Aggiorna la quantità di un articolo nel carrello
   * @param rigaId - ID della riga del carrello
   * @param request - Dati della richiesta (nuova quantità)
   * @returns Observable<CarrelloDTO> - Carrello aggiornato
   */
  updateCartItemQuantity(rigaId: number, request: UpdateCartItemRequestDTO): Observable<CarrelloDTO> {
    return this.http.put<CarrelloDTO>(`${this.apiUrl}/api/carrello/righe/${rigaId}`, request).pipe(
      catchError(this.handleError)
    );
  }

  /**
   * Rimuove un articolo dal carrello
   * @param rigaId - ID della riga del carrello
   * @returns Observable<CarrelloDTO> - Carrello aggiornato
   */
  removeCartItem(rigaId: number): Observable<CarrelloDTO> {
    return this.http.delete<CarrelloDTO>(`${this.apiUrl}/api/carrello/righe/${rigaId}`).pipe(
      catchError(this.handleError)
    );
  }

  /**
   * Svuota completamente il carrello
   * @returns Observable<void> - Operazione completata
   */
  clearCart(): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/api/carrello`).pipe(
      catchError(this.handleError)
    );
  }

  getCartSummary(): Observable<{
    totalElementi: number;
    totale: number;
    ultimaModifica: string;
  }> {
    return this.http.get<{
      totalElementi: number;
      totale: number;
      ultimaModifica: string;
    }>(`${this.apiUrl}/api/carrello/sommario`).pipe(
      catchError(this.handleError)
    );
  }

  // === ORDINI ===
  
  /**
   * Ottiene l'elenco paginato di tutti gli ordini dell'acquirente
   * @param pagination - Parametri di paginazione
   * @returns Observable<PaginatedResponse<OrdineExtendedSummaryDTO>> - Elenco ordini
   */
  getOrders(pagination?: PaginationParams): Observable<PaginatedResponse<OrdineExtendedSummaryDTO>> {
    let params = new HttpParams();
    
    if (pagination) {
      params = params.set('page', pagination.page.toString());
      params = params.set('size', pagination.size.toString());
      if (pagination.sortBy) {
        params = params.set('sortBy', pagination.sortBy);
      }
      if (pagination.sortDirection) {
        params = params.set('sortDirection', pagination.sortDirection);
      }
    }
    
    return this.http.get<PaginatedResponse<OrdineExtendedSummaryDTO>>(`${this.apiUrl}/api/ordini`, { params }).pipe(
      catchError(this.handleError)
    );
  }

  /**
   * Ottiene i dettagli di un ordine specifico
   * @param id - ID dell'ordine
   * @returns Observable<OrdineDetailDTO> - Dettagli ordine
   */
  getOrderById(id: number): Observable<OrdineDetailDTO> {
    return this.http.get<OrdineDetailDTO>(`${this.apiUrl}/api/ordini/${id}`).pipe(
      catchError(this.handleError)
    );
  }

  /**
   * Crea uno o più ordini dal contenuto del carrello (uno per ogni venditore)
   * @param request - Dati per la creazione dell'ordine
   * @returns Observable<OrdineDetailDTO[]> - Lista ordini creati
   */
  createOrderFromCart(request: CreateOrdineRequestDTO): Observable<OrdineDetailDTO[]> {
    return this.http.post<OrdineDetailDTO[]>(`${this.apiUrl}/api/ordini`, request).pipe(
      catchError(this.handleError)
    );
  }

  /**
   * Conferma il pagamento per un ordine in stato ATTESA_PAGAMENTO
   * @param id - ID dell'ordine
   * @param request - Dati del pagamento
   * @returns Observable<OrdineDetailDTO> - Ordine aggiornato
   */
  confirmOrderPayment(id: number, request: PagamentoRequestDTO): Observable<OrdineDetailDTO> {
    return this.http.put<OrdineDetailDTO>(`${this.apiUrl}/api/ordini/${id}/pagamento`, request).pipe(
      catchError(this.handleError)
    );
  }

  /**
   * Ottiene lo stato corrente e lo storico di un ordine
   * @param id - ID dell'ordine
   * @returns Observable<OrderStatusDTO> - Stato ordine
   */
  getOrderStatus(id: number): Observable<OrderStatusDTO> {
    return this.http.get<OrderStatusDTO>(`${this.apiUrl}/api/ordini/${id}/stato`).pipe(
      catchError(this.handleError)
    );
  }

  /**
   * Annulla un ordine (solo se non ancora spedito)
   * @param id - ID dell'ordine
   * @param request - Motivo dell'annullamento
   * @returns Observable<OrdineDetailDTO> - Ordine aggiornato
   */
  cancelOrderWithReason(id: number, request: CancelOrderRequestDTO): Observable<OrdineDetailDTO> {
    return this.http.put<OrdineDetailDTO>(`${this.apiUrl}/api/ordini/${id}/annulla`, request).pipe(
      catchError(this.handleError)
    );
  }

  // === EVENTI ===
  
  getEvents(pagination?: PaginationParams): Observable<PaginatedResponse<EventoSummaryDTO>> {
    let params = new HttpParams();
    
    if (pagination) {
      params = params.set('page', pagination.page.toString());
      params = params.set('size', pagination.size.toString());
      if (pagination.sortBy) {
        params = params.set('sortBy', pagination.sortBy);
      }
      if (pagination.sortDirection) {
        params = params.set('sortDirection', pagination.sortDirection);
      }
    }
    
    return this.http.get<PaginatedResponse<EventoSummaryDTO>>(`${this.apiUrl}/api/eventi`, { params }).pipe(
      catchError(this.handleError)
    );
  }

  getEventById(id: number): Observable<EventoDetailDTO> {
    return this.http.get<EventoDetailDTO>(`${this.apiUrl}/api/eventi/${id}`).pipe(
      catchError(this.handleError)
    );
  }

  searchEvents(query: string): Observable<EventoSummaryDTO[]> {
    return this.http.get<EventoSummaryDTO[]>(`${this.apiUrl}/api/eventi/cercaEventi`, {
      params: { query }
    }).pipe(
      catchError(this.handleError)
    );
  }

  registerForEvent(eventId: number, request: EventoRegistrazioneRequestDTO): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/api/eventi/${eventId}/registra`, request).pipe(
      catchError(this.handleError)
    );
  }

  cancelEventRegistration(eventId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/api/eventi/${eventId}/registra`).pipe(
      catchError(this.handleError)
    );
  }

  // === PACCHETTI ===
  
  getPackages(pagination?: PaginationParams): Observable<PaginatedResponse<PacchettoSummaryDTO>> {
    let params = new HttpParams();
    
    if (pagination) {
      params = params.set('page', pagination.page.toString());
      params = params.set('size', pagination.size.toString());
      if (pagination.sortBy) {
        params = params.set('sortBy', pagination.sortBy);
      }
      if (pagination.sortDirection) {
        params = params.set('sortDirection', pagination.sortDirection);
      }
    }
    
    return this.http.get<PaginatedResponse<PacchettoSummaryDTO>>(`${this.apiUrl}/api/pacchetti`, { params }).pipe(
      catchError(this.handleError)
    );
  }

  getPackageById(id: number): Observable<PacchettoDetailDTO> {
    return this.http.get<PacchettoDetailDTO>(`${this.apiUrl}/api/pacchetti/${id}`).pipe(
      catchError(this.handleError)
    );
  }

  searchPackages(query: string): Observable<PacchettoSummaryDTO[]> {
    return this.http.get<PacchettoSummaryDTO[]>(`${this.apiUrl}/api/pacchetti/cercaPacchetti`, {
      params: { query }
    }).pipe(
      catchError(this.handleError)
    );
  }

  getPackageComposition(id: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/api/pacchetti/${id}/composizione`).pipe(
      catchError(this.handleError)
    );
  }

  // === STATISTICHE ACQUIRENTE ===
  
  getAcquirenteStats(): Observable<AcquirenteStatsDTO> {
    return this.http.get<AcquirenteStatsDTO>(`${this.apiUrl}/api/acquirente/stats`).pipe(
      catchError(this.handleError)
    );
  }

  // === CONDIVISIONE SOCIAL ===
  
  /**
   * Condivide un prodotto sui social media
   * @param productId - ID del prodotto da condividere
   * @param request - Dati per la condivisione
   * @returns Observable<ShareResponseDTO> - Risultato della condivisione
   */
  shareProductOnSocial(productId: number, request: ShareRequestDTO): Observable<ShareResponseDTO> {
    return this.http.post<ShareResponseDTO>(`${this.apiUrl}/api/prodotti/${productId}/share`, request).pipe(
      catchError(this.handleError)
    );
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

  // Converte i filtri in HttpParams per le chiamate API
  private buildParamsFromFilters(filters?: ProductFilters, pagination?: PaginationParams): HttpParams {
    let params = new HttpParams();
    
    if (pagination) {
      params = params.set('page', pagination.page.toString());
      params = params.set('size', pagination.size.toString());
      if (pagination.sortBy) {
        params = params.set('sortBy', pagination.sortBy);
      }
      if (pagination.sortDirection) {
        params = params.set('sortDirection', pagination.sortDirection);
      }
    }
    
    if (filters) {
      if (filters.search) {
        params = params.set('search', filters.search);
      }
      if (filters.categoria) {
        params = params.set('categoria', filters.categoria);
      }
      if (filters.prezzoMin !== undefined) {
        params = params.set('prezzoMin', filters.prezzoMin.toString());
      }
      if (filters.prezzoMax !== undefined) {
        params = params.set('prezzoMax', filters.prezzoMax.toString());
      }
      if (filters.venditoreId) {
        params = params.set('venditoreId', filters.venditoreId.toString());
      }
      if (filters.soloDisponibili) {
        params = params.set('soloDisponibili', 'true');
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

  // === GESTIONE ERRORI ===
  
  /**
   * Gestisce gli errori delle chiamate HTTP
   * @param error - Errore HTTP
   * @returns Observable<never> - Stream di errore
   */
  private handleError(error: any): Observable<never> {
    console.error('Errore nel servizio Acquirente:', error);
    
    let errorMessage = 'Si è verificato un errore imprevisto';
    
    if (error.error instanceof ErrorEvent) {
      // Errore client-side
      errorMessage = `Errore client-side: ${error.error.message}`;
    } else {
      // Errore server-side
      if (error.status === 401) {
        errorMessage = 'Non autorizzato. Effettua nuovamente il login.';
      } else if (error.status === 403) {
        errorMessage = 'Accesso negato. Non hai i permessi per eseguire questa operazione.';
      } else if (error.status === 404) {
        errorMessage = 'Risorsa non trovata.';
      } else if (error.status === 400) {
        errorMessage = error.error?.message || 'Richiesta non valida.';
      } else if (error.status >= 500) {
        errorMessage = 'Errore del server. Riprova più tardi.';
      } else {
        errorMessage = error.error?.message || `Errore ${error.status}: ${error.message}`;
      }
    }
    
    return throwError(() => new Error(errorMessage));
  }
}