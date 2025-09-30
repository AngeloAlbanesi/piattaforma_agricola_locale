import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { environment } from '../../../environments/environment';
import { AuthService } from '../services/auth.service';

export const authTokenInterceptor: HttpInterceptorFn = (req, next) => {
    const authService = inject(AuthService);
    const token = authService.authState().token;

    const isApiRequest = req.url.startsWith(environment.apiBaseUrl) || req.url.startsWith(environment.apiPrefix);

    if (token && isApiRequest) {
        const authReq = req.clone({
            setHeaders: {
                Authorization: `Bearer ${token}`,
            },
        });
        return next(authReq);
    }

    return next(req);
};
