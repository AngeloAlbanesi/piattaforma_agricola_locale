import { Injectable, signal } from '@angular/core';

export interface AuthContext {
    token: string | null;
    roles: string[];
    username: string | null;
}

@Injectable({ providedIn: 'root' })
export class AuthService {
    private readonly ctx = signal<AuthContext>({ token: null, roles: [], username: null });

    readonly authState = this.ctx.asReadonly();

    isAuthenticated(): boolean {
        return Boolean(this.ctx().token);
    }

    hasRole(role: string): boolean {
        return this.ctx().roles.includes(role);
    }

    setAuthState(state: AuthContext): void {
        this.ctx.set(state);
    }

    clearAuthState(): void {
        this.ctx.set({ token: null, roles: [], username: null });
    }
}
