import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { environment } from '../../../environments/environment';
import { AuthService } from '../services/auth.service';

export const authTokenInterceptor: HttpInterceptorFn = (req, next) => {
    const authService = inject(AuthService);
    const token = authService.authState().token;

    const isApiRequest = req.url.startsWith(environment.apiBaseUrl) || req.url.startsWith(environment.apiPrefix);
    const isLoginOrRegister = req.url.endsWith('/auth/login') || req.url.endsWith('/auth/register');

    // Aggiungi token a tutte le richieste API tranne login/register
    // NOTA: /auth/profile richiede autenticazione, quindi non escludiamo tutte le richieste /auth
    if (token && isApiRequest && !isLoginOrRegister) {
        const authReq = req.clone({
            setHeaders: {
                Authorization: `Bearer ${token}`,
            },
        });
        return next(authReq);
    }

    return next(req);
};
