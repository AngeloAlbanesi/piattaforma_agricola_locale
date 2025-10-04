import { Injectable, signal } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, BehaviorSubject, tap, retry, delay } from 'rxjs';
import { environment } from '../../../environments/environment';
import { UserDetailDTO, UserUpdateDTO } from '../models/curatore.models';

export interface AuthContext {
    token: string | null;
    roles: string[];
    username: string | null;
    userId?: number;
    role?: string;
}

export interface LoginRequest {
    email: string;
    password: string;
}

export interface RegisterRequest {
    username: string;
    email: string;
    password: string;
    nome: string;
    cognome: string;
    ruolo: string;
    telefono?: string;
    indirizzo?: string;
    datiAzienda?: {
        nomeAzienda: string;
        partitaIva: string;
        indirizzoAzienda: string;
        descrizioneAzienda: string;
    };
}

export interface AuthenticationResponse {
    token: string;
    type: string;
    id: number;
    username: string;
    email: string;
    roles: string[];
}

@Injectable({ providedIn: 'root' })
export class AuthService {
    private readonly ctx = signal<AuthContext>({ token: null, roles: [], username: null });
    private readonly apiAuthUrl = this.buildApiUrl('/auth');

    readonly authState = this.ctx.asReadonly();

    constructor(
        private http: HttpClient,
        private router: Router
    ) {
        // Ripristina stato auth dal localStorage all'avvio
        this.restoreAuthState();
    }

    private restoreAuthState(): void {
        const token = localStorage.getItem('auth_token');
        const username = localStorage.getItem('auth_username');
        const roles = JSON.parse(localStorage.getItem('auth_roles') || '[]');
        const userId = parseInt(localStorage.getItem('auth_user_id') || '0');
        const role = localStorage.getItem('auth_role') || undefined;

        if (token) {
            this.ctx.set({ token, roles, username, userId, role });
        }
    }

    private saveAuthState(response: AuthenticationResponse): void {
        const authData = {
            token: response.token,
            roles: response.roles,
            username: response.username,
            userId: response.id,
            role: response.roles[0] // Primo ruolo come ruolo principale
        };

        console.log('🔐 Salvataggio auth state:', authData);
        this.ctx.set(authData);

        // Salva nel localStorage
        localStorage.setItem('auth_token', response.token);
        localStorage.setItem('auth_username', response.username);
        localStorage.setItem('auth_roles', JSON.stringify(response.roles));
        localStorage.setItem('auth_user_id', response.id.toString());
        localStorage.setItem('auth_role', response.roles[0]);
        console.log('✅ Auth state salvato. Ruolo principale:', response.roles[0]);
    }

    isAuthenticated(): boolean {
        return Boolean(this.ctx().token);
    }

    hasRole(role: string): boolean {
        return this.ctx().roles.includes(role);
    }

    hasAnyRole(roles: string[]): boolean {
        return roles.some(role => this.hasRole(role));
    }

    getRole(): string | null {
        return this.ctx().role || null;
    }

    getUserId(): number | null {
        return this.ctx().userId || null;
    }

    login(credentials: LoginRequest): Observable<AuthenticationResponse> {
        return this.http.post<AuthenticationResponse>(`${this.apiAuthUrl}/login`, credentials).pipe(
            retry({ count: 2, delay: 1000 }),
            tap(response => {
                this.saveAuthState(response);
            })
        );
    }

    register(userData: RegisterRequest): Observable<AuthenticationResponse> {
        return this.http.post<AuthenticationResponse>(`${this.apiAuthUrl}/register`, userData).pipe(
            retry({ count: 2, delay: 1000 }),
            tap(response => {
                this.saveAuthState(response);
            })
        );
    }

    logout(): void {
        this.ctx.set({ token: null, roles: [], username: null });

        // Rimuovi dal localStorage
        localStorage.removeItem('auth_token');
        localStorage.removeItem('auth_username');
        localStorage.removeItem('auth_roles');
        localStorage.removeItem('auth_user_id');
        localStorage.removeItem('auth_role');

        // Reindirizza alla landing page
        this.router.navigate(['/']);
    }

    getAuthHeaders(): HttpHeaders {
        const token = this.ctx().token;
        let headers = new HttpHeaders({
            'Content-Type': 'application/json'
        });

        if (token) {
            headers = headers.set('Authorization', `Bearer ${token}`);
        }

        return headers;
    }

    // Metodi deprecatiati per compatibilità
    setAuthState(state: AuthContext): void {
        this.ctx.set(state);
    }

    clearAuthState(): void {
        this.logout();
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

    // === PROFILO (documentazione curatore) ===
    getProfile(): Observable<UserDetailDTO> {
        // La doc richiede Authorization anche su /auth/profile
        return this.http.get<UserDetailDTO>(`${this.apiAuthUrl}/profile`);
    }

    updateProfile(update: UserUpdateDTO): Observable<UserDetailDTO> {
        return this.http.put<UserDetailDTO>(`${this.apiAuthUrl}/profile`, update);
    }
}

// Tipi di ruolo disponibili
export const ROLES = {
    PRODUTTORE: 'PRODUTTORE',
    TRASFORMATORE: 'TRASFORMATORE',
    DISTRIBUTORE_TIPICITA: 'DISTRIBUTORE_TIPICITA',
    CURATORE: 'CURATORE',
    ANIMATORE_FILIERA: 'ANIMATORE_DELLA_FILIERA',
    ACQUIRENTE: 'ACQUIRENTE',
    GESTORE_PIATTAFORMA: 'GESTORE_PIATTAFORMA'
} as const;

export type UserRole = typeof ROLES[keyof typeof ROLES];
